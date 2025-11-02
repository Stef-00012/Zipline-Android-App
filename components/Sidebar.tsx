import Button from "@/components/Button";
import { type SidebarOption, sidebarOptions } from "@/constants/sidebar";
import { AuthContext } from "@/contexts/AuthProvider";
import { ZiplineContext } from "@/contexts/ZiplineProvider";
import { useShareIntent } from "@/hooks/useShareIntent";
import { styles } from "@/styles/components/sidebar";
import { type RelativePathString, usePathname, useRouter } from "expo-router";
import {
	type Dispatch,
	type SetStateAction,
	useContext,
	useEffect,
	useRef,
	useState,
} from "react";
import { Animated, Dimensions, View } from "react-native";

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
	const { webSettings } = useContext(ZiplineContext);

	const adminOnlyMetrics = webSettings
		? webSettings.config.features.metrics.adminOnly
		: true;

	const { role } = useContext(AuthContext);

	const [openStates, setOpenStates] = useState<Record<string, boolean>>({});
	const animatedHeights = useRef<Record<string, Animated.Value>>({}).current;

	const screenWidth = Dimensions.get("window").width;
	const translateX = useRef(new Animated.Value(-screenWidth)).current;

	const [displayed, setDisplayed] = useState<boolean>(open);

	const invitesEnabled = webSettings
		? webSettings.config.invites.enabled
		: false;

	const [isAdmin, setIsAdmin] = useState<boolean>(false);
	const [isSuperAdmin, setIsSuperAdmin] = useState<boolean>(false);

	// biome-ignore lint/correctness/useExhaustiveDependencies: Functions should not be parameters of the effect
	useEffect(() => {
		(async () => {
			if (role === "USER") {
				setIsAdmin(false);
				setIsSuperAdmin(false);
			}

			if (["ADMIN", "SUPERADMIN"].includes(role || "NOT_LOGGED"))
				setIsAdmin(true);
			if (role === "SUPERADMIN") setIsSuperAdmin(true);
		})();
	}, [open]);

	const pathname = usePathname();

	useEffect(() => {
		Animated.timing(translateX, {
			toValue: open ? 0 : -screenWidth,
			duration: 300,
			useNativeDriver: true,
		}).start();

		if (open) setDisplayed(true);
		else {
			setTimeout(() => {
				setDisplayed(false);
			}, 300);
		}
	}, [open, screenWidth, translateX]);

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
			<View
				style={{
					display: displayed ? undefined : "none",
				}}
			>
				{sidebarOptions.map(renderSidebarOptions)}
			</View>
		</Animated.View>
	);

	function renderSidebarOptions(option: SidebarOption) {
		if (
			(option.adminOnly && !isAdmin) ||
			(option.invitesRoute && !invitesEnabled) ||
			(option.superAdminOnly && !isSuperAdmin) ||
			(option.route === "/metrics" && adminOnlyMetrics && !isAdmin)
		)
			return <View key={option.route || option.name} />;

		if (option.type === "button") {
			const isActive = pathname === option.route;

			const route = option.route as RelativePathString;

			return (
				<Button
					key={route}
					containerStyle={{
						borderRadius: 7,
					}}
					buttonStyle={{
						justifyContent: "flex-start",
					}}
					textStyle={{
						fontWeight: "normal"
					}}
					icon={option.icon}
					onPress={() => {
						setOpen(false);

						if (isActive) return;

						resetShareIntent();

						router.push(route);
					}}
					color={isActive ? "#14192F" : "transparent"}
					textColor={isActive ? "#6D71B1" : "white"}
					text={option.name}
					rippleColor={isActive ? undefined : "#283557"}
				/>
			);
		}

		if (option.type === "select") {
			const open = openStates[option.name] ?? false;
			if (!animatedHeights[option.name]) {
				animatedHeights[option.name] = new Animated.Value(0);
			}

			const toggleSelect = () => {
				const newOpenState = !open;
				setOpenStates((prev) => ({
					...prev,
					[option.name]: newOpenState,
				}));

				Animated.timing(animatedHeights[option.name], {
					toValue: newOpenState ? option.subMenus.length * 40 : 0,
					duration: 300,
					useNativeDriver: false,
				}).start();
			};

			return (
				<View key={option.name}>
					<Button
						onPress={toggleSelect}
						color="transparent"
						icon={option.icon}
						text={option.name}
						buttonStyle={{
							justifyContent: "flex-start",
						}}
						textStyle={{
							fontWeight: "normal"
						}}
						rippleColor="#283557"
						open={open}
					/>
					<Animated.View
						style={{
							maxHeight: animatedHeights[option.name],
							overflow: "hidden",
						}}
					>
						<View style={{ paddingLeft: 20 }}>
							{option.subMenus.map(renderSidebarOptions)}
						</View>
					</Animated.View>
				</View>
			);
		}
	}
}
