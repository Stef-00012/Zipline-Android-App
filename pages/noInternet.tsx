import MaterialSymbols from "@/components/MaterialSymbols";
import { styles } from "@/styles/pages/noInternet";
import { Text, View } from "react-native";

export default function NoInternetPage() {
	return (
		<View style={styles.overlayContainer}>
			<View
				style={{
					display: "flex",
					justifyContent: "center",
					alignItems: "center",
					marginBottom: 20,
				}}
			>
				<MaterialSymbols size={50} name="wifi_off" color="#ffffff" />

				<Text style={styles.noInternetText}>No internet connection.</Text>
			</View>
		</View>
	);
}
