import { KeyboardAvoidingView } from "react-native-keyboard-controller";
import { isAuthenticated, login } from "@/functions/zipline/auth";
import { getVersion } from "@/functions/zipline/version";
import TextInput from "@/components/TextInput";
import * as db from "@/functions/database";
import { Text, View } from "react-native";
import Button from "@/components/Button";
import { useRouter } from "expo-router";
import { styles } from "@/styles/login";
import { useContext, /*useEffect,*/ useState } from "react";
import semver from "semver";
import { minimumVersion } from "@/constants/auth";
import { AuthContext } from "@/contexts/AuthProvider";

export default function Login() {
	const router = useRouter();
	const { updateAuth, updateUser } = useContext(AuthContext) 

	const [error, setError] = useState<string>();
	const [tokenLogin, setTokenLogin] = useState<boolean>(false);

	const [inputtedUrl, setInputtedUrl] = useState<string | null>(db.get("url"));
	const [inputtedToken, setInputtedToken] = useState<string | null>(
		db.get("token"),
	);

	const [inputtedUsername, setInputtedUsername] = useState<string | undefined>(
		undefined,
	);
	const [inputtedPassword, setInputtedPassword] = useState<string | undefined>(
		undefined,
	);
	const [inputtedTOTP, setInputtedTOTP] = useState<string | undefined>(
		undefined,
	);
	const [totpRequired, setTOTPRequired] = useState<boolean>(false);

	const urlRegex = /^http:\/\/(.*)?|https:\/\/(.*)?$/;

	// // biome-ignore lint/correctness/useExhaustiveDependencies: Functions should not be parameters of the effect
	// useEffect(() => {
	// 	if (role) {
	// 		updateAuth();
	// 		router.replace("/");
	// 	}
	// }, [role])

	return (
		<KeyboardAvoidingView behavior="height" style={styles.loginContainer}>
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
				) : (
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
						top: 5,
					}}
				/>

				<Button
					onPress={async () => {
						setError(undefined);

						if (!inputtedUrl) return setError("Please insert your Zipline URL");
						if (!urlRegex.test(inputtedUrl))
							return setError("Please insert a valid URL");

						const url = inputtedUrl.endsWith("/")
							? inputtedUrl.slice(0, -1)
							: inputtedUrl;

						db.set("url", url);

						if (tokenLogin) {
							if (!inputtedToken) return setError("Please insert your token");

							db.set("token", inputtedToken);

							const authenticated = await isAuthenticated();

							if (!authenticated)
								return setError(
									"There was an error during the login, make sure your token is valid and your server is running Zipline V4",
								);

							const versionData = await getVersion();

							const serverVersion =
								typeof versionData === "string"
									? "0.0.0"
									: "version" in versionData
										? versionData.version
										: versionData.details?.version;

							if (semver.lt(serverVersion, minimumVersion)) {
								await db.del("url");
								await db.del("token");

								return setError(
									`You must use Zipline v${minimumVersion} or greater and have 'Version checking' feature enabled`,
								);
							}

							await updateAuth();
							await updateUser();

							return router.replace("/");
						}

						if (!inputtedPassword)
							return setError("Please insert your password");

						if (!inputtedUsername)
							return setError("Please insert your username");

						const loginRes = await login(
							inputtedUsername,
							inputtedPassword,
							inputtedTOTP,
						);

						if (typeof loginRes === "string") return setError(loginRes);

						if (loginRes.totp) return setTOTPRequired(true);

						const token = loginRes.token;

						if (!token) return setError("Something went wrong...");

						db.set("token", token);

						const versionData = await getVersion();

						const serverVersion =
							typeof versionData === "string"
								? "0.0.0"
								: "version" in versionData
									? versionData.version
									: versionData.details.version;

						if (semver.lt(serverVersion, minimumVersion)) {
							await db.del("url");
							await db.del("token");

							return setError(
								`You must use Zipline v${minimumVersion} or greater and have 'Version checking' feature enabled`,
							);
						}

						await updateAuth();
						await updateUser();

						return router.replace("/");
					}}
					text="Login"
					color="#323ea8"
					margin={{
						top: 5,
					}}
				/>
			</View>
		</KeyboardAvoidingView>
	);
}
