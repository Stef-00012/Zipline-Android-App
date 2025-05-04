import { Table as NativeTable, Row } from "react-native-reanimated-table";
import { styles } from "@/styles/components/table";
import { Skeleton } from "moti/skeleton";
import { colors } from "@/constants/skeleton";
import {
    Pressable,
    ScrollView,
    Text,
    View,
	type DimensionValue,
} from "react-native";

interface Props {
    headerRow: Array<string>;
    rows: Array<Array<DimensionValue>>;
    rowWidth: Array<number>;
}

export default function SkeletonTable({
    headerRow,
    rows,
    rowWidth
}: Props) {
    const updatedHeaderRow = headerRow.map((row) => {
        return ["string", "number", "boolean"].includes(typeof row.row) ? (
			<Text
				style={{
					...styles.rowText,
					...styles.headerRow,
				}}
			>
				{row.row}
			</Text>
		) : (
			row.row
		)
    })
    
    return (
        <ScrollView showsHorizontalScrollIndicator={false} horizontal>
			<View>
				<NativeTable>
					<Row
						data={updatedHeaderRow}
						widthArr={rowWidth}
						style={styles.tableHeader}
						textStyle={{
							...styles.rowText,
							...styles.headerRow,
						}}
					/>
				</NativeTable>
				<ScrollView
					nestedScrollEnabled
					showsVerticalScrollIndicator={false}
					style={styles.tableVerticalScroll}
				>
					<NativeTable>
						{rows.map((row, index) => {
							let rowStyle = styles.row;

							if (index === 0)
								rowStyle = {
									...styles.row,
									...styles.firstRow,
								};

							if (index === rows.length - 1)
								rowStyle = {
									...styles.row,
									...styles.lastRow,
								};
								
							const rowData = row.map(data => {
							    return (
    							    <Skeleton colors={colors} height={14} width={data} />
    							)
							})

							return (
								<Row
									// biome-ignore lint/suspicious/noArrayIndexKey: .
									key={index}
									data={rowData}
									widthArr={rowWidth}
									style={rowStyle}
									textStyle={styles.rowText}
								/>
							);
						})}
					</NativeTable>
				</ScrollView>
			</View>
		</ScrollView>
    )
}