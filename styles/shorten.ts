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
    headerText: {
		marginTop: 10,
		fontSize: 16,
		fontWeight: "bold",
		color: "white",
	},
    textInput: {
        width: "100%",
        borderWidth: 2,
        borderColor: "#222c47",
        margin: "auto",
        color: "white",
        height: 40,
        padding: 3,
        borderRadius: 6,
        marginTop: 5
    },
    button: {
        width: "100%",
        backgroundColor: "#323ea8",
        padding: 10,
        marginTop: 15,
        borderRadius: 6,
        margin: "auto",
    },
    buttonText: {
        textAlign: "center",
        fontWeight: "bold",
        color: "white",
    },
    errorText: {
        color: "red",
        fontWeight: "bold",
        textAlign: "center"
    }
});
