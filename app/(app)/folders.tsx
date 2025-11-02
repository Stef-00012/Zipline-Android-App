import Button from "@/components/Button";
import LargeFolderView from "@/components/LargeFolderView";
import Popup from "@/components/Popup";
import Skeleton from "@/components/skeleton/Skeleton";
import SkeletonTable from "@/components/skeleton/Table";
import Switch from "@/components/Switch";
import Table from "@/components/Table";
import TextInput from "@/components/TextInput";
import { searchKeyNames } from "@/constants/folders";
import * as db from "@/functions/database";
import { timeDifference } from "@/functions/util";
import {
	createFolder,
	deleteFolder,
	editFolder,
	getFolders,
} from "@/functions/zipline/folders";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import { styles } from "@/styles/folders";
import type { APIFolders, DashURL } from "@/types/zipline";
import * as Clipboard from "expo-clipboard";
import { type ExternalPathString, Link, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ScrollView, Text, ToastAndroid, View } from "react-native";

export type FolderActions =
	| "viewFiles"
	| "visibility"
	| "uploadPolicy"
	| "edit"
	| "copyUrl"
	| "delete";

export default function Folders() {
	const router = useRouter();

	useAuth();
	useShareIntent();

	const foldersCompactView = db.get("foldersCompactView");

	const [showSearch, setShowSearch] = useState<boolean>(false);
	const [searchTerm, setSearchTerm] = useState<string>("");
	const [searchPlaceholder, setSearchPlaceholder] = useState<string>("");
	const [searchKey, setSearchKey] = useState<"name" | "id" | "files">("name");

	const [folders, setFolders] = useState<APIFolders | null>(null);

	const [createNewFolder, setCreateNewFolder] = useState<boolean>(false);

	const [newFolderName, setNewFolderName] = useState<string | null>(null);
	const [newFolderPublic, setNewFolderPublic] = useState<boolean>(false);

	const [newFolderError, setNewFolderError] = useState<string | null>(null);

	const [folderToEdit, setFolderToEdit] = useState<APIFolders[0] | null>(null);

	const [editFolderName, setEditFolderName] = useState<string | undefined>(
		undefined,
	);

	const [compactModeEnabled, setCompactModeEnabled] = useState<boolean>(
		foldersCompactView === "true",
	);

	const [editFolderError, setEditFolderError] = useState<string | null>(null);

	const dashUrl = db.get("url") as DashURL | null;

	const [sortKey, setSortKey] = useState<{
		id:
			| "name"
			| "public"
			| "allowUploads"
			| "createdAt"
			| "updatedAt"
			| "files"
			| "id";
		sortOrder: "asc" | "desc";
	}>({
		id: "createdAt",
		sortOrder: "asc",
	});

	useEffect(() => {
		(async () => {
			const folders = await getFolders();

			setFolders(typeof folders === "string" ? null : folders);
		})();
	}, []);

	// biome-ignore lint/correctness/useExhaustiveDependencies: search term should be resetted when search key changes
	useEffect(() => {
		setSearchPlaceholder("");
	}, [searchKey]);

	async function onAction(type: FolderActions, folder: APIFolders[0]) {
		switch (type) {
			case "viewFiles": {
				const folderId = folder.id;

				return router.push(`/files?folderId=${folderId}`);
			}

			case "copyUrl": {
				const urlDest = `${dashUrl}/folder/${folder.id}`;

				const saved = await Clipboard.setStringAsync(urlDest);

				if (saved)
					return ToastAndroid.show(
						"Folder URL copied to clipboard",
						ToastAndroid.SHORT,
					);

				return ToastAndroid.show(
					"Failed to paste to the clipboard",
					ToastAndroid.SHORT,
				);
			}

			case "visibility": {
				const folderId = folder.id;

				const success = await editFolder(folderId, {
					isPublic: !folder.public,
				});

				if (typeof success === "string")
					return ToastAndroid.show(
						`Failed to update the folder "${folder.name}"`,
						ToastAndroid.SHORT,
					);

				const newFolders = await getFolders();

				setFolders(typeof newFolders === "string" ? null : newFolders);

				return ToastAndroid.show(
					`Updated the folder "${folder.name}"'s visibility`,
					ToastAndroid.SHORT,
				);
			}

			case "uploadPolicy": {
				const folderId = folder.id;

				const success = await editFolder(folderId, {
					allowUploads: !folder.allowUploads,
				});

				if (typeof success === "string")
					return ToastAndroid.show(
						`Failed to update the folder "${folder.name}"`,
						ToastAndroid.SHORT,
					);

				const newFolders = await getFolders();

				setFolders(typeof newFolders === "string" ? null : newFolders);

				return ToastAndroid.show(
					`Updated the folder "${folder.name}"'s upload policy`,
					ToastAndroid.SHORT,
				);
			}

			case "edit": {
				setFolderToEdit(folder);
				setEditFolderName(folder.name);

				return;
			}

			case "delete": {
				const folderId = folder.id;

				const success = await deleteFolder(folderId);

				if (typeof success === "string")
					return ToastAndroid.show(
						`Failed to delete the folder "${folder.name}"`,
						ToastAndroid.SHORT,
					);

				const newFolders = await getFolders();

				setFolders(typeof newFolders === "string" ? null : newFolders);

				return ToastAndroid.show(
					`Deleted the folder "${folder.name}"`,
					ToastAndroid.SHORT,
				);
			}
		}
	}

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<Popup
					hidden={!createNewFolder}
					onClose={() => {
						setCreateNewFolder(false);
						setNewFolderName(null);
						setNewFolderPublic(false);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Create Folder</Text>
						{newFolderError && (
							<Text style={styles.errorText}>{newFolderError}</Text>
						)}

						<TextInput
							title="Name:"
							onValueChange={(content) => {
								setNewFolderName(content);
							}}
							value={newFolderName || ""}
							placeholder="myFolder"
						/>

						<Switch
							onValueChange={() => setNewFolderPublic((prev) => !prev)}
							description="Public folders are visible to everyone"
							value={newFolderPublic}
							title="Public"
						/>

						<Button
							onPress={async () => {
								setNewFolderError(null);

								if (!newFolderName || newFolderName.length <= 0)
									return setNewFolderError("Please insert a folder name");

								const createdFolder = await createFolder(
									newFolderName,
									newFolderPublic,
								);

								if (typeof createdFolder === "string")
									return setNewFolderError(createdFolder);

								setNewFolderName(null);
								setNewFolderPublic(false);

								const newFolders = await getFolders();

								setFolders(typeof newFolders === "string" ? null : newFolders);

								setCreateNewFolder(false);
							}}
							text="Create"
							color="#323ea8"
							containerStyle={{
								marginTop: 5,
							}}
						/>
					</View>
				</Popup>

				<Popup
					hidden={!folderToEdit}
					onClose={() => {
						setFolderToEdit(null);
						setEditFolderName(undefined);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Edit Folder Name</Text>
						{editFolderError && (
							<Text style={styles.errorText}>{editFolderError}</Text>
						)}

						{folderToEdit && (
							<View>
								<TextInput
									title="New Folder Name:"
									onValueChange={(content) => {
										setEditFolderName(content);
									}}
									value={editFolderName || ""}
									placeholder="myFolder"
								/>

								<Button
									onPress={async () => {
										setEditFolderError(null);

										if (!editFolderName || editFolderName.length <= 0)
											return setEditFolderError("Please insert a folder name");

										const folderId = folderToEdit.id;

										const editedFolder = await editFolder(folderId, {
											name: editFolderName,
										});

										if (typeof editedFolder === "string")
											return setEditFolderError(editedFolder);

										setEditFolderName(undefined);

										const newFolders = await getFolders();

										setFolders(
											typeof newFolders === "string" ? null : newFolders,
										);

										setFolderToEdit(null);
									}}
									text="Save"
									color="#323ea8"
									containerStyle={{
										marginTop: 10,
									}}
								/>
							</View>
						)}
					</View>
				</Popup>

				<View style={styles.header}>
					<Text style={styles.headerText}>Folders</Text>
					<View style={styles.headerButtons}>
						<Button
							onPress={() => {
								setCreateNewFolder(true);
							}}
							icon="create_new_folder"
							color="transparent"
							iconColor={folders && dashUrl ? "#2d3f70" : "#2d3f7055"}
							buttonStyle={{
								padding: 4,
							}}
							containerStyle={{
								borderColor: "#222c47",
								borderWidth: 2,
								marginHorizontal: 2
							}}
							iconSize={30}
							rippleColor="#283557"
							disabled={!folders || !dashUrl}
						/>

						<Button
							onPress={() => {
								db.set(
									"foldersCompactView",
									compactModeEnabled ? "false" : "true",
								);

								setCompactModeEnabled((prev) => !prev);
							}}
							icon={compactModeEnabled ? "view_module" : "view_agenda"}
							color="transparent"
							iconColor={folders && dashUrl ? "#2d3f70" : "#2d3f7055"}
							
							buttonStyle={{
								padding: 4,
							}}
							containerStyle={{
								borderColor: "#222c47",
								borderWidth: 2,
								marginHorizontal: 2
							}}
							iconSize={30}
							rippleColor="#283557"
							disabled={!folders || !dashUrl}
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
								buttonStyle={{
									padding: 5,
								}}
								containerStyle={{
									width: 30,
									height: 30,
								}}
							/>
						</View>

						<TextInput
							placeholder="Search..."
							defaultValue={searchPlaceholder}
							onValueChange={(text) => setSearchPlaceholder(text)}
							keyboardType={searchKey === "files" ? "numeric" : "default"}
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
					<View style={{ ...styles.foldersContainer, flex: 1 }}>
						{folders && dashUrl ? (
							// biome-ignore lint/complexity/noUselessFragments: The fragment is required
							<>
								{compactModeEnabled ? (
									<Table
										headerRow={[
											{
												row: "Name",
												id: "name",
												sortable: true,
												searchable: true,
											},
											{
												row: "Public",
												id: "public",
												sortable: true,
											},
											{
												row: "Uploads?",
												id: "allowUploads",
												sortable: true,
											},
											{
												row: "Created",
												id: "createdAt",
												sortable: true,
											},
											{
												row: "Last Updated At",
												id: "updatedAt",
												sortable: true,
											},
											{
												row: "Files",
												id: "files",
												sortable: true,
												searchable: true,
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
										rowWidth={[140, 90, 110, 140, 150, 100, 220, 252]}
										rows={folders
											.filter((folder) => {
												const filterKey =
													searchKey === "files"
														? folder[searchKey].length
														: folder[searchKey];

												return String(filterKey)
													.toLowerCase()
													.includes(searchTerm.toLowerCase());
											})
											.sort((a, b) => {
												const compareKeyA =
													sortKey.id === "createdAt" ||
													sortKey.id === "updatedAt"
														? new Date(a[sortKey.id])
														: sortKey.id === "files"
															? a[sortKey.id].length
															: a[sortKey.id];
												const compareKeyB =
													sortKey.id === "createdAt" ||
													sortKey.id === "updatedAt"
														? new Date(b[sortKey.id])
														: sortKey.id === "files"
															? b[sortKey.id].length
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
											.map((folder) => {
												const name = folder.public ? (
													<Link
														key={folder.id}
														href={
															`${dashUrl}/folder/${folder.id}` as ExternalPathString
														}
														style={{
															...styles.rowText,
															...styles.link,
														}}
													>
														{folder.name}
													</Link>
												) : (
													<Text key={folder.id} style={styles.rowText}>
														{folder.name}
													</Text>
												);

												const isPublic = (
													<Text key={folder.id} style={styles.rowText}>
														{folder.public ? "Yes" : "No"}
													</Text>
												);

												const allowUploads = (
													<Text key={folder.id} style={styles.rowText}>
														{folder.allowUploads ? "Yes" : "No"}
													</Text>
												);

												const created = (
													<Text key={folder.id} style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(folder.createdAt),
														)}
													</Text>
												);

												const lastUpdatedAt = (
													<Text key={folder.id} style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(folder.updatedAt),
														)}
													</Text>
												);

												const files = (
													<Text key={folder.id} style={styles.rowText}>
														{folder.files.length}
													</Text>
												);

												const id = (
													<Text key={folder.id} style={styles.rowText}>
														{folder.id}
													</Text>
												);

												const actions = (
													<View key={folder.id} style={styles.actionsContainer}>
														<Button
															icon="folder_open"
															color="#323ea8"
															onPress={() => {
																onAction("viewFiles", folder);
															}}
															containerStyle={{
																width: 32,
																height: 32,
															}}
															iconStyle={{
																marginBottom: 7
															}}
															iconSize={20}
														/>

														<Button
															icon="content_copy"
															color={folder.public ? "#323ea8" : "#181c28"}
															iconColor={folder.public ? "white" : "#2a3952"}
															onPress={async () => {
																onAction("copyUrl", folder);
															}}
															disabled={!folder.public}
															iconSize={20}
															containerStyle={{
																width: 32,
																height: 32,
															}}
															iconStyle={{
																marginBottom: 7
															}}
														/>

														<Button
															icon={folder.public ? "lock_open" : "lock"}
															color={folder.public ? "#323ea8" : "#343a40"}
															iconSize={20}
															containerStyle={{
																width: 32,
																height: 32,
															}}
															iconStyle={{
																marginBottom: 7
															}}
															onPress={async () => {
																onAction("visibility", folder);
															}}
														/>

														<Button
															icon={folder.allowUploads ? "block" : "share"}
															color={
																folder.allowUploads ? "#323ea8" : "#343a40"
															}
															iconSize={20}
															containerStyle={{
																width: 32,
																height: 32,
															}}
															iconStyle={{
																marginBottom: 7
															}}
															onPress={async () => {
																onAction("uploadPolicy", folder);
															}}
														/>

														<Button
															icon="edit"
															color="#323ea8"
															onPress={async () => {
																onAction("edit", folder);
															}}
															iconSize={20}
															containerStyle={{
																width: 32,
																height: 32,
															}}
															iconStyle={{
																marginBottom: 7
															}}
														/>

														<Button
															onPress={async () => {
																onAction("delete", folder);
															}}
															color="#CF4238"
															icon="delete"
															iconSize={20}
															containerStyle={{
																width: 32,
																height: 32,
															}}
															iconStyle={{
																marginBottom: 7
															}}
														/>
													</View>
												);

												return [
													name,
													isPublic,
													allowUploads,
													created,
													lastUpdatedAt,
													files,
													id,
													actions,
												];
											})}
									/>
								) : (
									<ScrollView>
										{folders.map((folder) => (
											<LargeFolderView
												key={folder.id}
												folder={folder}
												dashUrl={dashUrl}
												onAction={onAction}
											/>
										))}
									</ScrollView>
								)}
							</>
						) : (
							// biome-ignore lint/complexity/noUselessFragments: The fragment is required
							<>
								{compactModeEnabled ? (
									<SkeletonTable
										headerRow={[
											"Name",
											"Public",
											"Uploads?",
											"Created",
											"Last Updated At",
											"Files",
											"ID",
											"Actions",
										]}
										rowWidth={[140, 90, 110, 140, 150, 100, 220, 252]}
										rows={[...Array(12).keys()].map(() => {
											return [80, 30, 30, 90, 90, 40, 120, 200];
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
