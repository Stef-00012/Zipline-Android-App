import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	mainContainer: {
		backgroundColor: "#0c101c",
		flex: 1,
	},
	mainHeader: {
		fontWeight: "bold",
		fontSize: 28,
		color: "white",
		marginHorizontal: 10,
	},
	subHeader: {
		color: "gray",
		marginHorizontal: 10,
	},
	subHeaderNumber: {
		fontWeight: "bold",
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
	scrollView: {
		marginTop: 15,
		borderStyle: "solid",
		borderWidth: 2,
		borderColor: "#222c47",
		marginHorizontal: 10,
		borderRadius: 15,
	},
	recentFileContainer: {
		marginHorizontal: 10,
		marginVertical: 7.5,
	},
	headerText: {
		marginTop: 5,
		marginLeft: 10,
		fontSize: 23,
		fontWeight: "bold",
		color: "white",
	},
	subHeaderText: {
		fontSize: 18,
		fontWeight: "bold",
		color: "gray",
	},
	statText: {
		marginTop: 5,
		fontSize: 32,
		fontWeight: "bold",
		color: "white",
	},
	statContainer: {
		borderWidth: 2,
		borderColor: "#222c47",
		borderStyle: "solid",
		borderRadius: 10,
		padding: 10,
		marginHorizontal: 4,
		marginVertical: 7.5,
	},
	tableText: {
		color: "white",
		fontSize: 16,
		marginHorizontal: 10,
	},
	fileTypesContainer: {
		marginBottom: 10,
		maxHeight: 400,
		padding: 0,
	},
	tableHeadText: {
		color: "white",
		fontWeight: "bold",
		fontSize: 16,
		marginHorizontal: 10,
	},
});
