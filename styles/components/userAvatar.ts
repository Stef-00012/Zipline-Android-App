import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    userMenuContainer: {
		flexWrap: "wrap",
		flexDirection: "row",
		marginRight: 5,
	},
	userMenuAvatar: {
		borderRadius: 8,
	},
	userMenuText: {
		color: "white",
		fontWeight: "bold",
		marginLeft: 10,
		marginTop: 5,
		fontSize: 18,
	},
	userMenuTextWithSettingsIcon: {
		marginLeft: 0
	},
    settingsIcon: {
		width: 38,
		height: 38,
		justifyContent: "center",
		alignItems: "center",
	},
});