import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	overlayContainer: {
		...StyleSheet.absoluteFillObject,
		flex: 1,
		display: "flex",
		backgroundColor: "#0c101c",
		justifyContent: "center",
		alignItems: "center",
		zIndex: 999999,
		// marginBottom: -20,
		// paddingBottom: 30,
	},
	noInternetText: {
		fontSize: 30,
		fontWeight: "bold",
		margin: "auto",
		justifyContent: "center",
		alignItems: "center",
		color: "#ffffff",
	},
});
