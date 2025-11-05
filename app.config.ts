import type { ConfigContext, ExpoConfig } from "expo/config";
import {
	version as appVersion,
	versionCode as appVersionCode,
} from "./package.json";

const IS_DEV = process.env.APP_VARIANT === "development";
const IS_PRERELEASE = process.env.APP_VARIANT === "prerelease";
// const IS_PRODUCTION = process.env.APP_VARIANT === "production";

let appName = "Zipline";
let appId = "com.stefdp.zipline";
let assetsPath = "./assets/images";

if (IS_DEV) {
	appName = "Zipline (Dev)";
	appId += ".dev";
	assetsPath += "/dev";
}

if (IS_PRERELEASE) {
	appName = "Zipline (Pre)";
	appId += ".pre";
	assetsPath += "/pre";
}

export default ({ config }: ConfigContext): ExpoConfig => ({
	...config,
	name: appName,
	slug: "zipline",
	version: appVersion,
	orientation: "portrait",
	icon: `${assetsPath}/icon.png`,
	scheme: "zipline",
	newArchEnabled: true,
	userInterfaceStyle: "automatic",
	platforms: ["android"],
	description: "An Android app to manage your self-hosted zipline V4 instance.",
	primaryColor: "#121317",
	githubUrl: "https://github.com/Stef-00012/Zipline-Android-App",
	owner: "stef_dp",
	ios: {
		bundleIdentifier: appId,
	},
	android: {
		adaptiveIcon: {
			foregroundImage: `${assetsPath}/adaptive-icon.png`,
			monochromeImage: `${assetsPath}/monochromatic-adaptive-icon.png`,
			backgroundColor: "#121317",
		},
		playStoreUrl: "https://play.google.com/store/apps/details?id=com.stefdp.zipline",
		backgroundColor: "#121317",
		predictiveBackGestureEnabled: true,
		versionCode: appVersionCode,
		version: appVersion,
		package: appId,
		scheme: appId,
		newArchEnabled: true,
		edgeToEdgeEnabled: true,
		permissions: ["REQUEST_INSTALL_PACKAGES"],
	},
	plugins: [
		"expo-router",
		[
			"expo-font",
			{
				fonts: [
					"./assets/material-symbols.ttf"
				],
				android: {
					fonts: [
						{
							fontFamily: "MaterialSymbols",
							fontDefinitions: [
								{
									path: "./assets/material-symbols.ttf",
									weight: "400",
									style: "normal",
								}
							]
						}
					]
				}
			}
		],
		[
			"expo-dev-client",
			{
				launchMode: "most-recent",
				addGeneratedScheme: false,
			},
		],
		[
			"expo-splash-screen",
			{
				image: `${assetsPath}/splash-icon.png`,
				imageWidth: 300,
				resizeMode: "contain",
				backgroundColor: "#121317",
			},
		],
		[
			"expo-share-intent",
			{
				androidIntentFilters: ["*/*"],
				androidMultiIntentFilters: ["*/*"],
				disableIOS: true,
			},
		],
		[
			"@sentry/react-native/expo",
			{
				url: "https://sentry.io/",
				project: "zipline-android",
				organization: "stefdp"
			}
		],
		["expo-secure-store"],
		["expo-image-picker"],
		["expo-local-authentication"],
	],
	experiments: {
		typedRoutes: true,
		reactCompiler: true,
	},
	extra: {
		router: {
			origin: false,
		},
		eas: {
			projectId: "ec797fe5-7e0f-4c3a-8aa9-8588d9f44fb1",
		},
	},
	runtimeVersion: {
		policy: "appVersion",
	},
});
