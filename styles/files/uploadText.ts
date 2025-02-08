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
	},
	popupSubHeaderText: {
		marginTop: 10,
	},
	mainTextInput: {
		borderRadius: 15,
		padding: 15,
		height: 220,
		textAlignVertical: "top",
	},
	mainTextInputContainer: {
		marginHorizontal: 10,
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
});
