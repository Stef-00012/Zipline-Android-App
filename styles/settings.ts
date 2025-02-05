import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    mainContainer: {
        backgroundColor: "#0c101c",
        flex: 1,
    },
    settingsContainer: {
        flex: 1,
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
        paddingVertical: 10
    },
    headerText: {
        marginTop: 5,
        marginLeft: 10,
        fontSize: 23,
        fontWeight: "bold",
        color: "white",
    },
    errorText: {
        color: "red",
        fontWeight: "bold",
        textAlign: "center",
    },
    oauthSubSettingText: {
        fontSize: 18,
    },
    oauthSubSettingTextColored: {
        color: "575DB5",
    },
    button: {
        backgroundColor: "#323ea8",
        padding: 10,
        marginTop: 10,
        borderRadius: 6,
        justifyContent: "center"
    },
    buttonSecondary: {
        backgroundColor: "#323244"
    },
    buttonDanger: {
        backgroundColor: "#CF4238"
    },
    buttonText: {
        textAlign: "center",
        fontWeight: "bold",
        color: "white",
    },
    copyButton: {
        marginLeft: 10
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
	textInputDisabled: {
		color: "gray"
	},
    copyInputContainer: {
        width: "88%",
    },
    multilneTextInput: {
        height: 300,
        textAlignVertical: "top",
        fontFamily: "monospace"
    },
    switchContainer: {
        flexDirection: "row",
        marginTop: 10,
    },
    inputContainer: {
        flexDirection: "row",
    },
    switchText: {
        color: "white",
        fontSize: 16,
        marginLeft: 5,
        fontWeight: "bold",
        textAlignVertical: "center",
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
    avatarButton: {
        width: "100%",
        borderWidth: 2,
        borderColor: "#222c47",
        margin: "auto",
        color: "white",
        height: 40,
        justifyContent: "center",
        alignItems: "center",
        fontSize: 15,
        borderRadius: 6,
        marginTop: 5,
    },
    avatarButtonText: {
        textAlign: "center",
        fontWeight: "bold",
        color: "white",
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
        marginBottom: 5
    },
    avatarButtonsContainer: {
		flexDirection: "row",
		justifyContent: "flex-start",
	},
    manageAvatarButton: {
		width: "28.33%",
		marginHorizontal: "2.5%",
	},
    saveAvatarButtonDisabled: {
        backgroundColor: "#181c28"
    },
    saveAvatarButtonTextDisabled: {
        color: "gray"
    },
    cancelAvatarButton: {
        backgroundColor: "transparent",
        borderWidth: 2,
        borderColor: "#ff8787",
    },
    removeAvatarButton: {
        backgroundColor: "#e03131"
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
	actionButton: {
		borderRadius: 4,
		marginHorizontal: 0,
		backgroundColor: "#323ea8",
		padding: 5,
	},
    actionButtonDisabled: {
        backgroundColor: "#181c28"
    },
    actionButtonDanger: {
		backgroundColor: "#CF4238",
	},
    serverActionButtonRow: {
        flexDirection: "row"
    },
    serverActionButton: {
        marginHorizontal: "2.5%",
        width: "45%",
    },
    popupContent: {
        height: "auto",
        width: "100%"
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
        color: "white"
    },
    manageServerActionButtonsContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between'
    },
    manageServerActionButtonDanger: {
        backgroundColor: "#CF4238"
    },
    manageServerActionButtonCancel: {
        backgroundColor: "#181c28"
    }
});
