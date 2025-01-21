import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    header: {
        position: "absolute",
        top: 0,
        left: 0,
        right: 0,
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        padding: 10,
        backgroundColor: "#0c101c",
        zIndex: 99999,
        borderBottomWidth: 2,
        borderBottomColor: "#222c47",
        height: 70
    },

    headerLeft: {
        flexDirection: "column",
    },

    text: {
        fontFamily: "Arimo-Nerd-Font",
        color: "#fff",
    },

    settings: {
        width: 30,
        height: 30,
        marginRight: 10,
    },

    settingsIcon: {
        width: "100%",
        height: "100%",
    },
    userMenuContainer: {
        flexWrap: "wrap",
        flexDirection: "row",
    },
    userMenuAvatar: {
        borderRadius: 8
    },
    userMenuText: {
        color: "white",
        fontWeight: "bold",
        marginLeft: 10,
        marginTop: 5,
        fontSize: 18
    }
})