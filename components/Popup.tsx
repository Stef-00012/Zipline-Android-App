import { styles } from "@/styles/components/popup";
import { Pressable, type StyleProp, View, type ViewStyle } from "react-native";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";

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
		<Pressable
			style={{
				...styles.popupContainerOverlay,
				...(hidden && { display: "none" }),
			}}
			onPress={(e) => {
				if (e.target === e.currentTarget) onClose();
			}}
		>
			<View style={[styles.popupContainer, popupStyle]}>
				<KeyboardAwareScrollView showsVerticalScrollIndicator={false}>
					{children}
				</KeyboardAwareScrollView>
			</View>
		</Pressable>
	);
}
