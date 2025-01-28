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
		flex: 1
	},
    settingGroup: {
        margin: 10,
        padding: 10,
		borderWidth: 4,
		borderColor: "#222c47",
		borderRadius: 8
    },
	headerText: {
		marginTop: 5,
		marginLeft: 10,
		fontSize: 23,
		fontWeight: "bold",
		color: "white",
	},
	oauthSubSettingText: {
	    fontSize: 18
	},
	oauthSubSettingTextColored: {
	    color: "575DB5"
	},
    settingSaveButton: {
		backgroundColor: "#323ea8",
		padding: 10,
		marginTop: 10,
		borderRadius: 6,
	},
    settingSaveButtonText: {
		textAlign: "center",
		fontWeight: "bold",
		color: "white",
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
	multilneTextInput: {
		height: 150,
		textAlignVertical: "top"
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
})