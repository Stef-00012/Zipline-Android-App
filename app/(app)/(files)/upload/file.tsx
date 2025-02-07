import { uploadFiles, type UploadFileOptions } from "@/functions/zipline/files";
import {
	Pressable,
	ScrollView,
	Text,
	View,
	ToastAndroid,
} from "react-native";
import { guessExtension } from "@/functions/util";
import { getSettings } from "@/functions/zipline/settings";
import type { APIUploadResponse } from "@/types/zipline";
import { getFolders } from "@/functions/zipline/folders";
import {
	type ExternalPathString,
	Link,
	useRouter,
} from "expo-router";
import * as DocumentPicker from "expo-document-picker";
import { dates, formats } from "@/constants/upload";
import FileDisplay from "@/components/FileDisplay";
import * as FileSystem from "expo-file-system";
import { styles } from "@/styles/files/uploadFile";
import { useEffect, useState } from "react";
import Select from "@/components/Select";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import type { Mimetypes } from "@/types/mimetypes";
import * as Clipboard from "expo-clipboard";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { useDetectKeyboardOpen } from "@/hooks/isKeyboardOpen";
import Popup from "@/components/Popup";
import { useShareIntent } from "@/hooks/useShareIntent";
import { useAuth } from "@/hooks/useAuth";
import TextInput from "@/components/TextInput";
import Switch from "@/components/Switch";
import Button from "@/components/Button";

export interface SelectedFile {
	name: string;
	uri: string;
	mimetype?: string;
	size?: number;
}

interface Props {
	showFileSelector?: boolean;
	defaultFiles?: Array<SelectedFile>;
	fromShareIntent?: boolean;
}

export default function UploadFile({
	showFileSelector = true,
	defaultFiles,
	fromShareIntent = false
}: Props) {
	const router = useRouter();
	
	useAuth()
	useShareIntent(fromShareIntent)

	const [selectedFiles, setSelectedFiles] = useState<Array<SelectedFile>>(defaultFiles || []);
	const [uploadedFiles, setUploadedFiles] = useState<
		APIUploadResponse["files"]
	>([]);
	const [failedUploads, setFailedUploads] = useState<
		Array<{
			uri: string;
			name: string;
		}>
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
		Array<{
			label: string;
			value: string;
		}>
	>([]);

	const [defaultFormat, setDefaultFormat] = useState<string>("random");

	const [uploadButtonDisabled, setUploadButtonDisabled] =
		useState<boolean>(true);
	const [fileNameEnabled, setFileNameEnabled] = useState<boolean>(defaultFiles ? defaultFiles.length <= 1 : true);
	const [uploading, setUploading] = useState<boolean>(false);

	useEffect(() => {
		(async () => {
			const folders = await getFolders(true);
			const settings = await getSettings();

			if ( typeof settings !== "string") {
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
		setSelectedFiles([]);

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

	useEffect(() => {
		setUploadButtonDisabled(selectedFiles.length === 0);
		setFileNameEnabled(selectedFiles.length <= 1);
	}, [selectedFiles])

	const iskeyboardOpen = useDetectKeyboardOpen(false)

	return (
		<View style={styles.mainContainer}>
			<Popup onClose={() => {
				setUploadedFiles([]);
				setFailedUploads([]);
			}} hidden={!(uploadedFiles.length > 0 || failedUploads.length > 0)}>
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
											const saved = await Clipboard.setStringAsync(
												file.url,
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
										    right: 5
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
										    right: 5
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
									{file.name}
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

			<View>
				<View style={styles.header}>
					<View>
						<Text style={styles.headerText}>Selected Files</Text>
						<Text style={styles.subHeaderText}>Click a file to remove it</Text>
					</View>

					<View style={styles.headerButtons}>
					    <Button 
							onPress={() => {
								router.replace("/files")
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

				<ScrollView horizontal style={{
					...styles.scrollView,
					...(iskeyboardOpen && styles.scrollViewKeyboardOpen)
				}}>
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
				    <Button
				        width="90%"
				        disabled={uploading}
				        onPress={async () => {
							const output = await DocumentPicker.getDocumentAsync({
								type: "*/*",
								multiple: true,
								copyToCacheDirectory: true,
							});

							if (output.canceled || !output.assets) return;

							const newSelectedFiles: Array<SelectedFile> = output.assets
								.map((file) => ({
									name: file.name,
									uri: file.uri,
									mimetype: file.mimeType,
									size: file.size,
								}))
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
						    top: 10
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
			</KeyboardAwareScrollView>

			<View>
			    <Button
			        width="90%"
			        disabled={uploading || uploadButtonDisabled}
			        onPress={async () => {
						setUploading(true);

						const successful = [];
						const fails = [];

						for (const file of selectedFiles) {
							const fileInfo = await FileSystem.getInfoAsync(file.uri, {
								size: true,
							});

							if (!fileInfo.exists || fileInfo.isDirectory) {
								fails.push({
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
								mimetype,
							};

							const uploadedFile = await uploadFiles(fileData, {
								compression,
								expiresAt: deletesAt,
								filename: fileNameEnabled ? fileName : undefined,
								folder,
								format: nameFormat,
								maxViews,
								originalName,
								overrideDomain,
								password,
							});

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
					text={uploading ? "Uploading..." : "Upload File(s)"}
					color={(uploading || uploadButtonDisabled) ? "#373d79" : "#323ea8"}
					textColor={(uploading || uploadButtonDisabled) ? "gray" : "white"}
					margin={{
					    left: "auto",
					    right: "auto",
					    top: 10
					}}
			    />
			</View>
		</View>
	);
}
