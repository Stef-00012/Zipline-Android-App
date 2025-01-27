import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    sidebar: {
        position: 'absolute',
        top: 0,
        left: 0,
        height: "100%",
        backgroundColor: "#0c101c",
        zIndex: 99998,
    },
    sidebarOption: {
        width: "100%",
        flexDirection: "row",
        justifyContent: "flex-start",
        alignItems: "center",
        height: 45,
        padding: 5,
    },
    sidebarOptionActive: {
        backgroundColor: "#14192F"
    },
    sidebarOptionText: {
        color: "white",
        marginLeft: 10
    },
    sidebarOptionTextActive: {
        color: "#6D71B1"
    }
});