import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    mainContainer: {
        backgroundColor: "#0c101c",
        flex: 1,
        marginTop: 50
    },
    loadingContainer: {
        flex: 1,
        display: "flex"
    },
    loadingText: {
        fontSize: 40,
        fontWeight: "bold",
        margin: "auto",
        justifyContent: "center",
        alignItems: "center",
        color: "#304270",
    },
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
        margin: "auto"
    },
    textInput: {
        width: "100%",
        borderWidth: 2,
        borderColor: "#222c47",
        marginBottom: 5,
        marginTop: 5,
        color: "white",
        padding: 3,
        borderRadius: 6
    },
    button: {
        width: "100%",
        backgroundColor: "#323ea8",
        padding: 10,
        marginTop: 5,
        borderRadius: 6
    },
    buttonText: {
        textAlign: "center",
        fontWeight: "bold",
        color: "white"
    }
});
