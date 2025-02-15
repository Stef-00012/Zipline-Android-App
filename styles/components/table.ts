import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    tableHeader: {
		height: 50,
		padding: 8,
		backgroundColor: "#0c101c",
		borderTopLeftRadius: 10,
		borderTopRightRadius: 10,
	},
	row: {
		backgroundColor: "#0c101c",
		padding: 8,
		borderTopWidth: 1,
		borderBottomWidth: 1,
	},
	lastRow: {
		borderBottomWidth: 0,
	},
	firstRow: {
		borderTopWidth: 0,
	},
	tableVerticalScroll: {
		borderBottomLeftRadius: 10,
		borderBottomRightRadius: 10,
	},
	rowText: {
		color: "white",
		textAlign: "center",
	},
	headerRow: {
		fontWeight: "bold",
	},
});
