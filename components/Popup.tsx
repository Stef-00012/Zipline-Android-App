import {
	Keyboard,
	Modal,
	Pressable,
	type StyleProp,
	TouchableOpacity,
	TouchableWithoutFeedback,
	View,
	type ViewStyle,
} from "react-native";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { styles } from "@/styles/components/popup";
import { GestureHandlerRootView } from "react-native-gesture-handler";

interface Props {
	onClose: () => void;
	hidden: boolean;
	children: React.ReactNode;
	popupStyle?: StyleProp<ViewStyle>;
}

export default function Popup({
	onClose,
	hidden,
	children,
	popupStyle = {},
}: Props) {
	return (
		<Modal
			onRequestClose={onClose}
			onDismiss={onClose}
			visible={!hidden}
			transparent
			animationType="fade"
		>
			<GestureHandlerRootView>
				<Pressable
					style={styles.popupContainerOverlay}
					onPress={(e) => {
						if (e.target === e.currentTarget) {
							Keyboard.dismiss();
							onClose();
						}
					}}
				>
					<View style={[styles.popupContainer, popupStyle]}>
						<KeyboardAwareScrollView showsVerticalScrollIndicator={false}>
							{children}
						</KeyboardAwareScrollView>
					</View>
				</Pressable>
			</GestureHandlerRootView>
		</Modal>
	);
}
