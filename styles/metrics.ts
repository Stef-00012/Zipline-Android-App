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
        justifyContent: "space-between",
        alignItems: "center",
    },
	dateRangeText: {
		color: "gray",
		textAlignVertical: "center",
		fontSize: 12,
		marginTop: 10
	},
	scrollView: {
		marginTop: 15,
		borderStyle: "solid",
		borderWidth: 2,
		borderColor: "#222c47",
		marginHorizontal: 10,
		borderRadius: 15,
		padding: 15,
	},
	statsContainer: {
		paddingHorizontal: 7,
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
	},
	chartContainer: {
		margin: 10,
		padding: 10,
		justifyContent: "center",
		borderWidth: 2,
		borderColor: "#222c47",
		borderRadius: 10,
	},
	pieChartContainer: {
		marginHorizontal: "auto"
	},
	chartTitle: {
        fontSize: 23,
        fontWeight: "bold",
        color: "white",
		marginBottom: 5,
    },
	chartXAxisLabelText: {
		color: "gray"
	},
	chartYAxisTextStyle: {
		color: "gray",
	},
	tableHeader: {
        height: 50,
        backgroundColor: "#0c101c",
        borderTopLeftRadius: 10,
        borderTopRightRadius: 10,
    },
	rowText: {
        color: "white",
        textAlign: "center",
    },
	headerRow: {
		fontWeight: "bold"
	},
	tableVerticalScroll: {
        borderBottomLeftRadius: 10,
        borderBottomRightRadius: 10,
    },
	row: {
        backgroundColor: "#0c101c",
        padding: 8,
        borderTopWidth: 1,
        borderTopColor: "",
        borderBottomWidth: 1,
        borderBottomColor: "",
    },
    lastRow: {
        borderBottomWidth: 0,
    },
    firstRow: {
        borderTopWidth: 0,
    },
});
