import { styles } from "@/styles/components/select";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { Text, View } from "react-native";
import SelectDropdown from "react-native-select-dropdown";

export interface SelectProps {
	data: Array<{
		label: string;
		value: string;
		[key: string]: string | number | boolean | null;
	}>;
	placeholder: string;
	onSelect: (selectedItem: SelectProps["data"][0], index: number) => void;
	showScrollIndicator?: boolean;
	disabled?: boolean;
	defaultValue?: SelectProps["data"][0];
}

export default function Select({
	data,
	placeholder,
	showScrollIndicator,
	defaultValue,
	onSelect,
	disabled = false
}: SelectProps) {
	return (
		<SelectDropdown
			disabled={disabled}
			data={data}
			onSelect={onSelect}
			defaultValue={defaultValue}
			renderButton={(selectedItem, isOpen) => {
				return (
					<View style={styles.selectButton}>
						<Text style={{
							...styles.selectText,
							...(selectedItem?.label && styles.selectedText)
						}}>
							{selectedItem?.label || placeholder}
						</Text>

						<MaterialIcons
							name={isOpen ? "keyboard-arrow-up" : "keyboard-arrow-down"}
							size={32}
							color={styles.selectText.color}
						/>
					</View>
				);
			}}
			renderItem={(item, _index, isSelected) => {
				return (
					<View
						style={{
							...styles.menuItem,
							...(isSelected && styles.menuItemSelected),
						}}
					>
						<Text style={styles.menuItemText}>{item.label}</Text>
					</View>
				);
			}}
			showsVerticalScrollIndicator={showScrollIndicator}
			dropdownStyle={styles.select}
		/>
	);
}
