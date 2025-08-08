import { Text, View } from "react-native";
import Button from "@/components/Button";
import { styles } from "@/styles/components/domain";

interface Props {
	domain: string;
	disabled?: boolean;
	onDelete: () => void;
}

export default function Domain({ domain, disabled, onDelete }: Props) {
	return (
		<View style={styles.domainContainer}>
			<Text style={styles.domainText}>{domain}</Text>

			<Button
				icon="delete"
				color="transparent"
				iconColor="#CF4238"
				onPress={onDelete}
				iconSize={20}
				width={32}
				height={32}
				padding={6}
				disabled={disabled}
			/>
		</View>
	);
}
