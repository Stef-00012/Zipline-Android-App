import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	headerContainer: {
		flex: 1,
	},
	header: {
		position: "absolute",
		top: 0,
		left: 0,
		right: 0,
		flexDirection: "row",
		justifyContent: "space-between",
		alignItems: "center",
		padding: 10,
		backgroundColor: "#0c101c",
		zIndex: 99999,
		borderBottomWidth: 2,
		borderBottomColor: "#222c47",
		height: 70,
	},
	headerLeft: {
		flexDirection: "column",
	},
});
