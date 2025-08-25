import * as db from "@/functions/database";
import semver from "semver";
import React, {
	createContext,
	useState,
	useCallback,
	useMemo,
	useEffect,
} from "react";
import { getInstallerPackageNameSync } from "react-native-device-info";
import { knownInstallersPackage } from "@/constants/knownInstallers";
import axios from "axios";
import { repoName, username } from "@/constants/updates";
import { version as appVersion } from "@/package.json";
import { ToastAndroid } from "react-native";
import type { GitHubRelease } from "@/types/githubApi";
import { ExternalPathString, useRouter } from "expo-router";

interface Props {
	children: React.ReactNode;
}

interface UpdateData {
	updateAvailable: boolean;
	checkForUpdates: () => Promise<void>;
	downloadUpdate: () => Promise<string | void>;
}

export const UpdateContext = createContext<UpdateData>({
	updateAvailable: false,
	checkForUpdates: async () => {},
	downloadUpdate: async () => {},
});

export default function UpdateProvider({ children }: Props) {
    const router = useRouter()
	const installerPackageName = getInstallerPackageNameSync();

	const [updateAvailable, setUpdateAvailable] = useState<boolean>(false);
	const [downloadUrl, setDownloadUrl] = useState<string | null>(null);

	const checkForUpdatesGooglePlay = useCallback(async () => {}, []);

	const checkForUpdatesApk = useCallback(async () => {
        const res = await axios.get(
            `https://api.github.com/repos/${username}/${repoName}/releases/latest`,
        );

        const releaseData = res.data as GitHubRelease;

        if (!releaseData.assets?.length) {
            setUpdateAvailable(false)
            setDownloadUrl(null)

            return;

            // return {
            //     available: false,
            //     downloadUrl: null,
            //     nextVersion: null,
            //     currentVersion: appVersion,
            // };
        }

        const latestVersion = releaseData.tag_name;
        const coercedVersion = semver.coerce(latestVersion);

        if (!coercedVersion) {
            setUpdateAvailable(false)
            setDownloadUrl(null)

            return;

            // return {
            //     available: false,
            //     downloadUrl: null,
            //     nextVersion: null,
            //     currentVersion: appVersion,
            // };
        }

        if (semver.gt(coercedVersion, appVersion)) {
            setUpdateAvailable(true);

            const lastUpdateAlert = db.get("lastUpdateAlert");
            const latestUpdateAlertVersion = db.get("lastUpdateAlertVersion");

            // 1000 * 60 * 30 = 30 minutes
            if (
                !lastUpdateAlert ||
                Date.now() - new Date(lastUpdateAlert).getTime() > 1000 * 60 * 30 ||
                !latestUpdateAlertVersion ||
                semver.gt(coercedVersion, latestUpdateAlertVersion)
            ) {
                ToastAndroid.show(
                    "There's an Update Available, head to the user settings to download it",
                    ToastAndroid.LONG,
                );

                db.set("lastUpdateAlert", new Date().toISOString());
                db.set("lastUpdateAlertVersion", coercedVersion.raw);
            }

            const apkAsset = releaseData.assets.find(asset => asset.name.endsWith(".apk"))

            setDownloadUrl(apkAsset?.browser_download_url ?? null);

            // return {
            //     available: true,
            //     downloadUrl: apkAsset.browser_download_url,
            //     nextVersion: coercedVersion.raw,
            //     currentVersion: appVersion,
            // };
        }
    }, []);

	const downloadUpdateGooglePlay = useCallback(async () => {}, []);

	const downloadUpdateApk = useCallback(async () => {
        await checkForUpdatesApk();

        if (!downloadUrl || !updateAvailable) return "No update available";

        router.push(downloadUrl as ExternalPathString)
    }, []);

	const updateData = useMemo<UpdateData>(
		() => ({
			updateAvailable,
			checkForUpdates:
				installerPackageName === knownInstallersPackage.googlePlay
					? checkForUpdatesGooglePlay
					: checkForUpdatesApk,
			downloadUpdate:
				installerPackageName === knownInstallersPackage.googlePlay
					? downloadUpdateGooglePlay
					: downloadUpdateApk,
		}),
		[
			updateAvailable,
			installerPackageName === knownInstallersPackage.googlePlay
				? checkForUpdatesGooglePlay
				: checkForUpdatesApk,
			installerPackageName === knownInstallersPackage.googlePlay
				? downloadUpdateGooglePlay
				: downloadUpdateApk,
		],
	);

	useEffect(() => {
        if (installerPackageName === knownInstallersPackage.googlePlay) {
            checkForUpdatesGooglePlay();
        } else {
            checkForUpdatesApk();
        }
    }, []);

	return (
		<UpdateContext.Provider value={updateData}>
			{children}
		</UpdateContext.Provider>
	);
}
