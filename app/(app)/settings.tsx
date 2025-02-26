import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import type { APIExports, APISelfUser, DashURL } from "@/types/zipline";
import { View, Text, Pressable, ToastAndroid } from "react-native";
import { convertToBytes, getFileDataURI } from "@/functions/util";
import { getTokenWithToken } from "@/functions/zipline/auth";
import { useShareIntent } from "@/hooks/useShareIntent";
import { version as appVersion } from "@/package.json";
import * as DocumentPicker from "expo-document-picker";
import { useAppUpdates } from "@/hooks/useUpdates";
import { alignments } from "@/constants/settings";
import UserAvatar from "@/components/UserAvatar";
import TextInput from "@/components/TextInput";
import * as FileSystem from "expo-file-system";
import { useState, useEffect } from "react";
import * as Clipboard from "expo-clipboard";
import { styles } from "@/styles/settings";
import * as db from "@/functions/database";
import { useAuth } from "@/hooks/useAuth";
import Switch from "@/components/Switch";
import Button from "@/components/Button";
import Select from "@/components/Select";
import { useRouter } from "expo-router";
import Popup from "@/components/Popup";
import Table from "@/components/Table";
import {
	clearTempFiles,
	clearZeroByteFiles,
	generateThumbnails,
	getZeroByteFiles,
	requeryFileSize,
} from "@/functions/zipline/serverActions";
import {
	createUserExport,
	deleteUserExport,
	getUserExports,
} from "@/functions/zipline/exports";
import {
	editCurrentUser,
	type EditUserOptions,
	getCurrentUser,
	getCurrentUserAvatar,
} from "@/functions/zipline/user";
import ColorPicker from "@/components/ColorPicker";
import { getVersion } from "@/functions/zipline/version";

