import { roles } from "@/constants/auth";
import { AuthContext } from "@/contexts/AuthProvider";
import type { APIUser } from "@/types/zipline";
import { useRouter } from "expo-router";
import { useContext } from "react";

export const useAuth = (minimumRole: APIUser["role"] = "USER") => {
	const router = useRouter();

	const { role } = useContext(AuthContext);

	const minimumPosition = roles[minimumRole];
	const userPosition = role ? roles[role] : 0;

	if (userPosition < minimumPosition) return router.replace("/");
};
