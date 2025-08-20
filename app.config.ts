import type { ExpoConfig, ConfigContext } from "expo/config";
import {
	version as appVersion,
	versionCode as appVersionCode,
} from "./package.json";

const IS_DEV = process.env.APP_VARIANT === "development";
const IS_PRERELEASE = process.env.APP_VARIANT === "prerelease";

export default ({ config }: ConfigContext): ExpoConfig => ({
	...config,
	name: `Zipline${IS_DEV ? " (Dev)" : ""}${IS_PRERELEASE ? "(Pre)" : ""}`,
	slug: "zipline",
	version: appVersion,
	orientation: "portrait",
	icon: `./assets/images${IS_DEV ? "/dev" : ""}/icon.png`,
	scheme: "zipline",
	newArchEnabled: true,
	userInterfaceStyle: "automatic",
	platforms: ["android"],
	ios: {
		bundleIdentifier: `com.stefdp.zipline${IS_DEV ? ".dev" : ""}${IS_PRERELEASE ? ".pre" : ""}`,
	},
	android: {
		adaptiveIcon: {
			foregroundImage: `./assets/images${IS_DEV ? "/dev" : ""}/adaptive-icon.png`,
			monochromeImage: `./assets/images${IS_DEV ? "/dev" : ""}/monochromatic-adaptive-icon.png`,
			backgroundColor: "#121317",
		},
		versionCode: appVersionCode,
		version: appVersion,
		package: `com.stefdp.zipline${IS_DEV ? ".dev" : ""}${IS_PRERELEASE ? ".pre" : ""}`,
		scheme: `com.stefdp.zipline${IS_DEV ? ".dev" : ""}${IS_PRERELEASE ? ".pre" : ""}`,
		newArchEnabled: true,
		edgeToEdgeEnabled: true,
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
				image: `./assets/images${IS_DEV ? "/dev" : ""}/splash-icon.png`,
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
