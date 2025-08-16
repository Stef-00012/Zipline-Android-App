import { uploadFiles, type UploadFileOptions } from "@/functions/zipline/files";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { type ExternalPathString, Link, useRouter } from "expo-router";
import { ScrollView, Text, View, ToastAndroid } from "react-native";
import type { Preset, APIUploadResponse } from "@/types/zipline";
import { useDetectKeyboardOpen } from "@/hooks/isKeyboardOpen";
import { getFolders } from "@/functions/zipline/folders";
import { useShareIntent } from "@/hooks/useShareIntent";
import * as DocumentPicker from "expo-document-picker";
import { dates, formats } from "@/constants/upload";
import type { Mimetypes } from "@/types/mimetypes";
import FileDisplay from "@/components/FileDisplay";
import { styles } from "@/styles/files/uploadFile";
import { convertToBytes, guessExtension } from "@/functions/util";
import * as FileSystem from "expo-file-system";
import TextInput from "@/components/TextInput";
import { useContext, useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import { useAuth } from "@/hooks/useAuth";
import Select from "@/components/Select";
import Switch from "@/components/Switch";
import Button from "@/components/Button";
import Popup from "@/components/Popup";
import { File } from "expo-file-system/next";
import { ZiplineContext } from "@/contexts/ZiplineProvider";
import bytes from "bytes";

export interface SelectedFile {
	name: string;
	uri: string;
	instance: File;
	mimetype?: string;
	size?: number;
}

interface Props {
	showFileSelector?: boolean;
	defaultFiles?: SelectedFile[];
	fromShareIntent?: boolean;
}

export default function UploadFile({
	showFileSelector = true,
	defaultFiles,
	fromShareIntent = false,
}: Props) {
	useAuth();
	
	const router = useRouter();
	const resetShareIntent = useShareIntent(fromShareIntent);
	const { webSettings } = useContext(ZiplineContext)

	const maxFileSize = webSettings
		? webSettings.config.files.maxFileSize
		: "99TB"

	const stringifiedPresets = db.get("uploadPresets") || "[]";

	const [presets, setPresets] = useState<Preset[]>(
		JSON.parse(stringifiedPresets),
	);

	const [selectedFiles, setSelectedFiles] = useState<SelectedFile[]>(
		defaultFiles || [],
	);
	const [removedFiles, setRemovedFiles] = useState<SelectedFile[]>([]);
	const [uploadedFiles, setUploadedFiles] = useState<
		APIUploadResponse["files"]
	>([]);
	const [failedUploads, setFailedUploads] = useState<
		{
			uri: string;
			name: string;
			error: string;
		}[]
	>([]);

	const [overrideDomain, setOverrideDomain] =
		useState<UploadFileOptions["overrideDomain"]>();
	const [originalName, setOriginalName] =
		useState<UploadFileOptions["originalName"]>(false);
	const [compression, setCompression] =
		useState<UploadFileOptions["compression"]>();
	const [deletesAt, setDeletesAt] = useState<UploadFileOptions["expiresAt"]>();
	const [nameFormat, setNameFormat] = useState<UploadFileOptions["format"]>();
	const [maxViews, setMaxViews] = useState<UploadFileOptions["maxViews"]>();
	const [fileName, setFileName] = useState<UploadFileOptions["filename"]>();
	const [password, setPassword] = useState<UploadFileOptions["password"]>();
	const [folder, setFolder] = useState<UploadFileOptions["folder"]>();

	const [folders, setFolders] = useState<
		{
			label: string;
			value: string;
		}[]
	>([]);

	const defaultFormat = webSettings
		? webSettings.config.files.defaultFormat
		: "random"

	const [uploadButtonDisabled, setUploadButtonDisabled] =
		useState<boolean>(true);
	const [fileNameEnabled, setFileNameEnabled] = useState<boolean>(
		defaultFiles ? defaultFiles.length <= 1 : true,
	);
	const [uploading, setUploading] = useState<boolean>(false);
	const [uploadPercentage, setUploadPercentage] = useState<string>("0");

	const [savePreset, setSavePreset] = useState<boolean>(false);
	const [newPresetName, setNewPresetName] = useState<string>("");
	const [newPresetError, setNewPresetError] = useState<string | null>(null);

	const [editPresetName, setEditPresetName] = useState<string>("");
	const [editPresetError, setEditPresetError] = useState<string | null>(null);
	const [presetToEdit, setPresetToEdit] = useState<string | null>(null);

	useEffect(() => {
		const bytesMaxFileSize = bytes(maxFileSize) || 0

		const removedFiles: SelectedFile[] = [];

		for (const file of selectedFiles) {
			if (file.size && file.size > bytesMaxFileSize) {
				removedFiles.push(file)
			}
		}

		if (removedFiles.length > 0) {
			const newSelectedFiles = selectedFiles.filter((file) => removedFiles.every((removedFile) => removedFile.uri !== file.uri))

			setSelectedFiles(newSelectedFiles)
			setRemovedFiles(removedFiles)
		}
	}, [selectedFiles, maxFileSize])

	useEffect(() => {
		(async () => {
			const folders = await getFolders(true);

			if (typeof folders === "string") return setFolders([]);

			const formattedFolders = folders.map((folder) => ({
				label: folder.name,
				value: folder.id,
			}));

			setFolders(formattedFolders);
		})();
	}, []);

	function afterUploadCleanup() {
		setUploading(false);
		setSelectedFiles([]);

		setFolder(undefined);
		setMaxViews(undefined);
		setFileName(undefined);
		setPassword(undefined);
		setOriginalName(false);
		setDeletesAt(undefined);
		setNameFormat(undefined);
		setCompression(undefined);
		setOverrideDomain(undefined);
	}

	useEffect(() => {
		setUploadButtonDisabled(selectedFiles.length === 0);
		setFileNameEnabled(selectedFiles.length <= 1);
	}, [selectedFiles]);

	const iskeyboardOpen = useDetectKeyboardOpen(false);

	return (
		<View style={styles.mainContainer}>
			<Popup
				onClose={() => {
					setUploadedFiles([]);
					setFailedUploads([]);
				}}
				hidden={!(uploadedFiles.length > 0 || failedUploads.length > 0)}
			>
				{uploadedFiles.length > 0 && (
					<View>
						<Text style={styles.headerText}>Uploaded Files</Text>
						<ScrollView style={styles.popupScrollView}>
							{uploadedFiles.map((file) => (
								<View key={file.id} style={styles.uploadedFileContainer}>
									<Link
										href={file.url as ExternalPathString}
										style={styles.uploadedFileUrl}
									>
										{file.url}
									</Link>

									<Button
										icon="content-copy"
										color="#323ea8"
										onPress={async () => {
											const saved = await Clipboard.setStringAsync(file.url);

											if (saved)
												return ToastAndroid.show(
													"URL copied to clipboard",
													ToastAndroid.SHORT,
												);

											ToastAndroid.show(
												"Failed to copy the URL",
												ToastAndroid.SHORT,
											);
										}}
										iconSize={20}
										width={32}
										height={32}
										padding={6}
										margin={{
											left: 5,
											right: 5,
										}}
									/>

									<Button
										icon="open-in-new"
										color="#323ea8"
										onPress={() => {
											router.replace(file.url as ExternalPathString);
										}}
										iconSize={20}
										width={32}
										height={32}
										padding={6}
										margin={{
											left: 5,
											right: 5,
										}}
									/>
								</View>
							))}
						</ScrollView>
					</View>
				)}

				{failedUploads.length > 0 && (
					<View
						style={{
							...(uploadedFiles.length > 0 && {
								marginTop: 10,
							}),
						}}
					>
						<Text style={styles.headerText}>Failed Files</Text>
						<ScrollView style={styles.popupScrollView}>
							{failedUploads.map((file) => (
								<Text key={file.uri} style={styles.failedFileText}>
									<Text style={{ fontWeight: "bold" }}>{file.name}</Text>:{" "}
									<Text style={{ color: "red" }}>{file.error}</Text>
								</Text>
							))}
						</ScrollView>
					</View>
				)}

				<Text
					style={{
						...styles.subHeaderText,
						...styles.popupSubHeaderText,
					}}
				>
					Press outside to close this popup
				</Text>
			</Popup>

			<Popup onClose={() => setSavePreset(false)} hidden={!savePreset}>
				<View>
					<Text style={styles.headerText}>Save Preset</Text>

					{newPresetError && (
						<Text style={styles.errorText}>{newPresetError}</Text>
					)}

					<TextInput
						title="Name"
						placeholder="My Cool Name"
						value={newPresetName}
						onValueChange={(text) => {
							setNewPresetName(text);
						}}
					/>

					<Button
						color="#323ea8"
						text="Save"
						icon="save"
						margin={{
							top: 10,
						}}
						onPress={() => {
							setNewPresetError(null);

							if (newPresetName.length <= 0)
								return setNewPresetError("Please add a preset name");
							if (presets.find((preset) => preset.name === newPresetName))
								return setNewPresetError(
									"A preset with this name already exists",
								);

							const newPresets: Preset[] = [
								...presets,
								{
									compression: compression,
									deletesAt: deletesAt,
									folder: folder,
									format: nameFormat,
									maxViews: maxViews,
									name: newPresetName,
									originalName: originalName,
									overrideDomain: overrideDomain,
									overrideFileName: fileName,
									password: password,
								},
							];

							setPresets(newPresets);

							db.set("uploadPresets", JSON.stringify(newPresets));

							ToastAndroid.show(
								`Successfully saved the preset "${newPresetName}"`,
								ToastAndroid.SHORT,
							);

							setNewPresetName("");
							setSavePreset(false);
						}}
					/>
				</View>
			</Popup>

			<Popup onClose={() => setPresetToEdit(null)} hidden={!presetToEdit}>
				<View>
					<Text style={styles.headerText}>Edit Preset</Text>

					{editPresetError && (
						<Text style={styles.errorText}>{editPresetError}</Text>
					)}

					<TextInput
						title="Name"
						placeholder="My Cool Name"
						value={editPresetName}
						onValueChange={(text) => {
							setEditPresetName(text);
						}}
					/>

					<Button
						color="#323ea8"
						text="Save"
						icon="save"
						margin={{
							top: 10,
						}}
						onPress={() => {
							setEditPresetError(null);

							if (editPresetName.length <= 0)
								return setEditPresetError("Please add a preset name");

							const newPresets: Preset[] = presets;

							const presetIndex = newPresets.findIndex(
								(preset) => preset.name === presetToEdit,
							);

							newPresets[presetIndex].name = editPresetName;

							setPresets(newPresets);

							db.set("uploadPresets", JSON.stringify(newPresets));

							ToastAndroid.show(
								`Successfully edited the preset "${editPresetName}"`,
								ToastAndroid.SHORT,
							);

							setPresetToEdit(null);
						}}
					/>
				</View>
			</Popup>

			<Popup onClose={() => setRemovedFiles([])} hidden={removedFiles.length <= 0}>
				<View>
					<Text style={styles.headerText}>Removed Files</Text>
					<Text style={styles.subHeaderText}>Some files have been removed because they exceed the limit of {convertToBytes(maxFileSize)}.</Text>

					{removedFiles.map((file) => (
						<Text style={styles.text} key={file.uri}>{file.name} ({file.size ? convertToBytes(file.size) : "Unknown Size"})</Text>
					))}
				</View>
			</Popup>

			<View>
				<View style={styles.header}>
					<View>
						<Text style={styles.headerText}>Selected Files</Text>
						<Text style={styles.subHeaderText}>Click a file to remove it</Text>
					</View>

					<View style={styles.headerButtons}>
						<Button
							onPress={() => {
								resetShareIntent();

								router.replace("/files");
							}}
							icon="folder-open"
							color="transparent"
							iconColor="#2d3f70"
							borderColor="#222c47"
							borderWidth={2}
							iconSize={30}
							padding={4}
							rippleColor="#283557"
						/>
					</View>
				</View>

				<ScrollView
					horizontal
					style={{
						...styles.scrollView,
						...(iskeyboardOpen && styles.scrollViewKeyboardOpen),
					}}
				>
					{selectedFiles.map((file) => (
						<View key={file.uri} style={styles.recentFileContainer}>
							<FileDisplay
								uri={file.uri}
								name={file.name}
								width={200}
								height={200}
								mimetype={file.mimetype}
								openable={false}
								onPress={() => {
									if (uploading) return;
									setSelectedFiles((alreadySelectedFiles) =>
										alreadySelectedFiles.filter(
											(selectedFile) => selectedFile.uri !== file.uri,
										),
									);
								}}
							/>
						</View>
					))}
				</ScrollView>
			</View>

			{showFileSelector && (
				<View>
					<Text style={{
						...styles.subHeaderText,
						...styles.fileLimitText
					}}>
						{convertToBytes(maxFileSize, {
							unitSeparator: " "
						})} limit per file
					</Text>

					<Button
						width="90%"
						disabled={uploading}
						onPress={async () => {
							const output = await DocumentPicker.getDocumentAsync({
								type: "*/*",
								multiple: true,
								copyToCacheDirectory: true, // temporary
							});

							if (output.canceled || output.assets?.length <= 0) return;

							const newSelectedFiles: SelectedFile[] = output.assets
								.map((file) => {
									const fileInstance = new File(file.uri);

									return {
										name: file.name,
										uri: file.uri,
										instance: fileInstance,
										mimetype: file.mimeType || fileInstance.type || undefined,
										size: file.size || fileInstance.size || undefined,
									};
								})
								.filter(
									(newFile) =>
										!selectedFiles.find(
											(selectedFile) =>
												newFile.size === selectedFile.size &&
												newFile.name === selectedFile.name,
										),
								);

							setSelectedFiles((alreadySelectedFiles) => [
								...alreadySelectedFiles,
								...newSelectedFiles,
							]);
						}}
						text="Select File(s)"
						color={uploading ? "#373d79" : "#323ea8"}
						textColor={uploading ? "gray" : "white"}
						margin={{
							left: "auto",
							right: "auto",
							top: 10,
						}}
					/>
				</View>
			)}

			<KeyboardAwareScrollView
				keyboardShouldPersistTaps="always"
				style={styles.inputsContainer}
				showsVerticalScrollIndicator={false}
			>
				<Text
					style={{
						...styles.inputHeader,
						...(uploading && styles.inputHeaderDisabled),
					}}
				>
					Deletes At:
				</Text>
				<Select
					data={dates}
					placeholder="Select Date..."
					disabled={uploading}
					onSelect={(selectedDate) => {
						if (selectedDate.length <= 0) return;

						if (selectedDate[0].value === "never")
							return setDeletesAt(undefined);

						const deletesDate = new Date(
							Date.now() + (selectedDate[0].milliseconds as number),
						);

						setDeletesAt(deletesDate);
					}}
				/>

				<Text
					style={{
						...styles.inputHeader,
						...(uploading && styles.inputHeaderDisabled),
					}}
				>
					Format:
				</Text>
				<Select
					data={[
						{
							label: `Default (${defaultFormat})`,
							value: defaultFormat || "random",
						},
						...formats,
					]}
					disabled={uploading}
					placeholder="Select Format..."
					onSelect={(selectedFormat) => {
						if (selectedFormat.length <= 0) return;

						setNameFormat(
							selectedFormat[0].value as UploadFileOptions["format"],
						);
					}}
				/>

				<TextInput
					title="Compression:"
					onValueChange={(content) => {
						let compressionPercentage = Number.parseInt(content);

						if (compressionPercentage > 100) compressionPercentage = 100;
						if (compressionPercentage < 0) compressionPercentage = 0;

						setCompression(compressionPercentage);
					}}
					keyboardType="numeric"
					disableContext={uploading}
					disabled={uploading}
					value={compression ? String(compression) : ""}
					placeholder="0"
				/>

				<TextInput
					title="Max Views:"
					onValueChange={(content) => {
						let maxViewsAmount = Number.parseInt(content);

						if (maxViewsAmount < 0) maxViewsAmount = 0;

						setMaxViews(maxViewsAmount);
					}}
					keyboardType="numeric"
					disableContext={uploading}
					disabled={uploading}
					value={maxViews ? String(maxViews) : ""}
					placeholder="0"
				/>

				<Text
					style={{
						...styles.inputHeader,
						...(uploading && styles.inputHeaderDisabled),
					}}
				>
					Folder:
				</Text>
				<Select
					data={[
						{
							label: "No Folder",
							value: "none",
						},
						...folders,
					]}
					defaultValue={folders.find((fold) => fold.value === folder)}
					disabled={uploading}
					placeholder="Select Folder..."
					onSelect={(selectedFolder) => {
						if (selectedFolder.length <= 0) return;

						setFolder(
							selectedFolder[0].value === "noFolder"
								? undefined
								: selectedFolder[0].value,
						);
					}}
					maxHeight={400}
				/>

				<TextInput
					title="Override Domain:"
					onValueChange={(content) => setOverrideDomain(content)}
					keyboardType="url"
					disableContext={uploading}
					disabled={uploading}
					value={overrideDomain || ""}
					placeholder="example.com"
				/>

				<TextInput
					title="Override File Name:"
					disableContext={fileNameEnabled || !uploading}
					disabled={!fileNameEnabled || uploading}
					onValueChange={(content) => setFileName(content)}
					value={fileNameEnabled ? fileName || "" : ""}
					placeholder="example.png"
				/>

				<TextInput
					title="Password:"
					onValueChange={(content) => setPassword(content)}
					disableContext={uploading}
					disabled={uploading}
					password
					value={password || ""}
					placeholder="myPassword"
				/>

				<Switch
					title="Add Original Name"
					value={originalName || false}
					disabled={uploading}
					onValueChange={() => setOriginalName((prev) => !prev)}
				/>

				<Text
					style={{
						...styles.inputHeader,
						...(uploading && styles.inputHeaderDisabled),
					}}
				>
					Preset:
				</Text>
				<View style={{ flexDirection: "row" }}>
					<Select
						width={"45%"}
						margin={{
							left: "2.5%",
							right: "2.5%",
						}}
						data={presets.map((preset) => ({
							label: preset.name,
							value: preset.name,
						}))}
						disabled={presets.length <= 0}
						placeholder="Select Preset..."
						onSelect={(selectePreset) => {
							if (selectePreset.length <= 0) return;

							const preset = selectePreset[0].value;

							const presetToUse = presets.find((pres) => pres.name === preset);

							if (!presetToUse)
								return ToastAndroid.show("Invalid Preset", ToastAndroid.SHORT);

							setDeletesAt(presetToUse.deletesAt);
							setNameFormat(presetToUse.format);
							setCompression(presetToUse.compression);
							setMaxViews(presetToUse.maxViews);
							setOverrideDomain(presetToUse.overrideDomain);
							setFileName(presetToUse.overrideFileName);
							setPassword(presetToUse.password);
							setOriginalName(presetToUse.originalName);

							if (folders.find((folder) => folder.value === presetToUse.folder))
								setFolder(presetToUse.folder);
						}}
						renderItem={(item, closeSelect) => (
							<View style={styles.selectItemContainer}>
								<Text style={styles.selectText}>{item.label}</Text>

								<View style={styles.selectButtonContainer}>
									<Button
										color="#323ea8"
										onPress={() => {
											setEditPresetName(
												presets.find((preset) => preset.name === item.value)
													?.name || "",
											);
											setPresetToEdit(item.value);

											closeSelect();
										}}
										width={30}
										height={30}
										icon="edit"
										iconSize={20}
										padding={5}
										margin={{
											right: 5,
										}}
									/>

									<Button
										color="#CF4238"
										onPress={() => {
											const newPresets = presets.filter(
												(preset) => preset.name !== item.value,
											);

											setPresets(newPresets);

											db.set("uploadPresets", JSON.stringify(newPresets));

											ToastAndroid.show(
												`Successfully deleted the preset "${item.label}"`,
												ToastAndroid.SHORT,
											);

											if (newPresets.length <= 0) closeSelect();
										}}
										width={30}
										height={30}
										icon="delete"
										iconSize={20}
										padding={5}
										margin={{
											left: 5,
										}}
									/>
								</View>
							</View>
						)}
					/>

					<Button
						width={"45%"}
						text="Save Preset"
						onPress={() => {
							setSavePreset(true);
						}}
						color="#323ea8"
						margin={{
							left: "2.5%",
							right: "2.5%",
							top: 5,
						}}
					/>
				</View>
			</KeyboardAwareScrollView>

			<View>
				<Button
					width="90%"
					disabled={uploading || uploadButtonDisabled}
					onPress={async () => {
						setUploading(true);

						const successful = [];
						const fails: typeof failedUploads = [];

						for (const file of selectedFiles) {
							const fileInfo = await FileSystem.getInfoAsync(file.uri, {
								size: true,
							});

							if (!fileInfo.exists || fileInfo.isDirectory) {
								fails.push({
									error: "File does not exist",
									uri: file.uri,
									name: file.name,
								});

								continue;
							}

							const mimetype =
								file.mimetype ||
								guessExtension(
									file.uri.split(".").pop() as Mimetypes[keyof Mimetypes],
								);

							const fileData = {
								uri: fileInfo.uri || file.uri,
								blob: file.instance.blob(),
								mimetype,
							};

							const uploadedFile = await uploadFiles(
								fileData,
								{
									compression,
									expiresAt: deletesAt,
									filename: fileNameEnabled ? fileName : undefined,
									folder,
									format: nameFormat,
									maxViews,
									originalName,
									overrideDomain,
									password,
								},
								(uploadData) => {
									setUploadPercentage(
										(
											(uploadData.totalBytesSent /
												uploadData.totalBytesExpectedToSend) *
											100
										).toFixed(2),
									);
								},
							);

							if (typeof uploadedFile === "string") {
								fails.push({
									error: uploadedFile,
									uri: file.uri,
									name: file.name,
								});

								continue;
							}

							successful.push(uploadedFile[0]);
						}

						setUploadedFiles(successful);
						setFailedUploads(fails);

						afterUploadCleanup();
					}}
					text={
						uploading ? `Uploading... ${uploadPercentage}%` : "Upload File(s)"
					}
					color={uploading || uploadButtonDisabled ? "#373d79" : "#323ea8"}
					textColor={uploading || uploadButtonDisabled ? "gray" : "white"}
					margin={{
						left: "auto",
						right: "auto",
						top: 10,
						bottom: 10,
					}}
				/>
			</View>
		</View>
	);
}
