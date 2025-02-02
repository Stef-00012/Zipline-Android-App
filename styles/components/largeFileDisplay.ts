import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    popupContainerOverlay: {
        position: "absolute",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: "rgba(0, 0, 0, 0.5)",
        zIndex: 1,
    },
    popupContainer: {
        maxHeight: 700,
        width: "90%",
        margin: "auto",
        backgroundColor: "#0c101c",
        alignItems: "center",
        borderRadius: 15,
        padding: 10,
    },
    fileHeader: {
        fontSize: 22,
        fontWeight: "bold",
        color: "white",
        marginBottom: 10,
    },
    fileInfoContainer: {
        flexDirection: "row",
        marginHorizontal: 10,
        marginVertical: 5
    },
    fileInfoHeader: {
        fontSize: 16,
        fontWeight: "bold",
        color: "white",
    },
    fileInfoText: {
        fontSize: 14,
        color: "gray",
        wordWrap: "break-word",
    },
})