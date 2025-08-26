import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import type { APIExports, APIVersion, DashURL } from "@/types/zipline";
import { getInstallerPackageNameSync } from "react-native-device-info";
import SkeletonColorPicker from "@/components/skeleton/ColorPicker";
import { View, Text, Pressable, ToastAndroid } from "react-native";
import { convertToBytes, guessExtension } from "@/functions/util";
import { knownInstallersName } from "@/constants/knownInstallers";
import SkeletonTextInput from "@/components/skeleton/TextInput";
import { getTokenWithToken } from "@/functions/zipline/auth";
import { UpdateContext } from "@/contexts/UpdateContext";
import VersionDisplay from "@/components/VersionDisplay";
import { getVersion } from "@/functions/zipline/version";
import { useState, useEffect, useContext } from "react";
import { useShareIntent } from "@/hooks/useShareIntent";
import SkeletonTable from "@/components/skeleton/Table";
import { version as appVersion } from "@/package.json";
import { AuthContext } from "@/contexts/AuthProvider";
import Skeleton from "@/components/skeleton/Skeleton";
import ColorPicker from "@/components/ColorPicker";
import { alignments } from "@/constants/settings";
import type { Mimetypes } from "@/types/mimetypes";
import * as ImagePicker from "expo-image-picker";
import UserAvatar from "@/components/UserAvatar";
import TextInput from "@/components/TextInput";
import * as FileSystem from "expo-file-system";
import * as Clipboard from "expo-clipboard";
import { styles } from "@/styles/settings";
import * as db from "@/functions/database";
import { useAuth } from "@/hooks/useAuth";
import Switch from "@/components/Switch";
import Button from "@/components/Button";
import Select from "@/components/Select";
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
} from "@/functions/zipline/user";

