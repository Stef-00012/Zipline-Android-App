import { Pressable, Text, View } from "react-native";
import { useRouter } from "expo-router";

import { styles } from "@/styles/not-found";

export default function NotFoundScreen() {
	const router = useRouter();

	return (
		<View style={styles.mainContainer}>
			<View>
				<Text style={styles.code}>404</Text>

				<Text style={styles.text}>This page doesn't exist</Text>

				<Pressable
					style={styles.button}
					onPress={() => {
						router.replace({
							pathname: "/",
						});
					}}
				>
					<Text style={styles.buttonText}>Head to the Dashboard</Text>
				</Pressable>
			</View>
		</View>
	);
}