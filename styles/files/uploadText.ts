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
        padding: 15,
        height: 220,
    },
    scrollViewKeyboardOpen: {
        display: "none"
    },
    recentFileContainer: {
        marginHorizontal: 10,
    },
    selectFilesButton: {
        width: "90%",
        margin: "auto",
    },
    button: {
        backgroundColor: "#323ea8",
        padding: 10,
        marginTop: 10,
        borderRadius: 6,
    },
    uploadButton: {
        width: "90%",
        margin: "auto",
        marginBottom: 10,
    },
    buttonDisabled: {
        backgroundColor: "#373d79",
    },
    buttonText: {
        textAlign: "center",
        fontWeight: "bold",
        color: "white",
    },
    buttonTextDisabled: {
        color: "gray",
    },
    textInput: {
        borderWidth: 2,
        borderColor: "#222c47",
        color: "white",
        height: 40,
        paddingHorizontal: 10,
        fontSize: 15,
        borderRadius: 6,
        marginTop: 5,
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
    switchContainer: {
        flexDirection: "row",
        marginTop: 10,
    },
    switchText: {
        color: "white",
        fontSize: 16,
        marginLeft: 5,
        fontWeight: "bold",
        textAlignVertical: "center",
    },
    switchTextDisabled: {
        color: "gray",
    },
    select: {
        borderWidth: 2,
        borderColor: "#222c47",
        color: "white",
        height: 40,
        padding: 3,
        borderRadius: 6,
        marginTop: 5,
        textAlign: "center",
    },
    uploadedFileContainer: {
        flexDirection: "row",
    },
    uploadedFileButton: {
        padding: 10,
        backgroundColor: "#323ea8",
        borderWidth: 2,
        borderRadius: 10,
        margin: 5,
        width: 40,
        height: 40,
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
    popupHeaderText: {
        marginTop: 5,
        marginLeft: 10,
        fontSize: 18,
        fontWeight: "bold",
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
        marginHorizontal: 10
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
});
