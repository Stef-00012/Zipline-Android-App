import { Switch as NativeSwitch } from "@react-native-material/core";
import { styles } from "@/styles/components/switch";
import { Text } from "react-native";
import { View } from "react-native";

interface Props {
	value: boolean;
	title?: string;
	onValueChange: (value: boolean, id?: Props["id"]) => void | Promise<void>;
	disabled?: boolean;
	id?: string;
}

export default function Switch({
	value,
	title,
	onValueChange,
	disabled = false,
	id,
}: Props) {
	return (
		<View style={styles.switchContainer}>
			<NativeSwitch
				disabled={disabled}
				value={value}
				onValueChange={(value) => onValueChange(value, id)}
				thumbColor={value ? "#2e3e6b" : "#222c47"}
				trackColor={{
					true: "#21273b",
					false: "#181c28",
				}}
			/>
			{title && (
				<Text
					style={{
						...styles.switchText,
						...(disabled && styles.switchTextDisabled),
					}}
				>
					{title}
				</Text>
			)}
		</View>
	);
}
