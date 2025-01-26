import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    selectButton: {
        flexDirection: "row",
        justifyContent: "center",
        alignItems: "center",
        paddingHorizontal: 12,
        borderWidth: 2,
        borderColor: "#222c47",
        color: "white",
        height: 40,
        padding: 3,
        borderRadius: 6,
        marginTop: 5,
    },
    selectText: {
        flex: 1,
        fontSize: 15,
        color: "#222c47",
    },
    select: {
        backgroundColor: "#181c28",
        borderRadius: 8,
        maxHeight: 250,
    },
    menuItem: {
        flexDirection: "row",
        paddingHorizontal: 12,
        justifyContent: "center",
        alignItems: "center",
        paddingVertical: 8,
        backgroundColor: "#0c101c"
    },
    menuItemText: {
        flex: 1,
        fontSize: 18,
        fontWeight: "bold",
        color: "#222c47",
    },
    menuItemSelected: {
        backgroundColor: "#0d1325"
    },
    selectedText: {
        color: "white"
    }
})