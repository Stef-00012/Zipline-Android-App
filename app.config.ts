import type { ExpoConfig, ConfigContext } from "expo/config";

const IS_DEV = process.env.APP_VARIANT === "development";
const IS_RELEASE = process.env.APP_VARIANT === "devrelease";

export default ({ config }: ConfigContext): ExpoConfig => ({
	...config,
	name: `Zipline${IS_DEV ? " (Dev)" : IS_RELEASE ? " (Dev Release)" : ""}`,
	slug: "zipline",
	version: "1.0.2",
	orientation: "portrait",
	icon: "./assets/images/icon.png",
	scheme: "zipline",
	newArchEnabled: true,
	userInterfaceStyle: "automatic",
	platforms: ["android"],
	ios: {
		bundleIdentifier: `com.stefdp.zipline${IS_DEV ? ".dev" : IS_RELEASE ? ".devrelease" : ""}`
	},
	android: {
		adaptiveIcon: {
			foregroundImage: "./assets/images/adaptive-icon.png",
			monochromeImage: "./assets/images/monochromatic-adaptive-icon.png",
			backgroundColor: "#121317",
		},
		package: `com.stefdp.zipline${IS_DEV ? ".dev" : IS_RELEASE ? ".devrelease" : ""}`
	},
	androidStatusBar: {
		barStyle: "light-content",
		backgroundColor: "#0c101c",
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
				image: "./assets/images/splash-icon.png",
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
