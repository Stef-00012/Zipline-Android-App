import { isAuthenticated } from "@/functions/zipline/auth";
import { useFocusEffect, useRouter } from "expo-router";
import { useState } from "react";
import { Pressable, Text, TextInput, View, ToastAndroid } from "react-native";
import { createURL, type CreateURLParams } from "@/functions/zipline/urls";
import * as Clipboard from "expo-clipboard";
import { styles } from "@/styles/components/shareIntentShorten";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { useShareIntentContext } from "expo-share-intent";

interface Props {
    defaultUrl: string
}

export default function ShareIntentShorten({
    defaultUrl
}: Props) {
	const router = useRouter();

	const { resetShareIntent } = useShareIntentContext();

	useFocusEffect(() => {
		(async () => {
			const authenticated = await isAuthenticated();

			if (!authenticated) return router.replace("/login");
		})();
	});

	const [url, setUrl] = useState<string | null>(defaultUrl);
	const [vanity, setVanity] = useState<string | null>(null);
	const [maxViews, setMaxViews] = useState<string | null>(null);
	const [password, setPassword] = useState<string | null>(null);

	const [outputUtl, setOutputUrl] = useState<string | null>(null);
	const [error, setError] = useState<string>();

	const urlRegex = /^http:\/\/(.*)?|https:\/\/(.*)?$/;

	return (
		<View style={styles.shortenContainer}>
			<View style={styles.shortenBox}>
				<View style={styles.header}>
					<Text style={styles.mainHeaderText}>Shorten URL</Text>

					<View style={styles.headerButtons}>
						<Pressable
							style={styles.headerButton}
							onPress={() => {
								resetShareIntent();

								router.replace("/urls")
							}}
						>
							<MaterialIcons
								name="link"
								size={30}
								color={styles.headerButton.color}
							/>
						</Pressable>
					</View>
				</View>

				{error && <Text style={styles.errorText}>{error}</Text>}

				<Text style={styles.headerText}>URL:</Text>
				<TextInput
					style={styles.textInput}
					onChangeText={(content) => {
						setUrl(content || null);
					}}
					value={url || ""}
					placeholder="https://google.com"
					placeholderTextColor="#222c47"
				/>

				<Text style={styles.headerText}>Vanity:</Text>
				<TextInput
					style={styles.textInput}
					onChangeText={(content) => {
						setVanity(content || null);
					}}
					value={vanity || ""}
					placeholder="google"
					placeholderTextColor="#222c47"
				/>

				<Text style={styles.headerText}>Max Views:</Text>
				<TextInput
					style={styles.textInput}
					onChangeText={(content) => {
						setMaxViews(content || null);
					}}
					value={maxViews || ""}
					placeholder="0"
					placeholderTextColor="#222c47"
				/>

				<Text style={styles.headerText}>Password:</Text>
				<TextInput
					style={styles.textInput}
					onChangeText={(content) => {
						setPassword(content || null);
					}}
					value={password || ""}
					placeholder="myPassword"
					placeholderTextColor="#222c47"
				/>

				<Pressable
					style={styles.button}
					onPress={async () => {
						setError(undefined);
						setOutputUrl(null);

						if (!url) return setError("Please insert a URL");
						if (!urlRegex.test(url))
							return setError("Please insert a valid URL");

						const urlData: CreateURLParams = {
							destination: url,
						};

						if (vanity) urlData.vanity = vanity;
						if (maxViews) urlData.maxViews = Number.parseInt(maxViews);
						if (password) urlData.password = password;

						const shortenedUrlData = await createURL(urlData);

						if (!shortenedUrlData)
							return setError("An error occurred while shortening the URL");

						const saved = await Clipboard.setStringAsync(shortenedUrlData.url);

						setUrl(null);
						setVanity(null);
						setMaxViews(null);
						setPassword(null);

						if (saved)
							return ToastAndroid.show(
								"Shortened URL copied to clipboard",
								ToastAndroid.SHORT,
							);

						setOutputUrl(shortenedUrlData.url);
					}}
				>
					<Text style={styles.buttonText}>Shorten</Text>
				</Pressable>

				{outputUtl && (
					<View>
						<Text style={styles.headerText}>Shortened URL:</Text>
						<TextInput
							style={styles.textInput}
							value={outputUtl}
							showSoftInputOnFocus={false}
							placeholderTextColor="#222c47"
						/>
					</View>
				)}
			</View>
		</View>
	);
}