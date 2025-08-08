import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { useState, useEffect, type ReactNode } from "react";
import { styles } from "@/styles/components/select";
import CheckBox from "@/components/CheckBox";
import {
	Text,
	View,
	TouchableOpacity,
	FlatList,
	Modal,
	type DimensionValue,
} from "react-native";

export interface SelectProps {
	data: {
		label: string;
		value: string;
		[key: string]: string | number | boolean | null;
	}[];
	placeholder: string;
	onSelect: (selectedItem: SelectProps["data"], id?: SelectProps["id"]) => void;
	showScrollIndicator?: boolean;
	disabled?: boolean;
	defaultValue?: SelectProps["data"][0];
	defaultValues?: SelectProps["data"];
	maxHeight?: number;
	multiple?: boolean;
	renderSelectedItem?: (
		item: SelectProps["data"][0],
		key: string,
	) => ReactNode | string;
	renderItem?: (
		item: SelectProps["data"][0],
		closeSelect: () => void,
	) => ReactNode;
	id?: string;
	width?: DimensionValue;
	margin?: {
		top?: DimensionValue;
		bottom?: DimensionValue;
		left?: DimensionValue;
		right?: DimensionValue;
	};
	title?: string;
	description?: string;
}

export default function Select({
	data,
	placeholder,
	showScrollIndicator,
	defaultValue,
	onSelect,
	defaultValues,
	disabled = false,
	maxHeight = 200,
	multiple = false,
	width,
	title,
	description,
	margin = {},
	renderSelectedItem = (item, key) => (
		<Text
			key={key}
			style={{
				...styles.selectText,
				...styles.selectedText,
			}}
		>
			{item.label}
		</Text>
	),
	renderItem = (item) => <Text style={styles.menuItemText}>{item.label}</Text>,
	id,
}: SelectProps) {
	const [selectedItems, setSelectedItems] = useState<SelectProps["data"]>(
		getDefaultValues(),
	);
	const [isOpen, setIsOpen] = useState(false);

	/*
        biome-ignore lint/correctness/useExhaustiveDependencies: this hook has to update the
        default values when they are changed, and those values are used in the function it uses
    */
	useEffect(() => {
		setSelectedItems(getDefaultValues());
	}, [defaultValue, defaultValues, multiple]);

	function getDefaultValues(): SelectProps["data"] {
		if (multiple) {
			if (defaultValues) return defaultValues;
			if (defaultValue) return [defaultValue];
			return [];
		}

		if (defaultValue) return [defaultValue];
		if (defaultValues?.length) return [defaultValues[0]];
		return [];
	}

	function handleSelect(item: SelectProps["data"][0], _index: number) {
		if (multiple) {
			setSelectedItems((prevItems) => {
				const isSelected = prevItems.some(
					(selectedItem) => selectedItem.value === item.value,
				);
				if (isSelected) {
					return prevItems.filter(
						(selectedItem) => selectedItem.value !== item.value,
					);
				}

				return [...prevItems, item];
			});
		} else {
			setSelectedItems([item]);
			setIsOpen(false);
			onSelect([item], id);
		}
	}

	return (
		<View
			style={{
				width: width,
				marginLeft: margin.left,
				marginRight: margin.right,
				marginTop: margin.top,
				marginBottom: margin.bottom,
			}}
		>
			{title && (
				<>
					<Text
						style={{
							...styles.inputHeader,
							...(!description && {
								marginBottom: 5,
							}),
							...(disabled && styles.inputHeaderDisabled),
						}}
					>
						{title}
					</Text>

					{description && (
						<Text style={styles.inputDescription}>{description}</Text>
					)}
				</>
			)}
			<TouchableOpacity
				style={styles.selectButton}
				onPress={() => {
					const open = !isOpen;

					setIsOpen(open);
					if (!open) onSelect(selectedItems, id);
				}}
				disabled={disabled}
			>
				<View style={styles.selectedTextContainer}>
					{selectedItems.length ? (
						selectedItems.map((item) => (
							<View key={item.value} style={styles.selectedItemContainer}>
								{renderSelectedItem(item, item.value)}
							</View>
						))
					) : (
						<Text style={styles.selectText}>{placeholder}</Text>
					)}
				</View>
				<MaterialIcons
					name={isOpen ? "keyboard-arrow-up" : "keyboard-arrow-down"}
					size={32}
					color={styles.selectText.color}
				/>
			</TouchableOpacity>

			{isOpen && (
				<Modal
					transparent
					animationType="fade"
					visible={isOpen}
					onRequestClose={() => {
						setIsOpen(false);
						onSelect(selectedItems, id);
					}}
				>
					<TouchableOpacity
						style={styles.selectContainer}
						onPress={() => {
							setIsOpen(false);
							onSelect(selectedItems, id);
						}}
					>
						<View
							style={{
								...styles.select,
								maxHeight: maxHeight,
							}}
						>
							<FlatList
								data={data}
								style={styles.openSelectContainer}
								keyExtractor={(_item, index) => index.toString()}
								showsVerticalScrollIndicator={showScrollIndicator}
								renderItem={({ item, index }) => (
									<TouchableOpacity
										style={{
											...styles.menuItem,
											...(selectedItems.some(
												(selectedItem) => selectedItem.value === item.value,
											) && styles.menuItemSelected),
										}}
										onPress={() => handleSelect(item, index)}
									>
										{multiple && (
											<CheckBox
												value={selectedItems.some(
													(selectedItem) => selectedItem.value === item.value,
												)}
												onValueChange={() => handleSelect(item, index)}
											/>
										)}
										{renderItem(item, () => {
											setIsOpen(false);
										})}
									</TouchableOpacity>
								)}
							/>
						</View>
					</TouchableOpacity>
				</Modal>
			)}
		</View>
	);
}
