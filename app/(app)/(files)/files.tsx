import {
	ScrollView,
	Text,
	ToastAndroid,
	View,
} from "react-native";
import type { APITags, APIFiles, DashURL, APIFile } from "@/types/zipline";
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
import {
	createTag,
	deleteTag,
	editTag,
	getTags,
} from "@/functions/zipline/tags";
import Popup from "@/components/Popup";
import { isLightColor } from "@/functions/color";
import { colorHash } from "@/functions/util";
import LargeFileDisplay from "@/components/LargeFileDisplay";
import TextInput from "@/components/TextInput";
import Button from "@/components/Button";

export default function Files() {
	const router = useRouter();

	const searchParams = useLocalSearchParams<{
		id?: string;
		page?: string;
		folderId?: string;
	}>();

	useAuth(searchParams.id ? "ADMIN" : "USER");
	useShareIntent();

	const [page, setPage] = useState<string>("1");
	const [favorites, setFavorites] = useState<boolean>(false);
	const [prevPageDisabled, setPrevPageDisabled] = useState<boolean>(true);
	const [nextPageDisabled, setNextPageDisabled] = useState<boolean>(false);
	const [allPageDisabled, setAllPageDisabled] = useState<boolean>(true);
	const [selectedPage, setSelectedPage] = useState<string>(page);

	const [tags, setTags] = useState<APITags | null>(null);
	const [tagsMenuOpen, setTagsMenuOpen] = useState<boolean>(false);

	const [createNewTag, setCreateNewTag] = useState<boolean>(false);
	const [tagToEdit, setTagToEdit] = useState<APITags[0] | null>(null);

	const [newTagName, setNewTagName] = useState<string | null>(null);
	const [newTagColor, setNewTagColor] = useState<string>("#ffffff");

	const [editTagName, setEditTagName] = useState<string | null>(null);
	const [editTagColor, setEditTagColor] = useState<string>("#ffffff");

	const [newTagError, setNewTagError] = useState<string | null>(null);
	const [editTagError, setEditTagError] = useState<string | null>(null);

	const [files, setFiles] = useState<APIFiles | null>(null);
	const [filesWidth, setFilesWidth] = useState<number>(0);

	const url = db.get("url") as DashURL | null;

	const [name, setName] = useState<string | null>(null);
	const [isFolder, setisFolder] = useState<boolean>(false);

	const [focusedFile, setFocusedFile] = useState<APIFile | null>(null);

	// biome-ignore lint/correctness/useExhaustiveDependencies: .
	useEffect(() => {
		setFiles(null);
		changePage();
	}, [page, favorites, searchParams.page]);

	const hexRegex = /^#([0-9a-f]{6}|[0-9a-f]{3})$/i;

	useEffect(() => {
		if (tagToEdit) {
			setEditTagName(tagToEdit.name);
			setEditTagColor(tagToEdit.color);
		}
	}, [tagToEdit]);

	async function changePage() {
		const tags = await getTags();

		setTags(typeof tags === "string" ? null : tags);

		if (searchParams.folderId) {
			const folder = await getFolder(searchParams.folderId);

			if (typeof folder === "string") return router.replace("/+not-found");

			setName(folder.name);

			setisFolder(true);
			return setFiles({
				page: folder.files,
				pages: 1,
				total: folder.files.length,
			});
		}

		let fetchPage = page;

		if (searchParams.page && Number.parseInt(searchParams.page) > 0) {
			fetchPage = searchParams.page;

			setPage(fetchPage);
		}

		const fetchOptions: GetFilesOptions = {
			favorite: favorites,
		};

		if (searchParams.id) {
			const user = await getUser(searchParams.id);

			if (typeof user === "string") return router.replace("/+not-found");

			fetchOptions.id = user.id;
			setName(user.username);
		}

		const files = await getFiles(fetchPage, fetchOptions);

		if (typeof files !== "string" && (files?.pages || 1) > 1)
			setAllPageDisabled(false);

		setFiles(typeof files === "string" ? null : files);
	}

	return (
		<View style={styles.mainContainer}>
			{focusedFile && (
				<LargeFileDisplay
					file={focusedFile}
					onClose={async (refresh) => {
						setFocusedFile(null);

						if (refresh) {
							changePage();
						}
					}}
					hidden={!focusedFile}
				/>
			)}

			<View style={styles.mainContainer}>
				<Popup
					hidden={!tagsMenuOpen}
					onClose={() => {
						setTagsMenuOpen(false);
					}}
				>
					<View style={styles.popupContent}>
						<View style={styles.header}>
							<Text style={styles.headerText}>Tags</Text>

							<View style={styles.headerButtons}>
								<Button
									onPress={() => {
										setCreateNewTag(true);
										setTagsMenuOpen(false);
									}}
									icon="add"
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

						<ScrollView style={styles.popupScrollView}>
							{tags?.map((tag) => (
								<View key={tag.id} style={styles.tagContainer}>
									<View style={styles.tagContainer}>
										<Text
											style={{
												...styles.tagName,
												backgroundColor: tag.color,
												color: isLightColor(tag.color) ? "black" : "white",
											}}
										>
											{tag.name}
										</Text>
										<Text style={styles.tagFilesText}>
											{tag.files.length} Files
										</Text>
									</View>

									<View style={styles.tagButtonContainer}>
										<Button
											icon="edit"
											color="#323ea8"
											onPress={() => {
												setTagToEdit(tag);
												setTagsMenuOpen(false);
											}}
											iconSize={20}
											width={32}
											height={32}
											padding={6}
											margin={{
												left: 5,
												right: 5,
											}}
										/>

										<Button
											icon="delete"
											color="#CF4238"
											onPress={async () => {
												const tagId = tag.id;

												const success = await deleteTag(tagId);

												if (typeof success === "string")
													return ToastAndroid.show(
														`Failed to delete the tag "${tag.name}"`,
														ToastAndroid.SHORT,
													);

												const newTags = tags.filter((tg) => tag.id !== tg.id);

												setTags(newTags);

												ToastAndroid.show(
													`Deleted the tag "${tag.name}"`,
													ToastAndroid.SHORT,
												);
											}}
											iconSize={20}
											width={32}
											height={32}
											padding={6}
											margin={{
												left: 5,
												right: 5,
											}}
										/>
									</View>
								</View>
							))}
						</ScrollView>

						<Text style={styles.popupSubHeaderText}>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup
					hidden={!createNewTag}
					onClose={() => {
						setCreateNewTag(false);
						setTagsMenuOpen(true);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Create Tag</Text>
						{newTagError && <Text style={styles.errorText}>{newTagError}</Text>}

						<TextInput
							title="Name:"
							onValueChange={(content) => {
								setNewTagName(content || null);
							}}
							value={newTagName || ""}
							placeholder="myTag"
						/>

						<TextInput
							title="Color:"
							onValueChange={(content) => {
								setNewTagColor(content);
							}}
							value={newTagColor || ""}
							maxLength={7}
							placeholder="#fa83d0"
						/>

						<View style={styles.manageTagButtonsContainer}>
							<Button
								color="#616060"
								text="Guess Color"
								onPress={async () => {
									const guess = colorHash(newTagName || "");

									setNewTagColor(guess);
								}}
								width="45%"
								margin={{
									left: "2.5%",
									right: "2.5%",
									top: 15,
								}}
							/>

							<Button
								color="#323ea8"
								text="Create"
								onPress={async () => {
									setNewTagError(null);

									if (!newTagName)
										return setNewTagError("Please insert a name");
									if (!hexRegex.test(newTagColor))
										return setNewTagError("Please insert a valid HEX color");

									const newTagData = await createTag(newTagName, newTagColor);

									if (typeof newTagData === "string")
										return setNewTagError(newTagData);

									setNewTagName(null);
									setNewTagColor("#ffffff");

									const newTags = await getTags();

									setTags(typeof newTags === "string" ? null : newTags);

									setCreateNewTag(false);
									setTagsMenuOpen(true);
								}}
								width="45%"
								margin={{
									left: "2.5%",
									right: "2.5%",
									top: 15,
								}}
							/>
						</View>

						<Text style={styles.popupSubHeaderText}>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup
					hidden={!tagToEdit}
					onClose={() => {
						setTagToEdit(null);
						setTagsMenuOpen(true);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Edit Tag</Text>
						{editTagError && (
							<Text style={styles.errorText}>{editTagError}</Text>
						)}

						{tagToEdit && (
							<View>
								<TextInput
									title="Name:"
									onValueChange={(content) => {
										setEditTagName(content || null);
									}}
									value={editTagName || ""}
									placeholder="myTag"
								/>

								<TextInput
									title="Color:"
									onValueChange={(content) => {
										setEditTagColor(content);
									}}
									value={editTagColor || ""}
									maxLength={7}
									placeholder="#fa83d0"
								/>

								<View style={styles.manageTagButtonsContainer}>
									<Button
										color="#616060"
										text="Guess Color"
										onPress={async () => {
											const guess = colorHash(editTagName || "");

											setEditTagColor(guess);
										}}
										width="45%"
										margin={{
											left: "2.5%",
											right: "2.5%",
											top: 15,
										}}
									/>

									<Button
										color="#323ea8"
										text="Create"
										onPress={async () => {
											setEditTagError(null);

											if (!editTagName)
												return setEditTagError("Please insert a name");
											if (!hexRegex.test(editTagColor))
												return setEditTagError(
													"Please insert a valid HEX color",
												);

											const editedTagData = await editTag(tagToEdit.id, {
												name:
													editTagName === tagToEdit.name
														? undefined
														: editTagName,
												color: editTagColor,
											});

											if (typeof editedTagData === "string")
												return setEditTagError(editedTagData);

											setEditTagName(null);
											setEditTagColor("#ffffff");

											const newTags = await getTags();

											setTags(typeof newTags === "string" ? null : newTags);

											setTagToEdit(null);
											setTagsMenuOpen(true);
										}}
										width="45%"
										margin={{
											left: "2.5%",
											right: "2.5%",
											top: 15,
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

				<View style={styles.header}>
					<Text style={styles.headerText}>
						{name
							? isFolder
								? `Files in ${name}`
								: `${name}'s Files`
							: "Files"}
					</Text>

					{!isFolder && (
						<View style={styles.headerButtons}>
							<Button
								onPress={() => {
									setFavorites((prev) => !prev);
								}}
								icon={favorites ? "star" : "star-border"}
								color="transparent"
								iconColor={favorites ? "#f1d01f" : "#2d3f70"}
								borderColor="#222c47"
								borderWidth={2}
								iconSize={30}
								padding={4}
								rippleColor="#283557"
								disabled={!files}
								margin={{
									left: 2,
									right: 2,
								}}
							/>

							{!name && (
								<>
									<Button
										onPress={() => {
											setTagsMenuOpen(true);
										}}
										icon="sell"
										color="transparent"
										iconColor="#2d3f70"
										borderColor="#222c47"
										borderWidth={2}
										iconSize={30}
										disabled={!files}
										padding={4}
										rippleColor="#283557"
										margin={{
											left: 2,
											right: 2,
										}}
									/>

									<Button
										onPress={() => {
											router.replace("/upload/file");
										}}
										icon="upload-file"
										color="transparent"
										iconColor="#2d3f70"
										borderColor="#222c47"
										borderWidth={2}
										iconSize={30}
										disabled={!files}
										padding={4}
										rippleColor="#283557"
										margin={{
											left: 2,
											right: 2,
										}}
									/>
								</>
							)}
						</View>
					)}
				</View>

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
										onPress={() => setFocusedFile(file)}
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
						<Button
							onPress={() => {
								setNextPageDisabled(false);

								if (page === "1") return;
								if (Number.parseInt(page) - 1 === 1) setPrevPageDisabled(true);
								if ((files?.pages || 1) < Number.parseInt(page)) {
									setNextPageDisabled(true);
									return setPage(String(files?.pages || "1"));
								}

								const newPage = String(Number.parseInt(page) - 1);

								setPage(newPage);
								setSelectedPage(newPage);
							}}
							text="PREV"
							color="transparent"
							textColor={allPageDisabled || prevPageDisabled ? "gray" : "white"}
							borderColor="#222c47"
							borderWidth={2}
							rippleColor="#283557"
							flex={1}
							disabled={allPageDisabled || prevPageDisabled}
							margin={{
								left: 5,
								right: 5,
							}}
						/>

						<Button
							onPress={() => {
								setPrevPageDisabled(true);
								setNextPageDisabled(false);

								const newPage = "1";

								setPage(newPage);
								setSelectedPage(newPage);
							}}
							text="1"
							color="transparent"
							textColor={allPageDisabled || prevPageDisabled ? "gray" : "white"}
							borderColor="#222c47"
							borderWidth={2}
							rippleColor="#283557"
							flex={1}
							disabled={allPageDisabled || prevPageDisabled}
							margin={{
								left: 5,
								right: 5,
							}}
						/>

						<TextInput
							inputStyle={styles.input}
							disabled={allPageDisabled}
							disableContext={allPageDisabled}
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

						<Button
							onPress={() => {
								setNextPageDisabled(true);
								setPrevPageDisabled(false);

								const newPage = files?.pages.toString() || page;

								setPage(newPage);
								setSelectedPage(newPage);
							}}
							text={files?.pages ? String(files?.pages) : "..."}
							color="transparent"
							textColor={nextPageDisabled || allPageDisabled ? "gray" : "white"}
							borderColor="#222c47"
							borderWidth={2}
							rippleColor="#283557"
							flex={1}
							disabled={nextPageDisabled || allPageDisabled}
							margin={{
								left: 5,
								right: 5,
							}}
						/>

						<Button
							onPress={() => {
								setPrevPageDisabled(false);

								if (page === String(files?.pages)) return;
								if (Number.parseInt(page) + 1 === files?.pages)
									setNextPageDisabled(true);

								const newPage = String(Number.parseInt(page) + 1);

								setPage(newPage);
								setSelectedPage(newPage);
							}}
							text="NEXT"
							color="transparent"
							textColor={nextPageDisabled || allPageDisabled ? "gray" : "white"}
							borderColor="#222c47"
							borderWidth={2}
							rippleColor="#283557"
							flex={1}
							disabled={nextPageDisabled || allPageDisabled}
							margin={{
								left: 5,
								right: 5,
							}}
						/>
					</View>
				)}
			</View>
		</View>
	);
}
