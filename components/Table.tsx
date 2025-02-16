import { Table as NativeTable, Row } from "react-native-reanimated-table";
import { Pressable, ScrollView, Text, View } from "react-native";
import { styles } from "@/styles/components/table";
import { MaterialIcons } from "@expo/vector-icons";
import { useState, type ReactNode } from "react";

type HeaderRow = {
	row: ReactNode;
	sortable?: boolean;
	searchable?: boolean;
	id?: string;
};

interface RowSort {
	[key: string]: "asc" | "desc" | null;
}

interface Props {
	headerRow: Array<HeaderRow>;
	rows: Array<Array<ReactNode>>;
	rowWidth: Array<number>;
	sortKey?: {
		id: string;
		sortOrder: "asc" | "desc";
	};
	onSortOrderChange?: (
		sortKey: string,
		sortOrder: "asc" | "desc",
	) => Promise<void> | void;
	onSearch?: (searchKey: string) => Promise<void> | void;
}

export default function Table({
	headerRow,
	rows,
	rowWidth,
	sortKey,
	onSearch = () => {},
	onSortOrderChange = () => {},
}: Props) {
	const defaultSortOrders: RowSort = {};

	for (const row of headerRow) {
		if (!row.id) continue;

		defaultSortOrders[row.id] = null;
	}

	if (sortKey) defaultSortOrders[sortKey.id] = sortKey.sortOrder;

	const [sortOrders, setSortOrders] = useState<RowSort>(defaultSortOrders);

	const updatedHeaderRow = headerRow.map((row) => {
		if (!row.id || (!row.searchable && !row.sortable)) return row.row;

		const sortIcon = getSortIcon(sortOrders[row.id]);

		return (
			<Pressable
				key={row.id}
				disabled={!row.sortable || !row.id}
				onPress={() => {
					if (!row.id) return;

					const sortOrder = sortOrders[row.id] === "asc" ? "desc" : "asc";

					setSortOrders((prev) => {
						if (!row.id) return prev;

						const previous = Object.keys(prev).reduce((acc, key) => {
							acc[key as string] = null;

							return acc;
						}, {} as RowSort);

						return {
							...previous,
							[row.id]: sortOrder,
						};
					});

					onSortOrderChange(row.id, sortOrder);
				}}
				style={styles.rowContainer}
			>
				{["string", "number", "boolean"].includes(typeof row.row) ? (
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
				)}

				<View style={styles.actionsContainer}>
					{row.sortable && (
						<MaterialIcons
							name={sortIcon}
							color={sortIcon === "sort" ? "#303e58" : "white"}
							size={16}
							style={styles.sortableIcon}
						/>
					)}

					{row.searchable && row.id && (
						<Pressable
							style={styles.searchButton}
							onPress={() => {
								if (row.id) return onSearch(row.id);
							}}
						>
							<MaterialIcons name="filter-alt" color="#303e58" size={16} />
						</Pressable>
					)}
				</View>
			</Pressable>
		);
	});

	function getSortIcon(
		order: "asc" | "desc" | null,
	): keyof typeof MaterialIcons.glyphMap {
		switch (order) {
			case "asc":
				return "north";
			case "desc":
				return "south";
			default:
				return "sort";
		}
	}

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
