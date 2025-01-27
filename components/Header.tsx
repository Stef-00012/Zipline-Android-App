import { getCurrentUser, getCurrentUserAvatar } from "@/functions/zipline/user";
import { type PropsWithChildren, useEffect, useRef, useState } from "react";
import { Stack, IconButton } from "@react-native-material/core";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { View, Text, Pressable, Image } from "react-native";
import type { APISelfUser } from "@/types/zipline";
import { styles } from "@/styles/components/header";
import type React from "react";
import Sidebar from "@/components/Sidebar.tsx"

export default function Header({ children }: PropsWithChildren) {
	const [avatar, setAvatar] = useState<string | null>(null);
	const [user, setUser] = useState<APISelfUser | null>(null);
	
	const [sidebarOpen, setSidebarOpen] = useState<boolean>(false);

	useEffect(() => {
		if (!avatar || !user) {
			(async () => {
				const avatar = await getCurrentUserAvatar();
				const user = await getCurrentUser();

				setAvatar(avatar);
				setUser(typeof user === "string" ? null : user);
			})();
		}
	}, [avatar, user]);

	return (
		<View style={styles.headerContainer}>
			{user ? (
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
									setSidebarOpen((prev) => !prev)
								}}
							/>
						</View>

						<Stack>
							<View>
                                <Pressable
                                    onPress={() => {
                                        console.debug("user menu pressed");
                                    }}
                                >
                                    <View style={styles.userMenuContainer}>
                                        {avatar ? (
											<Image
												source={{ uri: avatar }}
												width={35}
												height={35}
												style={styles.userMenuAvatar}
											/>
										) : (
											<View style={{
												...styles.userMenuAvatar,
												...styles.settingsIcon
											}}>
												<MaterialIcons size={22} name="settings" color="#fff" />
											</View>
										)}
                                        <Text style={{
											...styles.userMenuText,
											...(!avatar && styles.userMenuTextWithSettingsIcon)
										}}>{user.username}</Text>
                                    </View>
                                </Pressable>
                            </View>
						</Stack>
					</View>
				</View>
			) : (
				<View />
			)}
			<Sidebar open={sidebarOpen} paddingTop={user ? 70 : 0} setOpen={setSidebarOpen} />
			{children}
		</View>
	);
}
