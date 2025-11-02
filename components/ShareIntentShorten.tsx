import Button from "@/components/Button";
import TextInput from "@/components/TextInput";
import { createURL, type CreateURLParams } from "@/functions/zipline/urls";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import { styles } from "@/styles/components/shareIntentShorten";
import * as Clipboard from "expo-clipboard";
import { useRouter } from "expo-router";
import { useState } from "react";
import { Text, ToastAndroid, View } from "react-native";

interface Props {
	defaultUrl: string;
}

export default function ShareIntentShorten({ defaultUrl }: Props) {
	const router = useRouter();

	useAuth();
	const resetShareIntent = useShareIntent(true);

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
						<Button
							onPress={() => {
								resetShareIntent();

								router.push("/urls");
							}}
							icon="link"
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

				{error && <Text style={styles.errorText}>{error}</Text>}

				<TextInput
					title="URL:"
					onValueChange={(content) => {
						setUrl(content || null);
					}}
					value={url || ""}
					keyboardType="url"
					placeholder="https://google.com"
				/>

				<TextInput
					title="Vanity:"
					onValueChange={(content) => {
						setVanity(content || null);
					}}
					value={vanity || ""}
					placeholder="google"
				/>

				<TextInput
					title="Max Views:"
					onValueChange={(content) => {
						setMaxViews(content || null);
					}}
					value={maxViews || ""}
					placeholder="0"
				/>

				<TextInput
					title="Password:"
					onValueChange={(content) => {
						setPassword(content || null);
					}}
					value={password || ""}
					placeholder="myPassword"
				/>

				<Button
					color="#323ea8"
					text="Shorten"
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

						if (typeof shortenedUrlData === "string")
							return setError(shortenedUrlData);

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
					margin={{
						top: 15,
					}}
				/>

				{outputUtl && (
					<View>
						<TextInput
							title="Shortened URL:"
							value={outputUtl}
							showSoftInputOnFocus={false}
						/>
					</View>
				)}
			</View>
		</View>
	);
}
