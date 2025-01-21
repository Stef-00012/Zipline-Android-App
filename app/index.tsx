import { useRouter } from "expo-router";
import { useShareIntentContext } from "expo-share-intent";
import { useEffect, useState } from "react";
import { Text, View, TextInput, Pressable } from "react-native";
import { styles } from "@/styles/home";
import { getRecentFiles, getCurrentUser } from "@/functions/zipline/user";
import { getUserStats } from "@/functions/zipline/stats";
import type { APIRecentFiles, APISelfUser, APIUserStats } from "@/types/zipline";
import * as db from "@/functions/database";

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

	const [token, setToken] = useState<string | null>(db.get("token"));
	const [url, setUrl] = useState<string | null>(db.get("url"));
	const [user, setUser] = useState<APISelfUser | null>(null);
	const [recentFiles, setRecentFiles] = useState<APIRecentFiles | null>();
	const [stats, setStats] = useState<APIUserStats | null>();

	const mainContainerStyles = user ? {
		...styles.mainContainer
	} : {
		...styles.mainContainer,
		marginTop: 0
	};

	useEffect(() => {
		(async () => {
			const user = await getCurrentUser();
			const recentFiles = await getRecentFiles();
			const stats = await getUserStats();

			setUser(user);
			setRecentFiles(recentFiles);
			setStats(stats);
		})();
	});

	async function handleLogin() {
		const user = await getCurrentUser();
		const recentFiles = await getRecentFiles();
		const stats = await getUserStats();

		setUser(user);
		setRecentFiles(recentFiles);
		setStats(stats);
	}

	return (
		<View style={mainContainerStyles}>
			{url && token ? (
				<View style={{flex:1}}>
					{user ? (
						<View>
							<Text style={{color: "#fff"}}>{user.username}</Text>
						</View>
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
