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
		maxHeight: 600,
		width: "90%",
		margin: "auto",
		backgroundColor: "#0c101c",
		borderRadius: 15,
		padding: 15,
	},
});