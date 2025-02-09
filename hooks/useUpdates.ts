import { repoName, username } from "@/constants/updates";
import { version as appVersion } from "@/package.json";
import axios from "axios";
import { useEffect, useState } from "react";
import semver from "semver";
import * as db from "@/functions/database";
// import * as DocumentPicker from "expo-document-picker";
// import * as FileSystem from "expo-file-system";
import { ToastAndroid } from "react-native";
import { type ExternalPathString, useRouter } from "expo-router";

export function useAppUpdates() {
	const router = useRouter();

	const [isUpdateAvailable, setIsUpdateAvailable] = useState<boolean>(false);
	const [isChecking, setIsChecking] = useState<boolean>(false);
	const [isDownloading, setIsDownloading] = useState<boolean>(false);
	// const [downloadProgress, setDownloadProgress] = useState<number>(0);
	// const [downloadSize, setDownloadSize] = useState<number>(0);

	useEffect(() => {
		setIsUpdateAvailable(false)

		checkForUpdates();
	}, []);

	async function checkForUpdates(): Promise<
		| {
				downloadUrl: string;
				available: true;
				nextVersion: string;
				currentVersion: string;
		  }
		| {
				downloadUrl: null;
				available: false;
				nextVersion: null;
				currentVersion: string;
		  }
	> {
		setIsChecking(true);
		setIsUpdateAvailable(false)

		try {
			const res = await axios.get(
				`https://api.github.com/repos/${username}/${repoName}/releases/latest`,
			);

			if (!Array.isArray(res.data.assets) || !res.data.assets?.length)
				return {
					available: false,
					downloadUrl: null,
					nextVersion: null,
					currentVersion: appVersion,
				};

			const latestVersion = res.data.tag_name;
			const coercedVersion = semver.coerce(latestVersion);

			if (!coercedVersion) {
				setIsChecking(false);
				setIsUpdateAvailable(false);

				return {
					available: false,
					downloadUrl: null,
					nextVersion: null,
					currentVersion: appVersion,
				};
			}

			if (semver.gt(coercedVersion, appVersion)) {
				setIsUpdateAvailable(true);
				setIsChecking(false);

				const lastUpdateAlert = db.get("lastUpdateAlert");

				// 1000 * 60 * 30 = 30 minutes
				if (
					!lastUpdateAlert ||
					new Date().getTime() - new Date(lastUpdateAlert).getTime() >
						1000 * 60 * 30
				) {
					ToastAndroid.show(
						"There's an Update Available, head to the user settings to download it",
						ToastAndroid.LONG,
					);

					db.set("lastUpdateAlert", new Date().toISOString());
				}

				return {
					available: true,
					downloadUrl: res.data.assets[0].browser_download_url,
					nextVersion: coercedVersion.raw,
					currentVersion: appVersion,
				};
			}

			setIsChecking(false);
			setIsUpdateAvailable(false);

			return {
				available: false,
				downloadUrl: null,
				nextVersion: null,
				currentVersion: appVersion,
			};
		} catch (e) {
			setIsChecking(false);
			setIsUpdateAvailable(false);

			return {
				available: false,
				downloadUrl: null,
				nextVersion: null,
				currentVersion: appVersion,
			};
		}
	}

	async function downloadUpdate() {
		const update = await checkForUpdates();

		if (!update.available) return "No update available";

		setIsDownloading(true);

		const downloadUrl = update.downloadUrl;

		router.replace(downloadUrl as ExternalPathString);

		// let savedUpdateDownloadUri = db.get("updateDownloadPath");

		// if (!savedUpdateDownloadUri) {
		// 	const permissions =
		// 		await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();

		// 	if (!permissions.granted) {
		// 		setIsDownloading(false);

		// 		return "The permission to save the update APK was not granted";
		// 	}

		// 	db.set("updateDownloadPath", permissions.directoryUri);
		// 	savedUpdateDownloadUri = permissions.directoryUri;
		// }

		// const saveUri = await FileSystem.StorageAccessFramework.createFileAsync(
		// 	savedUpdateDownloadUri,
		// 	`zipline-v${update.nextVersion}.apk`,
		// 	"application/vnd.android.package-archive",
		// );

		// console.debug("saveUri", saveUri);

		// const downloadResumable = FileSystem.createDownloadResumable(
		// 	downloadUrl,
		// 	`${FileSystem.cacheDirectory}/app-release.apk`,
		// 	{},
		// 	(downloadProgress) => {
		// 		const progress = Math.floor((downloadProgress.totalBytesWritten / downloadProgress.totalBytesExpectedToWrite) * 100);

		// 		setDownloadProgress(progress)
		// 		setDownloadSize(downloadProgress.totalBytesExpectedToWrite);
		// 	},
		// );

		// const downloadResult = await downloadResumable.downloadAsync();

		// if (!downloadResult?.uri) {
		// 	console.debug("no uri")
		// 	setIsDownloading(false);

		// 	return "Failed to download the update APK";
		// }

		///////////////////////////////////////////////////////////////////////

		// console.debug("reading file as base64", downloadResult.uri);
		// const base64Export = await FileSystem.readAsStringAsync(
		// 	downloadResult.uri,
		// 	{
		// 		encoding: FileSystem.EncodingType.Base64,
		// 	},
		// );

		// console.debug("writing file as base64");
		// await FileSystem.writeAsStringAsync(saveUri, base64Export, {
		// 	encoding: FileSystem.EncodingType.Base64,
		// });

		///////////////////////////////////////////////////////////////////////

		// const chunkSize = 1024 * 1024; // 1MB
		// const fileInfo = await FileSystem.getInfoAsync(downloadResult.uri, {
		// 	size: true
		// });

		// if (!fileInfo.exists) {
		// 	console.debug("no exist")
		// 	setIsDownloading(false);

		// 	return "Something went wrong while downloading the apk...";
		// }

		// const totalChunks = Math.ceil(fileInfo.size / chunkSize);

		// for (let i = 0; i < totalChunks; i++) {
		// 	const start = i * chunkSize;
		// 	const end = Math.min(start + chunkSize, fileInfo.size);
		// 	console.debug("writing chunk", { i, start, end });
		// 	const chunk = await FileSystem.readAsStringAsync(downloadResult.uri, {
		// 		encoding: FileSystem.EncodingType.Base64,
		// 		position: start,
		// 		length: end - start,
		// 	});
		// 	await FileSystem.writeAsStringAsync(saveUri, chunk, {
		// 		encoding: FileSystem.EncodingType.Base64,
		// 	});
		// }

		// setIsDownloading(false);

		// return "Successfully downloaded the update, Please install the APK";
	}

	return {
		checkForUpdates,
		downloadUpdate,
		isChecking,
		isDownloading,
		isUpdateAvailable,
		// downloadProgress,
		// downloadSize
	};
}
