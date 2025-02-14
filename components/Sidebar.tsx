import { type SidebarOption, sidebarOptions } from "@/constants/sidebar";
import { type RelativePathString, usePathname } from "expo-router";
import { getSettings } from "@/functions/zipline/settings";
import { Animated, View, Dimensions } from "react-native";
import { getCurrentUser } from "@/functions/zipline/user";
import { styles } from "@/styles/components/sidebar";
import Button from "@/components/Button";
import { useRouter } from "expo-router";
import React, {
	type Dispatch,
	type SetStateAction,
	useEffect,
	useRef,
	useState,
} from "react";
import { useShareIntent } from "@/hooks/useShareIntent";

interface Props {
	open: boolean;
	paddingTop: number;
	setOpen: Dispatch<SetStateAction<boolean>>;
}

export default function Sidebar({
	open = false,
	paddingTop = 0,
	setOpen,
}: Props) {
	const router = useRouter();

	const resetShareIntent = useShareIntent(true);

	const [openStates, setOpenStates] = useState<Record<string, boolean>>({});

	const screenWidth = Dimensions.get("window").width;
	const translateX = useRef(new Animated.Value(-screenWidth)).current;

	const [invitesEnabled, setInvitesEnabled] = useState<boolean>(false);
	const [isAdmin, setIsAdmin] = useState<boolean>(false);

	useEffect(() => {
		(async () => {
			const settings = await getSettings();
			const user = await getCurrentUser();

			if (
				typeof user !== "string" &&
				["ADMIN", "SUPERADMIN"].includes(user.role)
			)
				setIsAdmin(true);
			if (typeof settings !== "string" && settings.invitesEnabled)
				setInvitesEnabled(true);
		})();
	});

	const pathname = usePathname();

	// biome-ignore lint/correctness/useExhaustiveDependencies:.
	useEffect(() => {
		Animated.timing(translateX, {
			toValue: open ? 0 : -screenWidth,
			duration: 300,
			useNativeDriver: true,
		}).start();
	}, [open, screenWidth]);

	return (
		<Animated.View
			style={[
				styles.sidebar,
				{
					transform: [
						{
							translateX,
						},
					],
					width: screenWidth,
					paddingTop,
				},
			]}
		>
			<View>{sidebarOptions.map(renderSidebarOptions)}</View>
		</Animated.View>
	);

	function renderSidebarOptions(option: SidebarOption) {
		if (option.adminOnly && !isAdmin)
			return <View key={option.route || option.name} />;

		if (option.invitesRoute && !invitesEnabled)
			return <View key={option.route || option.name} />;

		if (option.type === "button") {
			const isActive = pathname === option.route;

			const route = option.route as RelativePathString;

			return (
				<Button
					key={route}
					borderRadius={0}
					icon={option.icon}
					onPress={() => {
						setOpen(false);

						if (isActive) return;

						resetShareIntent();

						router.replace(route);
					}}
					color={isActive ? "#14192F" : "transparent"}
					textColor={isActive ? "#6D71B1" : "white"}
					text={option.name}
					position="left"
					rippleColor={isActive ? undefined : "#283557"}
					bold={false}
				/>
			);
		}

		if (option.type === "select") {
			const open = openStates[option.name] ?? false;

			return (
				<View key={option.name}>
					<Button
						onPress={() =>
							setOpenStates((prev) => ({
								...prev,
								[option.name]: !prev[option.name],
							}))
						}
						color="transparent"
						icon={option.icon}
						text={option.name}
						position="left"
						rippleColor="#283557"
						bold={false}
						open={open}
					/>
					{open && (
						<View style={{ paddingLeft: 20 }}>
							{option.subMenus.map(renderSidebarOptions)}
						</View>
					)}
				</View>
			);
		}
	}
}
