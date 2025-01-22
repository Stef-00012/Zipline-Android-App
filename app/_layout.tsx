import { ShareIntentProvider } from "expo-share-intent";
import { Slot, useRouter } from "expo-router";
import { StatusBar } from 'expo-status-bar';
import Header from "@/components/Header";

export default function Layout() {
	const router = useRouter();

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
			<StatusBar
				style="light"
				backgroundColor="#0c101c"
			/>
			
			<Header/>
			<Slot />
		</ShareIntentProvider>
	);
}
