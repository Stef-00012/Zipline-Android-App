import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	loginContainer: {
		display: "flex",
		flex: 1,
		backgroundColor: "#0c101c",
		justifyContent: "center",
		alignContent: "center",
	},
	loginBox: {
		width: "80%",
		borderWidth: 4,
		borderColor: "#222c47",
		borderStyle: "solid",
		padding: 10,
		borderRadius: 10,
		margin: "auto",
	},
	errorText: {
		color: "red",
	},
});
