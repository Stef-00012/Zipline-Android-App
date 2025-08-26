import { type PropsWithChildren, useContext, useState } from "react";
import { Stack, IconButton } from "@react-native-material/core";
import { useShareIntent } from "@/hooks/useShareIntent";
import { AuthContext } from "@/contexts/AuthProvider";
import { usePathname, useRouter } from "expo-router";
import { styles } from "@/styles/components/header";
import { getRippleColor } from "@/functions/util";
import UserAvatar from "@/components/UserAvatar";
import { View, Pressable } from "react-native";
import { colors } from "@/constants/skeleton";
import Sidebar from "@/components/Sidebar";
import { Skeleton } from "moti/skeleton";
import MaterialSymbols from "./MaterialSymbols";

export default function Header({ children }: PropsWithChildren) {
	const router = useRouter();
	const pathname = usePathname();

	const resetShareIntent = useShareIntent(true);
	const { avatar, user, isAuthenticating } = useContext(AuthContext);

	const [sidebarOpen, setSidebarOpen] = useState<boolean>(false);

	return (
		<View style={styles.headerContainer}>
			<View
				style={{
					display: pathname === "/login" ? "none" : undefined,
				}}
			>
				{(user && !isAuthenticating) ? (
					<View
						style={{
							marginBottom: 70,
						}}
					>
						<View style={styles.header}>
							<View style={styles.headerLeft}>
								<IconButton
									icon={() => (
										<MaterialSymbols name="menu" color={"#fff"} size={40} />
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

											router.push("/settings");
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
											<MaterialSymbols
												name="menu"
												color={"#ffffff77"}
												size={40}
											/>
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
			</View>
			<Sidebar
				open={sidebarOpen}
				paddingTop={user ? 70 : 0}
				setOpen={setSidebarOpen}
			/>
			{children}
		</View>
	);
}
