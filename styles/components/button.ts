import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	buttonContainer: {
		overflow: "hidden",
		borderRadius: 6,
		boxShadow: "0 4px 8px rgba(0, 0, 0, 0.125)",
	},
	button: {
		height: 40,
		alignItems: "center",
		flexDirection: "row",

		justifyContent: "center",
		paddingHorizontal: 4,
		paddingVertical: 2,
		gap: 6,
	},
	buttonText: {
		textAlign: "center",
		fontWeight: "bold",
	},
});
