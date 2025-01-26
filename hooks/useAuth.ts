import { isAuthenticated } from "@/functions/zipline/auth";
import { useFocusEffect, useRouter } from "expo-router";

export const useAuth = (adminOnly = false) => {
    const router = useRouter()

    useFocusEffect(() => {
        (async () => {
            const authenticated = await isAuthenticated();

            if (!authenticated) return router.replace("/login");

            if (adminOnly && authenticated === "USER") return router.replace("/")
        })();
    });
}