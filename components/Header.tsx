import { type PropsWithChildren, useContext, useState } from "react";
import { Stack, IconButton } from "@react-native-material/core";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { usePathname, useRouter } from "expo-router";
import { styles } from "@/styles/components/header";
import UserAvatar from "@/components/UserAvatar";
import { View, Pressable } from "react-native";
import Sidebar from "@/components/Sidebar";
import { useShareIntent } from "@/hooks/useShareIntent";
import { getRippleColor } from "@/functions/util";
import { Skeleton } from "moti/skeleton";
import { colors } from "@/constants/skeleton";
import { AuthContext } from "@/contexts/AuthProvider";

export default function Header({ children }: PropsWithChildren) {
	const router = useRouter();
	const pathname = usePathname();

	const resetShareIntent = useShareIntent(true);
	const { avatar, user } = useContext(AuthContext)

	const [sidebarOpen, setSidebarOpen] = useState<boolean>(false);

	return (
		<View style={{
			...styles.headerContainer,
			...(pathname === "/login" && {
				display: "none"
			})
		}}>
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
								android_ripple={{
									color: getRippleColor("#0c101c"),
								}}
								onPress={() => {
									setSidebarOpen((prev) => !prev);
								}}
							/>
						</View>

						<Stack>
							<View>
								<Pressable
									android_ripple={{
										color: getRippleColor("#0c101c"),
									}}
									disabled={pathname === "/settings"}
									style={{
										padding: 10,
										borderRadius: 10,
									}}
									onPress={() => {
										resetShareIntent();

										router.replace("/settings");
									}}
								>
									<UserAvatar username={user.username} avatar={avatar} />
								</Pressable>
							</View>
						</Stack>
					</View>
				</View>
			) : (
				<View
					style={{
						marginBottom: 70,
					}}
				>
					<Skeleton.Group show={!user}>
						<View style={styles.header}>
							<View style={styles.headerLeft}>
								<IconButton
									disabled
									icon={() => (
										<MaterialIcons name="menu" color={"#ffffff77"} size={40} />
									)}
									android_ripple={{
										color: getRippleColor("#0c101c"),
									}}
									onPress={() => {
										setSidebarOpen((prev) => !prev);
									}}
								/>
							</View>

							<Stack>
								<View>
									<View
										style={{
											padding: 10,
											borderRadius: 10,
										}}
									>
										<Skeleton colors={colors} height={36} width={80} />
									</View>
								</View>
							</Stack>
						</View>
					</Skeleton.Group>
				</View>
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
