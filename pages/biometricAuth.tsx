import Button from "@/components/Button";
import MaterialSymbols from "@/components/MaterialSymbols";
import { AuthContext } from "@/contexts/AuthProvider";
import { styles } from "@/styles/pages/biometricAuth";
import { useContext } from "react";
import { Text, View } from "react-native";

export default function BiometricAuthenticationPage() {
	const { isAuthenticating, requestBiometricsAuthentication } =
		useContext(AuthContext);

	if (!isAuthenticating) return null;

	return (
		<View style={styles.overlayContainer}>
			<MaterialSymbols name="lock" color="#323ea8" size={50} />

			<Text style={styles.lockedTitle}>Hackatime Locked</Text>

			<Button
                color="#323ea8"
				text="Unlock"
				containerStyle={styles.unlockButton}
				textStyle={styles.unlockButtonText}
				onPress={() => {
					requestBiometricsAuthentication();
				}}
			/>
		</View>
	);
}
