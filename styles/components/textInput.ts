import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	inputContainer: {
		flexDirection: "row",
		height: 40,
	},
	textInput: {
		borderWidth: 2,
		borderColor: "#222c47",
		color: "white",
		height: 40,
		paddingHorizontal: 10,
		fontSize: 15,
		borderRadius: 6,
	},
	textInputSideButton: {
		flex: 1,
	},
	inputHeader: {
		marginTop: 5,
		fontSize: 18,
		fontWeight: "bold",
		color: "white",
	},
	inputDescription: {
		color: "#6c7a8d",
		marginBottom: 5,
	},
	textInputDisabled: {
		color: "gray",
	},
	inputHeaderDisabled: {
		color: "gray",
	},
});
