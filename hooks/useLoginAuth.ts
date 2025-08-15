import { minimumVersion } from "@/constants/auth";
import { isAuthenticated } from "@/functions/zipline/auth";
import { getVersion } from "@/functions/zipline/version";
import { useFocusEffect, useRouter } from "expo-router";
import semver from "semver";

export const useLoginAuth = () => {
	const router = useRouter();

	useFocusEffect(() => {
		loginAuth();

		const interval = setInterval(loginAuth, 5000);

		return () => {
			clearInterval(interval);
		};
	});

	async function loginAuth() {
		const authenticated = await isAuthenticated();

		if (!authenticated) return;

		const versionData = await getVersion();

		const serverVersion =
			typeof versionData === "string"
				? "0.0.0"
				: "version" in versionData
					? versionData.version
					: versionData.details?.version;

		if (typeof versionData === "string" || semver.lt(serverVersion, minimumVersion))
			return;

		router.replace("/");
	}
};
