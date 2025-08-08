import { styles } from "@/styles/components/colorPicker";
import { View, Text } from "react-native";
import Skeleton from "@/components/skeleton/Skeleton";

interface CustomColorPickerProps {
	description?: string;
	title?: string;
}

export default function SkeletonColorPicker({
	description,
	title,
}: CustomColorPickerProps) {
	return (
		<>
			{title && (
				<>
					<Text
						style={{
							...styles.inputHeader,
							...(!description && {
								marginBottom: 5,
							}),
							...styles.inputHeaderDisabled,
						}}
					>
						{title}
					</Text>

					{description && (
						<Text style={styles.inputDescription}>{description}</Text>
					)}
				</>
			)}
			<View style={styles.input}>
				<Skeleton height={17} width={60} />
				<View
					style={{
						...styles.inputPreview,
						borderColor: "#ccc",
						backgroundColor: "#ffffff",
					}}
				/>
			</View>
		</>
	);
}
