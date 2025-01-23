import { getRecentFiles, getCurrentUser } from "@/functions/zipline/user";
import { Table, Row, Rows } from "react-native-table-component";
import { useShareIntentContext } from "expo-share-intent";
import { getUserStats } from "@/functions/zipline/stats";
import { useEffect, useState } from "react";
import * as db from "@/functions/database";
import { useRouter } from "expo-router";
import { styles } from "@/styles/home";
import bytes from "bytes";
import type {
	APIRecentFiles,
	APISelfUser,
	APIUserStats,
} from "@/types/zipline";
import {
	Text,
	View,
	TextInput,
	Pressable,
	ScrollView,
	Image,
} from "react-native";

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

	const [inputtedUrl, setInputtedUrl] = useState<string | null>(null);
	const [inputtedToken, setInputtedToken] = useState<string | null>(null);

	const [url, setUrl] = useState<string | null>(db.get("url"));
	const [token, setToken] = useState<string | null>(db.get("token"));

	const [user, setUser] = useState<APISelfUser | null>(null);
	const [stats, setStats] = useState<APIUserStats | null>();
	const [recentFiles, setRecentFiles] = useState<APIRecentFiles | null>();

	useEffect(() => {
		handleLogin();
	}, []);

	async function handleLogin() {
		const user = await getCurrentUser();
		const stats = await getUserStats();
		const recentFiles = await getRecentFiles();

		setUser(user);
		setStats(stats);
		setRecentFiles(recentFiles);
	}

	return (
		<View style={styles.mainContainer}>
			{url && token ? (
				<View
					style={{
						flex: 1,
					}}
				>
					{user && stats && recentFiles ? (
						<ScrollView>
							<View>
								<Text style={styles.headerText}>Recent Files</Text>
								<ScrollView horizontal style={styles.scrollView}>
									{recentFiles.map((file) => (
										<View key={file.id} style={styles.recentFileContainer}>
											<Image
												key={file.id}
												// src={`${url}/raw${file.url}`}
												source={{
													uri: `${url}/raw${file.url}`,
												}}
												alt={file.originalName || file.name}
												width={200}
												height={200}
												resizeMode="contain"
											/>
										</View>
									))}
								</ScrollView>
							</View>

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
											{bytes(stats.storageUsed, {
												unitSeparator: " ",
											})}
										</Text>
									</View>

									<View style={styles.statContainer}>
										<Text style={styles.subHeaderText}>
											Average Storage Used:
										</Text>
										<Text style={styles.statText}>
											{bytes(stats.avgStorageUsed, {
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
									<Table>
										<Row
											data={["File Type", "Count"]}
											widthArr={[250, 150]}
											textStyle={styles.tableHeadText}
										/>
										<Rows
											data={Object.entries(stats.sortTypeCount).sort(
												(a, b) => b[1] - a[1],
											)}
											widthArr={[250, 150]}
											textStyle={styles.tableText}
										/>
									</Table>
								</View>
							</View>
						</ScrollView>
					) : (
						<View style={styles.loadingContainer}>
							<Text style={styles.loadingText}>Loading...</Text>
						</View>
					)}
				</View>
			) : (
				<View style={styles.loginContainer}>
					<View style={styles.loginBox}>
						<TextInput
							style={styles.textInput}
							onChangeText={(content) => {
								setInputtedUrl(content.length > 0 ? content : null);
							}}
							placeholder="Zipline URL"
							placeholderTextColor="#222c47"
						/>

						<TextInput
							style={styles.textInput}
							secureTextEntry={true}
							onChangeText={(content) => {
								setInputtedToken(content.length > 0 ? content : null);
							}}
							placeholder="Zipline Token"
							placeholderTextColor="#222c47"
						/>
						<Pressable
							style={styles.button}
							onPress={async () => {
								setUrl(inputtedUrl);
								db.set("url", inputtedUrl || "");

								setToken(inputtedToken);
								db.set("token", inputtedToken || "");

								await handleLogin();
							}}
						>
							<Text style={styles.buttonText}>Login</Text>
						</Pressable>
					</View>
				</View>
			)}
		</View>
	);
}
