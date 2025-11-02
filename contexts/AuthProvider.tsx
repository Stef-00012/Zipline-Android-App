import { minimumVersion } from "@/constants/auth";
import * as db from "@/functions/database";
import { isAuthenticated } from "@/functions/zipline/auth";
import { getCurrentUser, getCurrentUserAvatar } from "@/functions/zipline/user";
import { getVersion } from "@/functions/zipline/version";
import { APISelfUser, APIUser } from "@/types/zipline";
import * as LocalAuthentication from "expo-local-authentication";
import { usePathname, useRouter } from "expo-router";
import React, {
	createContext,
	useCallback,
	useEffect,
	useMemo,
	useState,
} from "react";
import { BackHandler } from "react-native";
import semver from "semver";

interface Props {
	children: React.ReactNode;
}

interface AuthData {
	role: APIUser["role"] | false;
	user: APISelfUser | null;
	avatar: string | null;
	serverVersion: string;
	isAuthenticating: boolean;
	unlockWithBiometrics: boolean;
	supportsBiometrics: boolean;
	hasEnrolledBiometrics: boolean;
	supportsAuthenticationTypes: LocalAuthentication.AuthenticationType[];
	updateAuth: () => Promise<void>;
	updateUser: () => Promise<void>;
	updateBiometricsSetting: (enabled: boolean) => void;
	requestBiometricsAuthentication: () => Promise<boolean>;
}

export const AuthContext = createContext<AuthData>({
	role: false,
	user: null,
	avatar: null,
	isAuthenticating: false,
	unlockWithBiometrics: false,
	supportsBiometrics: false,
	hasEnrolledBiometrics: false,
	supportsAuthenticationTypes: [],
	serverVersion: "0.0.0",
	updateAuth: async () => {},
	updateUser: async () => {},
	updateBiometricsSetting: () => {},
	requestBiometricsAuthentication: async () => true,
});

export default function AuthProvider({ children }: Props) {
	const router = useRouter();
	const pathname = usePathname();

	const [role, setRole] = useState<APIUser["role"] | false>(false);
	const [version, setVersion] = useState<string>("0.0.0");

	const [user, setUser] = useState<APISelfUser | null>(null);
	const [avatar, setAvatar] = useState<string | null>(null);

	const [unlockWithBiometrics, setUnlockWithBiometrics] = useState<boolean>(
		db.get("unlockWithBiometrics") === "true",
	);

	const [supportsBiometrics, setSupportsBiometrics] = useState<boolean>(false);
	const [hasEnrolledBiometrics, setHasEnrolledBiometrics] =
		useState<boolean>(false);
	const [supportsAuthenticationTypes, setSupportsAuthenticationTypes] =
		useState<LocalAuthentication.AuthenticationType[]>([]);

	const [isAuthenticating, setIsAuthenticating] =
		useState<boolean>(unlockWithBiometrics);

	const updateAuth = useCallback(async () => {
		const userRole = await isAuthenticated();
		const versionData = await getVersion();

		const serverVersion =
			typeof versionData === "string"
				? "0.0.0"
				: "version" in versionData
					? versionData.version
					: versionData.details?.version;

		if (!userRole && pathname !== "/login") router.replace("/login");

		if (
			(typeof versionData === "string" ||
				semver.lt(serverVersion, minimumVersion)) &&
			pathname !== "/login"
		) {
			await db.del("url");
			await db.del("token");

			return router.replace("/login");
		}

		if (
			pathname === "/login" &&
			userRole &&
			semver.gte(serverVersion, minimumVersion)
		) {
			router.replace("/");
		}

		setRole(userRole);
		setVersion(serverVersion);
	}, []);

	const updateUser = useCallback(async () => {
		const currentUser = await getCurrentUser();
		const currentUserAvatar = await getCurrentUserAvatar();

		setUser(typeof currentUser === "string" ? null : currentUser);
		setAvatar(currentUserAvatar);
	}, []);

	const updateBiometricsSetting = useCallback((enabled: boolean) => {
		setUnlockWithBiometrics(enabled);
		db.set("unlockWithBiometrics", enabled ? "true" : "false");
	}, []);

	const requestBiometricsAuthentication = useCallback(async () => {
		if (unlockWithBiometrics) {
			setIsAuthenticating(true);

			const output = await LocalAuthentication.authenticateAsync({
				biometricsSecurityLevel: "weak",
				cancelLabel: "Close App",
				promptMessage: "Unlock Zipline",
				requireConfirmation: true,
			});

			setIsAuthenticating(false);

			if (output.success) return true;

			return false;
		}

		return true;
	}, [unlockWithBiometrics]);

	const authData = useMemo<AuthData>(
		() => ({
			role,
			user,
			avatar,
			isAuthenticating,
			unlockWithBiometrics,
			supportsBiometrics,
			hasEnrolledBiometrics,
			supportsAuthenticationTypes,
			updateAuth,
			updateUser,
			updateBiometricsSetting,
			requestBiometricsAuthentication,
			serverVersion: version,
		}),
		[
			role,
			user,
			avatar,
			isAuthenticating,
			unlockWithBiometrics,
			supportsBiometrics,
			hasEnrolledBiometrics,
			supportsAuthenticationTypes,
			updateAuth,
			updateUser,
			updateBiometricsSetting,
			requestBiometricsAuthentication,
			version,
		],
	);

	useEffect(() => {
		updateAuth();
		updateUser();

		(async () => {
			const hasHardware = await LocalAuthentication.hasHardwareAsync();
			const isEnrolled = await LocalAuthentication.isEnrolledAsync();
			const supportedAuthTypes =
				await LocalAuthentication.supportedAuthenticationTypesAsync();

			setSupportsBiometrics(hasHardware);
			setHasEnrolledBiometrics(isEnrolled);
			setSupportsAuthenticationTypes(supportedAuthTypes);
		})();
	}, []);

	useEffect(() => {
		(async () => {
			if (unlockWithBiometrics) {
				const authenticated = await requestBiometricsAuthentication();

				if (!authenticated) return BackHandler.exitApp();

				return;
			}
		})();
	}, [unlockWithBiometrics]);

	return (
		<AuthContext.Provider value={authData}>{children}</AuthContext.Provider>
	);
}
