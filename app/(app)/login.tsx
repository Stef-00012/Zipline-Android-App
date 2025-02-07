import { Pressable, Text, View } from "react-native";
import { isAuthenticated, login } from "@/functions/zipline/auth";
import { useRouter } from "expo-router";
import { useState } from "react";
import * as db from "@/functions/database";
import { styles } from "@/styles/login";
import { useLoginAuth } from "@/hooks/useLoginAuth";
import React from "react";
import TextInput from "@/components/TextInput";
import Button from "@/components/Button";

export default function Login() {
	const router = useRouter();

	const [error, setError] = useState<string>();
	const [tokenLogin, setTokenLogin] = useState<boolean>(false)

	const [inputtedUrl, setInputtedUrl] = useState<string | null>(db.get("url"));
	const [inputtedToken, setInputtedToken] = useState<string | null>(db.get("token"));

	const [inputtedUsername, setInputtedUsername] = useState<string | undefined>(undefined);
	const [inputtedPassword, setInputtedPassword] = useState<string | undefined>(undefined);
	const [inputtedTOTP, setInputtedTOTP] = useState<string | undefined>(undefined);
	const [totpRequired, setTOTPRequired] = useState<boolean>(false);

    const urlRegex = /^http:\/\/(.*)?|https:\/\/(.*)?$/;

	useLoginAuth()

	return (
		<View style={styles.loginContainer}>
			<View style={styles.loginBox}>
				{error && <Text style={styles.errorText}>{error}</Text>}

				{tokenLogin ? (
					<>
						<TextInput
							title="Zipline URL:"
							onValueChange={(content) => setInputtedUrl(content)}
							value={inputtedUrl || ""}
							keyboardType="url"
							placeholder="https://example.com"
						/>

						<TextInput
							title="Token:"
							onValueChange={(content) => setInputtedToken(content)}
							placeholder="myPassword123"
							password
						/>
					</>
				): (
					<>
						<TextInput
							title="Zipline URL:"
							onValueChange={(content) => setInputtedUrl(content)}
							value={inputtedUrl || ""}
							keyboardType="url"
							placeholder="https://example.com"
						/>

						<TextInput
							title="Username:"
							onValueChange={(content) => setInputtedUsername(content)}
							placeholder="My Cool Username"
						/>

						<TextInput
							title="Password:"
							onValueChange={(content) => setInputtedPassword(content)}
							placeholder="myPassword123"
							password
						/>

						{totpRequired && (
							<TextInput
								title="TOTP:"
								onValueChange={(content) => setInputtedTOTP(content)}
								keyboardType="numeric"
								placeholder="123456"
								maxLength={6}
							/>
						)}
					</>
				)}

				<Button
					onPress={() => setTokenLogin((prev) => !prev)}
					color="#616060"
					text={`Use ${tokenLogin ? "Password" : "Token"} Login`}
					margin={{
						top: 5
					}}
				/>

				<Button
					onPress={async () => {
                        setError(undefined)

                        if (!inputtedUrl) return setError("Please insert your Zipline URL");
                        if (!urlRegex.test(inputtedUrl)) return setError("Please insert a valid URL");

						const url = inputtedUrl.endsWith("/") ? inputtedUrl.slice(0, -1) : inputtedUrl

						db.set("url", url);

                        if (tokenLogin) {
							if (!inputtedToken) return setError("Please insert your token");

							db.set("token", inputtedToken);

							const authenticated = await isAuthenticated();

							if (!authenticated) return setError(
								"There was an error during the login, make sure your token is valid and your server is running Zipline V4",
							);

							return router.replace("/");
						}

						if (!inputtedPassword) return setError("Please insert your password");
						if (!inputtedUsername) return setError("Please insert your username");

						const loginRes = await login(inputtedUsername, inputtedPassword, inputtedTOTP)

						if (typeof loginRes === "string") return setError(loginRes)

						if (loginRes.totp) return setTOTPRequired(true);

						const token = loginRes.token;

						if (!token) return setError("Something went wrong...");

						db.set("token", token);

						return router.replace("/");
					}}
					text="Login"
					color="#323ea8"
					margin={{
						top: 5
					}}
				/>
			</View>
		</View>
	);
}
