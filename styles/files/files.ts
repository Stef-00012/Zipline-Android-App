import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	mainContainer: {
		backgroundColor: "#0c101c",
		flex: 1,
	},
	headerText: {
		marginTop: 5,
		marginLeft: 10,
		fontSize: 32,
		fontWeight: "bold",
		color: "white",
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
	imagesContainer: {
		borderWidth: 2,
		borderColor: "#222c47",
		borderStyle: "solid",
		borderRadius: 10,
		margin: 10,
		flex: 1,
		flexGrow: 1,
	},
	pagesContainer: {
		height: 50,
		flexDirection: "row",
		justifyContent: "space-between",
		alignItems: "center",
		flexWrap: "wrap",
		margin: 10,
	},
	imageContainer: {
		padding: 5,
		margin: 5,
		borderWidth: 2,
		borderColor: "#222c47",
		borderStyle: "solid",
		borderRadius: 10,
	},
	input: {
		flex: 1,
		padding: 10,
		height: 40,
		width: 70,
		backgroundColor: "#010124",
		color: "white",
		margin: 5,
		borderWidth: 2,
		borderRadius: 10,
		borderColor: "#222c47",
		textAlign: "center",
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
	mainHeaderText: {
		fontSize: 22,
		fontWeight: "bold",
		color: "white",
	},
	popupContent: {
		height: "auto",
		width: "100%",
		padding: 10,
	},
	popupScrollView: {
		flexGrow: 0,
		flexShrink: 1,
		maxHeight: 500,
	},
	popupSubHeaderText: {
		marginTop: 10,
		marginLeft: 10,
		fontSize: 13,
		fontWeight: "bold",
		color: "gray",
	},
	tagContainer: {
		flexDirection: "row",
		justifyContent: "space-between",
		alignItems: "center",
		paddingVertical: 4,
	},
	tagButtonContainer: {
		flexDirection: "row",
		justifyContent: "flex-end",
		alignItems: "center",
	},
	tagName: {
		padding: 3,
		borderRadius: 8,
	},
	tagFilesText: {
		color: "gray",
		marginLeft: 10,
	},
	manageTagButtonsContainer: {
		flexDirection: "row",
		justifyContent: "space-between",
	},
	rowText: {
		color: "white",
		textAlign: "center",
	},
	headerRow: {
		fontWeight: "bold",
	},
	row: {
		backgroundColor: "#0c101c",
		padding: 8,
		borderTopWidth: 1,
		borderTopColor: "",
		borderBottomWidth: 1,
		borderBottomColor: "",
	},
	lastRow: {
		borderBottomWidth: 0,
	},
	firstRow: {
		borderTopWidth: 0,
	},
	actionsContainer: {
		flexDirection: "row",
		justifyContent: "space-around",
		paddingTop: 10,
		paddingRight: 10,
	},
	tableHeader: {
		height: 50,
		padding: 8,
		backgroundColor: "#0c101c",
		borderTopLeftRadius: 10,
		borderTopRightRadius: 10,
	},
	tableVerticalScroll: {
		borderBottomLeftRadius: 10,
		borderBottomRightRadius: 10,
	},
	tagsContainer: {
		flexDirection: "row",
		marginHorizontal: 10,
		justifyContent: "center"
	},
	tag: {
		padding: 5,
		borderRadius: 50,
		marginHorizontal: 5
	}
});
