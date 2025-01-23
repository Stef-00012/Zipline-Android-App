import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    mainContainer: {
        backgroundColor: "#0c101c",
        flex: 1,
    },
    loadingContainer: {
        display: "flex",
        flex: 1
    },
    loadingText: {
        fontSize: 40,
        fontWeight: "bold",
        margin: "auto",
        color: "#304270",
        justifyContent: "center",
        alignItems: "center",
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
    },
    scrollView: {
        marginTop: 15,
        borderStyle: "solid",
        borderWidth: 2,
        borderColor: "#222c47",
        marginHorizontal: 10,
        borderRadius: 15,
        padding: 15
    },
    recentFileContainer: {
        marginHorizontal: 10,
    },
    statsContainer: {
        paddingHorizontal: 7
    },
    headerText: {
        marginTop: 5,
        marginLeft: 10,
        fontSize: 23,
        fontWeight: "bold",
        color: "white"
    },
    subHeaderText: {
        fontSize: 18,
        fontWeight: "bold",
        color: "gray"
    },
    statText: {
        marginTop: 5,
        fontSize: 32,
        fontWeight: "bold",
        color: "white"
    },
    statContainer: {
        borderWidth: 2,
        borderColor: "#222c47",
        borderStyle: "solid",
        borderRadius: 10,
        padding: 10,
        marginHorizontal: 4
    },
    tableText: {
        color: "white",
        fontSize: 16,
        marginHorizontal: 10
    },
    fileTypesContainer: {
        marginBottom: 10,
    },
    tableHeadText: {
        color: "white",
        fontWeight: "bold",
        fontSize: 16,
        marginHorizontal: 10
    }
});
