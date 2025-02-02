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
	inputHeader: {
		marginTop: 5,
		fontSize: 18,
		fontWeight: "bold",
		color: "white",
	},
	textInput: {
		width: "100%",
		borderWidth: 2,
		borderColor: "#222c47",
		marginBottom: 5,
		marginTop: 5,
		color: "white",
		height: 40,
		paddingHorizontal: 10,
		fontSize: 15,
		borderRadius: 6,
	},
	button: {
		width: "100%",
		backgroundColor: "#323ea8",
		padding: 10,
		marginTop: 5,
		borderRadius: 6,
	},
	buttonSecondary: {
		backgroundColor: "#616060"
	},
	buttonText: {
		textAlign: "center",
		fontWeight: "bold",
		color: "white",
	},
	errorText: {
		color: "red",
	},
});
