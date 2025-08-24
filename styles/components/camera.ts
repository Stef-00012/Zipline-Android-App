import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
    mainContainer: {
        backgroundColor: "transparent",
        flex: 1,
    },
    uiContainer: {
        flex: 1,
        backgroundColor: "transparent",
        zIndex: 10
    },
    captureButton: {
        width: 70,
        height: 70,
        borderRadius: 35,
        borderWidth: 4,
        justifyContent: "center",
        alignItems: "center",
        position: "absolute",
        left: "50%",
        bottom: 30,
        marginLeft: -35,
    },
    captureButtonPhoto: {
        borderColor: "white",
        backgroundColor: "rgba(255, 255, 255, 0.3)",
    },
    captureButtonVideo: {
        borderColor: "white",
        backgroundColor: "rgba(255, 0, 0, 0.5)",
    }
});
