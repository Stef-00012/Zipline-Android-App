import { styles } from "@/styles/components/select";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import React, { useState } from "react";
import { Text, View, TouchableOpacity, FlatList, Modal } from "react-native";
import CheckBox from "./CheckBox";

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
    maxHeight?: number;
    multiple?: boolean;
}

export default function Select({
    data,
    placeholder,
    showScrollIndicator,
    defaultValue,
    onSelect,
    disabled = false,
    maxHeight = 200,
    multiple = false
}: SelectProps) {
    const [selectedItems, setSelectedItems] = useState<SelectProps["data"]>(multiple ? [] : defaultValue ? [defaultValue] : []);
    const [isOpen, setIsOpen] = useState(false);

    const handleSelect = (item: SelectProps["data"][0], index: number) => {
        if (multiple) {
            setSelectedItems(prevItems => {
                const isSelected = prevItems.some(selectedItem => selectedItem.value === item.value);
                if (isSelected) {
                    return prevItems.filter(selectedItem => selectedItem.value !== item.value);
                } 

                return [...prevItems, item];
            });
        } else {
            setSelectedItems([item]);
            setIsOpen(false);
        }
        onSelect(item, index);
    };

    return (
        <View>
            <TouchableOpacity
                style={styles.selectButton}
                onPress={() => setIsOpen(!isOpen)}
                disabled={disabled}
            >
                <Text style={{
                    ...styles.selectText,
                    ...(selectedItems.length && styles.selectedText)
                }}>
                    {selectedItems.length ? selectedItems.map(item => item.label).join(', ') : placeholder}
                </Text>
                <MaterialIcons
                    name={isOpen ? "keyboard-arrow-up" : "keyboard-arrow-down"}
                    size={32}
                    color={styles.selectText.color}
                />
            </TouchableOpacity>

            {isOpen && (
                <Modal
                    transparent={true}
                    animationType="fade"
                    visible={isOpen}
                    onRequestClose={() => setIsOpen(false)}
                >
                    <TouchableOpacity
                        style={styles.selectContainer}
                        onPress={() => setIsOpen(false)}
                    >
                        <View style={{
                            ...styles.select,
                            maxHeight: maxHeight
                        }}>
                            <FlatList
                                data={data}
								style={styles.openSelectContainer}
                                keyExtractor={(item, index) => index.toString()}
                                showsVerticalScrollIndicator={showScrollIndicator}
                                renderItem={({ item, index }) => (
                                    <TouchableOpacity
                                        style={{
                                            ...styles.menuItem,
                                            ...(selectedItems.some(selectedItem => selectedItem.value === item.value) && styles.menuItemSelected),
                                        }}
                                        onPress={() => handleSelect(item, index)}
                                    >
                                        {multiple && (
                                            <CheckBox
                                                value={selectedItems.some(selectedItem => selectedItem.value === item.value)}
                                                onValueChange={() => handleSelect(item, index)}
                                            />
                                        )}
                                        <Text style={styles.menuItemText}>{item.label}</Text>
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