import { Pressable, ScrollView, Text, TextInput, ToastAndroid, View } from "react-native";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import type { APITags, APIFiles, DashURL } from "@/types/zipline";
import { getFiles, type GetFilesOptions } from "@/functions/zipline/files";
import FileDisplay from "@/components/FileDisplay";
import { styles } from "@/styles/files/files";
import { useEffect, useState } from "react";
import * as db from "@/functions/database";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import { getUser } from "@/functions/zipline/users";
import { getFolder } from "@/functions/zipline/folders";
import React from "react";
import { createTag, deleteTag, editTag, getTags } from "@/functions/zipline/tags";
import Popup from "@/components/Popup";
import { isLightColor } from "@/functions/color";
import { colorHash } from "@/functions/util";

export default function Files() {
	const router = useRouter();

	const searchParams = useLocalSearchParams<{
		id?: string;
		page?: string;
		folderId?: string;
	}>()
	
	useAuth(!!searchParams.id)
	useShareIntent()

	const [page, setPage] = useState<string>("1");
	const [favorites, setFavorites] = useState<boolean>(false)
	const [prevPageDisabled, setPrevPageDisabled] = useState<boolean>(true);
	const [nextPageDisabled, setNextPageDisabled] = useState<boolean>(false);
	const [allPageDisabled, setAllPageDisabled] = useState<boolean>(true);
	const [selectedPage, setSelectedPage] = useState<string>(page);

	const [tags, setTags] = useState<APITags | null>(null)
	const [tagsMenuOpen, setTagsMenuOpen] = useState<boolean>(false)

	const [createNewTag, setCreateNewTag] = useState<boolean>(false)
	const [tagToEdit, setTagToEdit] = useState<APITags[0] | null>(null)

	const [newTagName, setNewTagName] = useState<string | null>(null)
	const [newTagColor, setNewTagColor] = useState<string>("#ffffff")

	const [editTagName, setEditTagName] = useState<string | null>(null)
	const [editTagColor, setEditTagColor] = useState<string>("#ffffff")

	const [newTagError, setNewTagError] = useState<string | null>(null)
	const [editTagError, setEditTagError] = useState<string | null>(null)

	const [files, setFiles] = useState<APIFiles | null>(null);
	const [filesWidth, setFilesWidth] = useState<number>(0);

	const url = db.get("url") as DashURL | null;

	const [name, setName] = useState<string | null>(null);
	const [isFolder, setisFolder] = useState<boolean>(false)

	// biome-ignore lint/correctness/useExhaustiveDependencies: .
	useEffect(() => {
		(async () => {
			const tags = await getTags()

			setTags(typeof tags === "string" ? null : tags)

			if (searchParams.folderId) {
				const folder = await getFolder(searchParams.folderId)

				if (typeof folder === "string") return router.replace("/+not-found");

				setName(folder.name)

				setisFolder(true)
				return setFiles({
					page: folder.files,
					pages: 1,
					total: folder.files.length
				})
			}

			let fetchPage = page;

			if (searchParams.page && Number.parseInt(searchParams.page) > 0) {
				fetchPage = searchParams.page;

				setPage(fetchPage);
			}

			const fetchOptions: GetFilesOptions = {
				favorite: favorites
			}

			if (searchParams.id) {
				const user = await getUser(searchParams.id)

				if (typeof user === "string") return router.replace("/+not-found")

				fetchOptions.id = user.id
				setName(user.username)
			}

			const files = await getFiles(fetchPage, fetchOptions);

			if (typeof files !== "string" && (files?.pages || 1) > 1) setAllPageDisabled(false);

			setFiles(typeof files === "string" ? null : files);
		})();
	}, [page, favorites, searchParams.page]);

	const hexRegex = /^#([0-9a-f]{6}|[0-9a-f]{3})$/i

	useEffect(() => {
		if (tagToEdit) {
			setEditTagName(tagToEdit.name);
			setEditTagColor(tagToEdit.color)
		}
	}, [tagToEdit])

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<Popup hidden={!tagsMenuOpen} onClose={() => {
					setTagsMenuOpen(false)
				}}>
					<View style={styles.popupContent}>
						<View style={styles.header}>
							<Text style={styles.headerText}>Tags</Text>

							<View style={styles.headerButtons}>
								<Pressable
									style={styles.headerButton}
									onPress={() => {
										setCreateNewTag(true)
										setTagsMenuOpen(false)
									}}
								>
									<MaterialIcons
										name="add"
										size={30}
										color={styles.headerButton.color}
									/>
								</Pressable>
							</View>
						</View>

						<ScrollView style={styles.popupScrollView}>
							{tags?.map((tag) => (
								<View key={tag.id} style={styles.tagContainer}>
									<View style={styles.tagContainer}>
										<Text style={{
											...styles.tagName,
											backgroundColor: tag.color,
											color: isLightColor(tag.color) ? "black" : "white"
										}}>{tag.name}</Text>
										<Text style={styles.tagFilesText}>{tag.files.length} Files</Text>
									</View>

									<View style={styles.tagButtonContainer}>
										<Pressable style={styles.tagButton}>
											<MaterialIcons
												name="edit"
												size={20}
												color={"white"}
												onPress={() => {
													setTagToEdit(tag)
													setTagsMenuOpen(false)
												}}
											/>
										</Pressable>

										<Pressable style={{
											...styles.tagButton,
											...styles.tagButtonDanger
										}}>
											<MaterialIcons
												name="delete"
												size={20}
												color={"white"}
												onPress={async () => {
													const tagId = tag.id

													const success = await deleteTag(tagId)

													if (typeof success === "string") return ToastAndroid.show(
														`Failed to delete the tag "${tag.name}"`,
														ToastAndroid.SHORT
													)

													const newTags = tags.filter(tg => tag.id !== tg.id)

													setTags(newTags)

													ToastAndroid.show(
														`Deleted the tag "${tag.name}"`,
														ToastAndroid.SHORT
													)
												}}
											/>
										</Pressable>
									</View>
								</View>
							))}
						</ScrollView>

						<Text
							style={styles.popupSubHeaderText}
						>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup hidden={!createNewTag} onClose={() => {
					setCreateNewTag(false)
					setTagsMenuOpen(true)
				}}>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Create Tag</Text>
						{newTagError && <Text style={styles.errorText}>{newTagError}</Text>}

						<Text style={styles.popupHeaderText}>Name:</Text>
						<TextInput
							style={styles.textInput}
							onChangeText={(content) => {
								setNewTagName(content || null);
							}}
							value={newTagName || ""}
							placeholder="myTag"
							placeholderTextColor="#222c47"
						/>
		
						<Text style={styles.popupHeaderText}>Color:</Text>
						<TextInput
							style={styles.textInput}
							onChangeText={(content) => {
								setNewTagColor(content);
							}}
							value={newTagColor || ""}
							maxLength={7}
							placeholder="#fa83d0"
							placeholderTextColor="#222c47"
						/>

						<View style={styles.manageTagButtonsContainer}>
							<Pressable
								style={{
									...styles.button,
									...styles.manageTagButton,
									...styles.guessButton
								}}
								onPress={async () => {
									const guess = colorHash(newTagName || "")

									setNewTagColor(guess)
								}}
							>
								<Text style={styles.buttonText}>Guess Color</Text>
							</Pressable>

							<Pressable
								style={{
									...styles.button,
									...styles.manageTagButton
								}}
								onPress={async () => {
									setNewTagError(null);
			
									if (!newTagName) return setNewTagError("Please insert a name");
									if (!hexRegex.test(newTagColor)) return setNewTagError("Please insert a valid HEX color");

									const newTagData = await createTag(newTagName, newTagColor)
			
									if (typeof newTagData === "string")
										return setNewTagError(newTagData);
			
									setNewTagName(null);
									setNewTagColor("#ffffff");

									const newTags = await getTags()

									setTags(typeof newTags === "string" ? null : newTags)

									setCreateNewTag(false);
									setTagsMenuOpen(true)
								}}
							>
								<Text style={styles.buttonText}>Create</Text>
							</Pressable>
						</View>

						<Text
							style={styles.popupSubHeaderText}
						>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup hidden={!tagToEdit} onClose={() => {
					setTagToEdit(null)
					setTagsMenuOpen(true)
				}}>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Edit Tag</Text>
						{editTagError && <Text style={styles.errorText}>{editTagError}</Text>}

						{tagToEdit && (
							<View>
								<Text style={styles.popupHeaderText}>Name:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => {
										setEditTagName(content || null);
									}}
									value={editTagName || ""}
									placeholder="myTag"
									placeholderTextColor="#222c47"
								/>

								<Text style={styles.popupHeaderText}>Color:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => {
										setEditTagColor(content);
									}}
									value={editTagColor || ""}
									maxLength={7}
									placeholder="#fa83d0"
									placeholderTextColor="#222c47"
								/>

								<View style={styles.manageTagButtonsContainer}>
									<Pressable
										style={{
											...styles.button,
											...styles.manageTagButton,
											...styles.guessButton
										}}
										onPress={async () => {
											const guess = colorHash(editTagName || "")

											setEditTagColor(guess)
										}}
									>
										<Text style={styles.buttonText}>Guess Color</Text>
									</Pressable>

									<Pressable
										style={{
											...styles.button,
											...styles.manageTagButton
										}}
										onPress={async () => {
											setEditTagError(null);

											if (!editTagName) return setEditTagError("Please insert a name");
											if (!hexRegex.test(editTagColor)) return setEditTagError("Please insert a valid HEX color");

											

											const editedTagData = await editTag(tagToEdit.id, {
												name: editTagName === tagToEdit.name ? undefined : editTagName,
												color: editTagColor
											})

											if (typeof editedTagData === "string")
												return setEditTagError(editedTagData);

											setEditTagName(null);
											setEditTagColor("#ffffff");

											const newTags = await getTags()

											setTags(typeof newTags === "string" ? null : newTags)

											setTagToEdit(null);
											setTagsMenuOpen(true)
										}}
									>
										<Text style={styles.buttonText}>Edit</Text>
									</Pressable>
								</View>

								<Text
									style={styles.popupSubHeaderText}
								>
									Press outside to close this popup
								</Text>
							</View>
						)}
					</View>
				</Popup>

				{files && (
					<View style={styles.header}>
						<Text style={styles.headerText}>{name ? isFolder ? `Files in ${name}` : `${name}'s Files` : "Files"}</Text>
						
						{!isFolder && (
							<View style={styles.headerButtons}>
								<Pressable
									style={styles.headerButton}
									onPress={() => {
										setFavorites((prev) => !prev);
									}}
								>
									<MaterialIcons
										name={favorites ? "star" : "star-border"}
										size={30}
										color={favorites ? styles.headerStarButtonActive.color : styles.headerButton.color}
									/>
								</Pressable>

								{!name && (
									<>
										<Pressable
											style={styles.headerButton}
											onPress={() => {
												setTagsMenuOpen(true)
											}}
										>
											<MaterialIcons
												name="sell"
												size={30}
												color={styles.headerButton.color}
											/>
										</Pressable>

										<Pressable
											style={styles.headerButton}
											onPress={() => {
												router.replace("/upload/file")
											}}
										>
											<MaterialIcons
												name="upload-file"
												size={30}
												color={styles.headerButton.color}
											/>
										</Pressable>
									</>
								)}
							</View>
						)}
					</View>
				)}

				<ScrollView
					showsVerticalScrollIndicator={false}
					contentContainerStyle={styles.imagesContainer}
					onLayout={(event) => setFilesWidth(event.nativeEvent.layout.width)}
				>
					{files ? (
						<ScrollView showsVerticalScrollIndicator={false}>
							{files.page.map((file) => (
								<View key={file.id} style={styles.imageContainer}>
									<FileDisplay
										uri={`${url}/raw/${file.name}`}
										width={filesWidth - 50}
										originalName={file.originalName}
										name={file.name}
										autoHeight
										passwordProtected={file.password}
										file={file}
									/>
								</View>
							))}
						</ScrollView>
					) : (
						<View style={styles.loadingContainer}>
							<Text style={styles.loadingText}>Loading...</Text>
						</View>
					)}
				</ScrollView>

				{!isFolder && (
					<View style={styles.pagesContainer}>
						<Pressable
							style={styles.pageButton}
							disabled={prevPageDisabled || allPageDisabled}
							onPress={() => {
								setNextPageDisabled(false);

								if (page === "1") return;
								if (Number.parseInt(page) - 1 === 1) setPrevPageDisabled(true);
								if ((files?.pages || 1) < Number.parseInt(page)) {
									setNextPageDisabled(true);
									return setPage(String(files?.pages|| "1"))
								}

								const newPage = String(Number.parseInt(page) - 1);

								setPage(newPage);
								setSelectedPage(newPage);
							}}
						>
							<Text
								style={
									allPageDisabled || prevPageDisabled
										? {
												...styles.pageButtonText,
												...styles.pageButtonTextDisabled,
											}
										: styles.pageButtonText
								}
							>
								PREV
							</Text>
						</Pressable>

						<Pressable
							style={styles.pageButton}
							disabled={allPageDisabled || prevPageDisabled}
							onPress={() => {
								setPrevPageDisabled(true);
								setNextPageDisabled(false);

								const newPage = "1";

								setPage(newPage);
								setSelectedPage(newPage);
							}}
						>
							<Text
								style={
									allPageDisabled || prevPageDisabled
										? {
												...styles.pageButtonText,
												...styles.pageButtonTextDisabled,
											}
										: styles.pageButtonText
								}
							>
								1
							</Text>
						</Pressable>

						<TextInput
							style={
								allPageDisabled
									? {
											...styles.input,
											...styles.inputDisabled,
										}
									: styles.input
							}
							keyboardType="numeric"
							returnKeyType="done"
							onChange={(event) => setSelectedPage(event.nativeEvent.text)}
							onSubmitEditing={(event) => {
								let selectedPage = Number.parseInt(event.nativeEvent.text);

								if (
									!selectedPage ||
									selectedPage < 1 ||
									selectedPage > (files?.pages || 1)
								)
									selectedPage = 1;

								setPrevPageDisabled(false);
								setNextPageDisabled(false);

								if (selectedPage === 1) setPrevPageDisabled(true);
								if (selectedPage === (files?.pages || 1))
									setNextPageDisabled(true);

								setPage(String(selectedPage));
								setSelectedPage(String(selectedPage));
							}}
							value={selectedPage}
						/>

						<Pressable
							style={styles.pageButton}
							disabled={allPageDisabled || nextPageDisabled}
							onPress={() => {
								setNextPageDisabled(true);
								setPrevPageDisabled(false);

								const newPage = files?.pages.toString() || page;

								setPage(newPage);
								setSelectedPage(newPage);
							}}
						>
							<Text
								style={
									allPageDisabled || nextPageDisabled
										? {
												...styles.pageButtonText,
												...styles.pageButtonTextDisabled,
											}
										: styles.pageButtonText
								}
							>
								{files?.pages || "..."}
							</Text>
						</Pressable>

						<Pressable
							style={styles.pageButton}
							disabled={nextPageDisabled || allPageDisabled}
							onPress={() => {
								setPrevPageDisabled(false);

								if (page === String(files?.pages)) return;
								if (Number.parseInt(page) + 1 === files?.pages)
									setNextPageDisabled(true);

								const newPage = String(Number.parseInt(page) + 1);

								setPage(newPage);
								setSelectedPage(newPage);
							}}
						>
							<Text
								style={
									allPageDisabled || nextPageDisabled
										? {
												...styles.pageButtonText,
												...styles.pageButtonTextDisabled,
											}
										: styles.pageButtonText
								}
							>
								NEXT
							</Text>
						</Pressable>
					</View>
				)}
			</View>
		</View>
	);
}
