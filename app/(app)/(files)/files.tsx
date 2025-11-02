import Button from "@/components/Button";
import CheckBox from "@/components/CheckBox";
import ColorPicker from "@/components/ColorPicker";
import FileDisplay from "@/components/FileDisplay";
import LargeFileDisplay from "@/components/LargeFileDisplay";
import Popup from "@/components/Popup";
import Select from "@/components/Select";
import SkeletonTable from "@/components/skeleton/Table";
import Table from "@/components/Table";
import TextInput from "@/components/TextInput";
import { searchKeyNames } from "@/constants/files";
import { colors } from "@/constants/skeleton";
import { isLightColor } from "@/functions/color";
import * as db from "@/functions/database";
import { colorHash, convertToBytes, timeDifference } from "@/functions/util";
import {
	bulkEditFiles,
	deleteFile,
	getFiles,
	type GetFilesOptions,
} from "@/functions/zipline/files";
import { getFolder, getFolders } from "@/functions/zipline/folders";
import {
	createTag,
	deleteTag,
	editTag,
	getTags,
} from "@/functions/zipline/tags";
import { getUser } from "@/functions/zipline/users";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import { styles } from "@/styles/files/files";
import type {
	APIFile,
	APIFiles,
	APIFoldersNoIncl,
	APITags,
	DashURL,
} from "@/types/zipline";
import * as Clipboard from "expo-clipboard";
import { Directory, Paths } from "expo-file-system";
import * as FileSystem from "expo-file-system/legacy";
import { startActivityAsync } from "expo-intent-launcher";
import { useLocalSearchParams, useRouter } from "expo-router";
import { Skeleton } from "moti/skeleton";
import { useEffect, useState } from "react";
import {
	type ColorValue,
	ScrollView,
	Text,
	ToastAndroid,
	View,
} from "react-native";

