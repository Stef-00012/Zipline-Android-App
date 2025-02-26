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

		if (
			typeof versionData === "string" ||
			semver.lt(versionData.version, "4.0.0")
		)
			return;

		router.replace("/");
	}
};
