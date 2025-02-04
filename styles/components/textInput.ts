import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    inputContainer: {
        flexDirection: "row",
        height: 40,
    },
    textInput: {
		width: "100%",
		borderWidth: 2,
		borderColor: "#222c47",
// 		marginBottom: 5,
// 		marginTop: 5,
		color: "white",
// 		height: 40,
		paddingHorizontal: 10,
		fontSize: 15,
		borderRadius: 6,
	},
    textInputSideButton: {
		width: "86%",
	},
    inputSideButton: {
        width: 40,
        height: "auto"
    },
    inputHeader: {
		marginTop: 5,
		marginBottom: 5,
		fontSize: 18,
		fontWeight: "bold",
		color: "white",
	},
    sideButton: {
        backgroundColor: "#323ea8",
        padding: 10,
        marginLeft: 10,
        height: "100%",
        width: 40,
        borderRadius: 6,
        justifyContent: "center",
        alignItems: "center"
    },
    textInputDisabled: {
		color: "gray"
	},
    inputHeaderDisabled: {
		color: "gray",
	},
})