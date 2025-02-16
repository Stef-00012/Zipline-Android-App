import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	tableHeader: {
		height: 50,
		padding: 8,
		backgroundColor: "#0c101c",
		borderTopLeftRadius: 10,
		borderTopRightRadius: 10,
		borderBottomWidth: 2,
		borderColor: "#222c4755",
	},
	row: {
		backgroundColor: "#0c101c",
		padding: 8,
		borderTopWidth: 1,
		borderBottomWidth: 1,
		borderColor: "#222c4755",
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
	},
	headerRow: {
		fontWeight: "bold",
		color: "white",
	},
	actionsContainer: {
		flexDirection: "row",
		justifyContent: "flex-end",
	},
	sortableIcon: {
		alignSelf: "center",
	},
	searchButton: {
		borderRadius: 5,
		backgroundColor: "#191b27",
		padding: 2,
		marginLeft: 4,
	},
	rowContainer: {
		flexDirection: "row",
		justifyContent: "space-between",
		marginHorizontal: 10,
	},
});
