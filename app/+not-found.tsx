import { Pressable, Text, View } from "react-native";
import { useRouter } from "expo-router";

import { styles } from "@/styles/not-found";
import Button from "@/components/Button";

export default function NotFoundScreen() {
	const router = useRouter();

	return (
		<View style={styles.mainContainer}>
			<View>
				<Text style={styles.code}>404</Text>

				<Text style={styles.text}>This page does not exist.</Text>

				<Button onPress={() => {
						// router.replace({
						// 	pathname: "/",
						// });
					}} text="Head to the Dashboard" color="#323ea8" />

				{/* <Pressable
					style={styles.button}
					onPress={() => {
						router.replace({
							pathname: "/",
						});
					}}
				>
					<Text style={styles.buttonText}>Head to the Dashboard</Text>
				</Pressable> */}
			</View>
		</View>
	);
}