import { uploadFiles, type UploadFileOptions } from "@/functions/zipline/files";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { type ExternalPathString, Link, useRouter } from "expo-router";
import { ScrollView, Text, View, ToastAndroid } from "react-native";
import type { APIUploadResponse, Preset } from "@/types/zipline";
import { guessExtension, guessMimetype } from "@/functions/util";
import Select, { type SelectProps } from "@/components/Select";
import { useDetectKeyboardOpen } from "@/hooks/isKeyboardOpen";
import { getFolders } from "@/functions/zipline/folders";
import { useShareIntent } from "@/hooks/useShareIntent";
import * as DocumentPicker from "expo-document-picker";
import type { Mimetypes } from "@/types/mimetypes";
import { dates, formats } from "@/constants/upload";
import { styles } from "@/styles/files/uploadText";
import * as FileSystem from "expo-file-system";
import TextInput from "@/components/TextInput";
import { useContext, useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import { useAuth } from "@/hooks/useAuth";
import Switch from "@/components/Switch";
import Button from "@/components/Button";
import Popup from "@/components/Popup";
import { ZiplineContext } from "@/contexts/ZiplineProvider";
import { Directory, File, Paths } from "expo-file-system/next";

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
	useAuth();
	
	const router = useRouter();
	const resetShareIntent = useShareIntent(fromShareIntent);
	const { webSettings, publicSettings } = useContext(ZiplineContext)

	const [mimetypes, setMimetypes] = useState<SelectProps["data"]>([])

	const stringifiedPresets = db.get("uploadPresets") || "[]";

	const [presets, setPresets] = useState<Preset[]>(
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
		{
			label: string;
			value: string;
		}[]
	>([]);

	const chunkSize = webSettings
		? webSettings.config.chunks.size
		: "25mb"

	const maxChunkSize = webSettings
		? webSettings.config.chunks.max
		: "95mb"

	const chunksEnabled = webSettings
		? webSettings.config.chunks.enabled
		: false

	const defaultFormat = webSettings
		? webSettings.config.files.defaultFormat
		: "random"

	const domainList = publicSettings
	? publicSettings.domains ?? []
	: []

const domains = domainList
	.map((domain) => ({
		label: domain,
		value: domain
	}))

	const [isReading, setIsReading] = useState<boolean>(false);

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
		const mimetypeData = webSettings
			? webSettings.codeMap
			: []

		setMimetypes(mimetypeData.map((mimetype) => ({
			label: mimetype.name,
			value: mimetype.ext,
			mimetype: mimetype.mime
		})))
	}, [webSettings])

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
		setUploadButtonDisabled(true);
		setText("")

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
						<Text style={styles.headerText}>Uploaded File</Text>
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

				{isReading && <Text style={styles.readText}>Reading your file... This may take a few moments</Text>}

				<View style={styles.mainTextInputContainer}>
					<TextInput
						multiline
						disableContext={uploading || isReading}
						disabled={uploading || isReading}
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
						disabled={uploading || isReading}
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
								copyToCacheDirectory: false,
							});

							if (output.canceled || !output.assets) return;

							const newSelectedFiles: SelectedFile = output.assets[0];

							let fileURI = newSelectedFiles.uri;
							const cacheDir = new Directory(Paths.cache)

							setIsReading(true)

							const extension = fileURI.split(".").pop() || "txt";
							const mimetype =
								(mimetypes.find(
									(format) => format.value === extension,
								)?.mimetype as string) ||
								guessMimetype(extension as keyof Mimetypes);

							const outputURI = `${cacheDir.uri}/upload.${extension}`

							const outputFile = await FileSystem.getInfoAsync(outputURI)
							
							if (outputFile.exists) await FileSystem.deleteAsync(outputURI)
							
							await FileSystem.copyAsync({
								from: fileURI,
								to: outputURI,
							})

							fileURI = outputURI;

							const base64 = await FileSystem.readAsStringAsync(fileURI, {
								encoding: FileSystem.EncodingType.Base64,
							});

							const decoded = atob(base64);

							setText(decoded);
							setFileType(mimetype);
							setFileExtension(extension);
							setIsReading(false)
						}}
						text="Select File"
						color={(uploading || isReading) ? "#373d79" : "#323ea8"}
						textColor={(uploading || isReading) ? "gray" : "white"}
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
						...((uploading || isReading) && styles.inputHeaderDisabled),
					}}
				>
					File Type:
				</Text>
				<Select
					data={mimetypes}
					placeholder="Select File Type..."
					disabled={uploading || isReading}
					defaultValue={mimetypes.find(
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
						...((uploading || isReading) && styles.inputHeaderDisabled),
					}}
				>
					Deletes At:
				</Text>
				<Select
					data={dates}
					placeholder="Select Date..."
					disabled={uploading || isReading}
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
						...((uploading || isReading) && styles.inputHeaderDisabled),
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
					disabled={uploading || isReading}
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
					disableContext={uploading || isReading}
					disabled={uploading || isReading}
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
					disabled={uploading || isReading}
					disableContext={uploading || isReading}
					value={maxViews ? String(maxViews) : ""}
					placeholder="0"
				/>

				<Text
					style={{
						...styles.inputHeader,
						...((uploading || isReading) && styles.inputHeaderDisabled),
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
					disabled={uploading || isReading}
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
						...((uploading || isReading) && styles.inputHeaderDisabled),
					}}
				>
					Override Domain:
				</Text>
				<Select
					data={[
						{
							label: "Default Domain",
							value: "defaultDomain",
						},
						...domains,
					]}
					defaultValue={domains.find((domain) => domain.value === overrideDomain)}
					disabled={uploading || isReading}
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
					disabled={uploading || isReading}
					disableContext={uploading || isReading}
					onValueChange={(content) => setFileName(content)}
					value={fileName || ""}
					placeholder="example.png"
				/>

				<TextInput
					title="Password:"
					onValueChange={(content) => setPassword(content)}
					disabled={uploading || isReading}
					disableContext={uploading || isReading}
					password
					value={password || ""}
					placeholder="myPassword"
				/>

				<Switch
					title="Add Original Name"
					value={originalName || false}
					disabled={uploading || isReading}
					onValueChange={() => setOriginalName((prev) => !prev)}
				/>

				<Text
					style={{
						...styles.inputHeader,
						...((uploading || isReading) && styles.inputHeaderDisabled),
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
					disabled={uploading || isReading || uploadButtonDisabled}
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
							blob: new File(fileURI).blob(),
							mimetype,
						};

						const uploadedFiles = await uploadFiles(
							fileData, 
							{
								chunksEnabled,
								chunkSize,
								maxChunkSize
							}, {
								compression,
								expiresAt: deletesAt,
								filename: fileName,
								folder,
								format: nameFormat,
								maxViews,
								originalName,
								overrideDomain,
								password,
							}
						);

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
					color={uploading || isReading || uploadButtonDisabled ? "#373d79" : "#323ea8"}
					textColor={uploading || isReading || uploadButtonDisabled ? "gray" : "white"}
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
