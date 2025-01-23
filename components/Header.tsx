import { getCurrentUser, getCurrentUserAvatar } from "@/functions/zipline/user";
import { type PropsWithChildren, useEffect, useRef, useState } from "react";
import { Stack, IconButton } from "@react-native-material/core";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { View, Text, Pressable, Image } from "react-native";
import type { APISelfUser } from "@/types/zipline";
import { styles } from "@/styles/header";
import type React from "react";

export default function Header({ children }: PropsWithChildren) {
	const [avatar, setAvatar] = useState<string | null>(null);
	const [user, setUser] = useState<APISelfUser | null>(null);

	useEffect(() => {
		if (!avatar || !user) {
			(async () => {
				const avatar = await getCurrentUserAvatar();
				const user = await getCurrentUser();
	
				setAvatar(avatar);
				setUser(user);
			})();
		}
	}, [avatar, user]);

	return (
		<View style={styles.headerContainer}>
			{avatar && user ? (
				<View
					style={{
						marginBottom: 70 
					}}
				>
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
			{children}
		</View>
	);
}
