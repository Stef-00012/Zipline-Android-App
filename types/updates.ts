import type SpInAppUpdates from "sp-react-native-in-app-updates";

export interface UpdateCheckResult {
	available: boolean;
	downloadUrl: string | null;
	newVersion: string | null;
	inAppUpdates?: SpInAppUpdates;
}
