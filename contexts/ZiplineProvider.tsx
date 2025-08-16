import { APIPublicSettings, APIWebSettings } from "@/types/zipline";
import React, {
    createContext,
    useState,
    useCallback,
    useMemo,
    useEffect,
} from "react";
import { getPublicSettings, getWebSettings } from "@/functions/zipline/settings";

interface Props {
    children: React.ReactNode;
}

interface ZiplineSettings {
    publicSettings: APIPublicSettings | false;
    webSettings: APIWebSettings | false;
    updateSettings: () => Promise<{
        publicSettings: APIPublicSettings | false,
        webSettings: APIWebSettings | false
    }>;
}

export const ZiplineContext = createContext<ZiplineSettings>({
    webSettings: false,
    publicSettings: false,
    updateSettings: async () => ({
        webSettings: false,
        publicSettings: false
    }),
});

export default function ZiplineProvider({ children }: Props) {
    const [publicSettings, setPublicSettings] = useState<APIPublicSettings | false>(false);
    const [webSettings, setWebSettings] = useState<APIWebSettings | false>(false);

    const updateSettings: () => ReturnType<ZiplineSettings["updateSettings"]> = useCallback(async () => {
        const publicSettings = await getPublicSettings();
        const webSettings = await getWebSettings();

        setPublicSettings(typeof publicSettings === "string" ? false : publicSettings);
        setWebSettings(typeof webSettings === "string" ? false : webSettings);

        return {
            publicSettings: typeof publicSettings === "string" ? false : publicSettings,
            webSettings: typeof webSettings === "string" ? false : webSettings,
        };
    }, [])

    const ziplineSettings = useMemo<ZiplineSettings>(() => ({
        publicSettings: publicSettings,
        webSettings: webSettings,
        updateSettings,
    }), [publicSettings, webSettings, updateSettings]);

    useEffect(() => {
        updateSettings()
    }, []);

    return <ZiplineContext.Provider value={ziplineSettings}>{children}</ZiplineContext.Provider>;
}
