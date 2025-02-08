import { styles } from "@/styles/components/select";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { useState, useEffect, type ReactNode } from "react";
import { Text, View, TouchableOpacity, FlatList, Modal } from "react-native";
import CheckBox from "./CheckBox";

export interface SelectProps {
	data: Array<{
		label: string;
		value: string;
		[key: string]: string | number | boolean | null;
	}>;
	placeholder: string;
	onSelect: (selectedItem: SelectProps["data"]) => void;
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
	renderItem?: (item: SelectProps["data"][0]) => ReactNode;
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

	function handleSelect(item: SelectProps["data"][0], index: number) {
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
			onSelect([item]);
		}
	}

	return (
		<View>
			<TouchableOpacity
				style={styles.selectButton}
				onPress={() => {
					const open = !isOpen;

					setIsOpen(open);
					if (!open) onSelect(selectedItems);
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
						onSelect(selectedItems);
					}}
				>
					<TouchableOpacity
						style={styles.selectContainer}
						onPress={() => {
							setIsOpen(false);
							onSelect(selectedItems);
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
								keyExtractor={(item, index) => index.toString()}
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
										{renderItem(item)}
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