export default function UserSettings() {
	const router = useRouter();

	const {
		checkForUpdates,
		downloadUpdate,
		// downloadProgress,
		// downloadSize,
		isChecking,
		isDownloading,
		isUpdateAvailable,
	} = useAppUpdates();

	useAuth();
	useShareIntent();

	const [updateAlertDisabled, setUpdateAlertDisabled] = useState<boolean>(
		db.get("disableUpdateAlert") === "true",
	);

	const [user, setUser] = useState<APISelfUser | null>(null);
	const [token, setToken] = useState<string | null>(null);
	const [currentAvatar, setCurrentAvatar] = useState<string | undefined>(
		undefined,
	);
	const [exports, setExports] = useState<APIExports | null>([]);
	const [zeroByteFiles, setZeroByteFiles] = useState<number>(0);

	const [tokenVisible, setTokenVisible] = useState<boolean>(false);

	const [currentUsername, setCurrentUsername] = useState<string | undefined>(
		undefined,
	);
	const [username, setUsername] = useState<string | undefined>(undefined);
	const [password, setPassword] = useState<string | undefined>(undefined);
	const [avatar, setAvatar] = useState<string | undefined>(undefined);
	const [avatarName, setAvatarName] = useState<string | null>(null);

	const [viewEnabled, setViewEnabled] = useState<boolean>(false);
	const [viewAlign, setViewAlign] = useState<"left" | "center" | "right">(
		"left",
	);
	const [viewShowMimetype, setViewShowMimetype] = useState<boolean>(false);
	const [viewContent, setViewContent] = useState<string | undefined>(undefined);
	const [viewEmbed, setViewEmbed] = useState<boolean>(false);
	const [viewEmbedTitle, setViewEmbedTitle] = useState<string | undefined>(
		undefined,
	);
	const [viewEmbedDescription, setViewEmbedDescription] = useState<
		string | undefined
	>(undefined);
	const [viewEmbedColor, setViewEmbedColor] = useState<string | undefined>(
		undefined,
	);
	const [viewEmbedSiteName, setViewEmbedSiteName] = useState<
		string | undefined
	>(undefined);

	const [clearZeroByteFilesPopupOpen, setClearZeroByteFilesPopupOpen] =
		useState<boolean>(false);
	const [clearTempFilesPopupOpen, setClearTempFilesPopupOpen] =
		useState<boolean>(false);
	const [requerySizeOfFilesPopupOpen, setRequerySizeOfFilesPopupOpen] =
		useState<boolean>(false);
	const [generateThumbnailsPopupOpen, setGenerateThumbnailsPopupOpen] =
		useState<boolean>(false);

	const [requerySizeForceUpdate, setRequerySizeForceUpdate] =
		useState<boolean>(false);
	const [requerySizeForceDelete, setRequerySizeForceDelete] =
		useState<boolean>(false);

	const [generateThumbnailsRerun, setGenerateThumbnailsRerun] =
		useState<boolean>(false);

	const [ziplineVersion, setZiplineVersion] = useState<string | null>(null)

	const url = db.get("url") as DashURL;

	useEffect(() => {
		(async () => {
			const user = await getCurrentUser();
			const token = await getTokenWithToken();
			const avatar = await getCurrentUserAvatar();
			const exports = await getUserExports();
			const zeroByteFiles = await getZeroByteFiles();
			const versionData = await getVersion()

			setUser(typeof user === "string" ? null : user);
			setToken(typeof token === "string" ? null : token.token);
			setCurrentAvatar(avatar || undefined);
			setExports(typeof exports === "string" ? null : exports);
			setZeroByteFiles(
				typeof zeroByteFiles === "string" ? 0 : zeroByteFiles.files.length,
			);
			setZiplineVersion(typeof versionData === "string" ? null : versionData.version)
		})();
	}, []);

	useEffect(() => {
		if (user) {
			setUsername(user.username);
			setCurrentUsername(user.username);

			setViewEnabled(user.view.enabled || false);
			setViewShowMimetype(user.view.showMimetype || false);
			setViewContent(user.view.content);
			setViewAlign(user.view.align || "left");
			setViewEmbed(user.view.embed || false);
			setViewEmbedTitle(user.view.embedTitle);
			setViewEmbedDescription(user.view.embedDescription);
			setViewEmbedSiteName(user.view.embedSiteName);
			setViewEmbedColor(user.view.embedColor);
		}
	}, [user]);

	const [saveError, setSaveError] = useState<string | null>(null);

	type SaveCategories = "userInfo" | "viewingFiles";

	async function handleSave(category: SaveCategories) {
		setSaveError(null);
		let saveSettings: Partial<EditUserOptions> = {};

		switch (category) {
			case "userInfo": {
				saveSettings = {
					password,
				};

				if (username !== currentUsername) saveSettings.username = username;

				setPassword(undefined);

				break;
			}

			case "viewingFiles": {
				saveSettings = {
					view: {
						enabled: viewEnabled,
						align: viewAlign,
						content: viewContent,
						embed: viewEmbed,
						embedColor: viewEmbedColor,
						embedDescription: viewEmbedDescription,
						embedSiteName: viewEmbedSiteName,
						embedTitle: viewEmbedTitle,
						showMimetype: viewShowMimetype,
					},
				};
			}
		}

		if (Object.keys(saveSettings).length <= 0) return "Something went wrong...";

		const success = await editCurrentUser(saveSettings);

		if (typeof success === "string") return setSaveError(success);

		return ToastAndroid.show(
			"Successfully saved the settings",
			ToastAndroid.SHORT,
		);
	}

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<Popup
					hidden={!clearZeroByteFilesPopupOpen}
					onClose={() => {
						setClearZeroByteFilesPopupOpen(false);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Are you sure?</Text>

						<Text style={styles.serverActionWarningText}>
							This will delete {zeroByteFiles} files from the database and
							datasource.
						</Text>

						<View style={styles.manageServerActionButtonsContainer}>
							<Button
								onPress={() => {
									setClearZeroByteFilesPopupOpen(false);
								}}
								text="Cancel"
								color="#181c28"
								margin={{
									right: 10,
									top: 10,
								}}
							/>

							<Button
								color="#CF4238"
								margin={{
									right: 10,
									top: 10,
								}}
								text="Yes, Delete"
								onPress={async () => {
									const success = await clearZeroByteFiles();

									if (typeof success === "string") {
										setSaveError(success);
										setClearZeroByteFilesPopupOpen(false);

										return;
									}

									setClearZeroByteFilesPopupOpen(false);

									ToastAndroid.show(success.status, ToastAndroid.SHORT);
								}}
							/>
						</View>
					</View>

					<Text style={styles.popupSubHeaderText}>
						Press outside to close this popup
					</Text>
				</Popup>

				<Popup
					hidden={!clearTempFilesPopupOpen}
					onClose={() => {
						setClearTempFilesPopupOpen(false);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Are you sure?</Text>

						<Text style={styles.serverActionWarningText}>
							This will delete temporary files stored within the temporary
							directory (defined in the configuration). This should not cause
							harm unless there are files that are being processed still.
						</Text>

						<View style={styles.manageServerActionButtonsContainer}>
							<Button
								onPress={() => {
									setClearTempFilesPopupOpen(false);
								}}
								text="Cancel"
								color="#181c28"
								margin={{
									right: 10,
									top: 10,
								}}
							/>

							<Button
								color="#CF4238"
								margin={{
									right: 10,
									top: 10,
								}}
								text="Yes, Delete"
								onPress={async () => {
									const success = await clearTempFiles();

									if (typeof success === "string") {
										setSaveError(success);
										setClearTempFilesPopupOpen(false);

										return;
									}

									setClearTempFilesPopupOpen(false);

									ToastAndroid.show(success.status, ToastAndroid.SHORT);
								}}
							/>
						</View>
					</View>

					<Text style={styles.popupSubHeaderText}>
						Press outside to close this popup
					</Text>
				</Popup>

				<Popup
					hidden={!requerySizeOfFilesPopupOpen}
					onClose={() => {
						setRequerySizeOfFilesPopupOpen(false);
						setRequerySizeForceDelete(false);
						setRequerySizeForceUpdate(false);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Are you sure?</Text>

						<Text style={styles.serverActionWarningText}>
							This will requery the size of every file stored within the
							database. Additionally you can use the options below.
						</Text>

						<Switch
							title="Force Update"
							value={requerySizeForceUpdate || false}
							onValueChange={() => setRequerySizeForceUpdate((prev) => !prev)}
						/>

						<Switch
							title="Force Delete"
							value={requerySizeForceDelete || false}
							onValueChange={() => setRequerySizeForceDelete((prev) => !prev)}
						/>

						<View style={styles.manageServerActionButtonsContainer}>
							<Button
								onPress={() => {
									setRequerySizeOfFilesPopupOpen(false);
									setRequerySizeForceDelete(false);
									setRequerySizeForceUpdate(false);
								}}
								text="Cancel"
								color="#181c28"
								margin={{
									right: 10,
									top: 10,
								}}
							/>

							<Button
								color="#CF4238"
								margin={{
									right: 10,
									top: 10,
								}}
								text="Requery"
								onPress={async () => {
									const success = await requeryFileSize({
										forceDelete: requerySizeForceDelete,
										forceUpdate: requerySizeForceUpdate,
									});

									if (typeof success === "string") {
										setSaveError(success);
										setRequerySizeOfFilesPopupOpen(false);
										setRequerySizeForceDelete(false);
										setRequerySizeForceUpdate(false);

										return;
									}

									setRequerySizeOfFilesPopupOpen(false);
									setRequerySizeForceDelete(false);
									setRequerySizeForceUpdate(false);

									ToastAndroid.show(success.status, ToastAndroid.SHORT);
								}}
							/>
						</View>
					</View>

					<Text style={styles.popupSubHeaderText}>
						Press outside to close this popup
					</Text>
				</Popup>

				<Popup
					hidden={!generateThumbnailsPopupOpen}
					onClose={() => {
						setGenerateThumbnailsPopupOpen(false);
						setGenerateThumbnailsRerun(false);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Are you sure?</Text>

						<Text style={styles.serverActionWarningText}>
							This will generate thumbnails for all files that do not have a
							thumbnail set. Additionally you can use the options below.
						</Text>

						<Switch
							title="Re-run"
							value={generateThumbnailsRerun || false}
							onValueChange={() => setGenerateThumbnailsRerun((prev) => !prev)}
						/>

						<View style={styles.manageServerActionButtonsContainer}>
							<Button
								onPress={() => {
									setGenerateThumbnailsPopupOpen(false);
									setGenerateThumbnailsRerun(false);
								}}
								text="Cancel"
								color="#181c28"
								margin={{
									right: 10,
									top: 10,
								}}
							/>

							<Button
								color="#CF4238"
								margin={{
									right: 10,
									top: 10,
								}}
								text="Generate"
								onPress={async () => {
									const success = await generateThumbnails(
										generateThumbnailsRerun,
									);

									if (typeof success === "string") {
										setSaveError(success);
										setGenerateThumbnailsPopupOpen(false);
										setGenerateThumbnailsRerun(false);

										return;
									}

									setGenerateThumbnailsPopupOpen(false);
									setGenerateThumbnailsRerun(false);

									ToastAndroid.show(success.status, ToastAndroid.LONG);
								}}
							/>
						</View>
					</View>

					<Text style={styles.popupSubHeaderText}>
						Press outside to close this popup
					</Text>
				</Popup>

				<View style={styles.header}>
					<Text style={styles.headerText}>User Settings</Text>

					{saveError && (
						<Text style={styles.errorText} key={saveError}>
							{saveError}
						</Text>
					)}
				</View>

				{user ? (
					<View style={styles.settingsContainer}>
						<KeyboardAwareScrollView style={styles.scrollView}>
							{/* User Info */}
							{token && (
								<View style={styles.settingGroup}>
									<Text style={styles.headerText}>User Info</Text>
									<Text style={styles.subHeaderText}>{user.id}</Text>

									<Pressable
										onPress={() => {
											setTokenVisible(true);
										}}
									>
										<TextInput
											title="Token:"
											showDisabledStyle={false}
											disabled
											disableContext
											value={tokenVisible ? token : "[Click to Reveal]"}
											onSideButtonPress={() => {
												Clipboard.setStringAsync(token);
											}}
											sideButtonIcon="content-copy"
										/>
									</Pressable>

									<TextInput
										title="Username:"
										onValueChange={(content) => setUsername(content)}
										placeholder="My Cool Username"
										value={username || ""}
									/>

									<TextInput
										title="Password:"
										onValueChange={(content) => setPassword(content)}
										placeholder="myPassword123"
										value={password || ""}
										password
									/>

									<Button
										onPress={() => handleSave("userInfo")}
										color="#323ea8"
										text="Save"
										icon="save"
										margin={{
											top: 10,
										}}
									/>
								</View>
							)}

							{/* Avatar */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Avatar</Text>

								<Button
									color="transparent"
									borderWidth={2}
									borderColor="#222c47"
									margin={{
										top: 5,
									}}
									rippleColor="gray"
									text={avatar ? (avatarName as string) : "Select an Avatar..."}
									onPress={async () => {
										const output = await DocumentPicker.getDocumentAsync({
											type: ["image/png", "image/jpeg", "image/jpg"],
											copyToCacheDirectory: true,
										});

										if (output.canceled || !output.assets) {
											setAvatar(undefined);
											setAvatarName(null);

											return;
										}

										const fileURI = output.assets[0].uri;

										const fileInfo = await FileSystem.getInfoAsync(fileURI);

										if (!fileInfo.exists) return;

										const avatarDataURI = await getFileDataURI(fileURI);

										setAvatar(avatarDataURI || undefined);

										const filename = fileURI.split("/").pop() || "avatar.png";

										setAvatarName(filename);
									}}
								/>

								<View style={styles.avatarPreviewContainer}>
									<Text style={styles.avatarPreviewHeader}>Avatar Preview</Text>
									<UserAvatar
										username={user.username}
										avatar={avatar || currentAvatar}
									/>
								</View>

								<View style={styles.avatarButtonsContainer}>
									{avatar && (
										<Button
											text="Cancel"
											color="transparent"
											textColor="white"
											height="auto"
											width="28.33%"
											margin={{
												left: "2.5%",
												right: "2.5%",
												top: 10,
											}}
											rippleColor="gray"
											borderWidth={2}
											borderColor="#ff8787"
											onPress={() => {
												setAvatar(undefined);
												setAvatarName(null);
											}}
										/>
									)}

									{currentAvatar && (
										<Button
											text="Remove Avatar"
											color="#e03131"
											textColor="white"
											height="auto"
											width="28.33%"
											margin={{
												left: "2.5%",
												right: "2.5%",
												top: 10,
											}}
											onPress={async () => {
												const success = await editCurrentUser({
													avatar: null,
												});

												if (typeof success === "string")
													return setSaveError(success);

												const newUserAvatar = await getCurrentUserAvatar();

												setCurrentAvatar(newUserAvatar || undefined);
												setAvatar(undefined);
												setAvatarName(null);

												ToastAndroid.show(
													"Successfully removed the avatar",
													ToastAndroid.SHORT,
												);
											}}
										/>
									)}

									<Button
										text="Save"
										color={avatar ? "#323ea8" : "#181c28"}
										textColor={avatar ? "white" : "gray"}
										height="auto"
										width="28.33%"
										margin={{
											left: "2.5%",
											right: "2.5%",
											top: 10,
										}}
										disabled={!avatar}
										onPress={async () => {
											const success = await editCurrentUser({
												avatar: avatar,
											});

											if (typeof success === "string")
												return setSaveError(success);

											const newUserAvatar = await getCurrentUserAvatar();

											setCurrentAvatar(newUserAvatar || undefined);
											setAvatar(undefined);
											setAvatarName(null);

											ToastAndroid.show(
												"Successfully saved the avatar",
												ToastAndroid.SHORT,
											);
										}}
									/>
								</View>
							</View>

							{/* Viewing Files */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Viewing Files</Text>

								<Switch
									title="Enable View Routes"
									value={viewEnabled || false}
									onValueChange={() => setViewEnabled((prev) => !prev)}
								/>

								<Switch
									title="Show Mimetype"
									disabled={!viewEnabled}
									value={viewShowMimetype || false}
									onValueChange={() => setViewShowMimetype((prev) => !prev)}
								/>

								<TextInput
									title="View Content:"
									disabled={!viewEnabled}
									disableContext={!viewEnabled}
									multiline
									inputStyle={styles.multilineTextInput}
									onValueChange={(content) => setViewContent(content)}
									value={viewContent || ""}
									placeholder="This is my file"
								/>

								<Text
									style={{
										...styles.inputHeader,
										...(!viewEnabled && styles.inputHeaderDisabled),
									}}
								>
									View Content Alignment:
								</Text>
								<Select
									disabled={!viewEnabled}
									data={alignments}
									onSelect={(selectedAlignment) => {
										if (selectedAlignment.length <= 0) return;

										setViewAlign(
											selectedAlignment[0].value as typeof viewAlign,
										);
									}}
									placeholder="Select Alignment..."
									defaultValue={alignments.find(
										(format) => format.value === "left",
									)}
								/>

								<Switch
									title="Embed"
									disabled={!viewEmbed || !viewEnabled}
									value={viewEmbed || false}
									onValueChange={() => setViewEmbed((prev) => !prev)}
								/>

								<TextInput
									title="Embed Title:"
									disabled={!viewEmbed || !viewEnabled}
									disableContext={!viewEmbed || !viewEnabled}
									onValueChange={(content) => setViewEmbedTitle(content)}
									value={viewEmbedTitle || ""}
									placeholder="My Cool Title"
								/>

								<TextInput
									title="Embed Description:"
									disabled={!viewEmbed || !viewEnabled}
									disableContext={!viewEmbed || !viewEnabled}
									onValueChange={(content) => setViewEmbedDescription(content)}
									value={viewEmbedDescription || ""}
									placeholder="My Cool Description"
								/>

								<TextInput
									title="Embed Site Name:"
									disableContext={!viewEmbed || !viewEnabled}
									disabled={!viewEmbed || !viewEnabled}
									onValueChange={(content) => setViewEmbedSiteName(content)}
									value={viewEmbedSiteName || ""}
									placeholder="My Cool Site Name"
								/>

								<ColorPicker
									title="Embed Color"
									initialColor={viewEmbedColor}
									onSelectColor={(color) => {
										setViewEmbedColor(color.hex);
									}}
									disabled={!viewEmbed || !viewEnabled}
								/>

								<Button
									onPress={() => handleSave("viewingFiles")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Export Files */}
							{exports && token && (
								<View style={styles.settingGroup}>
									<Text style={styles.headerText}>Export Files</Text>

									<Button
										onPress={async () => {
											const success = await createUserExport();

											if (typeof success === "string")
												return setSaveError(success);

											const newExports = await getUserExports();

											setExports(
												typeof newExports === "string" ? null : newExports,
											);

											ToastAndroid.show(
												"Successfully started creating the export",
												ToastAndroid.SHORT,
											);
										}}
										color="#323ea8"
										text="New Export"
										margin={{
											top: 10,
										}}
									/>

									<View style={styles.exportsContainer}>
										<Table
											headerRow={[
												{
													row: "ID",
												},
												{
													row: "Started On",
												},
												{
													row: "Files",
												},
												{
													row: "Size",
												},
												{
													row: "Actions",
												},
											]}
											rowWidth={[230, 130, 60, 90, 90]}
											rows={exports.map((zlExport, index) => {
												const id = (
													<Text key={zlExport.id} style={styles.rowText}>
														{zlExport.id}
													</Text>
												);

												const startedOn = (
													<Text key={zlExport.id} style={styles.rowText}>
														{new Date(zlExport.createdAt).toLocaleString()}
													</Text>
												);

												const files = (
													<Text key={zlExport.id} style={styles.rowText}>
														{zlExport.files}
													</Text>
												);

												const size = (
													<Text key={zlExport.id} style={styles.rowText}>
														{convertToBytes(Number.parseInt(zlExport.size), {
															unitSeparator: " ",
														})}
													</Text>
												);

												const actions = (
													<View
														key={zlExport.id}
														style={styles.actionsContainer}
													>
														<Button
															icon="delete"
															color="#CF4238"
															onPress={async () => {
																setSaveError(null);

																const exportId = zlExport.id;

																const success =
																	await deleteUserExport(exportId);

																if (typeof success === "string")
																	return setSaveError(success);

																const newExports = await getUserExports();

																setExports(
																	typeof newExports === "string"
																		? null
																		: newExports,
																);

																return ToastAndroid.show(
																	"Successfully deleted the export",
																	ToastAndroid.SHORT,
																);
															}}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>

														<Button
															icon="download"
															color={zlExport.completed ? "#323ea8" : "#181c28"}
															iconColor={zlExport.completed ? "white" : "gray"}
															disabled={!zlExport.completed}
															onPress={async () => {
																const exportId = zlExport.id;

																const downloadUrl = `${url}/api/user/export?id=${exportId}`;

																let savedExportDownloadUri =
																	db.get("exportDownloadPath");

																if (!savedExportDownloadUri) {
																	const permissions =
																		await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

																	if (!permissions.granted)
																		return ToastAndroid.show(
																			"The permission to save the file was not granted",
																			ToastAndroid.SHORT,
																		);

																	db.set(
																		"exportDownloadPath",
																		permissions.directoryUri,
																	);
																	savedExportDownloadUri =
																		permissions.directoryUri;
																}

																ToastAndroid.show(
																	"Downloading...",
																	ToastAndroid.SHORT,
																);

																const saveUri =
																	await FileSystem.StorageAccessFramework.createFileAsync(
																		savedExportDownloadUri,
																		zlExport.path,
																		"application/zip",
																	);

																const downloadResult =
																	await FileSystem.downloadAsync(
																		downloadUrl,
																		`${FileSystem.cacheDirectory}/${zlExport.path}`,
																		{
																			headers: {
																				Authorization: token,
																			},
																		},
																	);

																if (!downloadResult.uri)
																	return ToastAndroid.show(
																		"Something went wrong while downloading the file",
																		ToastAndroid.SHORT,
																	);

																const base64Export =
																	await FileSystem.readAsStringAsync(
																		downloadResult.uri,
																		{
																			encoding: FileSystem.EncodingType.Base64,
																		},
																	);

																await FileSystem.writeAsStringAsync(
																	saveUri,
																	base64Export,
																	{
																		encoding: FileSystem.EncodingType.Base64,
																	},
																);

																ToastAndroid.show(
																	"Successfully downloaded the export",
																	ToastAndroid.SHORT,
																);
															}}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>
													</View>
												);

												let rowStyle = styles.row;

												if (index === 0)
													rowStyle = {
														...styles.row,
														...styles.firstRow,
													};

												if (index === exports.length - 1)
													rowStyle = {
														...styles.row,
														...styles.lastRow,
													};

												return [id, startedOn, files, size, actions];
											})}
										/>
									</View>
								</View>
							)}

							{/* Server Actions */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Server Actions</Text>

								<View style={styles.serverActionButtonRow}>
									<Button
										onPress={async () => {
											setClearZeroByteFilesPopupOpen(true);

											const zeroByteFiles = await getZeroByteFiles();
											setZeroByteFiles(
												typeof zeroByteFiles === "string"
													? 0
													: zeroByteFiles.files.length,
											);
										}}
										color="#323ea8"
										text="Clear Zero Byte Files"
										width="45%"
										margin={{
											left: "2.5%",
											right: "2.5%",
											top: 10,
										}}
									/>

									<Button
										onPress={() => setClearTempFilesPopupOpen(true)}
										color="#323ea8"
										text="Clear Temp Files"
										width="45%"
										margin={{
											left: "2.5%",
											right: "2.5%",
											top: 10,
										}}
									/>
								</View>

								<View style={styles.serverActionButtonRow}>
									<Button
										onPress={() => setRequerySizeOfFilesPopupOpen(true)}
										color="#323ea8"
										text="Requery Size of Files"
										width="45%"
										margin={{
											left: "2.5%",
											right: "2.5%",
											top: 10,
										}}
									/>

									<Button
										onPress={() => setGenerateThumbnailsPopupOpen(true)}
										color="#323ea8"
										text="Generate Thumbnails"
										width="45%"
										margin={{
											left: "2.5%",
											right: "2.5%",
											top: 10,
										}}
									/>
								</View>
							</View>

							{/* App Settings */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>App Settings</Text>

								<Text style={styles.subHeaderText}>
									App Version: {appVersion}
								</Text>

								{ziplineVersion && (
									<Text style={styles.subHeaderText}>
										Zipline Version: {ziplineVersion}
									</Text>
								)}

								<Switch
									title="Disable Update Alert"
									value={updateAlertDisabled}
									onValueChange={() => {
										db.set(
											"disableUpdateAlert",
											updateAlertDisabled ? "false" : "true",
										);

										setUpdateAlertDisabled((prev) => !prev);
									}}
								/>

								{isUpdateAvailable ? (
									<Button
										onPress={async () => {
											await downloadUpdate();

											// ToastAndroid.show(message, ToastAndroid.SHORT);
										}}
										color={isDownloading ? "#373d79" : "#323ea8"}
										text={
											isDownloading
												? "Downloading Update..."
												: "Download Update"
										}
										// text={
										// 	isDownloading
										// 		? `Downloading Update... ${downloadProgress}%${
										// 				downloadSize
										// 					? ` (${convertToBytes(downloadSize, {
										// 							unitSeparator: " ",
										// 						})})`
										// 					: ""
										// 			}`
										// 		: "Download Update"
										// }
										disabled={isDownloading}
										margin={{
											top: 10,
										}}
									/>
								) : (
									<Button
										onPress={async () => {
											const update = await checkForUpdates();

											ToastAndroid.show(
												`${update.available ? "" : "No "}Update Avaible${update.nextVersion ? ` (v${update.currentVersion} -> v${update.nextVersion})` : ""}`,
												ToastAndroid.SHORT,
											);
										}}
										color={isChecking ? "#373d79" : "#323ea8"}
										text={
											isChecking
												? "Checking for Updates..."
												: "Check for Updates"
										}
										disabled={isChecking}
										margin={{
											top: 10,
										}}
									/>
								)}

								<Button
									onPress={async () => {
										const permissions =
											await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

										if (!permissions.granted)
											return ToastAndroid.show(
												"The permission to the folder was not granted",
												ToastAndroid.SHORT,
											);

										db.set("exportDownloadPath", permissions.directoryUri);

										ToastAndroid.show(
											"Successfully changed the folder",
											ToastAndroid.SHORT,
										);
									}}
									color="#323244"
									text="Change Export Download Folder"
									margin={{
										top: 10,
									}}
								/>

								<Button
									onPress={async () => {
										const permissions =
											await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

										if (!permissions.granted)
											return ToastAndroid.show(
												"The permission to the folder was not granted",
												ToastAndroid.SHORT,
											);

										db.set("fileDownloadPath", permissions.directoryUri);

										ToastAndroid.show(
											"Successfully changed the folder",
											ToastAndroid.SHORT,
										);
									}}
									color="#323244"
									text="Change File Download Folder"
									margin={{
										top: 10,
									}}
								/>

								<Button
									onPress={async () => {
										const permissions =
											await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

										if (!permissions.granted)
											return ToastAndroid.show(
												"The permission to the folder was not granted",
												ToastAndroid.SHORT,
											);

										db.set("updateDownloadPath", permissions.directoryUri);

										ToastAndroid.show(
											"Successfully changed the folder",
											ToastAndroid.SHORT,
										);
									}}
									color="#323244"
									text="Change Updates Download Folder"
									margin={{
										top: 10,
									}}
								/>

								<Button
									onPress={async () => {
										await db.del("url");
										await db.del("token");

										router.replace("/login");
									}}
									color="#CF4238"
									text="Logout"
									margin={{
										top: 10,
									}}
								/>
							</View>
						</KeyboardAwareScrollView>
					</View>
				) : (
					<View style={styles.loadingContainer}>
						<Text style={styles.loadingText}>Loading...</Text>
					</View>
				)}
			</View>
		</View>
	);
}
