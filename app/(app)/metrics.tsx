import Button from "@/components/Button";
import Popup from "@/components/Popup";
import { getStats } from "@/functions/zipline/stats";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import { styles } from "@/styles/metrics";
import type { APIStats } from "@/types/zipline";
import { Redirect } from "expo-router";
import { useEffect, useState } from "react";
import { ScrollView, Text, View } from "react-native";
import DatePicker from "@/components/DatePicker";
import { add } from "date-fns";
import type { DateType } from "react-native-ui-datepicker";

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

    const [datePickerOpen, setDatePickerOpen] = useState(false)

    const [range, setRange] = useState<{
        startDate: DateType;
        endDate: DateType;
    }>({
        startDate: add(new Date(), {
            weeks: -1
        }),
        endDate: new Date()
    });

    
    // MAIN:

    return <Redirect href="/temp_wip" />

    return (
        <View style={styles.mainContainer}>
            <View style={styles.mainContainer}>
                <DatePicker
                    open={datePickerOpen}
                    onClose={() => {
                        setDatePickerOpen(false)
                        console.log("close")
                    }}
                    onChange={(params) => {
                        setRange(params)

                        // if (params.endDate && params.startDate) {
                        //     setDatePickerOpen(false)

                        //     handle date change
                        // }
                        console.log("params", params)
                    }}
                    mode="range"
                    startDate={range.startDate}
                    endDate={range.endDate}
                    maxDate={new Date()}
                />

                {stats ? (
                    <View>
                        <ScrollView>
                            <View style={styles.header}>
                                <Text style={styles.headerText}>Metrics</Text>
                                <Text style={styles.dateRangeText}>{new Date(range.startDate as string).toLocaleDateString()} to {new Date(range.endDate as string).toLocaleDateString()}</Text>
                            </View>
                        </ScrollView>
                        {/* <Button
                            onPress={() => {
                                setDatePickerOpen(true)
                            }}
                            text="test"
                            color="blue"
                        /> */}
                    </View>
                ) : (
                    <View style={styles.loadingContainer}>
                        <Text style={styles.loadingText}>Loading...</Text>
                    </View>
                )}
            </View>
        </View>
    )

    /* return (
        <View>
            <View
				style={{
					flex: 1,
				}}
			>
                {   ___________________________________________________________
                    |    elements   |         charts         |  date picker   |
                    | @mantine/core |    @mantine/charts     | @mantine/dates |
                    |               | react-native-chart-kit |                |
                    -----------------------------------------------------------
                }
            </View>
        </View>
    ) */
}