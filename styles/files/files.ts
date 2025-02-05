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
	pageButton: {
		flex: 1,
		padding: 10,
		backgroundColor: "#000010",
		borderWidth: 2,
		borderRadius: 10,
		borderColor: "#222c47",
		margin: 5,
		alignItems: "center",
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
	pageButtonTextDisabled: {
		color: "gray",
	},
	inputDisabled: {
		color: "gray",
	},
	pageButtonText: {
		color: "white",
		fontWeight: "bold",
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
	headerButton: {
		borderWidth: 2,
		borderRadius: 7,
		borderColor: "#222c47",
		marginHorizontal: 2,
		color: "#2d3f70",
	},
	headerStarButtonActive: {
		color: "#f1d01f"
	},
	button: {
		width: "100%",
		backgroundColor: "#323ea8",
		padding: 10,
		marginTop: 15,
		borderRadius: 6,
		margin: "auto",
	},
	buttonText: {
		textAlign: "center",
		fontWeight: "bold",
		color: "white",
	},
	errorText: {
		color: "red",
		fontWeight: "bold",
		textAlign: "center",
	},
	popupHeaderText: {
        marginTop: 10,
        fontSize: 16,
        fontWeight: "bold",
        color: "white",
    },
	mainHeaderText: {
        fontSize: 22,
        fontWeight: "bold",
        color: "white",
    },
	popupContent: {
        height: "auto",
        width: "100%",
		padding: 10
    },
	popupScrollView: {
		flexGrow: 0,
		flexShrink: 1,
		maxHeight: 500
	},
	popupSubHeaderText: {
        marginTop: 10,
        marginLeft: 10,
		fontSize: 13,
		fontWeight: "bold",
		color: "gray",
    },
	textInput: {
		width: "100%",
		borderWidth: 2,
		borderColor: "#222c47",
		margin: "auto",
		color: "white",
		height: 40,
		paddingHorizontal: 10,
		fontSize: 15,
		borderRadius: 6,
		marginTop: 5,
	},
	tagContainer: {
		flexDirection: "row",
		justifyContent: "space-between",
		alignItems: "center",
		paddingVertical: 4
	},
	tagButtonContainer: {
		flexDirection: "row",
		justifyContent: "flex-end",
		alignItems: "center",
	},
	tagButton: {
		borderRadius: 8,
		marginHorizontal: 5,
		backgroundColor: "#323ea8",
		padding: 5,
	},
	tagButtonDanger: {
		backgroundColor: "#CF4238",
	},
	tagName: {
		padding: 3,
		borderRadius: 8,
	},
	tagFilesText: {
		color: "gray",
		marginLeft: 10
	},
	manageTagButton: {
		width: "45%",
		marginHorizontal: "2.5%",
	},
	manageTagButtonsContainer: {
		flexDirection: "row",
		justifyContent: "space-between",
	},
	guessButton: {
		backgroundColor: "#616060"
	}
});
