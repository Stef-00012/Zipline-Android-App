import { isAuthenticated } from "@/functions/zipline/auth";
import { useFocusEffect, useRouter } from "expo-router";
import type { APIUser } from "@/types/zipline";
import { roles } from "@/constants/auth";

export const useAuth = (minimumRole: APIUser["role"] = "USER") => {
	const router = useRouter();
	const minimumPosition = roles[minimumRole];

	useFocusEffect(() => {
		(async () => {
			const authenticated = await isAuthenticated();

			if (!authenticated) return router.replace("/login");

			const userPosition = roles[authenticated];

			if (userPosition < minimumPosition) return router.replace("/");
		})();
	});
};
