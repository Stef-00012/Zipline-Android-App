import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	overlayContainer: {
		...StyleSheet.absoluteFillObject,
		flex: 1,
		display: "flex",
		backgroundColor: "#0c101c",
		alignItems: "center",
		zIndex: 999998,
		marginBottom: -20,
		paddingTop: 30,
	},
	lockedTitle: {
		fontSize: 28,
		fontWeight: "bold",
		alignItems: "center",
		color: "#cdd6f4",
	},
	unlockButton: {
		position: "absolute",
		bottom: "45%",
	},
	unlockButtonText: {
		fontSize: 18,
	},
});
