import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	mainContainer: {
		backgroundColor: "#181c28",
		borderRadius: 10,
		marginVertical: 5,
		marginHorizontal: 10,
	},
	titleContainer: {
		padding: 10,
		flexDirection: "row",
		justifyContent: "space-between",
	},
	urlCode: {
		textAlignVertical: "center",
		color: "white",
		fontSize: 16,
	},
	link: {
		color: "#575DB5",
	},
	divider: {
		height: 3,
		width: "100%",
		backgroundColor: "#222c47",
	},
	keysContainer: {
		padding: 10,
	},
	key: {
		color: "#6c7a8d",
		fontSize: 14,
	},
	keyName: {
		fontWeight: "bold",
	},
});
