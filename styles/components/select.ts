import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	selectButton: {
		flexDirection: "row",
		justifyContent: "center",
		alignItems: "center",
		paddingHorizontal: 12,
		borderWidth: 2,
		borderColor: "#222c47",
		color: "white",
		padding: 3,
		borderRadius: 6,
		marginTop: 5,
	},
	selectText: {
		flex: 1,
		textAlignVertical: "center",
		fontSize: 15,
		color: "#222c47",
	},
	select: {
		backgroundColor: "#181c28",
		maxHeight: 250,
		width: "90%",
		borderRadius: 10,
	},
	menuItem: {
		flexDirection: "row",
		paddingHorizontal: 12,
		justifyContent: "center",
		alignItems: "center",
		paddingVertical: 8,
		backgroundColor: "#0c101c",
	},
	menuItemText: {
		flex: 1,
		fontSize: 18,
		fontWeight: "bold",
		color: "#a6adc8",
	},
	menuItemSelected: {
		backgroundColor: "#0d1325",
	},
	selectedText: {
		color: "white",
	},
	selectContainer: {
		flex: 1,
		justifyContent: "center",
		alignItems: "center",
		backgroundColor: "rgba(0, 0, 0, 0.5)",
	},
	openSelectContainer: {
		borderRadius: 10,
	},
	selectedTextContainer: {
		flex: 1,
		flexDirection: "row",
		flexWrap: "wrap",
		justifyContent: "flex-start",
		margin: -3,
	},
	selectedItemContainer: {
		margin: 3,
	},
	inputHeader: {
		marginTop: 5,
		fontSize: 18,
		fontWeight: "bold",
		color: "white",
	},
	inputDescription: {
		color: "#6c7a8d",
		marginBottom: 5,
	},
	inputHeaderDisabled: {
		color: "gray",
	},
});
