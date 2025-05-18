import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	currentVersionContainer: {
		flexDirection: "row",
	},
	versionContainer: {
		padding: 1,
		paddingHorizontal: 4,
		borderRadius: 5,
		backgroundColor: "#161e35",
		flexDirection: "row",
	},
	versionText: {
		color: "gray",
		fontWeight: "bold",
	},
	upstreamVersionIndicator: {
		width: 10,
		height: 10,
		borderRadius: "50%",
		alignSelf: "center",
		marginRight: 5,
	},
	largeUpstreamVersionIndicator: {
		width: 10,
		height: 10,
		borderRadius: "50%",
		alignSelf: "center",
		marginLeft: 5,
		marginTop: 10,
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
		fontSize: 12,
		fontWeight: "bold",
		color: "gray",
	},
	text: {
		color: "white",
		fontSize: 16,
	},
	boldText: {
		fontWeight: "bold",
	},
	versionDataContainer: {
		borderRadius: 16,
		borderColor: "#212c47",
		borderWidth: 2,
		width: "100%",
		padding: 10,
		marginTop: 5,
	},
	versionDataRow: {
		flexDirection: "row",
		justifyContent: "space-between",
	},
	versionDataKey: {
		color: "gray",
		fontWeight: "bold",
	},
	versionDataValue: {
		color: "white",
	},
	versionDataLink: {
		color: "#575db5",
	},
});
