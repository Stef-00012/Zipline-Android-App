import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import type { APIExports, APISelfUser, DashURL } from "@/types/zipline";
import { useState, useEffect } from "react";
import { View, Text, Pressable, ToastAndroid, ScrollView } from "react-native";
import { styles } from "@/styles/settings";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { editCurrentUser, type EditUserOptions, getCurrentUser, getCurrentUserAvatar } from "@/functions/zipline/user";
import { MaterialIcons } from "@expo/vector-icons";
import * as Clipboard from "expo-clipboard"
import { getTokenWithToken } from "@/functions/zipline/auth";
import * as DocumentPicker from "expo-document-picker"
import * as FileSystem from "expo-file-system"
import { convertToBytes, getFileDataURI } from "@/functions/util";
import UserAvatar from "@/components/UserAvatar";
import Select from "@/components/Select";
import { alignments } from "@/constants/settings";
import { createUserExport, deleteUserExport, getUserExports } from "@/functions/zipline/exports";
import { Row, Table } from "react-native-table-component";
import * as db from "@/functions/database";
import Popup from "@/components/Popup";
import { clearTempFiles, clearZeroByteFiles, generateThumbnails, getZeroByteFiles, requeryFileSize } from "@/functions/zipline/serverActions";
import { useRouter } from "expo-router";
import TextInput from "@/components/TextInput"
import Switch from "@/components/Switch";

