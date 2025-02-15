import type { ReactNode } from "react";
import { type DimensionValue, ScrollView, View } from "react-native";
import { Table as NativeTable, Row } from "react-native-reanimated-table";
import { styles } from "@/styles/components/table";

interface Props {
	headerRow: Array<ReactNode>;
	rows: Array<Array<ReactNode>>;
	rowWidth: Array<number>;
	maxHeight?: DimensionValue;
}

export default function Table({ headerRow, rows, rowWidth, maxHeight }: Props) {
	return (
		<ScrollView showsHorizontalScrollIndicator={false} horizontal>
			<View>
				<NativeTable>
					<Row
						data={headerRow}
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

							return (
								<Row
									// biome-ignore lint/suspicious/noArrayIndexKey: .
									key={index}
									data={row}
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
	);
}
