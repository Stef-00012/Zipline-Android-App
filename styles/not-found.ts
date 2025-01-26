import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	mainContainer: {
		flex: 1,
		display: "flex",
		backgroundColor: "#0c101c",
		justifyContent: "center",
		alignItems: "center",
	},
	code: {
		color: "#304270",
		fontSize: 90,
		fontWeight: "bold",
		margin: "auto",
	},
	text: {
		color: "#304270",
		fontSize: 30,
		fontWeight: "bold",
		margin: "auto",
		justifyContent: "center",
		alignItems: "center",
	},
	button: {
		color: "white",
		backgroundColor: "#323ea8",
		padding: 10,
		borderRadius: 10,
		marginTop: 15
	},
	buttonText: {
		textAlign: "center",
		fontWeight: "bold",
		color: "white",
		fontSize: 16,
	},
});
