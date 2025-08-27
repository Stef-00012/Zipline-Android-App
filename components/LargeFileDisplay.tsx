import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { type ExternalPathString, useRouter } from "expo-router";
import { styles } from "@/styles/components/largeFileDisplay";
import FileDisplay from "@/components/FileDisplay";
import { getTags } from "@/functions/zipline/tags";
import { convertToBytes } from "@/functions/util";
import { isLightColor } from "@/functions/color";
import { Portal } from "react-native-portalize";
import * as FileSystem from "expo-file-system";
import TextInput from "@/components/TextInput";
import { useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import Select from "@/components/Select";
import Button from "@/components/Button";
import Popup from "@/components/Popup";
import axios from "axios";
import {
	removeFileFromFolder,
	addFileToFolder,
	getFolders,
} from "@/functions/zipline/folders";
import {
	type EditFileOptions,
	deleteFile,
	editFile,
} from "@/functions/zipline/files";
import type {
	APIFoldersNoIncl,
	APIFile,
	APITags,
	DashURL,
} from "@/types/zipline";
import {
	type ColorValue,
	ToastAndroid,
	Pressable,
	Text,
	View,
	BackHandler,
} from "react-native";
import MaterialSymbols from "./MaterialSymbols";
import { Directory, Paths } from "expo-file-system/next";
import { startActivityAsync } from "expo-intent-launcher";

interface Props {
	file: APIFile;
	hidden: boolean;
	onClose: (refresh?: boolean) => void | Promise<void>;
}

export default function LargeFileDisplay({ file, hidden, onClose }: Props) {
	const router = useRouter();

	const dashUrl = db.get("url") as DashURL | null;

	const [tags, setTags] = useState<APITags>([]);
	const [folders, setFolders] = useState<APIFoldersNoIncl>([]);

	const [fileContent, setFileContent] = useState<string | null>(null);

	const [filePassword, setFilePassword] = useState<boolean>(file.password);
	const [fileMaxViews, setFileMaxViews] = useState<number | null>(
		file.maxViews,
	);
	const [fileOriginalName, setFileOriginalName] = useState<string | null>(
		file.originalName,
	);
	const [fileType, setFileType] = useState<string>(file.type);
	const [fileFolderId, setFileFolderId] = useState<string | null>(
		file.folderId,
	);
	const [fileFavorite, setFileFavorite] = useState<boolean>(file.favorite);

	const [tempHidden, setTempHidden] = useState<boolean>(false);

	const [deleteFilePopup, setDeleteFilePopup] = useState<boolean>(false);
	const [editFilePopup, setEditFilePopup] = useState<boolean>(false);

	const [editFileMaxViews, setEditFileMaxViews] = useState<number | null>(
		file.maxViews,
	);
	const [editFileOriginalName, setEditFileOriginalName] = useState<
		string | null
	>(file.originalName);
	const [editFileType, setEditFileType] = useState<string>(file.type);
	const [editFilePassword, setEditFilePassword] = useState<string | null>(null);

	const [showApkPopup, setShowApkPopup] = useState<boolean>(false);
	const [apkDownloadPercentage, setApkDownloadPercentage] =
		useState<string>("0");

	// biome-ignore lint/correctness/useExhaustiveDependencies: Functions should not be parameters of the effect
	useEffect(() => {
		if (!hidden) {
			const { remove } = BackHandler.addEventListener(
				"hardwareBackPress",
				() => {
					onClose();
					remove();

					return true;
				},
			);
		}
	}, [hidden]);

	useEffect(() => {
		(async () => {
			const tags = await getTags();
			const folders = await getFolders(true);

			setTags(typeof tags === "string" ? [] : tags);
			setFolders(typeof folders === "string" ? [] : folders);
		})();
	}, []);

	useEffect(() => {
		if (fileType.startsWith("text/")) {
			(async () => {
				const res = await axios.get(`${dashUrl}/raw/${file.name}`, {
					responseType: "text",
				});

				setFileContent(res.data as string);
			})();
		}

		setDeleteFilePopup(false);
		setTempHidden(false);
	}, [file, dashUrl, fileType]);

	return (
		<>
			<Popup
				hidden={!deleteFilePopup}
				onClose={() => {
					setDeleteFilePopup(false);
					setTempHidden(false);
				}}
			>
				<View style={styles.popupContent}>
					<Text style={styles.mainHeaderText}>Are you sure?</Text>

					<Text style={styles.serverActionWarningText}>
						Are you sure you want to delete {file.name}? This action cannot be
						undone.
					</Text>

					<View style={styles.fileDeleteButtonsContainer}>
						<Button
							color="#181c28"
							text="Cancel"
							onPress={() => {
								setDeleteFilePopup(false);
								setTempHidden(false);
							}}
							margin={{
								top: 10,
								right: 10,
							}}
						/>

						<Button
							color="#CF4238"
							text={`Delete ${file.name}`}
							onPress={async () => {
								const fileId = file.id;

								const success = await deleteFile(fileId);

								if (typeof success === "string") {
									ToastAndroid.show(`Error: ${success}`, ToastAndroid.SHORT);

									setDeleteFilePopup(false);
									setTempHidden(false);

									return;
								}

								setDeleteFilePopup(false);
								setTempHidden(false);
								onClose(true);

								ToastAndroid.show(
									`Successfully deleted the file ${file.name}`,
									ToastAndroid.SHORT,
								);
							}}
							margin={{
								top: 10,
								right: 10,
							}}
						/>
					</View>
				</View>

				<Text style={styles.popupSubHeaderText}>
					Press outside to close this popup
				</Text>
			</Popup>

			<Popup
				hidden={!editFilePopup}
				onClose={() => {
					setEditFilePopup(false);
					setTempHidden(false);
				}}
			>
				<View style={styles.popupContent}>
					<Text style={styles.mainHeaderText}>Editing "{file.name}"</Text>

					<TextInput
						title="Max Views:"
						onValueChange={(content) => {
							setEditFileMaxViews(Math.abs(Number.parseInt(content)));
						}}
						value={editFileMaxViews ? String(editFileMaxViews) : ""}
						keyboardType="numeric"
						placeholder="Unlimited"
					/>

					<TextInput
						title="Original Name:"
						onValueChange={(content) => {
							setEditFileOriginalName(content);
						}}
						value={editFileOriginalName || ""}
					/>

					<TextInput
						title="Type:"
						onValueChange={(content) => {
							setEditFileType(content);
						}}
						value={editFileType || ""}
					/>

					{filePassword ? (
						<Button
							color="#CF4238"
							text="Remove Password"
							onPress={() => {
								const fileId = file.id;

								const success = editFile(fileId, {
									password: null,
								});

								if (typeof success === "string")
									return ToastAndroid.show(
										`Error: ${success}`,
										ToastAndroid.SHORT,
									);

								setEditFilePassword(null);

								setFilePassword(false);
								file.password = false;

								ToastAndroid.show(
									"Successfully removed the password",
									ToastAndroid.SHORT,
								);
							}}
							margin={{
								top: 10,
							}}
						/>
					) : (
						<TextInput
							title="Password:"
							password
							onValueChange={(content) => {
								setEditFilePassword(content);
							}}
							value={editFilePassword || ""}
						/>
					)}

					<Button
						color="#323ea8"
						text="Save Changes"
						icon="save"
						onPress={async () => {
							const fileId = file.id;

							const editData: EditFileOptions = {};

							editData.maxViews = editFileMaxViews || null;
							editData.type = editFileType;
							if (editFileOriginalName)
								editData.originalName = editFileOriginalName;
							if (editFilePassword) editData.password = editFilePassword;

							const success = await editFile(fileId, editData);

							if (typeof success === "string")
								return ToastAndroid.show(
									`Error: ${success}`,
									ToastAndroid.SHORT,
								);

							if (editFilePassword) {
								setFilePassword(true);
								file.password = true;
							}

							file.originalName = editFileOriginalName || null;
							setFileOriginalName(editFileOriginalName || null);

							file.type = editFileType;
							setFileType(editFileType);

							file.maxViews = editFileMaxViews || null;
							setFileMaxViews(editFileMaxViews);

							setEditFilePopup(false);
							setTempHidden(false);

							ToastAndroid.show(
								`Successfully edited the file ${file.name}`,
								ToastAndroid.SHORT,
							);
						}}
						margin={{
							top: 10,
						}}
					/>
				</View>
			</Popup>

			<Popup
				hidden={!showApkPopup}
				onClose={() => {
					setShowApkPopup(false);
					setTempHidden(false);
				}}
			>
				<View style={styles.popupContent}>
					<Text style={styles.mainHeaderText}>Downloading APK...</Text>

					<Text style={styles.popupText}>
						Downloading{" "}
						<Text
							style={{
								fontWeight: "bold",
							}}
						>
							{file.name}
						</Text>
						... {apkDownloadPercentage}%
					</Text>
				</View>

				<Text style={styles.popupSubHeaderText}>
					Press outside to close this popup
				</Text>
			</Popup>

			<Portal>
				<Pressable
					style={{
						...styles.popupContainerOverlay,
						...((hidden || tempHidden || !file) && { display: "none" }),
					}}
					onPress={(e) => {
						if (e.target === e.currentTarget) onClose();
					}}
				>
					<View style={styles.popupContainer}>
						<Text style={styles.fileHeader}>{file.name}</Text>

						<KeyboardAwareScrollView showsVerticalScrollIndicator={false}>
							{fileContent ? (
								<TextInput
									multiline
									showDisabledStyle={false}
									disabled
									inputStyle={styles.textDisplay}
									value={fileContent}
								/>
							) : (
								<FileDisplay
									passwordProtected={!!filePassword}
									uri={`${dashUrl}/raw/${file.name}`}
									originalName={fileOriginalName}
									mimetype={fileType}
									name={file.name}
									maxHeight={500}
									width={350}
									file={file}
									autoHeight
								/>
							)}

							<View style={styles.fileInfoContainer}>
								<MaterialSymbols name="description" size={28} color="white" />
								<View style={styles.fileInfoTextContainer}>
									<Text style={styles.fileInfoHeader}>Type</Text>
									<Text style={styles.fileInfoText}>{file.type}</Text>
								</View>
							</View>

							<View style={styles.fileInfoContainer}>
								<MaterialSymbols name="sd_storage" size={28} color="white" />
								<View style={styles.fileInfoTextContainer}>
									<Text style={styles.fileInfoHeader}>Size</Text>
									<Text style={styles.fileInfoText}>
										{convertToBytes(file.size, {
											unitSeparator: " ",
										})}
									</Text>
								</View>
							</View>

							<View style={styles.fileInfoContainer}>
								<MaterialSymbols name="visibility" size={28} color="white" />
								<View style={styles.fileInfoTextContainer}>
									<Text style={styles.fileInfoHeader}>View</Text>
									<Text style={styles.fileInfoText}>
										{file.views}
										{fileMaxViews &&
											!Number.isNaN(fileMaxViews) &&
											`/${fileMaxViews}`}
									</Text>
								</View>
							</View>

							<View style={styles.fileInfoContainer}>
								<MaterialSymbols name="file_upload" size={28} color="white" />
								<View style={styles.fileInfoTextContainer}>
									<Text style={styles.fileInfoHeader}>Created At</Text>
									<Text style={styles.fileInfoText}>
										{new Date(file.createdAt).toLocaleString()}
									</Text>
								</View>
							</View>

							<View style={styles.fileInfoContainer}>
								<MaterialSymbols name="autorenew" size={28} color="white" />
								<View style={styles.fileInfoTextContainer}>
									<Text style={styles.fileInfoHeader}>Updated At</Text>
									<Text style={styles.fileInfoText}>
										{new Date(file.updatedAt).toLocaleString()}
									</Text>
								</View>
							</View>

							{file.deletesAt && (
								<View style={styles.fileInfoContainer}>
									<MaterialSymbols name="auto_delete" size={28} color="white" />
									<View style={styles.fileInfoTextContainer}>
										<Text style={styles.fileInfoHeader}>Deletes At</Text>
										<Text style={styles.fileInfoText}>
											{new Date(file.deletesAt).toLocaleString()}
										</Text>
									</View>
								</View>
							)}

							{fileOriginalName && (
								<View style={styles.fileInfoContainer}>
									<MaterialSymbols name="title" size={28} color="white" />
									<View style={styles.fileInfoTextContainer}>
										<Text style={styles.fileInfoHeader}>Original Name</Text>
										<Text style={styles.fileInfoText}>{fileOriginalName}</Text>
									</View>
								</View>
							)}

							<Text style={styles.fileInfoHeader}>Tags</Text>
							<Select
								placeholder="Select Tags..."
								multiple
								disabled={tags.length <= 0}
								data={tags.map((tag) => ({
									label: tag.name,
									value: tag.id,
									color: tag.color,
								}))}
								onSelect={async (selectedTags) => {
									const newTags = selectedTags.map((tag) => tag.value);

									const success = editFile(file.id, {
										tags: newTags,
									});

									if (typeof success === "string")
										return ToastAndroid.show(
											`Error: ${success}`,
											ToastAndroid.SHORT,
										);

									file.tags = tags.filter((tag) => newTags.includes(tag.id));

									ToastAndroid.show(
										"Successfully updated the tags",
										ToastAndroid.SHORT,
									);
								}}
								renderItem={(item) => (
									<View style={styles.selectRenderItemContainer}>
										<Text
											style={{
												...styles.selectRenderItemText,
												color: isLightColor(item.color as string)
													? "black"
													: "white",
												backgroundColor: item.color as ColorValue,
											}}
										>
											{item.label}
										</Text>
									</View>
								)}
								defaultValues={tags
									.filter((tag) =>
										file.tags.find((fileTag) => fileTag.id === tag.id),
									)
									.map((tag) => ({
										label: tag.name,
										value: tag.id,
										color: tag.color,
									}))}
								renderSelectedItem={(item, key) => (
									<Text
										key={key}
										style={{
											...styles.selectRenderSelectedItemText,
											color: isLightColor(item.color as string)
												? "black"
												: "white",
											backgroundColor: item.color as ColorValue,
										}}
									>
										{item.label}
									</Text>
								)}
								maxHeight={500}
							/>

							<Text style={styles.fileInfoHeader}>Folder</Text>
							{fileFolderId ? (
								<Button
									color="#e03131"
									text={`Remove from folder "${folders.find((folder) => folder.id === file.folderId)?.name}"`}
									onPress={async () => {
										if (!fileFolderId) return;

										const folderId = fileFolderId;
										const fileId = file.id;

										const success = removeFileFromFolder(folderId, fileId);

										if (typeof success === "string")
											return ToastAndroid.show(
												`Error: ${success}`,
												ToastAndroid.SHORT,
											);

										setFileFolderId(null);
										file.folderId = null;

										ToastAndroid.show(
											"Successfully removed the file from the folder",
											ToastAndroid.SHORT,
										);
									}}
									margin={{
										top: 5,
									}}
								/>
							) : (
								<Select
									placeholder="Add to Folder..."
									data={folders.map((folder) => ({
										label: folder.name,
										value: folder.id,
									}))}
									defaultValue={
										file.folderId
											? {
													label: (
														folders.find(
															(folder) => folder.id === file.folderId,
														) as APIFoldersNoIncl[0]
													)?.name,
													value: file.folderId,
												}
											: undefined
									}
									onSelect={async (selectedFolder) => {
										if (selectedFolder.length <= 0) return;

										const folderId = selectedFolder[0].value;
										const fileId = file.id;

										const success = await addFileToFolder(folderId, fileId);

										if (typeof success === "string")
											return ToastAndroid.show(
												`Error: ${success}`,
												ToastAndroid.SHORT,
											);

										setFileFolderId(folderId);
										file.folderId = folderId;

										ToastAndroid.show(
											"Successfully added the file to the folder",
											ToastAndroid.SHORT,
										);
									}}
									maxHeight={400}
								/>
							)}

							<Text style={styles.subHeaderText}>{file.id}</Text>

							<View style={styles.actionButtonsContainer}>
								<Button
									icon="edit"
									color="#e8590c"
									onPress={() => {
										setEditFilePopup(true);
										setTempHidden(true);
									}}
									iconSize={20}
									width={30}
									height={30}
									padding={5}
									margin={{
										left: 5,
										right: 5,
									}}
								/>

								<Button
									icon="delete"
									color="#e03131"
									onPress={() => {
										setDeleteFilePopup(true);
										setTempHidden(true);
									}}
									iconSize={20}
									width={30}
									height={30}
									padding={5}
									margin={{
										left: 5,
										right: 5,
									}}
								/>

								<Button
									icon={fileFavorite ? "star_half" : "star_outline"}
									color={fileFavorite ? "#f08c00" : "#343a40"}
									onPress={async () => {
										const success = editFile(file.id, {
											favorite: !file.favorite,
										});

										if (typeof success === "string")
											return ToastAndroid.show(
												`Error: ${success}`,
												ToastAndroid.SHORT,
											);

										file.favorite = !fileFavorite;
										setFileFavorite((prev) => !prev);

										ToastAndroid.show(
											`Successfully ${fileFavorite ? "removed from" : "added to"} favorites`,
											ToastAndroid.SHORT,
										);
									}}
									iconSize={20}
									width={30}
									height={30}
									padding={5}
									margin={{
										left: 5,
										right: 5,
									}}
								/>

								<Button
									icon="open_in_new"
									color="#323ea8"
									onPress={() => {
										router.push(`${dashUrl}${file.url}` as ExternalPathString);
									}}
									iconSize={20}
									width={30}
									height={30}
									padding={5}
									margin={{
										left: 5,
										right: 5,
									}}
								/>

								<Button
									icon="content_copy"
									color="#343a40"
									onPress={async () => {
										const url = `${dashUrl}${file.url}`;

										const success = await Clipboard.setStringAsync(url);

										if (!success)
											return ToastAndroid.show(
												"Failed to copy the URL",
												ToastAndroid.SHORT,
											);

										ToastAndroid.show(
											"Copied URL to clipboard",
											ToastAndroid.SHORT,
										);
									}}
									iconSize={20}
									width={30}
									height={30}
									padding={5}
									margin={{
										left: 5,
										right: 5,
									}}
								/>

								<Button
									icon="file_download"
									color="#343a40"
									onPress={async () => {
										const downloadUrl = `${dashUrl}/raw/${file.name}?download=true`;

										let savedFileDownloadUri = db.get("fileDownloadPath");

										if (!savedFileDownloadUri) {
											const permissions =
												await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

											if (!permissions.granted)
												return ToastAndroid.show(
													"The permission to save the file was not granted",
													ToastAndroid.SHORT,
												);

											db.set("fileDownloadPath", permissions.directoryUri);
											savedFileDownloadUri = permissions.directoryUri;
										}

										ToastAndroid.show("Downloading...", ToastAndroid.SHORT);

										const saveUri =
											await FileSystem.StorageAccessFramework.createFileAsync(
												savedFileDownloadUri,
												file.name,
												file.type,
											);

										const downloadResult = await FileSystem.downloadAsync(
											downloadUrl,
											`${FileSystem.cacheDirectory}/${file.name}`,
										);

										if (!downloadResult.uri)
											return ToastAndroid.show(
												"Something went wrong while downloading the file",
												ToastAndroid.SHORT,
											);

										const base64File = await FileSystem.readAsStringAsync(
											downloadResult.uri,
											{
												encoding: FileSystem.EncodingType.Base64,
											},
										);

										await FileSystem.writeAsStringAsync(saveUri, base64File, {
											encoding: FileSystem.EncodingType.Base64,
										});

										ToastAndroid.show(
											"Successfully downloaded the file",
											ToastAndroid.SHORT,
										);
									}}
									iconSize={20}
									width={30}
									height={30}
									padding={5}
									margin={{
										left: 5,
										right: 5,
									}}
								/>

								<Button
									icon="apk_install"
									color="#343a40"
									iconColor={
										file.name.endsWith(".apk") && !file.password
											? "white"
											: "gray"
									}
									onPress={async () => {
										if (!file.name.endsWith(".apk"))
											return ToastAndroid.show(
												"The selected file is not an APK",
												ToastAndroid.SHORT,
											);

										if (file.password)
											return ToastAndroid.show(
												"Unable to install password protected APKs",
												ToastAndroid.SHORT,
											);

										setShowApkPopup(true);
										setTempHidden(true);

										ToastAndroid.show("Downloading...", ToastAndroid.SHORT);

										const downloadUrl = `${dashUrl}/raw/${file.name}?download=true`;

										const cacheDir = new Directory(Paths.cache);
										const saveURI = `${cacheDir.uri}/${file.name}`;

										const downloadResumable =
											FileSystem.createDownloadResumable(
												downloadUrl,
												saveURI,
												{},
												(downloadProgress) => {
													const percentage =
														(downloadProgress.totalBytesWritten /
															downloadProgress.totalBytesExpectedToWrite) *
														100;

													setApkDownloadPercentage(percentage.toFixed(2));
												},
											);

										const downloadResult =
											await downloadResumable.downloadAsync();

										if (!downloadResult?.uri)
											return ToastAndroid.show(
												"Something went wrong while downloading the file",
												ToastAndroid.SHORT,
											);

										const apkPathContent =
											await FileSystem.getContentUriAsync(saveURI);

										await startActivityAsync(
											"android.intent.action.INSTALL_PACKAGE",
											{
												data: apkPathContent,
												flags: 1,
											},
										);

										setShowApkPopup(false);
										setTempHidden(false);
									}}
									iconSize={20}
									width={30}
									height={30}
									padding={5}
									margin={{
										left: 5,
										right: 5,
									}}
									disabled={!file.name.endsWith(".apk") || file.password}
								/>
							</View>
						</KeyboardAwareScrollView>
					</View>
				</Pressable>
			</Portal>
		</>
	);
}
