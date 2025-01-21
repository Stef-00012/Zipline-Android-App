import { useRouter } from "expo-router";
import { Pressable, Text, View } from "react-native";

import { styles } from "@/styles/not-found";

export default function NotFoundScreen() {
	const router = useRouter();

	return (
		<View style={styles.container}>
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
				Head to the Dashboard
			</Pressable>
		</View>
	);
}