export default function Files() {
	const router = useRouter();

	const searchParams = useLocalSearchParams<{
		id?: string;
		page?: string;
		folderId?: string;
	}>();

	useAuth(searchParams.id ? "ADMIN" : "USER");
	useShareIntent();

	const filesCompactView = db.get("filesCompactView");

	const [page, setPage] = useState<string>("1");
	const [favorites, setFavorites] = useState<boolean>(false);
	const [prevPageDisabled, setPrevPageDisabled] = useState<boolean>(true);
	const [nextPageDisabled, setNextPageDisabled] = useState<boolean>(false);
	const [allPageDisabled, setAllPageDisabled] = useState<boolean>(true);
	const [selectedPage, setSelectedPage] = useState<string>(page);
	const [compactModeEnabled, setCompactModeEnabled] = useState<boolean>(
		filesCompactView === "true",
	);
	const [sortKey, setSortKey] = useState<{
		id: Exclude<GetFilesOptions["sortBy"], undefined>;
		sortOrder: Exclude<GetFilesOptions["order"], undefined>;
	}>({
		id: "createdAt",
		sortOrder: "desc",
	});

	const [showSearch, setShowSearch] = useState<boolean>(false);
	const [searchTerm, setSearchTerm] = useState<string>("");
	const [searchPlaceholder, setSearchPlaceholder] = useState<string>("");
	const [searchKey, setSearchKey] = useState<GetFilesOptions["searchField"]>();

	const [selectedFiles, setSelectedFiles] = useState<APIFile["id"][]>([]);

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
	const [allowUploads, setAllowUploads] = useState<boolean>(false);
	const [folderId, setFolderId] = useState<string | null>(null);
	const [isFolder, setIsFolder] = useState<boolean>(false);

	const [focusedFile, setFocusedFile] = useState<APIFile | null>(null);

	const [folders, setFolders] = useState<APIFoldersNoIncl>([]);

	const [showApkPopup, setShowApkPopup] = useState<boolean>(false);
	const [apkFileName, setApkFileName] = useState<string>("");
	const [apkDownloadPercentage, setApkDownloadPercentage] =
		useState<string>("0");

	const dashUrl = db.get("url") as DashURL | null;

	useEffect(() => {
		(async () => {
			const folders = await getFolders(true);

			setFolders(typeof folders === "string" ? [] : folders);
		})();
	}, []);

	// biome-ignore lint/correctness/useExhaustiveDependencies: search term should be resetted when search key changes
	useEffect(() => {
		setSearchPlaceholder("");
	}, [searchKey]);

	// biome-ignore lint/correctness/useExhaustiveDependencies: the keys are used in the changePage function
	useEffect(() => {
		setFiles(null);
		changePage();
	}, [page, favorites, searchParams.page, sortKey, searchTerm]);

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

			if (typeof folder === "string") return router.push("/+not-found");

			setName(folder.name);
			setAllowUploads(folder.allowUploads);
			setFolderId(folder.id);

			setIsFolder(true);
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

		if (compactModeEnabled) {
			fetchOptions.order = sortKey.sortOrder;
			fetchOptions.sortBy = sortKey.id;

			if (searchKey && searchTerm) {
				fetchOptions.searchField = searchKey;
				fetchOptions.searchQuery = searchTerm;
			}
		}

		if (searchParams.id) {
			const user = await getUser(searchParams.id);

			if (typeof user === "string") return router.push("/+not-found");

			fetchOptions.id = user.id;
			setName(user.username);
		}

		if (compactModeEnabled) fetchOptions.perPage = 20;

		const files = await getFiles(fetchPage, fetchOptions);

		if (typeof files !== "string" && (files?.pages || 1) > 1)
			setAllPageDisabled(false);

		setFiles(typeof files === "string" ? null : files);
	}

	// biome-ignore lint/correctness/useExhaustiveDependencies: should only run when compact mode changes
	useEffect(() => {
		if (
			!compactModeEnabled &&
			(sortKey.id !== "createdAt" || sortKey.sortOrder !== "asc")
		) {
			setSortKey({
				id: "createdAt",
				sortOrder: "desc",
			});
		}
	}, [compactModeEnabled]);

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

						<ColorPicker
							title="Embed Color"
							initialColor={newTagColor}
							onSelectColor={(color) => {
								setNewTagColor(color.hex);
							}}
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

								<ColorPicker
									title="Embed Color"
									initialColor={editTagColor}
									onSelectColor={(color) => {
										setEditTagColor(color.hex);
									}}
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

				<Popup
					hidden={!showApkPopup}
					onClose={() => {
						setShowApkPopup(false);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Downloading APK...</Text>
	
						<Text style={styles.popupText}>
							Downloading{" "}
							<Text
								style={{
									fontWeight: "bold",
								}}
							>
								{apkFileName}
							</Text>
							... {apkDownloadPercentage}%
						</Text>
					</View>
	
					<Text style={styles.popupSubHeaderText}>
						Press outside to close this popup
					</Text>
				</Popup>

				<View style={styles.header}>
					<Text style={styles.headerText}>
						{name
							? isFolder
								? `Files in ${name.length > 8 ? `${name.substring(0, 8)}...` : name}`
								: `${name.length > 8 ? `${name.substring(0, 8)}...` : name}'s Files`
							: "Files"}
					</Text>

					<View style={styles.headerButtons}>
						{!isFolder && (
							<>
								<Button
									onPress={() => {
										setFavorites((prev) => !prev);
									}}
									icon={favorites ? "star_half" : "star"}
									color="transparent"
									iconColor={
										favorites
											? files
												? "#f1d01f"
												: "#f1d01f55"
											: files
												? "#2d3f70"
												: "#2d3f7055"
									}
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
									<Button
										onPress={() => {
											setTagsMenuOpen(true);
										}}
										icon="sell"
										color="transparent"
										iconColor={files ? "#2d3f70" : "#2d3f7055"}
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
								)}
							</>
						)}

						<Button
							onPress={() => {
								const url = isFolder
									? (`/folders/upload?folderId=${folderId}` as `/folders/upload?folderId=${string | null}`)
									: "/upload/file";

								router.push(url);
							}}
							icon="upload_file"
							color="transparent"
							iconColor={
								(files && !isFolder) || (isFolder && allowUploads)
									? "#2d3f70"
									: "#2d3f7055"
							}
							borderColor="#222c47"
							borderWidth={2}
							iconSize={30}
							disabled={!files || (isFolder && !allowUploads)}
							padding={4}
							rippleColor="#283557"
							margin={{
								left: 2,
								right: 2,
							}}
						/>

						<Button
							onPress={() => {
								db.set(
									"filesCompactView",
									compactModeEnabled ? "false" : "true",
								);

								setCompactModeEnabled((prev) => !prev);

								setSelectedFiles([]);
							}}
							icon={compactModeEnabled ? "view_module" : "view_agenda"}
							color="transparent"
							iconColor={files ? "#2d3f70" : "#2d3f7055"}
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
					</View>
				</View>

				{(selectedFiles.length > 0 || showSearch) && (
					<View style={styles.selectedFilesContainer}>
						{showSearch && searchKey && tags && (
							<>
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

								{searchKey === "tags" ? (
									<Select
										placeholder="Select Tags..."
										multiple
										disabled={tags.length <= 0}
										data={tags.map((tag) => ({
											label: tag.name,
											value: tag.id,
											color: tag.color,
										}))}
										onSelect={async (selectedTags) => {
											if (selectedTags.length <= 0) {
												setSearchTerm("");
												setShowSearch(false);

												return;
											}

											const newTags = selectedTags.map((tag) => tag.value);

											setSearchTerm(`,${newTags.join(",")}`);
											setShowSearch(false);
										}}
										defaultValues={tags
											.filter((tag) => searchTerm.split(",").includes(tag.id))
											.map((tag) => ({
												label: tag.name,
												value: tag.id,
												color: tag.color,
											}))}
										renderItem={(item) => (
											<View style={styles.selectRenderItemContainer}>
												<Text
													style={{
														...styles.selectRenderItemText,
														color: isLightColor(item.color as string)
															? "black"
															: "white",
														backgroundColor: item.color as ColorValue,
													}}
												>
													{item.label}
												</Text>
											</View>
										)}
										renderSelectedItem={(item, key) => (
											<Text
												key={key}
												style={{
													...styles.selectRenderSelectedItemText,
													color: isLightColor(item.color as string)
														? "black"
														: "white",
													backgroundColor: item.color as ColorValue,
												}}
											>
												{item.label}
											</Text>
										)}
										maxHeight={500}
									/>
								) : (
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
								)}
							</>
						)}

						{selectedFiles.length > 0 && (
							<>
								<Text style={styles.popupSubHeaderText}>
									Selections are saved across page changes
								</Text>

								<View style={styles.selectedFilesActionsContainerRow}>
									<Button
										icon="delete"
										color="transparent"
										rippleColor="gray"
										borderWidth={2}
										borderColor="#ff8787"
										textColor="#ff8787"
										iconColor="#ff8787"
										width={"47%"}
										onPress={async () => {
											const success = await bulkEditFiles({
												files: selectedFiles,
												remove: true,
											});

											if (typeof success === "string")
												return ToastAndroid.show(
													`Error: ${success}`,
													ToastAndroid.SHORT,
												);

											await changePage();

											setSelectedFiles([]);

											return ToastAndroid.show(
												`Successfully deleted ${success.count} file${success.count > 1 ? "s" : ""}`,
												ToastAndroid.SHORT,
											);
										}}
										text={`Delete ${selectedFiles.length} File${selectedFiles.length > 1 ? "s" : ""}`}
										margin={{
											left: 5,
											right: 5,
										}}
									/>

									<Button
										icon="star"
										color="transparent"
										rippleColor="gray"
										borderWidth={2}
										borderColor="#f7d55a"
										iconColor="#f7d55a"
										textColor="#f7d55a"
										width={"47%"}
										onPress={async () => {
											const success = await bulkEditFiles({
												files: selectedFiles,
												favorite: true,
											});

											if (typeof success === "string")
												return ToastAndroid.show(
													`Error: ${success}`,
													ToastAndroid.SHORT,
												);

											await changePage();

											setSelectedFiles([]);

											return ToastAndroid.show(
												`Successfully added ${success.count} file${success.count > 1 ? "s" : ""} to favorites`,
												ToastAndroid.SHORT,
											);
										}}
										text={`Favorite ${selectedFiles.length} File${selectedFiles.length > 1 ? "s" : ""}`}
										margin={{
											left: 5,
											right: 5,
										}}
									/>
								</View>

								<View style={styles.selectedFilesActionsContainerRow}>
									<Select
										data={folders.map((folder) => ({
											label: folder.name,
											value: folder.id,
										}))}
										onSelect={async (selectedFolder) => {
											const folderId = selectedFolder[0].value;

											const success = await bulkEditFiles({
												files: selectedFiles,
												folder: folderId,
											});

											if (typeof success === "string")
												return ToastAndroid.show(
													`Error: ${success}`,
													ToastAndroid.SHORT,
												);

											await changePage();

											setSelectedFiles([]);

											return ToastAndroid.show(
												`Successfully added ${success.count} file${success.count > 1 ? "s" : ""} to "${success.name}"`,
												ToastAndroid.SHORT,
											);
										}}
										placeholder="Add to Folder..."
										maxHeight={400}
										width={"47%"}
										margin={{
											right: 5,
											left: 5,
											top: -5,
										}}
									/>

									<Button
										color="transparent"
										rippleColor="gray"
										borderWidth={2}
										borderColor="#585daf"
										textColor="#585daf"
										iconColor="#585daf"
										width={"47%"}
										onPress={() => setSelectedFiles([])}
										text="Clear Selection"
										margin={{
											left: 5,
										}}
									/>
								</View>
							</>
						)}
					</View>
				)}

				<ScrollView
					showsVerticalScrollIndicator={false}
					contentContainerStyle={styles.imagesContainer}
					onLayout={(event) => setFilesWidth(event.nativeEvent.layout.width)}
				>
					{files && dashUrl ? (
						// biome-ignore lint/complexity/noUselessFragments: The fragment is required
						<>
							{compactModeEnabled ? (
								<Table
									headerRow={[
										{
											row: (
												<CheckBox
													disabled={isFolder}
													key={"tableHeaderCheckbox"}
													value={files.page
														.map((file) => file.id)
														.every((fileId) => selectedFiles.includes(fileId))}
													onValueChange={() => {
														if (
															files.page
																.map((file) => file.id)
																.every((fileId) =>
																	selectedFiles.includes(fileId),
																)
														)
															return setSelectedFiles((prev) =>
																prev.filter(
																	(selectedFile) =>
																		!files.page
																			.map((file) => file.id)
																			.includes(selectedFile),
																),
															);

														setSelectedFiles((prev) => [
															...new Set([
																...prev,
																...files.page.map((file) => file.id),
															]),
														]);
													}}
												/>
											),
										},
										{
											sortable: !isFolder,
											searchable: !isFolder,
											id: "name",
											row: "Name",
										},
										{
											searchable: !isFolder,
											id: "tags",
											row: "Tags",
										},
										{
											sortable: !isFolder,
											searchable: !isFolder,
											id: "type",
											row: "Type",
										},
										{
											sortable: !isFolder,
											id: "size",
											row: "Size",
										},
										{
											sortable: !isFolder,
											id: "createdAt",
											row: "Created At",
										},
										{
											sortable: !isFolder,
											id: "favorite",
											row: "Favorite",
										},
										{
											sortable: !isFolder,
											searchable: !isFolder,
											id: "id",
											row: "ID",
										},
										{
											row: "Actions",
										},
									]}
									onSearch={(searchKey) => {
										setShowSearch(true);
										setSearchKey(searchKey as GetFilesOptions["searchField"]);
									}}
									onSortOrderChange={(key, order) => {
										setSortKey({
											id: key as typeof sortKey.id,
											sortOrder: order,
										});
									}}
									sortKey={{
										id: sortKey.id as string,
										sortOrder: sortKey.sortOrder || "desc",
									}}
									rowWidth={[30, 150, 150, 150, 80, 130, 100, 220, 256]}
									rows={files.page.map((file) => {
										const checkbox = (
											<CheckBox
												disabled={isFolder}
												key={file.id}
												value={selectedFiles.includes(file.id)}
												onValueChange={() => {
													setSelectedFiles((prev) => {
														if (prev.includes(file.id))
															return prev.filter(
																(selectedFileId) => selectedFileId !== file.id,
															);

														return [...prev, file.id];
													});
												}}
											/>
										);

										const name = (
											<Text key={file.id} style={styles.rowText}>
												{file.name}
											</Text>
										);

										const tags = (
											<View key={file.id} style={styles.tagsContainer}>
												{file.tags.map((tag) => (
													<Text
														key={tag.id}
														style={{
															...styles.tag,
															backgroundColor: tag.color,
															color: isLightColor(tag.color)
																? "black"
																: "white",
														}}
													>
														{tag.name}
													</Text>
												))}
											</View>
										);

										const type = (
											<Text key={file.id} style={styles.rowText}>
												{file.type}
											</Text>
										);

										const size = (
											<Text key={file.id} style={styles.rowText}>
												{convertToBytes(file.size, {
													unitSeparator: " ",
												})}
											</Text>
										);

										const createdAt = (
											<Text key={file.id} style={styles.rowText}>
												{timeDifference(new Date(), new Date(file.createdAt))}
											</Text>
										);

										const favorite = (
											<Text
												key={file.id}
												style={{
													...styles.rowText,
													color: file.favorite
														? "#f1d01f"
														: styles.rowText.color,
												}}
											>
												{file.favorite ? "Yes" : "No"}
											</Text>
										);

										const id = (
											<Text key={file.id} style={styles.rowText}>
												{file.id}
											</Text>
										);

										const actions = (
											<View key={file.id} style={styles.actionsContainer}>
												<Button
													icon="insert_drive_file"
													color="#323ea8"
													onPress={async () => {
														setFocusedFile(file);
													}}
													iconSize={20}
													width={32}
													height={32}
													padding={6}
												/>

												<Button
													icon="open_in_new"
													color="#323ea8"
													onPress={() => {
														router.push(`${dashUrl}${file.url}`);
													}}
													iconSize={20}
													width={32}
													height={32}
													padding={6}
												/>

												<Button
													icon="content_copy"
													color="#323ea8"
													onPress={async () => {
														const url = `${dashUrl}${file.url}`;

														const saved = await Clipboard.setStringAsync(url);

														if (saved)
															return ToastAndroid.show(
																"URL copied to clipboard",
																ToastAndroid.SHORT,
															);

														return ToastAndroid.show(
															"Failed to paste to the clipboard",
															ToastAndroid.SHORT,
														);
													}}
													iconSize={20}
													width={32}
													height={32}
													padding={6}
												/>

												<Button
													icon="file_download"
													color="#343a40"
													onPress={async () => {
														const downloadUrl = `${dashUrl}/raw/${file.name}?download=true`;

														let savedFileDownloadUri =
															db.get("fileDownloadPath");

														if (!savedFileDownloadUri) {
															const permissions =
																await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

															if (!permissions.granted)
																return ToastAndroid.show(
																	"The permission to save the file was not granted",
																	ToastAndroid.SHORT,
																);

															db.set(
																"fileDownloadPath",
																permissions.directoryUri,
															);
															savedFileDownloadUri = permissions.directoryUri;
														}

														ToastAndroid.show(
															"Downloading...",
															ToastAndroid.SHORT,
														);

														const saveUri =
															await FileSystem.StorageAccessFramework.createFileAsync(
																savedFileDownloadUri,
																file.name,
																file.type,
															);

														const downloadResult =
															await FileSystem.downloadAsync(
																downloadUrl,
																`${FileSystem.cacheDirectory}/${file.name}`,
															);

														if (!downloadResult.uri)
															return ToastAndroid.show(
																"Something went wrong while downloading the file",
																ToastAndroid.SHORT,
															);

														const base64File =
															await FileSystem.readAsStringAsync(
																downloadResult.uri,
																{
																	encoding: FileSystem.EncodingType.Base64,
																},
															);

														await FileSystem.writeAsStringAsync(
															saveUri,
															base64File,
															{
																encoding: FileSystem.EncodingType.Base64,
															},
														);

														ToastAndroid.show(
															"Successfully downloaded the file",
															ToastAndroid.SHORT,
														);
													}}
													iconSize={20}
													width={32}
													height={32}
													padding={6}
												/>

												<Button
													icon="apk_install"
													color="#343a40"
													onPress={async () => {
														if (!file.name.endsWith(".apk"))
															return ToastAndroid.show(
																"The selected file is not an APK",
																ToastAndroid.SHORT,
															);

														if (file.password)
															return ToastAndroid.show(
																"Unable to install password protected APKs",
																ToastAndroid.SHORT,
															);

														setShowApkPopup(true);
														setApkFileName(file.name);

														ToastAndroid.show(
															"Downloading...",
															ToastAndroid.SHORT,
														);

														const downloadUrl = `${dashUrl}/raw/${file.name}?download=true`;

														const cacheDir = new Directory(Paths.cache);
														const saveURI = `${cacheDir.uri}/${file.name}`;

														const downloadResumable =
															FileSystem.createDownloadResumable(
																downloadUrl,
																saveURI,
																{},
																(downloadProgress) => {
																	const percentage =
																		(downloadProgress.totalBytesWritten /
																			downloadProgress.totalBytesExpectedToWrite) *
																		100;
				
																	setApkDownloadPercentage(percentage.toFixed(2));
																},
															);
				
														const downloadResult =
															await downloadResumable.downloadAsync();

														if (!downloadResult?.uri)
															return ToastAndroid.show(
																"Something went wrong while downloading the file",
																ToastAndroid.SHORT,
															);

														const apkPathContent =
															await FileSystem.getContentUriAsync(saveURI);

														await startActivityAsync(
															"android.intent.action.INSTALL_PACKAGE",
															{
																data: apkPathContent,
																flags: 1,
															},
														);

														setShowApkPopup(false);
														setApkFileName("");
													}}
													iconColor={
														file.name.endsWith(".apk") && !file.password
															? "white"
															: "gray"
													}
													disabled={
														!file.name.endsWith(".apk") || file.password
													}
													iconSize={20}
													width={32}
													height={32}
													padding={6}
												/>

												<Button
													icon="delete"
													color="#CF4238"
													onPress={async () => {
														const fileId = file.id;

														const success = await deleteFile(fileId);

														if (typeof success === "string")
															return ToastAndroid.show(
																`Error: ${success}`,
																ToastAndroid.SHORT,
															);

														ToastAndroid.show(
															`Successfully deleted the file ${file.name}`,
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

										return [
											checkbox,
											name,
											tags,
											type,
											size,
											createdAt,
											favorite,
											id,
											actions,
										];
									})}
								/>
							) : (
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
							)}
						</>
					) : (
						// biome-ignore lint/complexity/noUselessFragments: The fragment is required
						<>
							{compactModeEnabled ? (
								<SkeletonTable
									headerRow={[
										"Name",
										"Tags",
										"Type",
										"Size",
										"Created At",
										"Favorite",
										"ID",
										"Actions",
									]}
									rowWidth={[150, 150, 150, 80, 130, 100, 220, 256]}
									rows={[...Array(10).keys()].map(() => {
										return [80, 40, 60, 60, 90, 20, 120, 200];
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
											<Skeleton colors={colors} width="100%" height={250} />
										</View>
									))}
								</ScrollView>
							)}
						</>
					)}
				</ScrollView>

				{searchParams.folderId ? (
					<View style={styles.folderDataContainer}>
						<Text style={styles.folderIdText}>{searchParams.folderId}</Text>

						{typeof files?.total === "number" && (
							<Text style={styles.folderFilesText}>
								{files.total} file{files.total > 1 ? "s" : ""} found
							</Text>
						)}
					</View>
				) : (
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
