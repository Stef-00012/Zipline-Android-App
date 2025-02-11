import { type ExternalPathString, Link, useRouter } from "expo-router";
import { ScrollView, Text, View, ToastAndroid } from "react-native";
import type { APIFolders, DashURL } from "@/types/zipline";
import { Row, Table } from "react-native-reanimated-table";
import { useShareIntent } from "@/hooks/useShareIntent";
import { timeDifference } from "@/functions/util";
import TextInput from "@/components/TextInput";
import { useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import { styles } from "@/styles/folders";
import { useAuth } from "@/hooks/useAuth";
import Switch from "@/components/Switch";
import Button from "@/components/Button";
import Popup from "@/components/Popup";
import {
	createFolder,
	deleteFolder,
	editFolder,
	getFolders,
} from "@/functions/zipline/folders";

export default function Folders() {
	const router = useRouter();

	useAuth();
	useShareIntent();

	const [folders, setFolders] = useState<APIFolders | null>(null);

	const [createNewFolder, setCreateNewFolder] = useState<boolean>(false);

	const [newFolderName, setNewFolderName] = useState<string | null>(null);
	const [newFolderPublic, setNewFolderPublic] = useState<boolean>(false);

	const [newFolderError, setNewFolderError] = useState<string | null>(null);

	const dashUrl = db.get("url") as DashURL | null;

	useEffect(() => {
		(async () => {
			const folders = await getFolders();

			setFolders(typeof folders === "string" ? null : folders);
		})();
	}, []);

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
						/>
					</View>
				</Popup>

				{folders && dashUrl ? (
					<View style={{ flex: 1 }}>
						<View style={styles.header}>
							<Text style={styles.headerText}>Folders</Text>
							<View style={styles.headerButtons}>
								<Button
									onPress={() => {
										setCreateNewFolder(true);
									}}
									icon="create-new-folder"
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

						<View style={{ ...styles.foldersContainer, flex: 1 }}>
							<ScrollView showsHorizontalScrollIndicator={false} horizontal>
								<View>
									<Table>
										<Row
											data={["Name", "Public", "Created", "Actions"]}
											widthArr={[80, 50, 130, 150]}
											style={styles.tableHeader}
											textStyle={{
												...styles.rowText,
												...styles.headerRow,
											}}
										/>
									</Table>
									<ScrollView
										showsVerticalScrollIndicator={false}
										style={styles.tableVerticalScroll}
									>
										<Table>
											{folders.map((folder, index) => {
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

												const created = (
													<Text style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(folder.createdAt),
														)}
													</Text>
												);

												const actions = (
													<View style={styles.actionsContainer}>
														<Button
															icon="folder-open"
															color="#323ea8"
															onPress={() => {
																const folderId = folder.id;

																router.replace(`/files?folderId=${folderId}`);
															}}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>

														<Button
															icon="content-copy"
															color={folder.public ? "#323ea8" : "#181c28"}
															iconColor={folder.public ? "white" : "#2a3952"}
															onPress={async () => {
																const urlDest = `${dashUrl}/folder/${folder.id}`;

																const saved =
																	await Clipboard.setStringAsync(urlDest);

																if (saved)
																	return ToastAndroid.show(
																		"Folder URL copied to clipboard",
																		ToastAndroid.SHORT,
																	);

																return ToastAndroid.show(
																	"Failed to paste to the clipboard",
																	ToastAndroid.SHORT,
																);
															}}
															disabled={!folder.public}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>

														<Button
															onPress={async () => {
																const folderId = folder.id;

																const success = await editFolder(
																	folderId,
																	!folder.public,
																);

																if (typeof success === "string")
																	return ToastAndroid.show(
																		`Failed to update the folder "${folder.name}"`,
																		ToastAndroid.SHORT,
																	);

																ToastAndroid.show(
																	`Updated the folder "${folder.name}"'s visibility`,
																	ToastAndroid.SHORT,
																);

																const folderIndex = folders.findIndex(
																	(fold) => folder.id === fold.id,
																);

																const newFolders = [...folders];
																newFolders[folderIndex].public = !folder.public;

																setFolders(newFolders);
															}}
															color={folder.public ? "#323ea8" : "#343a40"}
															icon={folder.public ? "lock-open" : "lock"}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>

														<Button
															onPress={async () => {
																const folderId = folder.id;

																const success = await deleteFolder(folderId);

																if (typeof success === "string")
																	return ToastAndroid.show(
																		`Failed to delete the folder "${folder.name}"`,
																		ToastAndroid.SHORT,
																	);

																const newFolders = folders.filter(
																	(fold) => fold.id !== folder.id,
																);

																setFolders(newFolders);

																ToastAndroid.show(
																	`Deleted the folder "${folder.name}"`,
																	ToastAndroid.SHORT,
																);
															}}
															color="#CF4238"
															icon="delete"
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

												if (index === folders.length - 1)
													rowStyle = {
														...styles.row,
														...styles.lastRow,
													};

												return (
													<Row
														key={folder.id}
														data={[name, isPublic, created, actions]}
														widthArr={[80, 50, 130, 150]}
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
				) : (
					<View style={styles.loadingContainer}>
						<Text style={styles.loadingText}>Loading...</Text>
					</View>
				)}
			</View>
		</View>
	);
}
