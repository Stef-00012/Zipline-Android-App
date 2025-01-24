import { Pressable, ScrollView, Text, TextInput, View } from "react-native";
import AutoHeightImage from "react-native-auto-height-image";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { useShareIntentContext } from "expo-share-intent";
import { getFiles } from "@/functions/zipline/files";
import type { APIFiles } from "@/types/zipline";
import { useEffect, useState } from "react";
import * as db from "@/functions/database";
import { styles } from "@/styles/files";
import { useRouter } from "expo-router";

export default function Home() {
	const router = useRouter();
	const { hasShareIntent } = useShareIntentContext();

	// biome-ignore lint/correctness/useExhaustiveDependencies: <explanation>
	useEffect(() => {
		if (hasShareIntent) {
			router.replace({
				pathname: "/shareintent",
			});
		}
	}, [hasShareIntent]);

	const [page, setPage] = useState<string>("1");
	const [prevPageDisabled, setPrevPageDisabled] = useState<boolean>(true);
	const [nextPageDisabled, setNextPageDisabled] = useState<boolean>(false);
	const [allPageDisabled, setAllPageDisabled] = useState<boolean>(true);
	const [selectedPage, setSelectedPage] = useState<string>(page);

	const [files, setFiles] = useState<APIFiles | null>(null);
	const [filesWidth, setFilesWidth] = useState<number>(0);

	const url = db.get("url");

	useEffect(() => {
		(async () => {
			const files = await getFiles(page);

			if ((files?.pages || 1) > 1) setAllPageDisabled(false);

			setFiles(files);
		})();
	}, [page]);

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<View style={styles.header}>
					<Text style={styles.headerText}>Files</Text>
					<View style={styles.headerButtons}>
						<Pressable style={styles.headerButton} onPress={() => {
							console.info("Favorite Files Clicked")
						}}>
							<MaterialIcons name="star-border" size={30} color={styles.headerButton.color} />
						</Pressable>

						<Pressable style={styles.headerButton} onPress={() => {
							console.info("Tags Clicked")
						}}>
							<MaterialIcons name="sell" size={30} color={styles.headerButton.color} />
						</Pressable>

						<Pressable style={styles.headerButton} onPress={() => {
							console.info("Upload File Clicked")
						}}>
							<MaterialIcons name="upload-file" size={30} color={styles.headerButton.color} />
						</Pressable>
					</View>
				</View>

				<ScrollView
					contentContainerStyle={styles.imagesContainer}
					onLayout={(event) => setFilesWidth(event.nativeEvent.layout.width)}
				>
					{files ? (
						<ScrollView>
							{files.page.map((file) => (
								<View key={file.id} style={styles.imageContainer}>
									<AutoHeightImage
										source={{
											uri: `${url}/raw/${file.name}`,
										}}
										width={filesWidth - 50}
										alt={file.originalName || file.name}
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
