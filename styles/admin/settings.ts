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
		paddingVertical: 10,
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
	multilneTextInput: {
		height: 150,
		textAlignVertical: "top",
		fontFamily: "monospace",
	},
	inputHeader: {
		marginTop: 5,
		fontSize: 18,
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
	externalLinkTitle: {
		marginTop: 10,
		marginBottom: 5,
		fontSize: 18,
		fontWeight: "bold",
		color: "white",
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
	externalUrlsScrollView: {
		maxHeight: 300,
		borderRadius: 8,
	},
});
