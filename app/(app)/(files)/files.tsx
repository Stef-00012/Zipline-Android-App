import { Pressable, ScrollView, Text, TextInput, View } from "react-native";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import type { APIFiles, DashURL } from "@/types/zipline";
import { getFiles } from "@/functions/zipline/files";
import FileDisplay from "@/components/FileDisplay";
import { styles } from "@/styles/files/files";
import { useEffect, useState } from "react";
import * as db from "@/functions/database";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";

export default function Files() {
	const router = useRouter();

	// // biome-ignore lint/correctness/useExhaustiveDependencies: <explanation>
	// useEffect(() => {
	// 	if (hasShareIntent) {
	// 		router.replace({
	// 			pathname: "/shareintent",
	// 		});
	// 	}
	// }, [hasShareIntent]);

	const searchParams = useLocalSearchParams<{
		id?: string;
		favorites?: string
		page?: string
	}>()
	
	useAuth(!!searchParams.id)
	useShareIntent()

	const [page, setPage] = useState<string>("1");
	const [prevPageDisabled, setPrevPageDisabled] = useState<boolean>(true);
	const [nextPageDisabled, setNextPageDisabled] = useState<boolean>(false);
	const [allPageDisabled, setAllPageDisabled] = useState<boolean>(true);
	const [selectedPage, setSelectedPage] = useState<string>(page);

	const [files, setFiles] = useState<APIFiles | null>(null);
	const [filesWidth, setFilesWidth] = useState<number>(0);

	const url = db.get("url") as DashURL | null;

	useEffect(() => {
		(async () => {
			let fetchPage = page;

			if (searchParams.page && Number.parseInt(searchParams.page) > 0) {
				fetchPage = searchParams.page;

				setPage(fetchPage);
			}

			const files = await getFiles(fetchPage, {
				id: searchParams.id,
				favorite: searchParams.favorites === "true"
			});

			if ((files?.pages || 1) > 1) setAllPageDisabled(false);

			setFiles(files);
		})();
	}, [page, searchParams]);

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				{files && (
					<View style={styles.header}>
						<Text style={styles.headerText}>Files</Text>
						<View style={styles.headerButtons}>
							<Pressable
								style={styles.headerButton}
								onPress={() => {
									console.debug("Favorite Files Clicked");
								}}
							>
								<MaterialIcons
									name="star-border"
									size={30}
									color={styles.headerButton.color}
								/>
							</Pressable>

							<Pressable
								style={styles.headerButton}
								onPress={() => {
									console.debug("Tags Clicked");
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
						</View>
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
			</View>
		</View>
	);
}
