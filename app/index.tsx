import { getRecentFiles, getCurrentUser } from "@/functions/zipline/user";
import LargeFileDisplay from "@/components/LargeFileDisplay";
import { getUserStats } from "@/functions/zipline/stats";
import { useShareIntent } from "@/hooks/useShareIntent";
import { Text, View, ScrollView } from "react-native";
import { useAppUpdates } from "@/hooks/useUpdates";
import FileDisplay from "@/components/FileDisplay";
import { convertToBytes } from "@/functions/util";
import { /*useContext,*/ useEffect, useState } from "react";
import * as db from "@/functions/database";
import { useAuth } from "@/hooks/useAuth";
import { styles } from "@/styles/home";
import Table from "@/components/Table";
import SkeletonTable from "@/components/skeleton/Table";
import Skeleton from "@/components/skeleton/Skeleton";
import type {
	APIFile,
	APIRecentFiles,
	APISelfUser,
	APIUserStats,
	DashURL,
} from "@/types/zipline";
// import { AuthContext } from "@/contexts/AuthProvider";
// import { ZiplineContext } from "@/contexts/ZiplineProvider";

// ------------------------ DEV -------------------------

// import { useFocusEffect, useRouter } from "expo-router";

// ---------------------- END DEV -----------------------

export default function Home() {
	useAuth();
	useShareIntent();

	// const { role, updateAuth, serverVersion } = useContext(AuthContext)
	// const { webSettings, publicSettings, updateSettings } = useContext(ZiplineContext)

	// useEffect(() => {
	// 	console.info(role, serverVersion)

	// 	console.debug({
	// 		webSettings,
	// 		publicSettings,
	// 	})
	// }, [webSettings, publicSettings])

	useAppUpdates();

	const url = db.get("url") as DashURL | null;

	const [user, setUser] = useState<APISelfUser | null>(null);
	const [stats, setStats] = useState<APIUserStats | null>();
	const [recentFiles, setRecentFiles] = useState<APIRecentFiles | null>();

	const [focusedFile, setFocusedFile] = useState<APIFile | null>(null);

	// biome-ignore lint/correctness/useExhaustiveDependencies: Functions should not be parameters of the effect
	useEffect(() => {
		handleAuth();
	}, []);

	// ----------------- DEV ------------------

	// const router = useRouter();

	// useFocusEffect(() => {
	// 	if (__DEV__) {
	// 		// db.del("url")
	// 		// db.del("token")

	// 		// router.replace("/admin/settings");
	// 	}
	// });

	// --------------- END DEV ----------------

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
					<Text style={styles.mainHeader}>Welcome Back, {user.username}</Text>
					<Text style={styles.subHeader}>
						You have{" "}
						<Text style={styles.subHeaderNumber}>{stats?.filesUploaded}</Text>{" "}
						files uploaded.
					</Text>

					{user.quota?.filesQuota === "BY_FILES" && user.quota?.maxFiles && (
						<Text style={styles.subHeader}>
							You have uploaded{" "}
							<Text style={styles.subHeaderNumber}>{stats?.filesUploaded}</Text>{" "}
							files out of{" "}
							<Text style={styles.subHeaderNumber}>{user.quota.maxFiles}</Text>{" "}
							files allowed.
						</Text>
					)}

					{user.quota?.filesQuota === "BY_BYTES" && user.quota?.maxBytes && (
						<Text style={styles.subHeader}>
							You have used{" "}
							<Text style={styles.subHeaderNumber}>
								{convertToBytes(stats?.storageUsed || 0, {
									unitSeparator: " ",
								})}
							</Text>{" "}
							out of{" "}
							<Text style={styles.subHeaderNumber}>
								{convertToBytes(user.quota?.maxBytes, {
									unitSeparator: " ",
								})}
							</Text>{" "}
							of storage.
						</Text>
					)}

					{user.quota?.maxUrls && (
						<Text style={styles.subHeader}>
							You have created{" "}
							<Text style={styles.subHeaderNumber}>{stats?.urlsCreated}</Text>{" "}
							links out of{" "}
							<Text style={styles.subHeaderNumber}>{user.quota?.maxUrls}</Text>{" "}
							links allowed.
						</Text>
					)}

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
										<Text style={styles.subHeaderText}>
											Average Storage Used:
										</Text>
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
										<Text style={styles.subHeaderText}>
											File Average Views:
										</Text>
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
									<Table
										headerRow={[
											{
												row: "File Type",
											},
											{
												row: "Count",
											},
										]}
										rowWidth={[250, 150]}
										rows={Object.entries(stats.sortTypeCount).sort(
											(a, b) => b[1] - a[1],
										)}
									/>
								</View>
							</View>
						</>
					)}
				</ScrollView>
			) : (
				<View style={styles.mainContainer}>
					<Skeleton.Group show={!user}>
						<ScrollView
							style={{
								marginTop: 5,
							}}
						>
							<View
								style={{
									flexDirection: "row",
								}}
							>
								<Text style={styles.mainHeader}>Welcome back, </Text>
								<Skeleton width="60%" height={36} />
							</View>

							<View
								style={{
									marginTop: 5,
								}}
							>
								<Text style={styles.subHeader}>
									You have ## files uploaded.
								</Text>
							</View>

							<Text style={styles.headerText}>Recent Files</Text>

							<ScrollView horizontal style={styles.scrollView}>
								{[1, 2, 3].map((file) => (
									<View key={file} style={styles.recentFileContainer}>
										<Skeleton width={200} height={200} radius={10} />
									</View>
								))}
							</ScrollView>

							<Text style={styles.headerText}>Stats</Text>

							<ScrollView
								horizontal
								style={{
									...styles.scrollView,
								}}
							>
								{[
									["Files Uploaded:", 60],
									["Favorite Files:", 50],
									["Storage Used:", 90],
									["Average Storage Used:", 70],
									["File Views:", 50],
									["File Average Views:", 50],
									["Links Created:", 60],
									["Total Link View:", 50],
								].map((stat) => (
									<View key={stat[0]} style={styles.statContainer}>
										<Text style={styles.subHeaderText}>{stat[0]}</Text>
										<View
											style={{
												marginTop: 5,
											}}
										>
											<Skeleton width={stat[1] as number} height={36} />
										</View>
									</View>
								))}
							</ScrollView>

							<Text style={styles.headerText}>File Types</Text>

							<View
								style={{
									...styles.scrollView,
									...styles.fileTypesContainer,
								}}
							>
								<SkeletonTable
									headerRow={["File Type", "Count"]}
									rowWidth={[250, 150]}
									rows={[...Array(4).keys()].map(() => {
										return ["55%", 30];
									})}
									disableAnimations
								/>
							</View>
						</ScrollView>
					</Skeleton.Group>
				</View>
			)}
		</View>
	);
}
