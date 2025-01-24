import { getStats } from "@/functions/zipline/stats";
import type { APIStats } from "@/types/zipline";
import { useRouter } from "expo-router";
import { useShareIntentContext } from "expo-share-intent";
import { useEffect, useState } from "react";
import { View } from "react-native";

export default function Home() {
    const router = useRouter();
    const { hasShareIntent } = useShareIntentContext();

    // biome-ignore lint/correctness/useExhaustiveDependencies: <explanation>
    useEffect(() => {
        if (hasShareIntent) {
            router.replace({
                pathname: "/shareintent",
            });
        }
    }, [hasShareIntent]);

    const [stats, setStats] = useState<APIStats | null>();
    const [formattedStats, setFormattedStats] = useState<APIStats | null>();

    useEffect(() => {
        (async () => {
            const stats = await getStats();
            
            setStats(stats);
        })()
    }, [])

    

    return (
        <View>
            <View
				style={{
					flex: 1,
				}}
			>
                {/* ___________________________________________________________
                    |    elements   |         charts         |  date picker   |
                    | @mantine/core |    @mantine/charts     | @mantine/dates |
                    |               | react-native-chart-kit |                |
                    -----------------------------------------------------------
                */}
            </View>
        </View>
    )
}