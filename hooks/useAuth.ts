import { isAuthenticated } from "@/functions/zipline/auth";
import { getVersion } from "@/functions/zipline/version";
import { useFocusEffect, useRouter } from "expo-router";
import type { APIUser } from "@/types/zipline";
import * as db from "@/functions/database";
import { roles } from "@/constants/auth";
import semver from "semver";

export const useAuth = (minimumRole: APIUser["role"] = "USER") => {
	const router = useRouter();
	const minimumPosition = roles[minimumRole];

	useFocusEffect(() => {
		(async () => {
			const authenticated = await isAuthenticated();

			if (!authenticated) return router.replace("/login");

			const versionData = await getVersion();

			const serverVersion =
				typeof versionData === "string"
					? "0.0.0"
					: "version" in versionData
						? versionData.version
						: versionData.details?.version;

			if (
				typeof versionData === "string" ||
				semver.lt(serverVersion, "4.0.0")
			) {
				await db.del("url");
				await db.del("token");

				return router.replace("/login");
			}

			const userPosition = roles[authenticated];

			if (userPosition < minimumPosition) return router.replace("/");
		})();
	});
};
