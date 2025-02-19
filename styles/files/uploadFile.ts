import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	mainContainer: {
		backgroundColor: "#0c101c",
		flex: 1,
	},
	headerText: {
		marginTop: 5,
		marginLeft: 10,
		fontSize: 23,
		fontWeight: "bold",
		color: "white",
	},
	subHeaderText: {
		marginLeft: 10,
		fontSize: 13,
		fontWeight: "bold",
		color: "gray",
	},
	scrollView: {
		marginTop: 10,
		borderStyle: "solid",
		borderWidth: 2,
		borderColor: "#222c47",
		marginHorizontal: 10,
		borderRadius: 15,
		height: 220,
	},
	scrollViewKeyboardOpen: {
		display: "none",
	},
	recentFileContainer: {
		marginHorizontal: 10,
		marginVertical: 7.5,
	},
	inputHeader: {
		marginTop: 5,
		fontSize: 18,
		fontWeight: "bold",
		color: "white",
	},
	inputHeaderDisabled: {
		color: "gray",
	},
	inputsContainer: {
		width: "90%",
		marginHorizontal: "auto",
		marginTop: 20,
		marginBottom: 5,
	},
	uploadedFileContainer: {
		flexDirection: "row",
		alignItems: "center",
	},
	uploadedFileUrl: {
		textAlignVertical: "center",
		color: "#575db5",
		width: "70%",
	},
	failedFileText: {
		textAlign: "center",
		color: "white",
	},
	popupScrollView: {
		flexGrow: 0,
		flexShrink: 1,
		maxHeight: 400,
	},
	popupSubHeaderText: {
		marginTop: 10,
	},
	header: {
		flexDirection: "row",
		justifyContent: "space-between",
		alignItems: "center",
	},
	headerButtons: {
		flexDirection: "row",
		justifyContent: "space-around",
		paddingTop: 10,
		paddingRight: 10,
	},
	errorText: {
		color: "red",
		fontWeight: "bold",
		textAlign: "center",
	},
	selectText: {
		flex: 1,
		textAlignVertical: "center",
		fontSize: 15,
		color: "white",
	},
	selectButtonContainer: {
		flexDirection: "row",
	},
	selectItemContainer: {
		flexDirection: "row",
		justifyContent: "space-between",
		width: "100%",
	},
});
