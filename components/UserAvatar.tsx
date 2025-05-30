import { styles } from "@/styles/components/userAvatar";
import { MaterialIcons } from "@expo/vector-icons";
import { Text, View, Image } from "react-native";

interface Props {
	username: string;
	avatar?: string | null;
}
export default function UserAvatar({ username, avatar }: Props) {
	return (
		<View style={styles.userMenuContainer}>
			{avatar ? (
				<Image
					source={{ uri: avatar }}
					width={35}
					height={35}
					style={styles.userMenuAvatar}
				/>
			) : (
				<View
					style={{
						...styles.userMenuAvatar,
						...styles.settingsIcon,
					}}
				>
					<MaterialIcons size={22} name="settings" color="#fff" />
				</View>
			)}
			<Text
				style={{
					...styles.userMenuText,
					...(!avatar && styles.userMenuTextWithSettingsIcon),
				}}
			>
				{username.length > 18 ? `${username.substring(0, 18)}...` : username}
			</Text>
		</View>
	);
}
