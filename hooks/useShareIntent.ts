import { useRouter } from "expo-router";
import { useShareIntentContext } from "expo-share-intent";
import { useEffect } from "react";

export const useShareIntent = (skipRedirect = false) => {
    const router = useRouter()
    const { hasShareIntent, resetShareIntent } = useShareIntentContext();

    if (skipRedirect) return resetShareIntent;

    // biome-ignore lint/correctness/useExhaustiveDependencies: .
    useEffect(() => {
        if (hasShareIntent) {
            router.replace({
                pathname: "/shareintent",
            });
        }
    }, [hasShareIntent]);

    return resetShareIntent;
}