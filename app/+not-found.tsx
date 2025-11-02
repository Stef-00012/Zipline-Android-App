import Button from "@/components/Button";
import { styles } from "@/styles/not-found";
import { useRouter } from "expo-router";
import { Text, View } from "react-native";

export default function NotFoundScreen() {
	const router = useRouter();

	return (
		<View style={styles.mainContainer}>
			<View>
				<Text style={styles.code}>404</Text>

				<Text style={styles.text}>This page does not exist.</Text>

				<Button
					onPress={() => {
						router.replace({
							pathname: "/",
						});
					}}
					text="Head to the Dashboard"
					color="#323ea8"
				/>
			</View>
		</View>
	);
}
