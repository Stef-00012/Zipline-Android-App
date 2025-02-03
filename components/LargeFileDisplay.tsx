import { styles } from "@/styles/components/largeFileDisplay";
import type { APIFile, APIFoldersNoIncl, APITags, DashURL } from "@/types/zipline";
import { type ColorValue, Pressable, Text, TextInput, ToastAndroid, View } from "react-native";
import FileDisplay from "./FileDisplay";
import * as db from "@/functions/database";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { MaterialIcons } from "@expo/vector-icons";
import Select from "./Select";
import { convertToBytes } from "@/functions/util";
import { useEffect, useState } from "react";
import { getTags } from "@/functions/zipline/tags";
import { isLightColor } from "@/functions/color";
import { getFolders } from "@/functions/zipline/folders";
import axios from "axios";
import { editFile } from "@/functions/zipline/files";
import { type ExternalPathString, useRouter } from "expo-router";
import * as Clipboard from "expo-clipboard";
import * as FileSystem from "expo-file-system"

interface Props {
	file: APIFile;
	hidden: boolean;
	onClose: () => void;
}

// WIP
export default function LargeFileDisplay({ file, hidden, onClose }: Props) {
	const router = useRouter()

	const dashUrl = db.get("url") as DashURL | null;

	const [tags, setTags] = useState<APITags>([]);
	const [folders, setFolders] = useState<APIFoldersNoIncl>([]);

	const [fileContent, setFileContent] = useState<string | null>(null)
	
	const [tempHidden, setTempHidden] = useState<boolean>(false)

	useEffect(() => {
		(async () => {
			const tags = await getTags()
			const folders = await getFolders(true)

			setTags(typeof tags === "string" ? [] : tags)
			setFolders(typeof folders === "string" ? [] : folders)
		})()
	}, [])

	useEffect(() => {
		if (file.type.startsWith("text/")) {
			(async () => {
				const res =	await axios.get(`${dashUrl}/raw/${file.name}`, {
					responseType: "text"
				})

				setFileContent(res.data as string)
			})()
		}
	}, [file, dashUrl])

	return (
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
							editable={false}
							style={styles.textDisplay}
							value={fileContent}
						/>
					) : (
						<FileDisplay
							passwordProtected={!!file.password}
							uri={`${dashUrl}/raw/${file.name}`}
							originalName={file.originalName}
							mimetype={file.type}
							name={file.name}
							maxHeight={500}
							width={350}
							file={file}
							autoHeight
						/>
					)}

					<View style={styles.fileInfoContainer}>
						<MaterialIcons name="description" size={28} color="white" />
						<View style={styles.fileInfoTextContainer}>
							<Text style={styles.fileInfoHeader}>Type</Text>
							<Text style={styles.fileInfoText}>{file.type}</Text>
						</View>
					</View>

					<View style={styles.fileInfoContainer}>
						<MaterialIcons name="sd-storage" size={28} color="white" />
						<View style={styles.fileInfoTextContainer}>
							<Text style={styles.fileInfoHeader}>Size</Text>
							<Text style={styles.fileInfoText}>{convertToBytes(file.size, {
								unitSeparator: " "
							})}</Text>
						</View>
					</View>

					<View style={styles.fileInfoContainer}>
						<MaterialIcons name="visibility" size={28} color="white" />
						<View style={styles.fileInfoTextContainer}>
							<Text style={styles.fileInfoHeader}>View</Text>
							<Text style={styles.fileInfoText}>{file.views}</Text>
						</View>
					</View>

					<View style={styles.fileInfoContainer}>
						<MaterialIcons name="file-upload" size={28} color="white" />
						<View style={styles.fileInfoTextContainer}>
							<Text style={styles.fileInfoHeader}>Created At</Text>
							<Text style={styles.fileInfoText}>{new Date(file.createdAt).toLocaleString()}</Text>
						</View>
					</View>

					<View style={styles.fileInfoContainer}>
						<MaterialIcons name="autorenew" size={28} color="white" />
						<View style={styles.fileInfoTextContainer}>
							<Text style={styles.fileInfoHeader}>Updated At</Text>
							<Text style={styles.fileInfoText}>{new Date(file.updatedAt).toLocaleString()}</Text>
						</View>
					</View>

					<Text style={styles.fileInfoHeader}>Tags</Text>
					<Select
						placeholder="Select Tags..."
						multiple
						disabled={tags.length <= 0}
						data={tags.map(tag => ({
							label: tag.name,
							value: tag.id,
							color: tag.color
						}))}
						onSelect={console.log}
						renderItem={(item) => (
							<View style={styles.selectRenderItemContainer}>
								<Text style={{
									...styles.selectRenderItemText,
									color: isLightColor(item.color as string) ? "black" : "white",
									backgroundColor: item.color as ColorValue,
								}}>{item.label}</Text>
							</View>
						)}
						defaultValues={tags.filter(tag => file.tags.find(fileTag => fileTag.id === tag.id)).map(tag => ({
							label: tag.name,
							value: tag.id,
							color: tag.color
						}))}
						renderSelectedItem={(item, key) => (
							<Text key={key} style={{
								...styles.selectRenderSelectedItemText,
								color: isLightColor(item.color as string) ? "black" : "white",
								backgroundColor: item.color as ColorValue,
							}}>{item.label}</Text>
						)}
						maxHeight={500}
					/>

					<Text style={styles.fileInfoHeader}>Folder</Text>
					{file.folderId ? (
						<Pressable style={styles.removeFolderButton} onPress={() => console.debug("Remove from folder clicked")}>
							<Text style={styles.removeFolderButtonText}>Remove from folder "{folders.find(folder => folder.id === file.folderId)?.name}"</Text>
						</Pressable>
					) : (
						<Select
							placeholder="Add to Folder..."
							data={folders.map(folder => ({
								label: folder.name,
								value: folder.id,
							}))}
							defaultValue={file.folderId ? {
								label: (folders.find(folder => folder.id === file.folderId) as APIFoldersNoIncl[0])?.name,
								value: file.folderId
							} : undefined}
							onSelect={console.log}
						/>
					)}

					<Text style={styles.subHeaderText}>{file.id}</Text>

					<View style={styles.actionButtonsContainer}>
						<Pressable style={{
							...styles.actionButton,
							...styles.actionButtonEdit
						}}>
							<MaterialIcons name="edit" size={20} color="white"  />
						</Pressable>

						<Pressable style={{
							...styles.actionButton,
							...styles.actionButtonDelete
						}}>
							<MaterialIcons name="delete" size={20} color="white"  />
						</Pressable>

						<Pressable style={{
							...styles.actionButton,
							...(!file.favorite && styles.actionButtonFavorite)
						}} onPress={async () => {
							const success = editFile(file.id, {
								favorite: !file.favorite
							})

							if (typeof success === "string") return ToastAndroid.show(
								`Error: ${success}`,
								ToastAndroid.SHORT
							)

							file.favorite = !file.favorite

							ToastAndroid.show(
								`Successfully ${file.favorite ? "added to" : "removed from"} favorites`,
								ToastAndroid.SHORT
							)
						}}>
							<MaterialIcons name={file.favorite ? "star-outline" : "star"} size={20} color="white"  />
						</Pressable>

						<Pressable style={{
							...styles.actionButton,
							...styles.actionButtonOpen
						}} onPress={() => {
							router.replace(`${dashUrl}${file.url}` as ExternalPathString)
						}}>
							<MaterialIcons name="open-in-new" size={20} color="white"  />
						</Pressable>

						<Pressable style={{
							...styles.actionButton
						}} onPress={async () => {
							const url = `${dashUrl}${file.url}`

							const success = await Clipboard.setStringAsync(url)

							if (!success) return ToastAndroid.show(
								"Failed to copy the URL",
								ToastAndroid.SHORT
							)

							ToastAndroid.show(
								"Copied URL to clipboard",
								ToastAndroid.SHORT
							)
						}}>
							<MaterialIcons name="content-copy" size={20} color="white"  />
						</Pressable>

						<Pressable style={{
							...styles.actionButton
						}} onPress={async () => {
							const downloadUrl = `${dashUrl}/raw/${file.name}?download=true`

							let savedFileDownloadUri = db.get("fileDownloadPath")

							if (!savedFileDownloadUri) {
								const permissions = await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

								if (!permissions.granted) return ToastAndroid.show(
									"The permission to save the file was not granted",
									ToastAndroid.SHORT
								);

								db.set("fileDownloadPath", permissions.directoryUri)
								savedFileDownloadUri = permissions.directoryUri
							}

							ToastAndroid.show(
								"Downloading...",
								ToastAndroid.SHORT
							)

							const saveUri = await FileSystem.StorageAccessFramework.createFileAsync(savedFileDownloadUri, file.name, file.type)

							const downloadResult = await FileSystem.downloadAsync(downloadUrl, `${FileSystem.cacheDirectory}/${file.name}`)

							if (!downloadResult.uri) return ToastAndroid.show(
								"Something went wrong while downloading the file",
								ToastAndroid.SHORT
							)

							const base64File = await FileSystem.readAsStringAsync(downloadResult.uri, {
								encoding: FileSystem.EncodingType.Base64
							})

							await FileSystem.writeAsStringAsync(saveUri, base64File, {
								encoding: FileSystem.EncodingType.Base64
							})

							ToastAndroid.show(
								"Successfully downloaded the file",
								ToastAndroid.SHORT
							)
						}}>
							<MaterialIcons name="file-download" size={20} color="white"  />
						</Pressable>
					</View>
				</KeyboardAwareScrollView>
			</View>
		</Pressable>
	);
}
