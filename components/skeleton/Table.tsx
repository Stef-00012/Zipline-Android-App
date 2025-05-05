import { Table as NativeTable, Row } from "react-native-reanimated-table";
import { styles } from "@/styles/components/table";
import { Skeleton } from "moti/skeleton";
import { colors } from "@/constants/skeleton";
import { ScrollView, Text, View, type DimensionValue } from "react-native";
import type { ReactNode } from "react";

interface Props {
	headerRow: Array<ReactNode>;
	rows: Array<Array<DimensionValue>>;
	rowWidth: Array<number>;
	rowHeight?: DimensionValue;
	disableAnimations?: boolean;
}

export default function SkeletonTable({
	headerRow,
	rows,
	rowWidth,
	rowHeight,
	disableAnimations,
}: Props) {
	const updatedHeaderRow = headerRow.map((row, index) => (
		<Text
			// biome-ignore lint/suspicious/noArrayIndexKey: <explanation>
			key={index}
			style={{
				...styles.rowText,
				...styles.headerRow,
			}}
		>
			{row}
		</Text>
	));

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
							let rowStyle = {
								...styles.row,
								...(rowHeight && { height: rowHeight }),
							};

							if (index === 0)
								rowStyle = {
									...styles.row,
									...styles.firstRow,
									...(rowHeight && { height: rowHeight }),
								};

							if (index === rows.length - 1)
								rowStyle = {
									...styles.row,
									...styles.lastRow,
									...(rowHeight && { height: rowHeight }),
								};

							const rowData = row.map((data, index) => {
								return (
									<>
										{disableAnimations ? (
											<View
												// biome-ignore lint/suspicious/noArrayIndexKey: <explanation>
												key={index}
												style={{
													backgroundColor: colors[1],
													height: 14,
													width: data,
													borderRadius: 8,
												}}
											/>
										) : (
											<Skeleton
												// biome-ignore lint/suspicious/noArrayIndexKey: <explanation>
												key={index}
												colors={colors}
												height={14}
												width={data}
											/>
										)}
									</>
								);
							});

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
	);
}
