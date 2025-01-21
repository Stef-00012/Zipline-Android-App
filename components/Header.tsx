import { Stack, IconButton } from "@react-native-material/core";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { View, Text, Pressable, Image } from "react-native";
import type React from "react";
import { styles } from "@/styles/header";
import { useEffect, useState } from "react";
import { getUser, getUserAvatar } from "@/functions/zipline/user";
import type { APISelfUser } from "@/types/zipline";

export default function Header() {
	const [avatar, setAvatar] = useState<string | null>(null);
	const [user, setUser] = useState<APISelfUser | null>(null);

	useEffect(() => {
		(async () => {
			const avatar = await getUserAvatar();
			const user = await getUser();

			setAvatar(avatar);
			setUser(user);
		})();
	}, []);

	return (
		<View>
			{avatar && user ? (
				<View>
					<View style={styles.header}>
						<View style={styles.headerLeft}>
							<IconButton
								icon={() => (
									<MaterialIcons name="menu" color={"#fff"} size={40} />
								)}
								onPress={() => {
									console.log("menu pressed");
								}}
							/>
						</View>

						<Stack>
							<View>
                                <Pressable
                                    onPress={() => {
                                        console.log("user menu pressed");
                                    }}
                                >
                                    <View style={styles.userMenuContainer}>
                                        <Image
                                            source={{ uri: avatar }}
                                            width={35}
                                            height={35}
                                            style={styles.userMenuAvatar}
                                        />
                                        <Text style={styles.userMenuText}>{user.username}</Text>
                                    </View>
                                </Pressable>
                            </View>
						</Stack>
					</View>
				</View>
			) : (
				<View />
			)}
		</View>
	);
}
