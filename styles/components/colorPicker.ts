import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	inputHeader: {
		marginTop: 5,
		marginBottom: 5,
		fontSize: 18,
		fontWeight: "bold",
		color: "white",
	},
	inputHeaderDisabled: {
		color: "gray",
	},
	input: {
		flexDirection: "row",
		alignItems: "center",
		paddingLeft: 12,
		justifyContent: "space-between",
		borderWidth: 2,
		borderColor: "#222c47",
		height: 40,
		paddingHorizontal: 10,
		borderRadius: 6,
	},
	inputPreview: {
		width: 25,
		height: 25,
		borderRadius: 15,
		marginHorizontal: 10,
		borderWidth: 1.5,
	},
	pickerPreview: {
		height: 40,
		borderRadius: 20,
		borderWidth: 1.5,
		borderColor: "#222c47",
		marginBottom: 10,
	},
	panel: {
		borderRadius: 16,
	},
	slider: {
		borderRadius: 20,
		marginTop: 20,
	},
	swatchesContainer: {
		paddingVertical: 10,
		marginTop: 20,
		borderTopWidth: 2,
		borderColor: "#222c47",
		alignItems: "center",
		flexWrap: "nowrap",
		gap: 10,
	},
	swatches: {
		borderRadius: 20,
		height: 30,
		width: 30,
		borderWidth: 1.5,
		borderColor: "#222c47",
		margin: 0,
		marginBottom: 0,
		marginHorizontal: 0,
		marginVertical: 0,
	},
	colorInputMainContainer: {
		paddingTop: 20,
		borderTopWidth: 2,
		borderColor: "#222c47",
	},
	colorInputContainer: {
		paddingHorizontal: 3,
	},
	colorInput: {
		color: "white",
		borderColor: "#222c47",
		borderWidth: 2,
	},
});
