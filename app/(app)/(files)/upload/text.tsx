import { uploadFiles, type UploadFileOptions } from "@/functions/zipline/files";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { avaibleTextMimetypes, dates, formats } from "@/constants/upload";
import { type ExternalPathString, Link, useRouter } from "expo-router";
import { ScrollView, Text, View, ToastAndroid } from "react-native";
import type { APIUploadResponse, Preset } from "@/types/zipline";
import { guessExtension, guessMimetype } from "@/functions/util";
import { useDetectKeyboardOpen } from "@/hooks/isKeyboardOpen";
import { getSettings } from "@/functions/zipline/settings";
import { getFolders } from "@/functions/zipline/folders";
import { useShareIntent } from "@/hooks/useShareIntent";
import * as DocumentPicker from "expo-document-picker";
import type { Mimetypes } from "@/types/mimetypes";
import { styles } from "@/styles/files/uploadText";
import * as FileSystem from "expo-file-system";
import TextInput from "@/components/TextInput";
import { useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import { useAuth } from "@/hooks/useAuth";
import Select from "@/components/Select";
import Switch from "@/components/Switch";
import Button from "@/components/Button";
import Popup from "@/components/Popup";

interface SelectedFile {
	name: string;
	uri: string;
	mimetype?: string;
	size?: number;
}

interface Props {
	showFileSelector?: boolean;
	defaultText?: string;
	fromShareIntent?: boolean;
}

export default function UploadText({
	showFileSelector = true,
	defaultText,
	fromShareIntent = false,
}: Props) {
	const router = useRouter();

	useAuth();
	const resetShareIntent = useShareIntent(fromShareIntent);

	const stringifiedPresets = db.get("uploadPresets") || "[]";

	const [presets, setPresets] = useState<Array<Preset>>(
		JSON.parse(stringifiedPresets),
	);

	const [uploadedFile, setUploadedFile] =
		useState<APIUploadResponse["files"][0]>();
	const [failedUpload, setFailedUpload] = useState<boolean>(false);
	const [uploadError, setUploadError] = useState<string>("");

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
	const [fileType, setFileType] = useState<string>("text/x-zipline-plain");
	const [fileExtension, setFileExtension] = useState<string>("txt");

	const [folders, setFolders] = useState<
		Array<{
			label: string;
			value: string;
		}>
	>([]);

	const [defaultFormat, setDefaultFormat] = useState<string>("random");

	const [uploadButtonDisabled, setUploadButtonDisabled] =
		useState<boolean>(true);
	const [uploading, setUploading] = useState<boolean>(false);

	const [text, setText] = useState<string>(defaultText || "");

	useEffect(() => {
		setUploadButtonDisabled(text.length === 0);
	}, [text]);

	const isKeyboardOpen = useDetectKeyboardOpen(false);

	const [savePreset, setSavePreset] = useState<boolean>(false);
	const [newPresetName, setNewPresetName] = useState<string>("");
	const [newPresetError, setNewPresetError] = useState<string | null>(null);

	const [editPresetName, setEditPresetName] = useState<string>("");
	const [editPresetError, setEditPresetError] = useState<string | null>(null);
	const [presetToEdit, setPresetToEdit] = useState<string | null>(null);

	useEffect(() => {
		(async () => {
			const folders = await getFolders(true);
			const settings = await getSettings();

			if (typeof settings !== "string") {
				setDefaultFormat(settings.filesDefaultFormat);
			}

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
		setUploadButtonDisabled(true);

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

	return (
		<View style={styles.mainContainer}>
			<Popup
				onClose={() => {
					setUploadedFile(undefined);
					setFailedUpload(false);
				}}
				hidden={!(uploadedFile || failedUpload)}
			>
				{uploadedFile && (
					<View>
						<Text style={styles.headerText}>Uploaded Files</Text>
						<ScrollView style={styles.popupScrollView}>
							<View key={uploadedFile.id} style={styles.uploadedFileContainer}>
								<Link
									href={uploadedFile.url as ExternalPathString}
									style={styles.uploadedFileUrl}
								>
									{uploadedFile.url}
								</Link>

								<Button
									icon="content-copy"
									color="#323ea8"
									onPress={async () => {
										const saved = await Clipboard.setStringAsync(
											uploadedFile.url,
										);

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
										router.replace(uploadedFile.url as ExternalPathString);
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
						</ScrollView>
					</View>
				)}

				{failedUpload && (
					<View>
						<Text style={styles.headerText}>Failed Upload</Text>

						<ScrollView style={styles.popupScrollView}>
							<Text style={styles.failedFileText}>
								<Text style={{ fontWeight: "bold" }}>
									The text failed to upload
								</Text>
								{"\n"}
								<Text style={{ color: "red" }}>{uploadError}</Text>
							</Text>
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

							const newPresets: Array<Preset> = [
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

							const newPresets: Array<Preset> = presets;

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

			<View>
				<View style={styles.header}>
					<Text style={styles.headerText}>Upload Text</Text>

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

				<View style={styles.mainTextInputContainer}>
					<TextInput
						multiline
						disableContext={uploading}
						disabled={uploading}
						inputStyle={{
							...styles.mainTextInput,
							...(isKeyboardOpen && { height: 100 }),
						}}
						value={text}
						onValueChange={(text) => setText(text)}
					/>
				</View>
			</View>

			{showFileSelector && (
				<View>
					<Button
						width="90%"
						disabled={uploading}
						onPress={async () => {
							const output = await DocumentPicker.getDocumentAsync({
								type: [
									"text/*",
									"application/json",
									"application/javascript",
									"application/typescript",
									"application/xml",
									"application/xhtml+xml",
									"application/sql",
									"application/graphql",
									"application/ld+json",
									"application/vnd.api+json",
									"application/x-yaml",
									"application/yaml",
									"video/vnd.dlna.mpeg-tts",
								],
								copyToCacheDirectory: true,
							});

							if (output.canceled || !output.assets) return;

							const newSelectedFiles: SelectedFile = output.assets[0];

							const fileURI = newSelectedFiles.uri;

							const extension = fileURI.split(".").pop() || "txt";
							const mimetype =
								(avaibleTextMimetypes.find(
									(format) => format.value === extension,
								)?.mimetype as string) ||
								guessMimetype(extension as keyof Mimetypes);

							const base64 = await FileSystem.readAsStringAsync(fileURI, {
								encoding: FileSystem.EncodingType.Base64,
							});

							const decoded = atob(base64);

							setText(decoded);
							setFileType(mimetype);
							setFileExtension(extension);
						}}
						text="Select File"
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
					File Type:
				</Text>
				<Select
					data={avaibleTextMimetypes}
					placeholder="Select File Type..."
					disabled={uploading}
					defaultValue={avaibleTextMimetypes.find(
						(format) => format.value === "txt",
					)}
					onSelect={(selectedType) => {
						if (selectedType.length <= 0) return;

						setFileType(selectedType[0].mimetype as string);
						setFileExtension(selectedType[0].value);
					}}
				/>

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
					disableContext={!uploading}
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
					disabled={uploading}
					disableContext={!uploading}
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
					disabled={uploading}
					disableContext={!uploading}
					value={overrideDomain || ""}
					placeholder="example.com"
				/>

				<TextInput
					title="Override File Name:"
					disabled={uploading}
					disableContext={!uploading}
					onValueChange={(content) => setFileName(content)}
					value={fileName || ""}
					placeholder="example.png"
				/>

				<TextInput
					title="Password:"
					onValueChange={(content) => setPassword(content)}
					disabled={uploading}
					disableContext={!uploading}
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

							console.log(presetToUse);

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

						const extension = fileExtension || "txt";

						const mimetype =
							fileType ||
							guessExtension(extension as Mimetypes[keyof Mimetypes]) ||
							"text/plain";

						const fileURI = `${FileSystem.cacheDirectory}/uploadText.${extension}`;

						await FileSystem.writeAsStringAsync(fileURI, text, {
							encoding: FileSystem.EncodingType.UTF8,
						});

						const fileData = {
							uri: fileURI,
							mimetype,
						};

						const uploadedFiles = await uploadFiles(fileData, {
							compression,
							expiresAt: deletesAt,
							filename: fileName,
							folder,
							format: nameFormat,
							maxViews,
							originalName,
							overrideDomain,
							password,
						});

						if (typeof uploadedFiles === "string") {
							setUploadError(uploadedFiles);
							setUploading(false);

							return setFailedUpload(true);
						}

						const upload = uploadedFiles[0];

						setUploadedFile(upload);
						setFailedUpload(false);

						afterUploadCleanup();
					}}
					text={uploading ? "Uploading..." : "Upload File"}
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
