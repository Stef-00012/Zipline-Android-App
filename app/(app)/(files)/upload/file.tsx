import Button from "@/components/Button";
import FileDisplay from "@/components/FileDisplay";
import Popup from "@/components/Popup";
import Select from "@/components/Select";
import Switch from "@/components/Switch";
import TextInput from "@/components/TextInput";
import { dates, formats } from "@/constants/upload";
import { ZiplineContext } from "@/contexts/ZiplineProvider";
import * as db from "@/functions/database";
import { convertToBytes, guessExtension } from "@/functions/util";
import { uploadFiles, type UploadFileOptions } from "@/functions/zipline/files";
import { getFolders } from "@/functions/zipline/folders";
import { useDetectKeyboardOpen } from "@/hooks/isKeyboardOpen";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import { styles } from "@/styles/files/uploadFile";
import type { Mimetypes } from "@/types/mimetypes";
import type { APIUploadResponse, Preset } from "@/types/zipline";
import bytes from "bytes";
import * as Clipboard from "expo-clipboard";
import * as DocumentPicker from "expo-document-picker";
import { Directory, File, Paths } from "expo-file-system";
import * as FileSystem from "expo-file-system/legacy";
import * as ImagePicker from "expo-image-picker";
import { Link, useRouter, type ExternalPathString } from "expo-router";
import { useContext, useEffect, useState } from "react";
import { ScrollView, Text, ToastAndroid, View } from "react-native";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";

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
	const { webSettings, publicSettings } = useContext(ZiplineContext);

	const maxFileSize = webSettings
		? webSettings.config.files.maxFileSize
		: "99TB";

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

	const [isCopying, setIsCopying] = useState<boolean>(false);

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

	const chunkSize = webSettings ? webSettings.config.chunks.size : "25mb";

	const maxChunkSize = webSettings ? webSettings.config.chunks.max : "95mb";

	const chunksEnabled = webSettings ? webSettings.config.chunks.enabled : false;

	const defaultFormat = webSettings
		? webSettings.config.files.defaultFormat
		: "random";

	const domainList = publicSettings ? (publicSettings.domains ?? []) : [];

	const domains = domainList.map((domain) => ({
		label: domain,
		value: domain,
	}));

	const [uploadButtonDisabled, setUploadButtonDisabled] =
		useState<boolean>(true);
	const [fileNameEnabled, setFileNameEnabled] = useState<boolean>(
		defaultFiles ? defaultFiles.length <= 1 : true,
	);
	const [uploading, setUploading] = useState<boolean>(false);
	const [uploadPercentage, setUploadPercentage] = useState<string>("0");

	const [isChunked, setIsChunked] = useState<boolean>(false);
	const [numberOfChunks, setNumberOfChunks] = useState<number>(0);
	const [currentChunk, setCurrentChunk] = useState<number>(0);
	const [chunkPercentage, setChunkPercentage] = useState<string>("0");

	const [savePreset, setSavePreset] = useState<boolean>(false);
	const [newPresetName, setNewPresetName] = useState<string>("");
	const [newPresetError, setNewPresetError] = useState<string | null>(null);

	const [editPresetName, setEditPresetName] = useState<string>("");
	const [editPresetError, setEditPresetError] = useState<string | null>(null);
	const [presetToEdit, setPresetToEdit] = useState<string | null>(null);

	const [cameraPermission, requestCameraPermission] =
		ImagePicker.useCameraPermissions();

	useEffect(() => {
		const bytesMaxFileSize = bytes(maxFileSize) || 0;

		const removedFiles: SelectedFile[] = [];

		for (const file of selectedFiles) {
			if (file.size && file.size > bytesMaxFileSize) {
				removedFiles.push(file);
			}
		}

		if (removedFiles.length > 0) {
			const newSelectedFiles = selectedFiles.filter((file) =>
				removedFiles.every((removedFile) => removedFile.uri !== file.uri),
			);

			setSelectedFiles(newSelectedFiles);
			setRemovedFiles(removedFiles);
		}
	}, [selectedFiles, maxFileSize]);

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
										icon="content_copy"
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
										containerStyle={{
											width: 32,
											height: 32,
											marginHorizontal: 5
										}}
										iconStyle={{
											marginBottom: 7
										}}
									/>

									<Button
										icon="open_in_new"
										color="#323ea8"
										onPress={() => {
											router.push(file.url as ExternalPathString);
										}}
										iconSize={20}
										containerStyle={{
											width: 32,
											height: 32,
											marginHorizontal: 5
										}}
										iconStyle={{
											marginBottom: 7
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
						containerStyle={{
							marginTop: 10,
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
						containerStyle={{
							marginTop: 10,
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

			<Popup
				onClose={() => setRemovedFiles([])}
				hidden={removedFiles.length <= 0}
			>
				<View>
					<Text style={styles.headerText}>Removed Files</Text>
					<Text style={styles.subHeaderText}>
						Some files have been removed because they exceed the limit of{" "}
						{convertToBytes(maxFileSize)}.
					</Text>

					{removedFiles.map((file) => (
						<Text style={styles.text} key={file.uri}>
							{file.name} (
							{file.size ? convertToBytes(file.size) : "Unknown Size"})
						</Text>
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
							onPress={async () => {
								if (
									cameraPermission?.status ===
									ImagePicker.PermissionStatus.UNDETERMINED
								)
									await requestCameraPermission();
								if (
									cameraPermission?.status ===
									ImagePicker.PermissionStatus.DENIED
								) {
									return ToastAndroid.show(
										"Camera permission is denied",
										ToastAndroid.SHORT,
									);
								}

								const res = await ImagePicker.launchCameraAsync({
									allowsEditing: true,
									base64: false,
									exif: false,
									mediaTypes: ["videos", "images"],
									videoMaxDuration: 0,
									quality: 1,
								});

								if (res.canceled || res.assets.length <= 0) return;

								const image = res.assets[0];

								const date = new Date().toISOString().replace(/:/g, "-");
								const extension = image.uri.split(".").pop() as string;

								const fileName = `Camera_${date}.${extension}`;

								setSelectedFiles((alreadySelectedFiles) => [
									...alreadySelectedFiles,
									{
										instance: new File(image.uri),
										name: image.fileName || fileName,
										uri: image.uri,
										mimetype: image.mimeType,
										size: image.fileSize,
									},
								]);
							}}
							icon="camera_alt"
							color="transparent"
							iconColor="#2d3f70"
							containerStyle={{
								borderColor: "#222c47",
								borderWidth: 2,
								marginHorizontal: 2
							}}
							buttonStyle={{
								padding: 4
							}}
							iconSize={30}
							rippleColor="#283557"
						/>

						<Button
							onPress={() => {
								resetShareIntent();

								router.push("/files");
							}}
							icon="folder_open"
							color="transparent"
							iconColor="#2d3f70"
							containerStyle={{
								borderColor: "#222c47",
								borderWidth: 2,
								marginHorizontal: 2
							}}
							buttonStyle={{
								padding: 4
							}}
							iconSize={30}
							rippleColor="#283557"
						/>
					</View>
				</View>

				{isCopying && (
					<Text style={styles.copyText}>
						Preparing your files... This may take a few moments
					</Text>
				)}

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
					<Text
						style={{
							...styles.subHeaderText,
							...styles.fileLimitText,
						}}
					>
						{convertToBytes(maxFileSize, {
							unitSeparator: " ",
						})}{" "}
						limit per file
					</Text>

					<Button
						containerStyle={{
							width: "90%",
							marginHorizontal: "auto",
							marginTop: 10,
						}}
						disabled={uploading || isCopying}
						onPress={async () => {
							const output = await DocumentPicker.getDocumentAsync({
								type: "*/*",
								multiple: true,
								copyToCacheDirectory: false,
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

							const cacheDir = new Directory(Paths.cache);

							setIsCopying(true);

							for (const file of newSelectedFiles) {
								const fileSAFURI = file.uri;
								const outputURI = `${cacheDir.uri}/${file.name}`;

								const outputFile = await FileSystem.getInfoAsync(outputURI);

								if (outputFile.exists) await FileSystem.deleteAsync(outputURI);

								await FileSystem.copyAsync({
									from: fileSAFURI,
									to: outputURI,
								});

								file.uri = outputURI;
							}

							setIsCopying(false);

							setSelectedFiles((alreadySelectedFiles) => [
								...alreadySelectedFiles,
								...newSelectedFiles,
							]);
						}}
						text="Select File(s)"
						color={uploading || isCopying ? "#373d79" : "#323ea8"}
						textColor={uploading || isCopying ? "gray" : "white"}
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
						...((uploading || isCopying) && styles.inputHeaderDisabled),
					}}
				>
					Deletes At:
				</Text>
				<Text style={styles.selectDescription}>
					The file will automatically delete itself after this time. You can set a default expiration time in the <Link style={styles.linkText} href="/admin/settings">settings</Link>.
				</Text>
				<Select
					data={dates}
					placeholder="Select Date..."
					disabled={uploading || isCopying}
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
						...((uploading || isCopying) && styles.inputHeaderDisabled),
					}}
				>
					Name Format:
				</Text>
				<Text style={styles.selectDescription}>
					The file name format to use when upload this file, the "File name" field will override this value.
				</Text>
				<Select
					data={[
						{
							label: `Default (${defaultFormat})`,
							value: defaultFormat || "random",
						},
						...formats,
					]}
					disabled={uploading || isCopying}
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
					description="The compression level to use on images (only). The above format will be used to compress images. Leave blank to disable compression."
					onValueChange={(content) => {
						let compressionPercentage = Number.parseInt(content, 10);

						if (compressionPercentage > 100) compressionPercentage = 100;
						if (compressionPercentage < 0) compressionPercentage = 0;

						setCompression(compressionPercentage);
					}}
					keyboardType="numeric"
					disableContext={uploading || isCopying}
					disabled={uploading || isCopying}
					value={compression ? String(compression) : ""}
					placeholder="0"
				/>

				<TextInput
					title="Max Views:"
					description="The maximum number of views the files can have before they are deleted. Leave blank to allow as many views as you want."
					onValueChange={(content) => {
						let maxViewsAmount = Number.parseInt(content, 10);

						if (maxViewsAmount < 0) maxViewsAmount = 0;

						setMaxViews(maxViewsAmount);
					}}
					keyboardType="numeric"
					disableContext={uploading || isCopying}
					disabled={uploading || isCopying}
					value={maxViews ? String(maxViews) : ""}
					placeholder="0"
				/>

				<Text
					style={{
						...styles.inputHeader,
						...((uploading || isCopying) && styles.inputHeaderDisabled),
					}}
				>
					Add to a Folder:
				</Text>
				<Text style={styles.selectDescription}>
					Add this file to a folder. Use the "no folder" option not add the file to a folder. This value is not saved to your browser, and is cleared after uploading.
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
					disabled={uploading || isCopying}
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

				<Text
					style={{
						...styles.inputHeader,
						...((uploading || isCopying) && styles.inputHeaderDisabled),
					}}
				>
					Override Domain:
				</Text>
				<Text style={styles.selectDescription}>
					Override the domain with this value. This will change the domain returned in your uploads. Leave blank to use the default domain.
				</Text>
				<Select
					data={[
						{
							label: "Default Domain",
							value: "defaultDomain",
						},
						...domains,
					]}
					defaultValue={domains.find(
						(domain) => domain.value === overrideDomain,
					)}
					disabled={uploading || isCopying}
					placeholder="Select Domain..."
					onSelect={(selectedDomain) => {
						if (selectedDomain.length <= 0) return;

						setOverrideDomain(
							selectedDomain[0].value === "defaultDomain"
								? undefined
								: selectedDomain[0].value,
						);
					}}
					maxHeight={400}
				/>

				<TextInput
					title="Override File Name:"
					description={'Override the file name with this value. Leave blank to use the "Name Format" option. This value is ignored if you are uploading more than one file. This value is not saved to your browser, and is cleared after uploading.'}
					disableContext={fileNameEnabled || !uploading || isCopying}
					disabled={!fileNameEnabled || uploading || isCopying}
					onValueChange={(content) => setFileName(content)}
					value={fileNameEnabled ? fileName || "" : ""}
					placeholder="example.png"
				/>

				<TextInput
					title="Password:"
					description="Set a password for these files. Leave blank to disable password protection. This value is not saved to your browser, and is cleared after uploading."
					onValueChange={(content) => setPassword(content)}
					disableContext={uploading || isCopying}
					disabled={uploading || isCopying}
					password
					value={password || ""}
					placeholder="myPassword"
				/>

				<Switch
					title="Add Original Name"
					description={'Add the original file name, so that the file can be downloaded with the original name. This will still use the "Name Format" option for its file name.'}
					value={originalName || false}
					disabled={uploading || isCopying}
					onValueChange={() => setOriginalName((prev) => !prev)}
				/>

				<Text
					style={{
						...styles.inputHeader,
						...((uploading || isCopying) && styles.inputHeaderDisabled),
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
						disabled={presets.length <= 0 || uploading || isCopying}
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
										containerStyle={{
											width: 30,
											height: 30,
											marginRight: 5
										}}
										iconStyle={{
											marginBottom: 7
										}}
										buttonStyle={{
											padding: 5
										}}
										icon="edit"
										iconSize={20}
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
										containerStyle={{
											width: 30,
											height: 30,
											marginRight: 5
										}}
										iconStyle={{
											marginBottom: 7
										}}
										buttonStyle={{
											padding: 5
										}}
										icon="delete"
										iconSize={20}
									/>
								</View>
							</View>
						)}
					/>

					<Button
						containerStyle={{
							width: "45%",
							marginHorizontal: "2.5%",
							marginTop: 5
						}}
						text="Save Preset"
						onPress={() => {
							setSavePreset(true);
						}}
						disabled={uploading || isCopying}
						color={uploading || isCopying ? "#373d79" : "#323ea8"}
					/>
				</View>
			</KeyboardAwareScrollView>

			<View>
				<Button
					containerStyle={{
						width: "90%",
						marginHorizontal: "auto",
						marginVertical: 10
					}}
					disabled={uploading || uploadButtonDisabled || isCopying}
					onPress={async () => {
						setUploading(true);

						const successful = [];
						const fails: typeof failedUploads = [];

						for (const file of selectedFiles) {
							const fileInfo = await FileSystem.getInfoAsync(file.uri);

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

							const fileURI = fileInfo.uri || file.uri;

							const fileData = {
								uri: fileURI,
								blob: new File(fileURI),
								mimetype,
							};

							const uploadedFile = await uploadFiles(
								fileData,
								{
									chunksEnabled,
									chunkSize,
									maxChunkSize,
								},
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
								(uploadData, chunkData) => {
									const chunkPercentage =
										(uploadData.totalBytesSent /
											uploadData.totalBytesExpectedToSend) *
										100;
									let percentage = chunkPercentage;

									if (chunkData) {
										setIsChunked(true);
										setCurrentChunk(chunkData.currentChunk);
										setNumberOfChunks(chunkData.totalChunks);

										percentage =
											percentage / chunkData.totalChunks +
											(100 / chunkData.totalChunks) *
												(chunkData.currentChunk - 1);
									} else {
										setIsChunked(false);
									}

									setUploadPercentage(percentage.toFixed(2));
									setChunkPercentage(chunkPercentage.toFixed(2));
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
					color={
						uploading || isCopying || uploadButtonDisabled
							? "#373d79"
							: "#323ea8"
					}
					textColor={
						uploading || isCopying || uploadButtonDisabled ? "gray" : "white"
					}
					textJsx={(disabled, hasIcon) => (
						<View
							style={
								hasIcon && {
									marginLeft: 5,
								}
							}
						>
							<Text
								style={{
									...styles.uploadButtonText,
									color: disabled ? "gray" : "white",
								}}
							>
								{uploading ? (
									<Text>
										{isChunked ? (
											<Text>
												<Text>Uploading... {uploadPercentage}%</Text>
												<Text style={styles.uploadButtonChunkText}>
													{" "}
													{currentChunk}/{numberOfChunks} - {chunkPercentage}%
												</Text>
											</Text>
										) : (
											<Text>Uploading... {uploadPercentage}%</Text>
										)}
									</Text>
								) : (
									<Text>Upload File(s)</Text>
								)}
							</Text>
						</View>
					)}
				/>
			</View>
		</View>
	);
}
