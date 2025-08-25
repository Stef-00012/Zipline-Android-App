import type { ExpoConfig, ConfigContext } from "expo/config";
import {
	version as appVersion,
	versionCode as appVersionCode,
} from "./package.json";

const IS_DEV = process.env.APP_VARIANT === "development";
const IS_PRERELEASE = process.env.APP_VARIANT === "prerelease";

let environmentAppName = "";
let environmentAppIdentifier = "";
let environmentAssetsPath = "";

if (IS_DEV) {
	environmentAppName = " (Dev)";
	environmentAppIdentifier = ".dev";
	environmentAssetsPath = "/dev";
}

if (IS_PRERELEASE) {
	environmentAppName = " (Pre)";
	environmentAppIdentifier = ".pre";
	environmentAssetsPath = "/pre";
}

export default ({ config }: ConfigContext): ExpoConfig => ({
	...config,
	name: `Zipline${environmentAppName}`,
	slug: "zipline",
	version: appVersion,
	orientation: "portrait",
	icon: `./assets/images${environmentAssetsPath}/icon.png`,
	scheme: "zipline",
	newArchEnabled: true,
	userInterfaceStyle: "automatic",
	platforms: ["android"],
	ios: {
		bundleIdentifier: `com.stefdp.zipline${environmentAppIdentifier}`,
	},
	android: {
		adaptiveIcon: {
			foregroundImage: `./assets/images${environmentAssetsPath}/adaptive-icon.png`,
			monochromeImage: `./assets/images${environmentAssetsPath}/monochromatic-adaptive-icon.png`,
			backgroundColor: "#121317",
		},
		versionCode: appVersionCode,
		version: appVersion,
		package: `com.stefdp.zipline${environmentAppIdentifier}`,
		scheme: `com.stefdp.zipline${environmentAppIdentifier}`,
		newArchEnabled: true,
		edgeToEdgeEnabled: true,
		permissions: [
			"REQUEST_INSTALL_PACKAGES"
		]
	},
	plugins: [
		"expo-router",
		[
			"expo-secure-store",
			{
				faceIDPermission:
					"Allow $(PRODUCT_NAME) to access your Face ID biometric data.",
			},
		],
		[
			"expo-splash-screen",
			{
				image: `./assets/images${environmentAssetsPath}/splash-icon.png`,
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
		["expo-image-picker"],
	],
	experiments: {
		typedRoutes: true,
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
