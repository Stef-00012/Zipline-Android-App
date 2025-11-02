import Button from "@/components/Button";
import Skeleton from "@/components/skeleton/Skeleton";
import { styles } from "@/styles/components/domain";
import { View } from "react-native";

export default function SkeletonDomain() {
	return (
		<View style={styles.domainContainer}>
			<Skeleton width="70%" />

			<Button
				icon="delete"
				color="transparent"
				iconColor="#CF4238"
				onPress={() => {}}
				iconSize={20}
				width={32}
				height={32}
				padding={6}
				disabled={true}
			/>
		</View>
	);
}
