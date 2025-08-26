import { GestureHandlerRootView } from "react-native-gesture-handler";
import { KeyboardProvider } from "react-native-keyboard-controller";
import { SafeAreaView } from "react-native-safe-area-context";
import ZiplineProvider from "@/contexts/ZiplineProvider";
import { ShareIntentProvider } from "expo-share-intent";
import UpdateProvider from "@/contexts/UpdateContext";
import NetInfo from "@react-native-community/netinfo";
import * as SplashScreen from 'expo-splash-screen';
import AuthProvider from "@/contexts/AuthProvider";
import { Slot, useRouter } from "expo-router";
import { Host } from "react-native-portalize";
import { styles } from "@/styles/noInternet";
import { StatusBar } from "expo-status-bar";
import { useEffect, useState } from "react";
import { Text, View } from "react-native";
import Header from "@/components/Header";
import { useFonts } from 'expo-font';

SplashScreen.preventAutoHideAsync();

export default function Layout() {
	const router = useRouter();
	const [hasInternet, setHasInternet] = useState<boolean>(false);
	
	const [loaded, error] = useFonts({
		MaterialSymbols: require('../assets/material-symbols.ttf'),
	})

	useEffect(() => {
		if (loaded || error) {
			SplashScreen.hideAsync();
		}

		if (error) console.error(error)
	}, [loaded, error])

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

	if (!loaded && !error) return null;

	return (
		<ShareIntentProvider
			options={{
				scheme: "zipline",
				// debug: __DEV__,
				resetOnBackground: true,
				onResetShareIntent: () =>
					router.replace({
						pathname: "/",
					}),
			}}
		>
			<StatusBar style="light" />

			<SafeAreaView style={{ flex: 1, backgroundColor: "#0c101c" }}>
				<KeyboardProvider>
					<GestureHandlerRootView>
						<AuthProvider>
							<ZiplineProvider>
								<UpdateProvider>
									{hasInternet ? (
										<Host>
											<Header>
												<Slot />
											</Header>
										</Host>
									) : (
										<Header>
											<View style={styles.noInternetContainer}>
												<Text style={styles.noInternetText}>
													No internet connection.
												</Text>
											</View>
										</Header>
									)}
								</UpdateProvider>
							</ZiplineProvider>
						</AuthProvider>
					</GestureHandlerRootView>
				</KeyboardProvider>
			</SafeAreaView>
		</ShareIntentProvider>
	);
}