export default function UserSettings() {
	const {
		updateAuth,
		updateUser,
		user,
		avatar: currentAvatar,
	} = useContext(AuthContext);

	const {
		checkForUpdates,
		downloadUpdate,
		downloadPercentage,
		isDownloading,
		updateAvailable,
		isCheckingUpdate,
	} = useContext(UpdateContext);

	useAuth();
	useShareIntent();

	const installerPackage = getInstallerPackageNameSync();
	const installerPackageName =
		installerPackage in knownInstallersName
			? knownInstallersName[
					installerPackage as keyof typeof knownInstallersName
				]
			: __DEV__
				? "Development Instance"
				: installerPackage;

	const [mediaLibraryPermission, requestMediaLibraryPermission] =
		ImagePicker.useMediaLibraryPermissions();
	const [cameraPermission, requestCameraPermission] =
		ImagePicker.useCameraPermissions();

	const [updateAlertDisabled, setUpdateAlertDisabled] = useState<boolean>(
		db.get("disableUpdateAlert") === "true",
	);

	// const [user, setUser] = useState<APISelfUser | null>(null);
	const [token, setToken] = useState<string | null>(null);
	// const [currentAvatar, setCurrentAvatar] = useState<string | undefined>(
	// 	undefined,
	// );
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

	const [showAvatarPopup, setShowAvatarPopup] = useState<boolean>(false);

	const [ziplineVersion, setZiplineVersion] = useState<APIVersion | null>(null);

	const url = db.get("url") as DashURL;

	useEffect(() => {
		(async () => {
			const token = await getTokenWithToken();
			const exports = await getUserExports();
			const zeroByteFiles = await getZeroByteFiles();
			const versionData = await getVersion();

			setToken(typeof token === "string" ? null : token.token);
			setExports(typeof exports === "string" ? null : exports);
			setZeroByteFiles(
				typeof zeroByteFiles === "string" ? 0 : zeroByteFiles.files.length,
			);
			setZiplineVersion(typeof versionData === "string" ? null : versionData);
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

				<Popup
					hidden={!showAvatarPopup}
					onClose={() => {
						setShowAvatarPopup(false);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Select Avatar</Text>

						<Button
							color="transparent"
							borderWidth={2}
							borderColor="#222c47"
							margin={{
								top: 5,
							}}
							rippleColor="gray"
							text="Upload Image"
							onPress={async () => {
								if (
									mediaLibraryPermission?.status ===
									ImagePicker.PermissionStatus.UNDETERMINED
								)
									await requestMediaLibraryPermission();

								if (
									mediaLibraryPermission?.status ===
									ImagePicker.PermissionStatus.DENIED
								) {
									return ToastAndroid.show(
										"Media Library permission is required to select an avatar",
										ToastAndroid.LONG,
									);
								}

								const output = await ImagePicker.launchImageLibraryAsync({
									mediaTypes: ["images"],
									allowsEditing: true,
									allowsMultipleSelection: false,
									quality: 1,
									aspect: [1, 1],
									base64: true,
									defaultTab: "photos",
									exif: false,
								});

								if (output.canceled || output.assets.length <= 0) {
									setAvatar(undefined);
									setAvatarName(null);

									return;
								}

								const image = output.assets[0];

								const imageURI = image.uri;

								const fileInfo = await FileSystem.getInfoAsync(imageURI);

								if (!fileInfo.exists) return;

								const base64Data = image.base64 as string;

								const extension = imageURI.split(".").pop();
								const mimeType =
									image.mimeType ||
									guessExtension(extension as Mimetypes[keyof Mimetypes]);

								const avatarDataURI = `data:${mimeType};base64,${base64Data}`;

								setAvatar(avatarDataURI || undefined);

								const filename =
									image.fileName || imageURI.split("/").pop() || "avatar.png";

								setAvatarName(filename);
								setShowAvatarPopup(false);
							}}
						/>

						<Button
							color="transparent"
							borderWidth={2}
							borderColor="#222c47"
							margin={{
								top: 5,
							}}
							rippleColor="gray"
							text="Take Picture"
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
										"Camera permission is required to take a picture",
										ToastAndroid.LONG,
									);
								}

								const output = await ImagePicker.launchCameraAsync({
									mediaTypes: ["images"],
									allowsEditing: true,
									allowsMultipleSelection: false,
									quality: 1,
									aspect: [1, 1],
									base64: true,
									exif: false,
								});

								if (output.canceled || output.assets.length <= 0) {
									setAvatar(undefined);
									setAvatarName(null);

									return;
								}

								const image = output.assets[0];

								const imageURI = image.uri;

								const fileInfo = await FileSystem.getInfoAsync(imageURI);

								if (!fileInfo.exists) return;

								const base64Data = image.base64 as string;

								const extension = imageURI.split(".").pop();
								const mimeType =
									image.mimeType ||
									guessExtension(extension as Mimetypes[keyof Mimetypes]);

								const avatarDataURI = `data:${mimeType};base64,${base64Data}`;

								setAvatar(avatarDataURI || undefined);

								const filename =
									image.fileName || imageURI.split("/").pop() || "avatar.png";

								setAvatarName(filename);
								setShowAvatarPopup(false);
							}}
						/>
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
										description="Leave blank to keep the same password"
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
										setShowAvatarPopup(true);
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

												await updateUser();

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

											await updateUser();

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
								<Text style={styles.headerDescription}>
									All text fields support using variables.
								</Text>

								<Switch
									title="Enable View Routes"
									value={viewEnabled || false}
									onValueChange={() => setViewEnabled((prev) => !prev)}
									description="Enable viewing files through customizable view-routes"
								/>

								<Switch
									title="Show Mimetype"
									disabled={!viewEnabled}
									value={viewShowMimetype || false}
									onValueChange={() => setViewShowMimetype((prev) => !prev)}
									description="Show the mimetype of the file in the view-route"
								/>

								<TextInput
									title="View Content:"
									description="Change the content within view-routes. Most HTML is valid, while the use of JavaScript is unavailable."
									disabled={!viewEnabled}
									disableContext={!viewEnabled}
									multiline
									inputStyle={styles.multilineTextInput}
									onValueChange={(content) => setViewContent(content)}
									value={viewContent || ""}
									placeholder="This is my file"
								/>

								<Select
									title="View Content Alignment:"
									description="Change the alignment of the content within view-routes"
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
									description="Enable the following embed properties. These properties take advantage of OpenGraph tags. View routes will need to be enabled for this to work."
									disabled={!viewEnabled}
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
											rows={exports.map((zlExport) => {
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

												return [id, startedOn, files, size, actions];
											})}
										/>
									</View>
								</View>
							)}

							{/* Server Actions */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Server Actions</Text>
								<Text style={styles.headerDescription}>
									Helpful scripts and tools for server management.
								</Text>

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

								<Text style={styles.subHeaderText}>
									App Source: {installerPackageName}
								</Text>

								{ziplineVersion && (
									<View style={styles.versionContainer}>
										<Text style={styles.subHeaderText}>Zipline Version: </Text>
										<VersionDisplay versionData={ziplineVersion} />
									</View>
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

								{updateAvailable ? (
									<Button
										onPress={async () => {
											await downloadUpdate();
										}}
										color={isDownloading ? "#373d79" : "#323ea8"}
										text={
											isDownloading
												? `Downloading Update... ${downloadPercentage.toFixed(2)}%`
												: "Download Update"
										}
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
												`${update.available ? "" : "No "}Update Avaible${update.newVersion ? ` (v${appVersion} -> v${update.newVersion})` : ""}`,
												ToastAndroid.SHORT,
											);
										}}
										color={isCheckingUpdate ? "#373d79" : "#323ea8"}
										text={
											isCheckingUpdate
												? "Checking for Updates..."
												: "Check for Updates"
										}
										disabled={isCheckingUpdate}
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

										await updateAuth();
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
					<View style={styles.settingsContainer}>
						<KeyboardAwareScrollView style={styles.scrollView}>
							{/* User Info */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>User Info</Text>
								<Skeleton height={14} width="60%" />

								<SkeletonTextInput
									title="Token:"
									sideButtonIcon="content-copy"
									skeletonWidth="80%"
								/>

								<SkeletonTextInput title="Username:" skeletonWidth={50} />

								<SkeletonTextInput
									title="Password:"
									description="Leave blank to keep the same password"
									skeletonWidth="40%"
								/>

								<Button
									disabled
									onPress={() => {}}
									color="#373d79"
									textColor="gray"
									text="Save"
									icon="save"
									iconColor="gray"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Avatar */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Avatar</Text>

								<Button
									color="transparent"
									disabled
									borderWidth={2}
									borderColor="#222c47"
									margin={{
										top: 5,
									}}
									textColor="gray"
									text="Select an Avatar..."
									onPress={async () => {}}
								/>

								<View style={styles.avatarPreviewContainer}>
									<Text style={styles.avatarPreviewHeader}>Avatar Preview</Text>
									<Skeleton width={150} height={50} />
								</View>

								<Button
									text="Save"
									color="#181c28"
									textColor="gray"
									height="auto"
									width="28.33%"
									margin={{
										left: "2.5%",
										right: "2.5%",
										top: 10,
									}}
									disabled
									onPress={() => {}}
								/>
							</View>

							{/* Viewing Files */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Viewing Files</Text>
								<Text style={styles.headerDescription}>
									All text fields support using variables.
								</Text>

								<Switch
									title="Enable View Routes"
									value={false}
									disabled
									onValueChange={() => {}}
									description="Enable viewing files through customizable view-routes"
								/>

								<Switch
									title="Show Mimetype"
									disabled
									value={false}
									onValueChange={() => {}}
									description="Show the mimetype of the file in the view-route"
								/>

								<SkeletonTextInput
									title="View Content:"
									description="Change the content within view-routes. Most HTML is valid, while the use of JavaScript is unavailable."
									skeletonWidth="50%"
								/>

								<Select
									title="View Content Alignment:"
									description="Change the alignment of the content within view-routes"
									disabled
									data={alignments}
									onSelect={() => {}}
									placeholder="Select Alignment..."
								/>

								<Switch
									title="Embed"
									description="Enable the following embed properties. These properties take advantage of OpenGraph tags. View routes will need to be enabled for this to work."
									disabled
									value={false}
									onValueChange={() => {}}
								/>

								<SkeletonTextInput title="Embed Title:" skeletonWidth="30%" />

								<SkeletonTextInput
									title="Embed Description:"
									skeletonWidth="40%"
								/>

								<SkeletonTextInput
									title="Embed Site Name:"
									skeletonWidth="40%"
								/>

								{/* --remember TO DO */}
								<SkeletonColorPicker title="Embed Color" />
								{/* TO DO */}

								<Button
									onPress={() => handleSave("viewingFiles")}
									color="#373d79"
									disabled
									text="Save"
									icon="save"
									textColor="gray"
									iconColor="gray"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Export Files */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Export Files</Text>

								<Button
									onPress={async () => {}}
									color="#373d79"
									disabled
									text="New Export"
									textColor="gray"
									margin={{
										top: 10,
									}}
								/>

								<View style={styles.exportsContainer}>
									<SkeletonTable
										headerRow={["ID", "Started On", "Files", "Size", "Actions"]}
										rowWidth={[230, 130, 60, 90, 90]}
										rows={[...Array(4).keys()].map(() => {
											return [170, 80, 30, 50, 70];
										})}
										disableAnimations
									/>
								</View>
							</View>

							{/* Server Actions */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Server Actions</Text>
								<Text style={styles.headerDescription}>
									Helpful scripts and tools for server management.
								</Text>

								<View style={styles.serverActionButtonRow}>
									<Button
										onPress={async () => {}}
										color="#373d79"
										textColor="gray"
										disabled
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
										color="#373d79"
										textColor="gray"
										disabled
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
										color="#373d79"
										textColor="gray"
										disabled
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
										color="#373d79"
										textColor="gray"
										disabled
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

								<View
									style={{
										flexDirection: "row",
									}}
								>
									<Text style={styles.subHeaderText}>App Version: </Text>
									<Skeleton width={45} height={15} />
								</View>

								<View
									style={{
										flexDirection: "row",
									}}
								>
									<Text style={styles.subHeaderText}>Zipline Version: </Text>
									<Skeleton width={40} height={15} />
								</View>

								<Switch
									title="Disable Update Alert"
									value={false}
									disabled
									onValueChange={() => {}}
								/>

								<Button
									onPress={async () => {}}
									color="#373d79"
									text="Check for Updates"
									textColor="gray"
									disabled
									margin={{
										top: 10,
									}}
								/>

								<Button
									onPress={() => {}}
									color="#181c28"
									disabled
									text="Change Export Download Folder"
									textColor="gray"
									margin={{
										top: 10,
									}}
								/>

								<Button
									onPress={() => {}}
									color="#181c28"
									disabled
									textColor="gray"
									text="Change File Download Folder"
									margin={{
										top: 10,
									}}
								/>

								<Button
									onPress={() => {}}
									color="#181c28"
									textColor="gray"
									disabled
									text="Change Updates Download Folder"
									margin={{
										top: 10,
									}}
								/>

								<Button
									onPress={() => {}}
									color="#CF423877"
									text="Logout"
									disabled
									textColor="gray"
									margin={{
										top: 10,
									}}
								/>
							</View>
						</KeyboardAwareScrollView>
					</View>
				)}
			</View>
		</View>
	);
}
