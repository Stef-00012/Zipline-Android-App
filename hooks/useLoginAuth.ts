import { isAuthenticated } from "@/functions/zipline/auth";
import { useFocusEffect, useRouter } from "expo-router";

export const useLoginAuth = () => {
    const router = useRouter()

    useFocusEffect(() => {
        loginAuth()

        const interval = setInterval(loginAuth, 5000)

        return () => {
            clearInterval(interval)
        }
    });

    async function loginAuth() {
        const authenticated = await isAuthenticated();

        if (authenticated) return router.replace("/");
    }
}