import { GestureHandlerRootView } from "react-native-gesture-handler";
import { KeyboardProvider } from "react-native-keyboard-controller";
import { SafeAreaView } from "react-native-safe-area-context";
import { ShareIntentProvider } from "expo-share-intent";
import NetInfo from "@react-native-community/netinfo";
import { Slot, useRouter } from "expo-router";
import { styles } from "@/styles/noInternet";
import { StatusBar } from "expo-status-bar";
import { useEffect, useState } from "react";
import { Text, View } from "react-native";
import Header from "@/components/Header";
import { Host } from 'react-native-portalize';

export default function Layout() {
	const router = useRouter();
	const [hasInternet, setHasInternet] = useState<boolean>(false);

	useEffect(() => {
		NetInfo.fetch().then((state) => {
			setHasInternet(state.isConnected || false);
		});

		const unsubscribe = NetInfo.addEventListener((state) => {
			setHasInternet(state.isConnected || false);
		});

		return () => {
			unsubscribe();
		};
	}, []);

	return (
		<ShareIntentProvider
			options={{
				scheme: "zipline",
				debug: true,
				resetOnBackground: true,
				onResetShareIntent: () =>
					router.replace({
						pathname: "/",
					}),
			}}
		>
			<StatusBar style="light" backgroundColor="#0c101c" />

			<SafeAreaView style={{ flex: 1 }}>
				<KeyboardProvider>
					<GestureHandlerRootView>
						{hasInternet ? (
							<Header>
								<Host>
									<Slot />
								</Host>
							</Header>
						) : (
							<View style={styles.noInternetContainer}>
								<Text style={styles.noInternetText}>
									No internet connection.
								</Text>
							</View>
						)}
					</GestureHandlerRootView>
				</KeyboardProvider>
			</SafeAreaView>
		</ShareIntentProvider>
	);
}
