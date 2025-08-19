import { isAuthenticated } from "@/functions/zipline/auth";
import { getVersion } from "@/functions/zipline/version";
import * as db from "@/functions/database";
import { APISelfUser, APIUser } from "@/types/zipline";
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
import { getCurrentUser, getCurrentUserAvatar } from "@/functions/zipline/user";

interface Props {
	children: React.ReactNode;
}

interface AuthData {
    role: APIUser["role"] | false;
    user: APISelfUser | null;
    avatar: string | null;
    serverVersion: string;
    updateAuth: () => Promise<void>;
    updateUser: () => Promise<void>;
}

export const AuthContext = createContext<AuthData>({
	role: false,
    user: null,
    avatar: null,
    serverVersion: "0.0.0",
	updateAuth: async () => {},
	updateUser: async () => {},
});

export default function AuthProvider({ children }: Props) {
    const router = useRouter();
    const pathname = usePathname();

	const [role, setRole] = useState<APIUser["role"] | false>(false);
    const [version, setVersion] = useState<string>("0.0.0");

    const [user, setUser] = useState<APISelfUser | null>(null);
    const [avatar, setAvatar] = useState<string | null>(null);

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
            semver.lt(serverVersion, minimumVersion)) && pathname !== "/login"
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

    const updateUser = useCallback(async () => {
        const currentUser = await getCurrentUser();
        const currentUserAvatar = await getCurrentUserAvatar();

        setUser(typeof currentUser === "string" ? null : currentUser);
        setAvatar(currentUserAvatar)
    }, [])

    const authData = useMemo<AuthData>(() => ({
        role,
        user,
        avatar,
        updateAuth,
        updateUser,
        serverVersion: version,
    }), [role, user, avatar, updateAuth, updateUser, version]);

	useEffect(() => {
		updateAuth();
        updateUser();
	}, []);

	return <AuthContext.Provider value={authData}>{children}</AuthContext.Provider>;
}
