import { getCurrentUser, getCurrentUserAvatar } from "@/functions/zipline/user";
import { type PropsWithChildren, useEffect, useState } from "react";
import { Stack, IconButton } from "@react-native-material/core";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { usePathname, useRouter } from "expo-router";
import type { APISelfUser } from "@/types/zipline";
import { styles } from "@/styles/components/header";
import UserAvatar from "@/components/UserAvatar";
import { View, Pressable } from "react-native";
import Sidebar from "@/components/Sidebar";
import type React from "react";
import { useShareIntent } from "@/hooks/useShareIntent";

export default function Header({ children }: PropsWithChildren) {
	const router = useRouter();
	const pathname = usePathname();

	const resetShareIntent = useShareIntent(true);

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

	useEffect(() => {
		let interval: NodeJS.Timeout | undefined;
		if (!user) {
			interval = setInterval(fetchUser, 5000);
		}

		if (user && interval) clearInterval(interval);

		return () => {
			if (interval) clearInterval(interval);
		};
	}, [user]);

	async function fetchUser() {
		const avatar = await getCurrentUserAvatar();
		const user = await getCurrentUser();

		setAvatar(avatar);
		setUser(typeof user === "string" ? null : user);
	}

	return (
		<View style={styles.headerContainer}>
			{user ? (
				<View
					style={{
						marginBottom: 70,
					}}
				>
					<View style={styles.header}>
						<View style={styles.headerLeft}>
							<IconButton
								icon={() => (
									<MaterialIcons name="menu" color={"#fff"} size={40} />
								)}
								onPress={() => {
									setSidebarOpen((prev) => !prev);
								}}
							/>
						</View>

						<Stack>
							<View>
								<Pressable
									onPress={() => {
										if (pathname !== "/settings") {
											resetShareIntent();

											router.replace("/settings");
										}
									}}
								>
									<UserAvatar username={user.username} avatar={avatar} />
								</Pressable>
							</View>
						</Stack>
					</View>
				</View>
			) : (
				<View />
			)}
			<Sidebar
				open={sidebarOpen}
				paddingTop={user ? 70 : 0}
				setOpen={setSidebarOpen}
			/>
			{children}
		</View>
	);
}
