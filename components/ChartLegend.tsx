import { styles } from "@/styles/components/chartLegend";
import { Text, View, type ColorValue } from "react-native";

interface Props {
    data: Array<{
        label: string;
        color: ColorValue;
    }>
}

export default function ChartLegend({ data }: Props) {
    return (
        <View style={styles.mainLegendContainer}>
            {data.map(({ label, color }) => (
                <View key={label} style={styles.legendContainer}>
                    <View style={{ ...styles.legendColor, backgroundColor: color }} />
                    <Text style={styles.legendtText}>{label}</Text>
                </View>
            ))}
        </View>
    )
}