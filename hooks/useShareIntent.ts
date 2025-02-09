import { useRouter } from "expo-router";
import { useShareIntentContext } from "expo-share-intent";
import { useEffect, useState } from "react";

export const useShareIntent = (skipRedirect = false) => {
	const router = useRouter();
	const { hasShareIntent, resetShareIntent } = useShareIntentContext();
	const [isMounted, setIsMounted] = useState<boolean>(false)

	useEffect(() => {
        setIsMounted(true);
    }, []);

	if (skipRedirect) return resetShareIntent;

	// biome-ignore lint/correctness/useExhaustiveDependencies: .
	useEffect(() => {
		if (isMounted && hasShareIntent) {
			router.replace({
				pathname: "/shareintent",
			});
		}
	}, [hasShareIntent, isMounted]);

	return resetShareIntent;
};
