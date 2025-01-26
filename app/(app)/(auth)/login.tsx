import { Pressable, Text, View, TextInput } from "react-native";
import { isAuthenticated } from "@/functions/zipline/auth";
import { useRouter } from "expo-router";
import { useState } from "react";
import * as db from "@/functions/database";
import { styles } from "@/styles/auth/login";
import { useLoginAuth } from "@/hooks/useLoginAuth";

export default function Login() {
	const router = useRouter();

	const [error, setError] = useState<string>();

	const [inputtedUrl, setInputtedUrl] = useState<string | null>(db.get("url"));
	const [inputtedToken, setInputtedToken] = useState<string | null>(db.get("token"));

    const urlRegex = /^http:\/\/(.*)?|https:\/\/(.*)?$/;

	useLoginAuth()

	return (
		<View style={styles.loginContainer}>
			<View style={styles.loginBox}>
				{error && <Text style={styles.errorText}>{error}</Text>}

				<TextInput
					onChangeText={(content) => {
                        setInputtedUrl(content.length > 0 ? content : null);
					}}
					keyboardType="url"
					value={inputtedUrl || undefined}
                    style={styles.textInput}
					placeholderTextColor="#222c47"
					placeholder="Zipline URL"
				/>

				<TextInput
					onChangeText={(content) => {
                        setInputtedToken(content.length > 0 ? content : null);
					}}
					value={inputtedToken || undefined}
                    style={styles.textInput}
					secureTextEntry={true}
					keyboardType="visible-password"
					placeholderTextColor="#222c47"
					placeholder="Zipline Token"
                    selectTextOnFocus
				/>
				<Pressable
					style={styles.button}
					onPress={async () => {
                        setError(undefined)

                        if (!inputtedUrl) return setError("Please insert your Zipline URL");
                        if (!urlRegex.test(inputtedUrl)) return setError("Please insert a valid URL");

                        if (!inputtedToken) return setError("Please insert your token");

						const url = inputtedUrl.endsWith("/") ? inputtedUrl.slice(0, -1) : inputtedUrl

						db.set("url", url);
						db.set("token", inputtedToken);

						const authenticated = await isAuthenticated();

                        if (!authenticated) return setError(
							"There was an error during the login, make sure your token is valid and your server is running Zipline V4",
						);

						return router.replace("/");
					}}
				>
					<Text style={styles.buttonText}>Login</Text>
				</Pressable>
			</View>
		</View>
	);
}
