import { uploadFiles, type UploadFileOptions } from "@/functions/zipline/files";
import {
	Pressable,
	ScrollView,
	Text,
	View,
	ToastAndroid,
} from "react-native";
import { guessExtension, guessMimetype } from "@/functions/util";
import { getSettings } from "@/functions/zipline/settings";
import type { APIUploadResponse } from "@/types/zipline";
import { getFolders } from "@/functions/zipline/folders";
import {
	type ExternalPathString,
	Link,
	useRouter,
} from "expo-router";
import * as DocumentPicker from "expo-document-picker";
import { avaibleTextMimetypes, dates, formats } from "@/constants/upload";
import * as FileSystem from "expo-file-system";
import { styles } from "@/styles/files/uploadText";
import { useEffect, useState } from "react";
import Select from "@/components/Select";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import type { Mimetypes } from "@/types/mimetypes";
import * as Clipboard from "expo-clipboard";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { useDetectKeyboardOpen } from "@/hooks/isKeyboardOpen";
import Popup from "@/components/Popup";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import TextInput from "@/components/TextInput";
import Switch from "@/components/Switch";

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
	fromShareIntent = false
}: Props) {
	const router = useRouter();

	useAuth()
	useShareIntent(fromShareIntent)

	const [uploadedFile, setUploadedFile] = useState<
		APIUploadResponse["files"][0]
	>();
	const [failedUpload, setFailedUpload] = useState<boolean>(false);

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
	}, [text])

	const isKeyboardOpen = useDetectKeyboardOpen(false);

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

		setFolder(undefined)
		setMaxViews(undefined)
		setFileName(undefined)
		setPassword(undefined)
		setOriginalName(false)
		setDeletesAt(undefined)
		setNameFormat(undefined)
		setCompression(undefined)
		setOverrideDomain(undefined)
	}

	return (
		<View style={styles.mainContainer}>
			<Popup onClose={() => {
				setUploadedFile(undefined);
				setFailedUpload(false);
			}} hidden={!(uploadedFile || failedUpload)}>
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

							<Pressable
								style={styles.uploadedFileButton}
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
							>
								<MaterialIcons
									name="content-copy"
									size={16}
									color="white"
								/>
							</Pressable>

							<Pressable
								style={styles.uploadedFileButton}
								onPress={() => {
									router.replace(uploadedFile.url as ExternalPathString);
								}}
							>
								<MaterialIcons
									name="open-in-new"
									size={16}
									color="white"
								/>
							</Pressable>
						</View>
						</ScrollView>
					</View>
				)}

				{failedUpload && (
					<View>
						<Text style={styles.headerText}>Failed Files</Text>

						<ScrollView style={styles.popupScrollView}>
							<Text style={styles.failedFileText}>The text failed to upload</Text>
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

			<View>
				<View style={styles.header}>
					<Text style={styles.headerText}>Upload Text</Text>

					<View style={styles.headerButtons}>
						<Pressable
							style={styles.headerButton}
							onPress={() => {
								router.replace("/files")
							}}
						>
							<MaterialIcons
								name="folder-open"
								size={30}
								color={styles.headerButton.color}
							/>
						</Pressable>
					</View>
				</View>

				<View style={styles.mainTextInputContainer}>
					<TextInput
						multiline
						disableContext={uploading}
						disabled={uploading}
						inputStyle={{
							...styles.mainTextInput,
							...(isKeyboardOpen && { height: 100 })
						}}
						value={text}
						onValueChange={(text) => setText(text)}
					/>
				</View>
			</View>

			{showFileSelector && (
				<View>
					<Pressable
						style={{
							...styles.button,
							...styles.selectFilesButton,
							...(uploading && styles.buttonDisabled),
						}}
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

							const fileURI = newSelectedFiles.uri

							const extension = fileURI.split(".").pop() || "txt"
							const mimetype = avaibleTextMimetypes.find(format => format.value === extension)?.mimetype as string || guessMimetype(extension as keyof Mimetypes)

							const base64 = await FileSystem.readAsStringAsync(fileURI, {
								encoding: FileSystem.EncodingType.Base64,
							})
							
							const decoded = atob(base64)

							setText(decoded)
							setFileType(mimetype)
							setFileExtension(extension)
						}}
					>
						<Text
							style={{
								...styles.buttonText,
								...(uploading && styles.buttonTextDisabled),
							}}
						>
							Select file
						</Text>
					</Pressable>
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
					defaultValue={avaibleTextMimetypes.find(format => format.value === "txt")}
					onSelect={(selectedType) => {
						setFileType(selectedType[0].mimetype as string)
						setFileExtension(selectedType[0].value)
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
						if (selectedDate[0].value === "never") return setDeletesAt(undefined);

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
					onSelect={(selectedFormat) =>
						setNameFormat(selectedFormat[0].value as UploadFileOptions["format"])
					}
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
					disabled={!uploading}
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
					disabled={!uploading}
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
					disabled={uploading}
					placeholder="Select Folder..."
					onSelect={(selectedFolder) =>
						setFolder(
							selectedFolder[0].value === "noFolder"
								? undefined
								: selectedFolder[0].value,
						)
					}
				/>

				<TextInput
					title="Override Domain:"
					onValueChange={(content) => setOverrideDomain(content)}
					keyboardType="url"
					disabled={!uploading}
					disableContext={!uploading}
					value={overrideDomain || ""}
					placeholder="example.com"
				/>

				<TextInput
					title="Override File Name:"
					disabled={!uploading}
					disableContext={!uploading}
					onValueChange={(content) => setFileName(content)}
					value={fileName || ""}
					placeholder="example.png"
				/>

				<TextInput
					title="Password:"
					onValueChange={(content) => setPassword(content)}
					disabled={!uploading}
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
			</KeyboardAwareScrollView>

			<View>
				<Pressable
					disabled={uploading || uploadButtonDisabled}
					style={{
						...styles.button,
						...styles.uploadButton,
						...(uploadButtonDisabled && styles.buttonDisabled),
					}}
					onPress={async () => {
						setUploading(true);

						const extension = fileExtension || "txt"

						const mimetype = fileType || guessExtension(extension as Mimetypes[keyof Mimetypes]) || "text/plain"

						const fileURI = `${FileSystem.cacheDirectory}/uploadText.${extension}`

						await FileSystem.writeAsStringAsync(fileURI, text, {
							encoding: FileSystem.EncodingType.UTF8,
						})

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
							return setFailedUpload(true)
						}

						const upload = uploadedFiles[0]

						setUploadedFile(upload);
						setFailedUpload(false);

						afterUploadCleanup();
					}}
				>
					<Text
						style={{
							...styles.buttonText,
							...((uploadButtonDisabled || uploading) &&
								styles.buttonTextDisabled),
						}}
					>
						{uploading ? "Uploading" : "Upload File(s)"}
					</Text>
				</Pressable>
			</View>
		</View>
	);
}
