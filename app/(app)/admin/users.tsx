import { fileQuotaTypes, searchKeyNames, userRoles } from "@/constants/users";
import { ScrollView, Text, View, ToastAndroid } from "react-native";
import { getFileDataURI, timeDifference } from "@/functions/util";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { useShareIntent } from "@/hooks/useShareIntent";
import * as DocumentPicker from "expo-document-picker";
import LargeUserView from "@/components/LargeUserView";
import * as FileSystem from "expo-file-system";
import TextInput from "@/components/TextInput";
import { styles } from "@/styles/admin/users";
import { useEffect, useState } from "react";
import * as db from "@/functions/database";
import { useAuth } from "@/hooks/useAuth";
import Button from "@/components/Button";
import Select from "@/components/Select";
import Popup from "@/components/Popup";
import Table from "@/components/Table";
import { router } from "expo-router";
import { Image } from "expo-image";
import {
	createUser,
	deleteUser,
	editUser,
	type EditUserOptions,
	getUsers,
} from "@/functions/zipline/users";
import type {
	APIUser,
	APIUserQuota,
	APIUsersNoIncl,
	DashURL,
} from "@/types/zipline";
import SkeletonTable from "@/components/skeleton/Table";
import Skeleton from "@/components/skeleton/Skeleton";

export type UserActions = "viewFiles" | "edit" | "delete";

