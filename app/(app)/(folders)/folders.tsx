import { Pressable, ScrollView, Text, View, ToastAndroid, TextInput } from "react-native";
import type { APIFolders, APISettings, DashURL } from "@/types/zipline";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { getSettings } from "@/functions/zipline/settings";
import { Row, Table } from "react-native-table-component";
import { timeDifference } from "@/functions/util";
import { styles } from "@/styles/folders/folders";
import { useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import {
	type ExternalPathString,
	Link,
	useRouter,
} from "expo-router";
import { createFolder, deleteFolder, editFolder, getFolders } from "@/functions/zipline/folders";
import Popup from "@/components/Popup";
import { Switch } from "@react-native-material/core";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";

export default function Folders() {
	const router = useRouter();

	useAuth()
	useShareIntent()

	const [folders, setFolders] = useState<APIFolders | null>(null);
	const [settings, setSettings] = useState<APISettings | null>(null);

	const [createNewFolder, setCreateNewFolder] = useState<boolean>(false);

	const [newFolderName, setNewFolderName] = useState<string | null>(null);
	const [newFolderPublic, setNewFolderPublic] = useState<boolean>(false);

	const [newFolderError, setNewFolderError] = useState<string | null>(null);

	const dashUrl = db.get("url") as DashURL | null;

	useEffect(() => {
		(async () => {
			const folders = await getFolders();
			const settings = await getSettings();

			setFolders(typeof folders === "string" ? null : folders);
			setSettings(typeof settings === "string" ? null : settings);
		})();
	}, []);

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<Popup hidden={!createNewFolder} onClose={() => {
					setCreateNewFolder(false)
					setNewFolderName(null)
					setNewFolderPublic(false)
				}}>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Create Folder</Text>
						{newFolderError && <Text style={styles.errorText}>{newFolderError}</Text>}

						<Text style={styles.popupHeaderText}>Name:</Text>
						<TextInput
							style={styles.textInput}
							onChangeText={(content) => {
								setNewFolderName(content);
							}}
							value={newFolderName || ""}
							placeholder="myFolder"
							placeholderTextColor="#222c47"
						/>

						<View style={styles.switchContainer}>
							<Switch
								value={newFolderPublic}
								onValueChange={() => setNewFolderPublic((prev) => !prev)}
								thumbColor={newFolderPublic ? "#2e3e6b" : "#222c47"}
								trackColor={{
									true: "#21273b",
									false: "#181c28",
								}}
							/>
							<Text
								style={{
									...styles.switchText
								}}
							>
								Public
							</Text>
						</View>

						<Pressable
							style={styles.button}
							onPress={async () => {
								setNewFolderError(null)

								if (!newFolderName || newFolderName.length <= 0) return setNewFolderError("Please insert a folder name");

								const createdFolder = await createFolder(newFolderName, newFolderPublic);
		
								if (typeof createdFolder === "string")
									return setNewFolderError(createdFolder);

								setNewFolderName(null);
								setNewFolderPublic(false);

								const newFolders = await getFolders()

								setFolders(typeof newFolders === "string" ? null : newFolders)

								setCreateNewFolder(false);
							}}
						>
							<Text style={styles.buttonText}>Create</Text>
						</Pressable>
					</View>
				</Popup>

				{folders && settings && dashUrl ? (
					<View style={{ flex: 1 }}>
						<View style={styles.header}>
							<Text style={styles.headerText}>Folders</Text>
							<View style={styles.headerButtons}>
								<Pressable
									style={styles.headerButton}
									onPress={() => {
										setCreateNewFolder(true)
									}}
								>
									<MaterialIcons
										name="create-new-folder"
										size={30}
										color={styles.headerButton.color}
									/>
								</Pressable>
							</View>
						</View>

						<View style={{ ...styles.foldersContainer, flex: 1 }}>
							<ScrollView
								showsHorizontalScrollIndicator={false}
								horizontal={true}
							>
								<View>
									<Table>
										<Row
											data={[
												"Name",
												"Public",
												"Created",
												"Actions",
											]}
											widthArr={[80, 50, 130, 150]}
											style={styles.tableHeader}
											textStyle={styles.rowText}
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
                                                    <Text
														key={folder.id}
														style={styles.rowText}
													>
														{folder.name}
													</Text>
                                                )

												const isPublic = (
													<Text
														key={folder.id}
														style={styles.rowText}
													>
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
                                                        <Pressable
															style={styles.actionButton}
															onPress={() => {
																const folderId = folder.id;

																router.replace(`/files?folderId=${folderId}`)
															}}
														>
															<MaterialIcons
																name="folder-open"
																size={20}
																color={"white"}
															/>
														</Pressable>

														<Pressable
															style={{
                                                                ...styles.actionButton,
                                                                ...(!folder.public && styles.actionButtonDisabled)
                                                            }}
                                                            disabled={!folder.public}
															onPress={async () => {
																const urlDest = `${dashUrl}/folder/${folder.id}`

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
														>
															<MaterialIcons
																name="content-copy"
																size={20}
																color={folder.public ? "white" : "#2a3952"}
															/>
														</Pressable>

														<Pressable
															style={{
                                                                ...styles.actionButton,
                                                                ...(!folder.public && styles.actionButtonPrivate)
                                                            }}
															onPress={async () => {
																const folderId = folder.id;

																const success = await editFolder(folderId, !folder.public);

                                                                if (typeof success === "string") return ToastAndroid.show(
                                                                    `Failed to update the folder "${folder.name}"`,
                                                                    ToastAndroid.SHORT
                                                                )
                                                                    
                                                                ToastAndroid.show(
                                                                    `Updated the folder "${folder.name}"'s visibility`,
                                                                    ToastAndroid.SHORT
                                                                )

                                                                const folderIndex = folders.findIndex((fold) => folder.id === fold.id)

                                                                const newFolders = [...folders];
                                                                newFolders[folderIndex].public = !folder.public;

                                                                setFolders(newFolders);
															}}
														>
															<MaterialIcons
																name={folder.public ? "lock-open" : "lock"}
																size={20}
																color={"white"}
															/>
														</Pressable>

														<Pressable
															style={{
																...styles.actionButton,
																...styles.actionButtonDanger,
															}}
															onPress={async () => {
																const folderId = folder.id;

																const success = await deleteFolder(folderId);

                                                                if (typeof success === "string") return ToastAndroid.show(
                                                                    `Failed to delete the folder "${folder.name}"`,
                                                                    ToastAndroid.SHORT
                                                                )

                                                                const newFolders = folders.filter((fold) => fold.id !== folder.id)

                                                                setFolders(newFolders)

                                                                ToastAndroid.show(
                                                                    `Deleted the folder "${folder.name}"`,
                                                                    ToastAndroid.SHORT
                                                                )
															}}
														>
															<MaterialIcons
																name="delete"
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

												if (index === folders.length - 1)
													rowStyle = {
														...styles.row,
														...styles.lastRow,
													};

												return (
													<Row
														key={folder.id}
														data={[
															name,
															isPublic,
															created,
															actions,
														]}
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
