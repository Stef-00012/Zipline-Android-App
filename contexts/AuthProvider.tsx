import { isAuthenticated } from "@/functions/zipline/auth";
import { getVersion } from "@/functions/zipline/version";
import * as db from "@/functions/database";
import { APIUser } from "@/types/zipline";
import { usePathname, useRouter } from "expo-router";
import semver from "semver";
import React, {
	createContext,
	useState,
	useCallback,
	useMemo,
	useEffect,
} from "react";
import { minimumVersion } from "@/constants/auth";

interface Props {
	children: React.ReactNode;
}

interface AuthData {
    role: APIUser["role"] | false;
    serverVersion: string;
    updateAuth: () => Promise<void>;
}

export const AuthContext = createContext<AuthData>({
	role: false,
    serverVersion: "0.0.0",
	updateAuth: async () => {},
});

export default function AuthProvider({ children }: Props) {
    const router = useRouter();
    const pathname = usePathname();

	const [role, setRole] = useState<APIUser["role"] | false>(false);
    const [version, setVersion] = useState<string>("0.0.0");

    const updateAuth = useCallback(async () => {
        const userRole = await isAuthenticated();
        const versionData = await getVersion()

        const serverVersion =
            typeof versionData === "string"
                ? "0.0.0"
                : "version" in versionData
                    ? versionData.version
                    : versionData.details?.version;

        if (!userRole) router.replace("/login");

        if (
            typeof versionData === "string" ||
            semver.lt(serverVersion, minimumVersion)
        ) {
            await db.del("url");
            await db.del("token");

            return router.replace("/login");
        }

        if (pathname === "/login" && userRole && semver.gte(serverVersion, minimumVersion)) {
            router.replace("/");
        }
        
        setRole(userRole);
        setVersion(serverVersion)
    }, [])

    const authData = useMemo<AuthData>(() => ({
        role,
        updateAuth,
        serverVersion: version,
    }), [role, updateAuth]);

	useEffect(() => {
		updateAuth()
	}, []);

	return <AuthContext.Provider value={authData}>{children}</AuthContext.Provider>;
}
