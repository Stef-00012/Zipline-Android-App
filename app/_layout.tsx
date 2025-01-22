import { Slot, useRouter } from "expo-router";
import Header from "@/components/Header";

import { ShareIntentProvider } from "expo-share-intent";

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
			<Header/>
			<Slot />
		</ShareIntentProvider>
	);
}