export default function UserSettings() {
	const router = useRouter()

	useAuth();
	useShareIntent();

	const [user, setUser] = useState<APISelfUser | null>(null);
	const [token, setToken] = useState<string | null>(null);
	const [currentAvatar, setCurrentAvatar] = useState<string | undefined>(undefined)
	const [exports, setExports] = useState<APIExports | null>([])
	const [zeroByteFiles, setZeroByteFiles] = useState<number>(0)
	
	const [tokenVisible, setTokenVisible] = useState<boolean>(false)

	const [currentUsername, setCurrentUsername] = useState<string | undefined>(undefined)
	const [username, setUsername] = useState<string | undefined>(undefined)
	const [password, setPassword] = useState<string | undefined>(undefined)
	const [avatar, setAvatar] = useState<string | undefined>(undefined)
	const [avatarName, setAvatarName] = useState<string | null>(null)

	const [viewEnabled, setViewEnabled] = useState<boolean>(false);
	const [viewAlign, setViewAlign] = useState<"left" | "center" | "right">("left");
	const [viewShowMimetype, setViewShowMimetype] = useState<boolean>(false);
	const [viewContent, setViewContent] = useState<string | undefined>(undefined);
	const [viewEmbed, setViewEmbed] = useState<boolean>(false);
	const [viewEmbedTitle, setViewEmbedTitle] = useState<string | undefined>(undefined);
	const [viewEmbedDescription, setViewEmbedDescription] = useState<string | undefined>(undefined);
	const [viewEmbedColor, setViewEmbedColor] = useState<string | undefined>(undefined);
	const [viewEmbedSiteName, setViewEmbedSiteName] = useState<string | undefined>(undefined);

	const [clearZeroByteFilesPopupOpen, setClearZeroByteFilesPopupOpen] = useState<boolean>(false);
	const [clearTempFilesPopupOpen, setClearTempFilesPopupOpen] = useState<boolean>(false);
	const [requerySizeOfFilesPopupOpen, setRequerySizeOfFilesPopupOpen] = useState<boolean>(false);
	const [generateThumbnailsPopupOpen, setGenerateThumbnailsPopupOpen] = useState<boolean>(false);

	const [requerySizeForceUpdate, setRequerySizeForceUpdate] = useState<boolean>(false);
	const [requerySizeForceDelete, setRequerySizeForceDelete] = useState<boolean>(false);

	const [generateThumbnailsRerun, setGenerateThumbnailsRerun] = useState<boolean>(false);

	const url = db.get("url") as DashURL

	useEffect(() => {
		(async () => {
			const user = await getCurrentUser();
			const token = await getTokenWithToken()
			const avatar = await getCurrentUserAvatar()
			const exports = await getUserExports()
			const zeroByteFiles = await getZeroByteFiles()

			setUser(typeof user === "string" ? null : user);
			setToken(typeof token === "string" ? null : token.token)
			setCurrentAvatar(avatar || undefined)
			setExports(typeof exports === "string" ? null : exports)
			setZeroByteFiles(typeof zeroByteFiles === "string" ? 0 : zeroByteFiles.files.length)
		})();
	}, []);

	useEffect(() => {
		if (user) {
			setUsername(user.username)
			setCurrentUsername(user.username)

			setViewEnabled(user.view.enabled || false)
			setViewShowMimetype(user.view.showMimetype || false)
			setViewContent(user.view.content)
			setViewAlign(user.view.align || "left")
			setViewEmbed(user.view.embed || false)
			setViewEmbedTitle(user.view.embedTitle)
			setViewEmbedDescription(user.view.embedDescription)
			setViewEmbedSiteName(user.view.embedSiteName)
			setViewEmbedColor(user.view.embedColor)
		}
	}, [user]);

	const [saveError, setSaveError] = useState<string | null>(null)

	type SaveCategories = "userInfo" | "viewingFiles"

	async function handleSave(category: SaveCategories) {
		setSaveError(null)
		let saveSettings: Partial<EditUserOptions> = {}

		switch(category) {
            case "userInfo": {
				saveSettings = {
					password,
				}

				if (username !== currentUsername) saveSettings.username = username

				setPassword(undefined)

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
					}
				}
			}
		}

		if (Object.keys(saveSettings).length <= 0) return "Something went wrong...";

		const success = await editCurrentUser(saveSettings)

		if (typeof success === "string") return setSaveError(success)

		return ToastAndroid.show(
			"Successfully saved the settings",
			ToastAndroid.SHORT
		)
	}

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<Popup hidden={!clearZeroByteFilesPopupOpen} onClose={() => {
					setClearZeroByteFilesPopupOpen(false)
				}}>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Are you sure?</Text>

						<Text style={styles.serverActionWarningText}>This will delete {zeroByteFiles} files from the database and datasource.</Text>

						<View style={styles.manageServerActionButtonsContainer}>
							<Pressable style={{
								...styles.button,
								...styles.manageServerActionButtonCancel,
								marginRight: 10
							}} onPress={() => {
								setClearZeroByteFilesPopupOpen(false)
							}}>
								<Text style={styles.buttonText}>Cancel</Text>
							</Pressable>

							<Pressable style={{
								...styles.button,
								...styles.manageServerActionButtonDanger,
								marginRight: 10
							}} onPress={async () => {
								const success = await clearZeroByteFiles()

								if (typeof success === "string") {
									setSaveError(success)
									setClearZeroByteFilesPopupOpen(false)

									return;
								}

								setClearZeroByteFilesPopupOpen(false)

								ToastAndroid.show(
									success.status,
									ToastAndroid.SHORT
								)
							}}>
								<Text style={styles.buttonText}>Yes, Delete</Text>
							</Pressable>
						</View>
					</View>

					<Text
						style={styles.popupSubHeaderText}
					>
						Press outside to close this popup
					</Text>
				</Popup>

				<Popup hidden={!clearTempFilesPopupOpen} onClose={() => {
					setClearTempFilesPopupOpen(false)
				}}>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Are you sure?</Text>

						<Text style={styles.serverActionWarningText}>This will delete temporary files stored within the temporary directory (defined in the configuration). This should not cause harm unless there are files that are being processed still.</Text>

						<View style={styles.manageServerActionButtonsContainer}>
							<Pressable style={{
								...styles.button,
								...styles.manageServerActionButtonCancel,
								marginRight: 10
							}} onPress={() => {
								setClearTempFilesPopupOpen(false)
							}}>
								<Text style={styles.buttonText}>Cancel</Text>
							</Pressable>

							<Pressable style={{
								...styles.button,
								...styles.manageServerActionButtonDanger,
								marginRight: 10
							}} onPress={async () => {
								const success = await clearTempFiles()

								if (typeof success === "string") {
									setSaveError(success)
									setClearTempFilesPopupOpen(false)

									return;
								}

								setClearTempFilesPopupOpen(false)

								ToastAndroid.show(
									success.status,
									ToastAndroid.SHORT
								)
							}}>
								<Text style={styles.buttonText}>Yes, Delete</Text>
							</Pressable>
						</View>
					</View>

					<Text
						style={styles.popupSubHeaderText}
					>
						Press outside to close this popup
					</Text>
				</Popup>

				<Popup hidden={!requerySizeOfFilesPopupOpen} onClose={() => {
					setRequerySizeOfFilesPopupOpen(false)
					setRequerySizeForceDelete(false)
					setRequerySizeForceUpdate(false)
				}}>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Are you sure?</Text>

						<Text style={styles.serverActionWarningText}>This will requery the size of every file stored within the database. Additionally you can use the options below.</Text>

						<Switch
							title="Force Update"
							value={requerySizeForceUpdate || false}
							onValueChange={() => setRequerySizeForceUpdate((prev) => !prev)}
						/>

						<Switch
							title="Force Delete"
							value={requerySizeForceDelete || false}
							onValueChange={() =>
								setRequerySizeForceDelete((prev) => !prev)
							}
						/>

						<View style={styles.manageServerActionButtonsContainer}>
							<Pressable style={{
								...styles.button,
								...styles.manageServerActionButtonCancel,
								marginRight: 10
							}} onPress={() => {
								setRequerySizeOfFilesPopupOpen(false)
								setRequerySizeForceDelete(false)
								setRequerySizeForceUpdate(false)
							}}>
								<Text style={styles.buttonText}>Cancel</Text>
							</Pressable>

							<Pressable style={{
								...styles.button,
								...styles.manageServerActionButtonDanger,
								marginRight: 10
							}} onPress={async () => {
								const success = await requeryFileSize({
									forceDelete: requerySizeForceDelete,
									forceUpdate: requerySizeForceUpdate
								})

								if (typeof success === "string") {
									setSaveError(success)
									setRequerySizeOfFilesPopupOpen(false)
									setRequerySizeForceDelete(false)
									setRequerySizeForceUpdate(false)

									return;
								}

								setRequerySizeOfFilesPopupOpen(false)
								setRequerySizeForceDelete(false)
								setRequerySizeForceUpdate(false)

								ToastAndroid.show(
									success.status,
									ToastAndroid.SHORT
								)
							}}>
								<Text style={styles.buttonText}>Requery</Text>
							</Pressable>
						</View>
					</View>

					<Text
						style={styles.popupSubHeaderText}
					>
						Press outside to close this popup
					</Text>
				</Popup>

				<Popup hidden={!generateThumbnailsPopupOpen} onClose={() => {
					setGenerateThumbnailsPopupOpen(false)
					setGenerateThumbnailsRerun(false)
				}}>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Are you sure?</Text>

						<Text style={styles.serverActionWarningText}>This will generate thumbnails for all files that do not have a thumbnail set. Additionally you can use the options below.</Text>

						<Switch
							title="Re-run"
							value={generateThumbnailsRerun || false}
							onValueChange={() =>
								setGenerateThumbnailsRerun((prev) => !prev)
							}
						/>

						<View style={styles.manageServerActionButtonsContainer}>
							<Pressable style={{
								...styles.button,
								...styles.manageServerActionButtonCancel,
								marginRight: 10
							}} onPress={() => {
								setGenerateThumbnailsPopupOpen(false)
								setGenerateThumbnailsRerun(false)
							}}>
								<Text style={styles.buttonText}>Cancel</Text>
							</Pressable>

							<Pressable style={{
								...styles.button,
								...styles.manageServerActionButtonDanger,
								marginRight: 10
							}} onPress={async () => {
								const success = await generateThumbnails(generateThumbnailsRerun)

								if (typeof success === "string") {
									setSaveError(success)
									setGenerateThumbnailsPopupOpen(false)
									setGenerateThumbnailsRerun(false)

									return;
								}

								setGenerateThumbnailsPopupOpen(false)
								setGenerateThumbnailsRerun(false)

								ToastAndroid.show(
									success.status,
									ToastAndroid.LONG
								)
							}}>
								<Text style={styles.buttonText}>Generate</Text>
							</Pressable>
						</View>
					</View>

					<Text
						style={styles.popupSubHeaderText}
					>
						Press outside to close this popup
					</Text>
				</Popup>

				{(user && token && exports) ? (
					<View style={styles.settingsContainer}>
						<View style={styles.header}>
							<Text style={styles.headerText}>User Settings</Text>

							{saveError && <Text style={styles.errorText} key={saveError}>{saveError}</Text>}
						</View>

						<KeyboardAwareScrollView style={styles.scrollView}>
							{/* User Info */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>User Info</Text>
                                <Text style={styles.subHeaderText}>{user.id}</Text>

                                <Pressable onPress={() => {
                                    setTokenVisible(true)
                                }}>
									<TextInput
										title="Token:"
										showDisabledStyle={false}
										disabled
										disableContext
										value={tokenVisible ? token : "[Click to Reveal]"}
										onSideButtonPress={() => {
											Clipboard.setStringAsync(token)
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

								<Pressable style={styles.button} onPress={() => handleSave("userInfo")}>
									<Text style={styles.buttonText}>Save</Text>
								</Pressable>
							</View>

							{/* Avatar */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Avatar</Text>

								<Text style={styles.headerText}>Avatar:</Text>
								<Pressable style={styles.avatarButton} onPress={async () => {
									const output = await DocumentPicker.getDocumentAsync({
										type: [
											"image/png",
											"image/jpeg",
											"image/jpg"
										],
										copyToCacheDirectory: true,
									});
		
									if (output.canceled || !output.assets) {
										setAvatar(undefined)
										setAvatarName(null)
		
										return;
									};
		
									const fileURI = output.assets[0].uri
		
									const fileInfo = await FileSystem.getInfoAsync(fileURI)
		
									if (!fileInfo.exists) return;
		
									const avatarDataURI = await getFileDataURI(fileURI)
		
									setAvatar(avatarDataURI || undefined)
									
									const filename = fileURI.split('/').pop() || "avatar.png"
		
									setAvatarName(filename)
								}}>
									<Text style={styles.avatarButtonText}>{avatar ? avatarName : "Select an Avatar..."}</Text>
								</Pressable>

								<View style={styles.avatarPreviewContainer}>
									<Text style={styles.avatarPreviewHeader}>Avatar Preview</Text>
									<UserAvatar username={user.username} avatar={avatar || currentAvatar} />
								</View>

								<View style={styles.avatarButtonsContainer}>
									{avatar && (
										<Pressable
											style={{
												...styles.button,
												...styles.manageAvatarButton,
												...styles.cancelAvatarButton
											}}
											onPress={() => {
												setAvatar(undefined)
												setAvatarName(null)
											}}
										>
											<Text style={styles.buttonText}>Cancel</Text>
										</Pressable>
									)}
		
									{currentAvatar && (
										<Pressable
											style={{
												...styles.button,
												...styles.manageAvatarButton,
												...styles.removeAvatarButton
											}}
											onPress={async () => {
												const success = await editCurrentUser({
													avatar: null
												})

												if (typeof success === "string") return setSaveError(success);

												const newUserAvatar = await getCurrentUserAvatar()

												setCurrentAvatar(newUserAvatar || undefined)
												setAvatar(undefined)
												setAvatarName(null)

												ToastAndroid.show(
													"Successfully removed the avatar",
													ToastAndroid.SHORT
												)
											}}
										>
											<Text style={styles.buttonText}>Remove Avatar</Text>
										</Pressable>
									)}

									<Pressable
										style={{
											...styles.button,
											...styles.manageAvatarButton,
											...(!avatar && styles.saveAvatarButtonDisabled)
										}}
										onPress={async () => {
											const success = await editCurrentUser({
												avatar: avatar
											})

											if (typeof success === "string") return setSaveError(success);

											const newUserAvatar = await getCurrentUserAvatar()

											setCurrentAvatar(newUserAvatar || undefined)
											setAvatar(undefined)
											setAvatarName(null)

											ToastAndroid.show(
												"Successfully saved the avatar",
												ToastAndroid.SHORT
											)
										}}
									>
										<Text style={{
											...styles.buttonText,
											...(!avatar && styles.saveAvatarButtonTextDisabled)
										}}>Save</Text>
									</Pressable>
								</View>
							</View>

							{/* Viewing Files */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Viewing Files</Text>

								<Switch
									title="Enable View Routes"
									value={viewEnabled || false}
									onValueChange={() =>
										setViewEnabled((prev) => !prev)
									}
								/>

								<Switch
									title="Show Mimetype"
									disabled={!viewEnabled}
									value={viewShowMimetype || false}
									onValueChange={() =>
										setViewShowMimetype((prev) => !prev)
									}
								/>

								<TextInput
									title="View Content:"
									disabled={!viewEnabled}
									disableContext={!viewEnabled}
									multiline
									onValueChange={(content) => setViewContent(content)}
									value={viewContent || ""}
									placeholder="This is my file"
								/>

								<Text style={{
									...styles.inputHeader,
									...(!viewEnabled && styles.inputHeaderDisabled)
								}}>View Content Alignment:</Text>
								<Select
									disabled={!viewEnabled}
									data={alignments}
									onSelect={(selectedAlignment) =>
										setViewAlign(
											selectedAlignment[0].value as typeof viewAlign,
										)
									}
									placeholder="Select Alignment..."
									defaultValue={alignments.find(
										(format) => format.value === "left",
									)}
								/>

								<Switch
									title="Embed"
									disabled={!viewEmbed || !viewEnabled}
									value={viewEmbed || false}
									onValueChange={() =>
										setViewEmbed((prev) => !prev)
									}
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

								<TextInput
									title="Embed Color:"
									disableContext={!viewEmbed || !viewEnabled}
									disabled={!viewEmbed || !viewEnabled}
									onValueChange={(content) => setViewEmbedColor(content)}
									value={viewEmbedColor || ""}
									placeholder="My Cool Color"
								/>

								<Pressable style={styles.button} onPress={() => handleSave("viewingFiles")}>
									<Text style={styles.buttonText}>Save</Text>
								</Pressable>
							</View>

							{/* Export Files */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Export Files</Text>

								<Pressable style={styles.button} onPress={async () => {
									const success = await createUserExport()

									if (typeof success === "string") return setSaveError(success);

									const newExports = await getUserExports()

									setExports(typeof newExports === "string" ? null : newExports)

									ToastAndroid.show(
										"Successfully started creating the export",
										ToastAndroid.SHORT
									)
								}}>
									<Text style={styles.buttonText}>New Export</Text>
								</Pressable>

								<View style={styles.exportsContainer}>
									<ScrollView
										showsHorizontalScrollIndicator={false}
										horizontal
									>
										<View>
											<Table>
												<Row
													data={[
														"ID",
														"Started On",
														"Files",
														"Size",
														"Actions",
													]}
													widthArr={[150, 130, 50, 70, 90]}
													style={styles.tableHeader}
													textStyle={styles.rowText}
												/>
											</Table>
											<ScrollView
												showsVerticalScrollIndicator={false}
												style={styles.tableVerticalScroll}
											>
												<Table>
													{exports.map((zlExport, index) => {
														const id = (
															<Text style={styles.rowText}>
																{zlExport.id}
															</Text>
														);
		
														const startedOn = (
															<Text style={styles.rowText}>
																{new Date(zlExport.createdAt).toLocaleString()}
															</Text>
														);
		
														const files = (
															<Text style={styles.rowText}>
																{zlExport.files}
															</Text>
														);

														const size = (
															<Text style={styles.rowText}>
																{convertToBytes(Number.parseInt(zlExport.size), {
																	unitSeparator: " "
																})}
															</Text>
														);

														const actions = (
															<View style={styles.actionsContainer}>
																<Pressable
																	style={{
																		...styles.actionButton,
																		...styles.actionButtonDanger
																	}}
																	onPress={async () => {
																		setSaveError(null)

																		const exportId = zlExport.id;

																		const success = await deleteUserExport(exportId)
		
																		if (typeof success === "string") return setSaveError(success)

																		const newExports = await getUserExports()

																		setExports(typeof newExports === "string" ? null : newExports)
		
																		return ToastAndroid.show(
																			"Successfully deleted the export",
																			ToastAndroid.SHORT,
																		);
																	}}
																>
																	<MaterialIcons
																		name="delete"
																		size={20}
																		color={"white"}
																	/>
																</Pressable>
		
																<Pressable
																	style={{
																		...styles.actionButton,
																		...(!zlExport.completed && styles.actionButtonDisabled)
																	}}
																	disabled={!zlExport.completed}
																	onPress={async () => {
																		const exportId = zlExport.id;

																		const downloadUrl = `${url}/api/user/export?id=${exportId}`

																		let savedExportDownloadUri = db.get("exportDownloadPath")

																		if (!savedExportDownloadUri) {
																			const permissions = await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

																			if (!permissions.granted) return ToastAndroid.show(
																				"The permission to save the file was not granted",
																				ToastAndroid.SHORT
																			);

																			db.set("exportDownloadPath", permissions.directoryUri)
																			savedExportDownloadUri = permissions.directoryUri
																		}

																		ToastAndroid.show(
																			"Downloading...",
																			ToastAndroid.SHORT
																		)

																		const saveUri = await FileSystem.StorageAccessFramework.createFileAsync(savedExportDownloadUri, zlExport.path, 'application/zip')

																		const downloadResult = await FileSystem.downloadAsync(downloadUrl, `${FileSystem.cacheDirectory}/${zlExport.path}`, {
																			headers: {
																				Authorization: token
																			},
																		})

																		if (!downloadResult.uri) return ToastAndroid.show(
																			"Something went wrong while downloading the file",
																			ToastAndroid.SHORT
																		)

																		const base64Export = await FileSystem.readAsStringAsync(downloadResult.uri, {
																			encoding: FileSystem.EncodingType.Base64
																		})

																		await FileSystem.writeAsStringAsync(saveUri, base64Export, {
																			encoding: FileSystem.EncodingType.Base64
																		})

																		ToastAndroid.show(
																			"Successfully downloaded the export",
																			ToastAndroid.SHORT
																		)
																	}}
																>
																	<MaterialIcons
																		name="download"
																		size={20}
																		color={"white"}
																	/>
																</Pressable>
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
		
														return (
															<Row
																key={zlExport.id}
																data={[
																	id,
																	startedOn,
																	files,
																	size,
																	actions,
																]}
																widthArr={[150, 130, 50, 70, 90]}
																style={rowStyle}
																textStyle={styles.rowText}
															/>
														);
													})}
												</Table>
											</ScrollView>
										</View>
									</ScrollView>
								</View>
							</View>

							{/* Server Actions */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Server Actions</Text>

								<View style={styles.serverActionButtonRow}>
									<Pressable style={{
										...styles.button,
										...styles.serverActionButton
									}} onPress={async () => {
										setClearZeroByteFilesPopupOpen(true)

										const zeroByteFiles = await getZeroByteFiles()
										setZeroByteFiles(typeof zeroByteFiles === "string" ? 0 : zeroByteFiles.files.length)
									}}>
										<Text style={styles.buttonText}>Clear Zero Byte Files</Text>
									</Pressable>

									<Pressable style={{
										...styles.button,
										...styles.serverActionButton
									}} onPress={() => setClearTempFilesPopupOpen(true)}>
										<Text style={styles.buttonText}>Clear Temp Files</Text>
									</Pressable>
								</View>

								<View style={styles.serverActionButtonRow}>
									<Pressable style={{
										...styles.button,
										...styles.serverActionButton
									}} onPress={() => setRequerySizeOfFilesPopupOpen(true)}>
										<Text style={styles.buttonText}>Requery Size of Files</Text>
									</Pressable>

									<Pressable style={{
										...styles.button,
										...styles.serverActionButton
									}} onPress={() => setGenerateThumbnailsPopupOpen(true)}>
										<Text style={styles.buttonText}>Generate Thumbnails</Text>
									</Pressable>
								</View>
							</View>

							{/* App Settings */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>App Settings</Text>

								<Pressable style={{
									...styles.button,
									...styles.buttonSecondary
								}} onPress={async () => {
									const permissions = await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

									if (!permissions.granted) return ToastAndroid.show(
										"The permission to the folder was not granted",
										ToastAndroid.SHORT
									);

									db.set("exportDownloadPath", permissions.directoryUri)

									ToastAndroid.show(
										"Successfully changed the folder",
										ToastAndroid.SHORT
									)
								}}>
									<Text style={styles.buttonText}>Change Export Download Folder</Text>
								</Pressable>

								<Pressable style={{
									...styles.button,
									...styles.buttonSecondary
								}} onPress={async () => {
									const permissions = await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

									if (!permissions.granted) return ToastAndroid.show(
										"The permission to the folder was not granted",
										ToastAndroid.SHORT
									);

									db.set("fileDownloadPath", permissions.directoryUri)

									ToastAndroid.show(
										"Successfully changed the folder",
										ToastAndroid.SHORT
									)
								}}>
									<Text style={styles.buttonText}>Change File Download Folder</Text>
								</Pressable>

								<Pressable style={{
									...styles.button,
									...styles.buttonDanger
								}} onPress={async () => {
									await db.del("url")
									await db.del("token")

									router.replace("/login")
								}}>
									<Text style={styles.buttonText}>Logout</Text>
								</Pressable>
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