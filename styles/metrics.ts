import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    mainContainer: {
		backgroundColor: "#0c101c",
		flex: 1,
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
	headerText: {
        marginTop: 5,
        marginLeft: 10,
        fontSize: 32,
        fontWeight: "bold",
        color: "white",
    },
	header: {
        flexDirection: "row",
        justifyContent: "flex-start",
        alignItems: "center",
    },
	dateRangeText: {
		color: "gray",
		textAlignVertical: "center",
		fontSize: 12
	}
});
