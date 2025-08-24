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

interface HeaderData {
    hidden: boolean;
    updateHidden: (hidden: boolean) => void;
}

export const HeaderContext = createContext<HeaderData>({
    hidden: false,
    updateHidden: () => {},
});

export default function HeaderProvider({ children }: Props) {
    const [headerHidden, setHeaderHidden] = useState<boolean>(false);

    const updateHidden = useCallback((hidden: boolean) => {
        setHeaderHidden(hidden)
    }, [])

    const headerData = useMemo<HeaderData>(() => ({
        hidden: headerHidden,
        updateHidden,
    }), [headerHidden, updateHidden]);

    return <HeaderContext.Provider value={headerData}>{children}</HeaderContext.Provider>;
}
