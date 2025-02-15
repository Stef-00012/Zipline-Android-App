import { TouchableOpacity, View } from "react-native";
import { styles } from "@/styles/components/checkbox";
import { MaterialIcons } from "@expo/vector-icons";

interface Props {
	value: boolean;
	disabled?: boolean;
	onValueChange: () => void;
}

export default function CheckBox({
	value,
	onValueChange,
	disabled = true,
}: Props) {
	return (
		<TouchableOpacity
			disabled={disabled}
			onPress={onValueChange}
			style={styles.checkboxContainer}
		>
			<View
				style={{
					...styles.checkbox,
					borderColor: value
						? disabled
							? "#323ea855"
							: "#323ea8"
						: disabled
							? "#222c4755"
							: "#222c47",
					backgroundColor: value ? "#323ea8" : "transparent",
				}}
			>
				{value && <MaterialIcons name="check" size={16} color="white" />}
			</View>
		</TouchableOpacity>
	);
}
