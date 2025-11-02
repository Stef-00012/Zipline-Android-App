import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	mainContainer: {
		backgroundColor: "#0c101c",
		flex: 1,
	},
	settingsContainer: {
		flex: 1,
	},

	multilineTextInput: {
		height: 150,
		textAlignVertical: "top",
		fontFamily: "monospace",
	},
	scrollView: {
		flex: 1,
	},
	settingGroup: {
		margin: 10,
		padding: 10,
		borderWidth: 4,
		borderColor: "#222c47",
		borderRadius: 8,
	},
	header: {
		paddingVertical: 10,
	},
	headerText: {
		marginTop: 5,
		marginLeft: 10,
		fontSize: 23,
		fontWeight: "bold",
		color: "white",
	},
	headerDescription: {
		marginLeft: 10,
		color: "#6c7a8d",
	},
	errorText: {
		color: "red",
		fontWeight: "bold",
		textAlign: "center",
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
	loadingContainer: {
		display: "flex",
		flex: 1,
	},
	loadingText: {
		fontSize: 40,
		fontWeight: "bold",
		margin: "auto",
		color: "#cdd6f4",
		justifyContent: "center",
		alignItems: "center",
	},
	avatarPreviewContainer: {
		marginTop: 10,
		padding: 10,
		borderWidth: 4,
		borderColor: "#222c47",
		borderRadius: 8,
	},
	avatarPreviewHeader: {
		fontSize: 15,
		fontWeight: "bold",
		color: "gray",
		marginBottom: 5,
	},
	avatarButtonsContainer: {
		flexDirection: "row",
		justifyContent: "flex-start",
	},
	row: {
		backgroundColor: "#0c101c",
		padding: 8,
		borderTopWidth: 1,
		borderTopColor: "",
		borderBottomWidth: 1,
		borderBottomColor: "",
	},
	rowText: {
		color: "white",
		textAlign: "center",
	},
	headerRow: {
		fontWeight: "bold",
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
	exportsContainer: {
		borderWidth: 4,
		borderColor: "#222c47",
		borderStyle: "solid",
		borderRadius: 10,
		margin: 10,
	},
	tableHeader: {
		height: 50,
		padding: 8,
		backgroundColor: "#0c101c",
		borderTopLeftRadius: 10,
		borderTopRightRadius: 10,
	},
	actionsContainer: {
		flexDirection: "row",
		justifyContent: "space-around",
		paddingTop: 10,
		paddingRight: 10,
	},
	serverActionButtonRow: {
		flexDirection: "row",
	},
	popupContent: {
		height: "auto",
		width: "100%",
	},
	mainHeaderText: {
		fontSize: 22,
		fontWeight: "bold",
		color: "white",
	},
	popupSubHeaderText: {
		marginTop: 10,
		marginLeft: 10,
		fontSize: 13,
		fontWeight: "bold",
		color: "gray",
	},
	serverActionWarningText: {
		marginTop: 10,
		color: "white",
	},
	manageServerActionButtonsContainer: {
		flexDirection: "row",
		justifyContent: "space-between",
	},
	versionContainer: {
		flexDirection: "row",
	},
	linkText: {
		color: "#5259AB",
		textDecorationColor: "#5259AB",
		textDecorationLine: "underline",
		textDecorationStyle: "solid",
	},
	rowIdCompleted: {
		color: "#66D57B",
	},
	rowIdProgress: {
		color: "#69788A"
	}
});
