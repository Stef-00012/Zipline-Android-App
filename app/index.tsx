import { getRecentFiles, getCurrentUser } from "@/functions/zipline/user";
import { Table, Row, Rows } from "react-native-reanimated-table";
import LargeFileDisplay from "@/components/LargeFileDisplay";
import { getUserStats } from "@/functions/zipline/stats";
import { useShareIntent } from "@/hooks/useShareIntent";
import { Text, View, ScrollView } from "react-native";
import { useAppUpdates } from "@/hooks/useUpdates";
import FileDisplay from "@/components/FileDisplay";
import { convertToBytes } from "@/functions/util";
import { useEffect, useState } from "react";
import * as db from "@/functions/database";
import { useAuth } from "@/hooks/useAuth";
import { styles } from "@/styles/home";
import type {
	APIFile,
	APIRecentFiles,
	APISelfUser,
	APIUserStats,
	DashURL,
} from "@/types/zipline";

// import { useFocusEffect, useRouter } from "expo-router";

export default function Home() {
	useAuth();
	useShareIntent();

	useAppUpdates();

	const url = db.get("url") as DashURL | null;

	const [user, setUser] = useState<APISelfUser | null>(null);
	const [stats, setStats] = useState<APIUserStats | null>();
	const [recentFiles, setRecentFiles] = useState<APIRecentFiles | null>();

	const [focusedFile, setFocusedFile] = useState<APIFile | null>(null);

	useEffect(() => {
		handleAuth();
	}, []);

// 	const router = useRouter();

// 	useFocusEffect(() => {
// 		if (__DEV__) {
// // 			db.del("url")
// // 			db.del("token")

// 			// router.replace("/metrics");
// 		}
// 	});

	async function handleAuth() {
		const user = await getCurrentUser();
		const stats = await getUserStats();
		const recentFiles = await getRecentFiles();

		setUser(typeof user === "string" ? null : user);
		setStats(typeof stats === "string" ? null : stats);
		setRecentFiles(typeof recentFiles === "string" ? null : recentFiles);
	}

	return (
		<View style={styles.mainContainer}>
			{focusedFile && (
				<LargeFileDisplay
					file={focusedFile}
					onClose={async (refresh) => {
						setFocusedFile(null);

						if (refresh) {
							const newRecentFiles = await getRecentFiles();
							setRecentFiles(
								typeof newRecentFiles === "string" ? null : newRecentFiles,
							);
						}
					}}
					hidden={!focusedFile}
				/>
			)}

			{user ? (
				<ScrollView>
					{recentFiles && (
						<View>
							<Text style={styles.headerText}>Recent Files</Text>

							<ScrollView horizontal style={styles.scrollView}>
								{recentFiles.map((file) => (
									<View key={file.id} style={styles.recentFileContainer}>
										<FileDisplay
											uri={`${url}/raw/${file.name}`}
											originalName={file.originalName}
											name={file.name}
											width={200}
											height={200}
											passwordProtected={file.password}
											onPress={() => setFocusedFile(file)}
										/>
									</View>
								))}
							</ScrollView>
						</View>
					)}

					{stats && (
						<>
							<View>
								<Text style={styles.headerText}>Stats</Text>

								<ScrollView
									horizontal
									style={{
										...styles.scrollView,
										...styles.statsContainer,
									}}
								>
									<View style={styles.statContainer}>
										<Text style={styles.subHeaderText}>Files Uploaded:</Text>
										<Text style={styles.statText}>{stats.filesUploaded}</Text>
									</View>

									<View style={styles.statContainer}>
										<Text style={styles.subHeaderText}>Favorite Files:</Text>
										<Text style={styles.statText}>{stats.favoriteFiles}</Text>
									</View>

									<View style={styles.statContainer}>
										<Text style={styles.subHeaderText}>Storage Used:</Text>
										<Text style={styles.statText}>
											{convertToBytes(stats.storageUsed, {
												unitSeparator: " ",
											})}
										</Text>
									</View>

									<View style={styles.statContainer}>
										<Text style={styles.subHeaderText}>Average Storage Used:</Text>
										<Text style={styles.statText}>
											{convertToBytes(stats.avgStorageUsed, {
												unitSeparator: " ",
											})}
										</Text>
									</View>

									<View style={styles.statContainer}>
										<Text style={styles.subHeaderText}>File Views:</Text>
										<Text style={styles.statText}>{stats.views}</Text>
									</View>

									<View style={styles.statContainer}>
										<Text style={styles.subHeaderText}>File Average Views:</Text>
										<Text style={styles.statText}>
											{Math.round(stats.avgViews)}
										</Text>
									</View>

									<View style={styles.statContainer}>
										<Text style={styles.subHeaderText}>Links Created:</Text>
										<Text style={styles.statText}>{stats.urlsCreated}</Text>
									</View>

									<View style={styles.statContainer}>
										<Text style={styles.subHeaderText}>Total Link View:</Text>
										<Text style={styles.statText}>{stats.urlViews}</Text>
									</View>
								</ScrollView>
							</View>

							<View>
								<Text style={styles.headerText}>File Types</Text>

								<View
									style={{
										...styles.scrollView,
										...styles.fileTypesContainer,
									}}
								>
									<Table>
										<Row
											data={["File Type", "Count"]}
											widthArr={[260, 150]}
											textStyle={styles.tableHeadText}
										/>
										<Rows
											data={Object.entries(stats.sortTypeCount).sort(
												(a, b) => b[1] - a[1],
											)}
											widthArr={[260, 150]}
											textStyle={styles.tableText}
										/>
									</Table>
								</View>
							</View>
						</>
					)}
				</ScrollView>
			) : (
				<View style={styles.loadingContainer}>
					<Text style={styles.loadingText}>Loading...</Text>
				</View>
			)}
		</View>
	);
}
