import { getInstallerPackageNameSync } from "react-native-device-info";
import { knownInstallersPackage } from "@/constants/knownInstallers";
import { startActivityAsync } from "expo-intent-launcher";
import { repoName, username } from "@/constants/updates";
import { Directory, Paths } from "expo-file-system/next";
import { version as appVersion } from "@/package.json";
import type { GitHubRelease } from "@/types/githubApi";
import { UpdateCheckResult } from "@/types/updates";
import * as FileSystem from "expo-file-system";
import { ToastAndroid } from "react-native";
import * as db from "@/functions/database";
import { useRouter } from "expo-router";
import semver from "semver";
import axios from "axios";
import SpInAppUpdates, {
    IAUUpdateKind,
	IAUInstallStatus,
} from "sp-react-native-in-app-updates";
import React, {
    createContext,
    useState,
    useCallback,
    useMemo,
    useEffect,
} from "react";

interface Props {
	children: React.ReactNode;
}

interface UpdateData {
	updateAvailable: boolean;
	newVersion: string | null;
	checkForUpdates: () => Promise<UpdateCheckResult>;
	downloadUpdate: () => Promise<string | void>;
	downloadPercentage: number;
	isDownloading: boolean;
	isCheckingUpdate: boolean;
}

export const UpdateContext = createContext<UpdateData>({
	updateAvailable: false,
	newVersion: null,
	downloadPercentage: 0,
	isDownloading: false,
	isCheckingUpdate: false,
	checkForUpdates: async () => ({
		available: false,
		downloadUrl: null,
		newVersion: null,
	}),
	downloadUpdate: async () => {},
});

export default function UpdateProvider({ children }: Props) {
	const router = useRouter();
	const installerPackageName = getInstallerPackageNameSync();

	const [updateAvailable, setUpdateAvailable] = useState<boolean>(false);
	const [newVersion, setNewVersion] = useState<string | null>(null);
	const [downloadPercentage, setDownloadPercentage] = useState<number>(0);
	const [isDownloading, setIsDownloading] = useState<boolean>(false);
	const [isCheckingUpdate, setIsCheckingUpdate] = useState<boolean>(false);

	const checkForUpdatesApk: () => Promise<UpdateCheckResult> =
		useCallback(async () => {
			setIsCheckingUpdate(true);

			const res = await axios.get(
				`https://api.github.com/repos/${username}/${repoName}/releases/latest`,
			);

			const releaseData = res.data as GitHubRelease;

			if (!releaseData.assets?.length) {
				setIsCheckingUpdate(false);

				return {
					available: false,
					downloadUrl: null,
					newVersion: null,
				};
			}

			const latestVersion = releaseData.tag_name;
			const coercedVersion = semver.coerce(latestVersion);

			if (!coercedVersion) {
				setIsCheckingUpdate(false);

				return {
					available: false,
					downloadUrl: null,
					newVersion: null,
				};
			}

			if (semver.gt(coercedVersion, appVersion)) {
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

					setUpdateAvailable(true);
					setNewVersion(coercedVersion.raw);

					db.set("lastUpdateAlert", new Date().toISOString());
					db.set("lastUpdateAlertVersion", coercedVersion.raw);
				}

				const apkAsset = releaseData.assets.find((asset) =>
					asset.name.endsWith(".apk"),
				);

				setIsCheckingUpdate(false);

				return {
					available: true,
					downloadUrl: apkAsset?.browser_download_url ?? null,
					newVersion: coercedVersion.raw,
				};
			}

			setIsCheckingUpdate(false);

			return {
				available: false,
				downloadUrl: null,
				newVersion: null,
			};
		}, []);

	const downloadUpdateApk = useCallback(async () => {
		const res = await checkForUpdatesApk();

		if (!res.downloadUrl || !res.available) return "No update available";

		setIsDownloading(true);

		const cacheDir = new Directory(Paths.cache);

		const updatePath = `${cacheDir.uri}/update.apk`;

		try {
			const downloadResumable = FileSystem.createDownloadResumable(
				res.downloadUrl,
				updatePath,
				{},
				(downloadProgress) => {
					const percentage =
						(downloadProgress.totalBytesWritten /
							downloadProgress.totalBytesExpectedToWrite) *
						100;

					setDownloadPercentage(percentage);
				},
			);

			await downloadResumable.downloadAsync();

			await startActivityAsync("android.intent.action.INSTALL_PACKAGE", {
				data: updatePath,
				flags: 1,
			});

			setIsDownloading(false);

			return "Successfully downloaded the update";
		} catch (e) {
			setIsDownloading(false);
			return `Something went wrong...`;
		}
	}, []);

	const checkForUpdatesGooglePlay: () => Promise<UpdateCheckResult> =
		useCallback(async () => {
			setIsCheckingUpdate(false);

			const inAppUpdates = new SpInAppUpdates(false);

			const needsUpdateRes = await inAppUpdates.checkNeedsUpdate();

			if (needsUpdateRes.shouldUpdate) {
				setIsCheckingUpdate(false);
				setUpdateAvailable(true);
				setNewVersion(needsUpdateRes.storeVersion);

				return {
					available: true,
					downloadUrl: null,
					newVersion: needsUpdateRes.storeVersion,
					inAppUpdates,
				};
			}

			setIsCheckingUpdate(false);

			return {
				available: false,
				downloadUrl: null,
				newVersion: null,
			};
		}, []);

	const downloadUpdateGooglePlay = useCallback(async () => {
		const res = await checkForUpdatesGooglePlay();

		if (!res.available || !res.inAppUpdates) return "No update available";

		const inAppUpdates = res.inAppUpdates;

		try {
			inAppUpdates.addStatusUpdateListener((status) => {
				const percentage =
					(status.bytesDownloaded / status.totalBytesToDownload) * 100;

				setDownloadPercentage(percentage);

				if (status.status === IAUInstallStatus.DOWNLOADED) {
					inAppUpdates.installUpdate();

					inAppUpdates.removeStatusUpdateListener((status) => {
						console.log("Removed listener");
					});
				}
			});

			await inAppUpdates.startUpdate({
				updateType: IAUUpdateKind.IMMEDIATE,
			});

			return "Successfully downloaded the update";
		} catch (e) {
			return "Something went wrong...";
		}
	}, []);

	const updateData = useMemo<UpdateData>(
		() => ({
			downloadPercentage,
			newVersion,
			isDownloading,
			isCheckingUpdate,
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
			downloadPercentage,
			newVersion,
			isDownloading,
			isCheckingUpdate,
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
