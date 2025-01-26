import { isAuthenticated } from "@/functions/zipline/auth";
import { useFocusEffect, useRouter } from "expo-router";

export const useLoginAuth = () => {
    const router = useRouter()

    useFocusEffect(() => {
        (async () => {
            const authenticated = await isAuthenticated();

            if (authenticated) return router.replace("/");
        })();
    });
}