export default function Users() {
	useAuth("ADMIN");
	useShareIntent();

	const usersCompactView = db.get("usersCompactView");

	const [showSearch, setShowSearch] = useState<boolean>(false);
	const [searchTerm, setSearchTerm] = useState<string>("");
	const [searchPlaceholder, setSearchPlaceholder] = useState<string>("");
	const [searchKey, setSearchKey] = useState<"username" | "id">("username");

	const [users, setUsers] = useState<APIUsersNoIncl | null>(null);

	const [userToEdit, setUserToEdit] = useState<APIUsersNoIncl[0] | null>(null);

	const [editUsername, setEditUsername] = useState<string | null>(null);
	const [editPassword, setEditPassword] = useState<string | null>(null);
	const [editAvatar, setEditAvatar] = useState<string>();
	const [editAvatarName, setEditAvatarName] = useState<string | null>(null);
	const [editRole, setEditRole] =
		useState<Exclude<APIUser["role"], "SUPERADMIN">>("USER");

	const [editFileQuotaType, setEditFileQuotaType] = useState<
		APIUserQuota["filesQuota"] | "NONE"
	>("BY_BYTES");
	const [editMaxBytes, setEditMaxBytes] = useState<
		APIUserQuota["maxBytes"] | null
	>(null);
	const [editMaxFileCount, setEditMaxFileCount] = useState<
		APIUserQuota["maxFiles"] | null
	>(null);
	const [editMaxUrls, setEditMaxUrls] = useState<
		APIUserQuota["maxUrls"] | null
	>(null);

	const [editError, setEditError] = useState<string>();

	const [createNewUser, setCreateNewUser] = useState<boolean>(false);

	const [newUserUsername, setNewUserUsername] = useState<string | null>(null);
	const [newUserPassword, setNewUserPassword] = useState<string | null>(null);
	const [newUserAvatar, setNewUserAvatar] = useState<string>();
	const [newUserAvatarName, setNewUserAvatarName] = useState<string | null>(
		null,
	);
	const [newUserRole, setNewUserRole] =
		useState<Exclude<APIUser["role"], "SUPERADMIN">>("USER");

	const [newUserError, setNewUserError] = useState<string>();

	const [userToDelete, setUserToDelete] = useState<APIUsersNoIncl[0] | null>(
		null,
	);
	const [userToDeleteData, setUserToDeleteData] = useState<
		APIUsersNoIncl[0] | null
	>(null);

	const [sortKey, setSortKey] = useState<{
		id: "username" | "role" | "createdAt" | "updatedAt" | "id";
		sortOrder: "asc" | "desc";
	}>({
		id: "createdAt",
		sortOrder: "asc",
	});

	const [compactModeEnabled, setCompactModeEnabled] = useState<boolean>(
		usersCompactView === "true",
	);

	const dashUrl = db.get("url") as DashURL | null;

	useEffect(() => {
		(async () => {
			const users = await getUsers(true);

			setUsers(typeof users === "string" ? null : users);
		})();
	}, []);

	useEffect(() => {
		if (userToEdit) {
			setEditUsername(userToEdit.username);
			setEditAvatar(userToEdit.avatar || undefined);
			setEditAvatarName(userToEdit.avatar ? "avatar.png" : null);
			setEditRole(userToEdit.role as Exclude<APIUser["role"], "SUPERADMIN">);

			if (userToEdit.quota) {
				setEditFileQuotaType(userToEdit.quota.filesQuota);
				setEditMaxBytes(userToEdit.quota.maxBytes);
				setEditMaxFileCount(userToEdit.quota.maxFiles);
				setEditMaxUrls(userToEdit.quota.maxUrls);
			} else {
				setEditFileQuotaType("NONE");
				setEditMaxBytes(null);
				setEditMaxFileCount(null);
				setEditMaxUrls(null);
			}
		}
	}, [userToEdit]);

	// biome-ignore lint/correctness/useExhaustiveDependencies: search term should be resetted when search key changes
	useEffect(() => {
		setSearchPlaceholder("");
	}, [searchKey]);

	async function onAction(type: UserActions, user: APIUsersNoIncl[0]) {
		switch (type) {
			case "viewFiles": {
				const userId = user.id;

				return router.replace(`/files?id=${userId}`);
			}

			case "edit": {
				const userId = user.id;

				return setUserToEdit(users?.find((usr) => usr.id === userId) || null);
			}

			case "delete": {
				return setUserToDelete(user);
			}
		}
	}

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<Popup
					hidden={!userToEdit}
					onClose={() => {
						setUserToEdit(null);

						setEditUsername(null);
						setEditAvatar(undefined);
						setEditAvatarName(null);
						setEditRole("USER");

						setEditFileQuotaType("NONE");
						setEditMaxBytes(null);
						setEditMaxFileCount(null);
						setEditMaxUrls(null);
					}}
				>
					{userToEdit && (
						<View style={styles.popupContent}>
							<Text style={styles.mainHeaderText}>
								Edit {userToEdit.username}
							</Text>
							{editError && <Text style={styles.errorText}>{editError}</Text>}

							<TextInput
								title="Username:"
								onValueChange={(content) => {
									setEditUsername(content || null);
								}}
								value={editUsername || ""}
								placeholder="myUser"
							/>

							<TextInput
								title="Password:"
								onValueChange={(content) => {
									setEditPassword(content || null);
								}}
								value={editPassword || ""}
								password
								placeholder="google"
							/>

							<Text style={styles.popupHeaderText}>Avatar:</Text>
							<Button
								color="transparent"
								borderWidth={2}
								borderColor="#222c47"
								margin={{
									top: 5,
								}}
								rippleColor="gray"
								text={
									editAvatar
										? (editAvatarName as string)
										: "Select an Avatar..."
								}
								onPress={async () => {
									const output = await DocumentPicker.getDocumentAsync({
										type: ["image/png", "image/jpeg", "image/jpg"],
										copyToCacheDirectory: true,
									});

									if (output.canceled || !output.assets) {
										setEditAvatar(undefined);
										setEditAvatarName(null);

										return;
									}

									const fileURI = output.assets[0].uri;

									const fileInfo = await FileSystem.getInfoAsync(fileURI);

									if (!fileInfo.exists) return;

									const avatarDataURI = await getFileDataURI(fileURI);

									setEditAvatar(avatarDataURI || undefined);

									const filename = fileURI.split("/").pop() || "avatar.png";

									setEditAvatarName(filename);
								}}
							/>

							<Text style={styles.popupHeaderText}>Role:</Text>
							<Select
								data={userRoles}
								onSelect={(selectedRole) => {
									if (selectedRole.length <= 0) return;

									setEditRole(
										selectedRole[0].value as Exclude<
											APIUser["role"],
											"SUPERADMIN"
										>,
									);
								}}
								placeholder="Select Role..."
								defaultValue={userRoles.find(
									(userRole) => userRole.value === editRole,
								)}
							/>

							<Text style={styles.mainHeaderText}>Quota</Text>

							<Text style={styles.popupHeaderText}>File Quota Type:</Text>
							<Select
								data={fileQuotaTypes}
								onSelect={(selectedQuota) => {
									if (selectedQuota.length <= 0) return;

									if (selectedQuota[0].value === "NONE") {
										setEditMaxBytes(null);
										setEditMaxFileCount(null);
										setEditFileQuotaType(
											selectedQuota[0].value as typeof editFileQuotaType,
										);
									}

									setEditFileQuotaType(
										selectedQuota[0].value as APIUserQuota["filesQuota"],
									);
								}}
								placeholder="Select Quota Type..."
								defaultValue={fileQuotaTypes.find(
									(userQuota) => userQuota.value === editFileQuotaType,
								)}
							/>

							{["BY_FILES", "BY_BYTES"].includes(editFileQuotaType) && (
								<View>
									{editFileQuotaType === "BY_BYTES" && (
										<View>
											<TextInput
												title="Max Bytes:"
												onValueChange={(content) => {
													setEditMaxBytes(content || null);
												}}
												value={editMaxBytes || ""}
												placeholder="2gb"
											/>
										</View>
									)}

									{editFileQuotaType === "BY_FILES" && (
										<View>
											<TextInput
												title="Max Files:"
												onValueChange={(content) => {
													setEditMaxFileCount(
														Math.abs(Number.parseInt(content)) || null,
													);
												}}
												value={editMaxFileCount ? String(editMaxFileCount) : ""}
												keyboardType="numeric"
												placeholder="20"
											/>
										</View>
									)}
								</View>
							)}

							<TextInput
								title="Max URLs:"
								onValueChange={(content) => {
									setEditMaxUrls(Math.abs(Number.parseInt(content)) || null);
								}}
								value={editMaxUrls ? String(editMaxUrls) : ""}
								placeholder="0"
							/>

							<Button
								color="#323ea8"
								text="Update"
								onPress={async () => {
									setEditError(undefined);

									if (!editUsername)
										return setEditError("Please insert a username");

									const userRole = editRole || "USER";

									const editUserOptions: EditUserOptions = {
										role: userRole,
										username: editUsername,
										avatar: editAvatar || undefined,
										password: editPassword || undefined,
										quota: {
											filesType: editFileQuotaType,
											maxBytes: editMaxBytes,
											maxFiles: editMaxFileCount,
											maxUrls: editMaxUrls,
										},
									};

									const editedUser = await editUser(
										userToEdit.id,
										editUserOptions,
									);

									if (typeof editedUser === "string")
										return setEditError(editedUser);

									ToastAndroid.show(
										`The user "${editedUser.username}" has been updated`,
										ToastAndroid.SHORT,
									);

									const updatedUsers = await getUsers(true);

									setUsers(
										typeof updatedUsers === "string" ? null : updatedUsers,
									);

									setEditUsername(null);
									setEditPassword(null);
									setEditAvatar(undefined);
									setEditRole("USER");

									setEditFileQuotaType("BY_BYTES");
									setEditMaxBytes(null);
									setEditMaxFileCount(null);
									setEditMaxUrls(null);

									setUserToEdit(null);
								}}
								margin={{
									top: 15,
								}}
							/>

							<Text style={styles.popupSubHeaderText}>
								Press outside to close this popup
							</Text>
						</View>
					)}
				</Popup>

				<Popup
					hidden={!createNewUser}
					onClose={() => {
						setCreateNewUser(false);

						setNewUserUsername(null);
						setNewUserPassword(null);
						setNewUserAvatar(undefined);
						setNewUserAvatarName(null);
						setNewUserRole("USER");
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Create User</Text>
						{newUserError && (
							<Text style={styles.errorText}>{newUserError}</Text>
						)}

						<TextInput
							title="Username:"
							onValueChange={(content) => {
								setNewUserUsername(content || null);
							}}
							value={newUserUsername || ""}
							placeholder="myUser"
						/>

						<TextInput
							title="Password:"
							onValueChange={(content) => {
								setNewUserPassword(content || null);
							}}
							value={newUserPassword || ""}
							password
							placeholder="google"
						/>

						<Text style={styles.popupHeaderText}>Avatar:</Text>
						<Button
							color="transparent"
							borderWidth={2}
							borderColor="#222c47"
							margin={{
								top: 5,
							}}
							rippleColor="gray"
							text={
								newUserAvatar
									? (newUserAvatarName as string)
									: "Select an Avatar..."
							}
							onPress={async () => {
								const output = await DocumentPicker.getDocumentAsync({
									type: ["image/png", "image/jpeg", "image/jpg"],
									copyToCacheDirectory: true,
								});

								if (output.canceled || !output.assets) {
									setNewUserAvatar(undefined);
									setNewUserAvatarName(null);

									return;
								}

								const fileURI = output.assets[0].uri;

								const fileInfo = await FileSystem.getInfoAsync(fileURI);

								if (!fileInfo.exists) return;

								const avatarDataURI = await getFileDataURI(fileURI);

								setNewUserAvatar(avatarDataURI || undefined);

								const filename = fileURI.split("/").pop() || "avatar.png";

								setNewUserAvatarName(filename);
							}}
						/>

						<Text style={styles.popupHeaderText}>Role:</Text>
						<Select
							data={userRoles}
							onSelect={(selectedRole) => {
								if (selectedRole.length <= 0) return;

								setNewUserRole(
									selectedRole[0].value as Exclude<
										APIUser["role"],
										"SUPERADMIN"
									>,
								);
							}}
							placeholder="Select Role..."
							defaultValue={userRoles.find(
								(userRole) => userRole.value === newUserRole,
							)}
						/>

						<Button
							onPress={async () => {
								setNewUserError(undefined);

								if (!newUserUsername)
									return setNewUserError("Please insert a username");
								if (!newUserPassword)
									return setNewUserError("Please insert a password");

								const userRole = newUserRole || "USER";

								const createdUser = await createUser(
									newUserUsername,
									newUserPassword,
									userRole,
									newUserAvatar,
								);

								if (typeof createdUser === "string")
									return setNewUserError(createdUser);

								ToastAndroid.show(
									`The user ${createdUser.username} has been created`,
									ToastAndroid.SHORT,
								);

								setNewUserUsername(null);
								setNewUserPassword(null);
								setNewUserAvatar(undefined);
								setNewUserAvatarName(null);
								setNewUserRole("USER");

								const newUsers = await getUsers(true);

								setUsers(typeof newUsers === "string" ? null : newUsers);

								setCreateNewUser(false);
							}}
							color="#323ea8"
							text="Create"
							margin={{
								top: 10,
							}}
						/>

						<Text style={styles.popupSubHeaderText}>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup
					hidden={!userToDelete}
					onClose={() => {
						setUserToDelete(null);
					}}
				>
					<View style={styles.popupContent}>
						{userToDelete && (
							<View>
								<Text style={styles.mainHeaderText}>
									Delete {userToDelete.username}?
								</Text>

								<Text style={styles.deleteWarningText}>
									Are you sure you want to delete{" "}
									<Text
										style={{
											fontWeight: "bold",
										}}
									>
										{userToDelete.username}
									</Text>
									? This action cannot be undone.
								</Text>

								<View style={styles.deleteActionButtonsContainer}>
									<Button
										onPress={() => {
											setUserToDelete(null);
										}}
										color="#323ea8"
										text="Cancel"
										width="40%"
										margin={{
											top: 15,
											right: 10,
											left: "auto",
										}}
									/>

									<Button
										onPress={() => {
											setUserToDeleteData(userToDelete);
											setUserToDelete(null);
										}}
										color="#CF4238"
										text="Delete"
										width="40%"
										margin={{
											top: 15,
											right: "auto",
											left: "auto",
										}}
									/>
								</View>

								<Text style={styles.popupSubHeaderText}>
									Press outside to close this popup
								</Text>
							</View>
						)}
					</View>
				</Popup>

				<Popup
					hidden={!userToDeleteData}
					onClose={() => {
						setUserToDeleteData(null);
					}}
				>
					<View style={styles.popupContent}>
						{userToDeleteData && (
							<View>
								<Text style={styles.mainHeaderText}>
									Delete {userToDeleteData.username}'s Data?
								</Text>

								<Text style={styles.deleteWarningText}>
									Would you like to delete{" "}
									<Text
										style={{
											fontWeight: "bold",
										}}
									>
										{userToDeleteData.username}
									</Text>
									's files and urls? This action cannot be undone.
								</Text>

								<Button
									onPress={async () => {
										const userId = userToDeleteData.id;

										const deletedUser = await deleteUser(userId, false);

										if (typeof deletedUser === "string")
											return ToastAndroid.show(
												`Failed to delete the user ${userToDeleteData.username}`,
												ToastAndroid.SHORT,
											);

										const newUsers = await getUsers(true);

										setUsers(typeof newUsers === "string" ? null : newUsers);

										ToastAndroid.show(
											`Successfully deleted the user ${userToDeleteData.username}`,
											ToastAndroid.SHORT,
										);

										setUserToDeleteData(null);
									}}
									color="#323ea8"
									text="No, keep everything & only delete user"
									margin={{
										top: 15,
									}}
								/>

								<Button
									onPress={async () => {
										const userId = userToDeleteData.id;

										const deletedUser = await deleteUser(userId, true);

										if (typeof deletedUser === "string")
											return ToastAndroid.show(
												`Failed to delete the user "${userToDeleteData.username}"`,
												ToastAndroid.SHORT,
											);

										const newUsers = await getUsers(true);

										setUsers(typeof newUsers === "string" ? null : newUsers);

										ToastAndroid.show(
											`Successfully deleted the user "${deletedUser.username}" and its data`,
											ToastAndroid.SHORT,
										);

										setUserToDeleteData(null);
									}}
									color="#CF4238"
									text="Yes, delete everything"
									margin={{
										top: 15,
									}}
								/>

								<Text style={styles.popupSubHeaderText}>
									Press outside to close this popup
								</Text>
							</View>
						)}
					</View>
				</Popup>

				<View style={styles.header}>
					<Text style={styles.headerText}>Users</Text>
					<View style={styles.headerButtons}>
						<Button
							onPress={() => {
								setCreateNewUser(true);
							}}
							icon="person-add"
							color="transparent"
							iconColor={users && dashUrl ? "#2d3f70" : "#2d3f7055"}
							borderColor="#222c47"
							borderWidth={2}
							iconSize={30}
							padding={4}
							rippleColor="#283557"
							disabled={!users || !dashUrl}
							margin={{
								left: 2,
								right: 2,
							}}
						/>

						<Button
							onPress={() => {
								db.set(
									"usersCompactView",
									compactModeEnabled ? "false" : "true",
								);

								setCompactModeEnabled((prev) => !prev);
							}}
							icon={compactModeEnabled ? "view-module" : "view-agenda"}
							color="transparent"
							iconColor={users && dashUrl ? "#2d3f70" : "#2d3f7055"}
							borderColor="#222c47"
							borderWidth={2}
							iconSize={30}
							padding={4}
							rippleColor="#283557"
							disabled={!users || !dashUrl}
							margin={{
								left: 2,
								right: 2,
							}}
						/>
					</View>
				</View>

				{showSearch && (
					<View style={styles.mainSearchContainer}>
						<View style={styles.searchContainer}>
							<Text style={styles.searchHeader}>
								Search by {searchKeyNames[searchKey]}
							</Text>

							<Button
								onPress={() => setShowSearch(false)}
								icon="close"
								color="#191b27"
								width={30}
								height={30}
								padding={5}
							/>
						</View>

						<TextInput
							placeholder="Search..."
							defaultValue={searchPlaceholder}
							onValueChange={(text) => setSearchPlaceholder(text)}
							onSubmitEditing={(event) => {
								const searchText = event.nativeEvent.text;

								setSearchTerm(searchText);
								setShowSearch(false);
							}}
							returnKeyType="search"
						/>
					</View>
				)}

				<View style={{ flex: 1 }}>
					<View style={{ ...styles.usersContainer, flex: 1 }}>
						{users && dashUrl ? (
							<>
								{compactModeEnabled ? (
									<Table
										headerRow={[
											{
												row: "Avatar",
											},
											{
												row: "Username",
												id: "username",
												sortable: true,
												searchable: true,
											},
											{
												row: "Role",
												id: "role",
												sortable: true,
											},
											{
												row: "Created",
												id: "createdAt",
												sortable: true,
											},
											{
												row: "Last Updated",
												id: "updatedAt",
												sortable: true,
											},
											{
												row: "ID",
												id: "id",
												sortable: true,
												searchable: true,
											},
											{
												row: "Actions",
											},
										]}
										sortKey={sortKey}
										onSearch={(key) => {
											setShowSearch(true);
											setSearchKey(key as typeof searchKey);
										}}
										onSortOrderChange={(key, order) => {
											setSortKey({
												id: key as typeof sortKey.id,
												sortOrder: order,
											});
										}}
										rowWidth={[80, 140, 100, 130, 140, 220, 130]}
										rows={users
											.filter((folder) => {
												const filterKey = folder[searchKey];

												return filterKey
													.toLowerCase()
													.includes(searchTerm.toLowerCase());
											})
											.sort((a, b) => {
												const compareKeyA =
													sortKey.id === "createdAt" ||
													sortKey.id === "updatedAt"
														? new Date(a[sortKey.id])
														: a[sortKey.id];

												const compareKeyB =
													sortKey.id === "createdAt" ||
													sortKey.id === "updatedAt"
														? new Date(b[sortKey.id])
														: b[sortKey.id];

												let result = 0;

												if (
													typeof compareKeyA === "string" &&
													typeof compareKeyB === "string"
												)
													result = compareKeyA.localeCompare(compareKeyB);
												else if (
													compareKeyA instanceof Date &&
													compareKeyB instanceof Date
												)
													result =
														compareKeyA.getTime() - compareKeyB.getTime();
												else result = Number(compareKeyA) - Number(compareKeyB);

												return sortKey.sortOrder === "desc" ? -result : result;
											})
											.map((user, index) => {
												const avatar = user.avatar ? (
													<Image
														key={user.id}
														source={{ uri: user.avatar }}
														style={styles.userAvatar}
														alt={`${user.username}'s avatar`}
													/>
												) : (
													<View key={user.id} style={styles.userAvatar}>
														<MaterialIcons
															name="person"
															size={30}
															color={"white"}
														/>
													</View>
												);

												const username = (
													<Text key={user.id} style={styles.rowText}>
														{user.username}
													</Text>
												);

												const role = (
													<Text key={user.id} style={styles.rowText}>
														{user.role.charAt(0).toUpperCase() +
															user.role.slice(1).toLowerCase()}
													</Text>
												);

												const created = (
													<Text key={user.id} style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(user.createdAt),
														)}
													</Text>
												);

												const lastUpdated = (
													<Text key={user.id} style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(user.updatedAt),
														)}
													</Text>
												);

												const id = (
													<Text key={user.id} style={styles.rowText}>
														{user.id}
													</Text>
												);

												const actions = (
													<View key={user.id} style={styles.actionsContainer}>
														<Button
															icon="folder-open"
															color="#323ea8"
															onPress={async () => {
																onAction("viewFiles", user);
															}}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>

														<Button
															icon="edit"
															color="#323ea8"
															onPress={() => {
																onAction("edit", user);
															}}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>

														<Button
															icon="delete"
															color="#CF4238"
															onPress={async () => {
																onAction("delete", user);
															}}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>
													</View>
												);

												return [
													avatar,
													username,
													role,
													created,
													lastUpdated,
													id,
													actions,
												];
											})}
									/>
								) : (
									<ScrollView>
										{users.map((user) => (
											<LargeUserView
												key={user.id}
												user={user}
												dashUrl={dashUrl}
												onAction={onAction}
											/>
										))}
									</ScrollView>
								)}
							</>
						) : (
							<>
								{compactModeEnabled ? (
									<SkeletonTable
										headerRow={[
											"Avatar",
											"Username",
											"Role",
											"Created",
											"Last Updated",
											"ID",
											"Actions",
										]}
										rowWidth={[80, 140, 100, 130, 140, 220, 130]}
										rows={[...Array(12).keys()].map(() => {
											return [50, 60, 50, 80, 80, 180, 100];
										})}
										rowHeight={55}
										disableAnimations
									/>
								) : (
									<ScrollView showsVerticalScrollIndicator={false}>
										{[...Array(4).keys()].map((index) => (
											<View
												key={index}
												style={{
													marginVertical: 5,
													marginHorizontal: 5,
												}}
											>
												<Skeleton width="100%" height={200} />
											</View>
										))}
									</ScrollView>
								)}
							</>
						)}
					</View>
				</View>
			</View>
		</View>
	);
}
