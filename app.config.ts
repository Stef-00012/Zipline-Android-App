import type { ExpoConfig, ConfigContext } from "expo/config";

const IS_DEV = process.env.APP_VARIANT === "development";

export default ({ config }: ConfigContext): ExpoConfig => ({
	...config,
	name: IS_DEV ? "Zipline (Debug)" : "Zipline",
	slug: "zipline",
	version: "1.0.0",
	orientation: "portrait",
	icon: "./assets/images/icon.png",
	scheme: "zipline",
	newArchEnabled: true,
	userInterfaceStyle: "automatic",
	platforms: ["android"],
	ios: {
		bundleIdentifier: IS_DEV
			? "com.stefdp.zipline.debug"
			: "com.stefdp.zipline",
	},
	android: {
		adaptiveIcon: {
			foregroundImage: "./assets/images/adaptive-icon.png",
			monochromeImage: "./assets/images/monochromatic-adaptive-icon.png",
			backgroundColor: "#121317",
		},
		package: IS_DEV ? "com.stefdp.zipline.debug" : "com.stefdp.zipline",
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
	updates: {
		url: "https://u.expo.dev/ec797fe5-7e0f-4c3a-8aa9-8588d9f44fb1",
		enabled: true,
		checkAutomatically: "NEVER",
		useEmbeddedUpdate: true,
	},
});
