import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	shortenContainer: {
		display: "flex",
		flex: 1,
		backgroundColor: "#0c101c",
		justifyContent: "center",
		alignContent: "center",
	},
	shortenBox: {
		width: "90%",
		borderWidth: 4,
		borderColor: "#222c47",
		borderStyle: "solid",
		padding: 10,
		borderRadius: 10,
		margin: "auto",
	},
	errorText: {
		color: "red",
		fontWeight: "bold",
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
	mainHeaderText: {
		fontSize: 23,
		fontWeight: "bold",
		color: "white",
	},
});
