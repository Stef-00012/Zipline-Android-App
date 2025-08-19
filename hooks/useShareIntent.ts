import { useShareIntentContext } from "expo-share-intent";
import { useEffect, useState } from "react";
import { useRouter } from "expo-router";

export const useShareIntent = (skipRedirect = false) => {
	const router = useRouter();
	const { hasShareIntent, resetShareIntent } = useShareIntentContext();
	const [isMounted, setIsMounted] = useState<boolean>(false)

	useEffect(() => {
        setIsMounted(true);
    }, []);

	
	// biome-ignore lint/correctness/useExhaustiveDependencies: Functions should not be parameters of the effect
	useEffect(() => {
		if (isMounted && hasShareIntent && !skipRedirect) {
			router.push({
				pathname: "/shareintent",
			});
		}
	}, [hasShareIntent, isMounted, skipRedirect]);

	if (skipRedirect) return resetShareIntent;

	return resetShareIntent;
};
