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
		flexDirection: "row",
		justifyContent: "space-between",
		alignItems: "center",
		marginVertical: 5,
	},
	headerText: {
		marginTop: 5,
		marginLeft: 10,
		fontSize: 23,
		textAlignVertical: "center",
		fontWeight: "bold",
		color: "white",
	},
	headerDescription: {
		color: "#6c7a8d",
		marginLeft: 10,
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
		fontSize: 18,
		fontWeight: "bold",
		color: "white",
	},
	externalLinkDescription: {
		color: "#6c7a8d",
		marginBottom: 5,
	},
	popupContent: {
		height: "auto",
		width: "100%",
		zIndex: 99999999999,
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
	headerButtons: {
		flexDirection: "row",
		justifyContent: "space-around",
		paddingTop: 10,
		paddingRight: 10,
	},
	popupScrollView: {
		flexGrow: 0,
		flexShrink: 1,
		maxHeight: 400,
	},
	tamperedSettingsWarning: {
		backgroundColor: "#2E1A24",
		padding: 10,
		margin: 10,
		borderRadius: 8,
	},
	tamperedSettingTitle: {
		color: "#F7A4A4",
		fontWeight: "bold",
	},
	tamperedSettingText: {
		color: "#ffffff",
	},
	tamperedSettingCount: {
		fontWeight: "bold",
	},
	tamperedSettingsLink: {
		color: "#4F529D",
		marginBottom: -5,
	},
});
