import { getStats } from "@/functions/zipline/stats";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import type { APIStats } from "@/types/zipline";
import { Redirect } from "expo-router";
import { useEffect, useState } from "react";
import { View } from "react-native";

export default function Metrics() {
    useAuth()
	useShareIntent()

    const [stats, setStats] = useState<APIStats | null>();
    const [formattedStats, setFormattedStats] = useState<APIStats | null>();

    useEffect(() => {
        (async () => {
            const stats = await getStats();
            
            setStats(typeof stats === "string" ? null : stats);
        })()
    }, [])

    return (
        <Redirect href="/temp_wip" />
    )

    // return (
    //     <View>
    //         <View
	// 			style={{
	// 				flex: 1,
	// 			}}
	// 		>
    //             {/* ___________________________________________________________
    //                 |    elements   |         charts         |  date picker   |
    //                 | @mantine/core |    @mantine/charts     | @mantine/dates |
    //                 |               | react-native-chart-kit |                |
    //                 -----------------------------------------------------------
    //             */}
    //         </View>
    //     </View>
    // )
}