import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    mainContainer: {
        backgroundColor: "#0c101c",
        flex: 1,
    },
    headerText: {
        marginTop: 5,
        marginLeft: 10,
        fontSize: 32,
        fontWeight: "bold",
        color: "white",
    },
    loadingContainer: {
        display: "flex",
        flex: 1,
    },
    loadingText: {
        fontSize: 40,
        fontWeight: "bold",
        margin: "auto",
        color: "#304270",
        justifyContent: "center",
        alignItems: "center",
    },
    urlsContainer: {
        borderWidth: 4,
        borderColor: "#222c47",
        borderStyle: "solid",
        borderRadius: 10,
        margin: 10,
    },
    pagesContainer: {
        height: 50,
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        flexWrap: "wrap",
        margin: 10,
    },
    imageContainer: {
        padding: 5,
        margin: 5,
        borderWidth: 2,
        borderColor: "#222c47",
        borderStyle: "solid",
        borderRadius: 10,
    },
    pageButton: {
        flex: 1,
        padding: 10,
        backgroundColor: "#000010",
        borderWidth: 2,
        borderRadius: 10,
        borderColor: "#222c47",
        margin: 5,
        alignItems: "center",
    },
    input: {
        flex: 1,
        padding: 10,
        height: 40,
        backgroundColor: "#010124",
        color: "white",
        margin: 5,
        borderWidth: 2,
        borderRadius: 10,
        borderColor: "#222c47",
        textAlign: "center",
    },
    pageButtonTextDisabled: {
        color: "gray",
    },
    inputDisabled: {
        color: "gray",
    },
    pageButtonText: {
        color: "white",
        fontWeight: "bold",
    },
    header: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
    },
    headerButtons: {
        flexDirection: "row",
        justifyContent: "space-around",
        paddingTop: 10,
        paddingRight: 10,
    },
    headerButton: {
        borderWidth: 2,
        borderRadius: 7,
        borderColor: "#222c47",
        marginHorizontal: 2,
        color: "#2d3f70"
    },
    
    container: {
        flex: 1,
        padding: 16,
        paddingTop: 30,
        backgroundColor: '#fff'
    },
    tableHeader: {
        height: 50,
        backgroundColor: '#0c101c',
        borderTopLeftRadius: 10,
        borderTopRightRadius: 10
    },
    dataWrapper: {
        marginTop: -1
    },
    row: {
        // height: 60,
        backgroundColor: '#0c101c',
        padding: 8,
        borderTopWidth: 1,
        borderTopColor: "",
        borderBottomWidth: 1,
        borderBottomColor: ""
    },
    lastRow: {
        borderBottomWidth: 0
    },
    firstRow: {
        borderTopWidth: 0
    },
    tableVerticalScroll: {
        borderBottomLeftRadius: 10,
        borderBottomRightRadius: 10,
    },
    rowText: {
        color: "white",
        textAlign: "center"
    },
    link: {
        color: "#575DB5"
    }
});
