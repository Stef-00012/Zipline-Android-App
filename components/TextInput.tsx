import {
	View,
	Text,
	TextInput,
	type ColorValue,
	Pressable,
	type TextInputProps,
} from "react-native";
import { styles } from "@/styles/components/textInput";
import { useState } from "react";
import { MaterialIcons } from "@expo/vector-icons";

interface Props {
	value?: string;
	title?: string;
	onPasswordToggle: (visibile: boolean) => void | Promise<void>;
	onCopy: () => void | Promise<void>;
	onValueChange: (newValue: string) => void | Promise<void>;
	disabled?: boolean;
	disableContext?: boolean;
	password?: boolean;
	type?:
		| "default"
		| "number-pad"
		| "decimal-pad"
		| "numeric"
		| "email-address"
		| "phone-pad"
		| "url"; // | "visible-password"
	placeholder?: string;
	copy?: boolean;
	buttonColor?: ColorValue;
	props: Omit<
		TextInputProps,
		| "keyboardType"
		| "value"
		| "onChangeText"
		| "placeholder"
		| "placeholderTextColor"
		| "disabled"
		| "contextMenuHidden"
		| "style"
		| "secureTextEntry"
		| "editable"
	>;
}

export default function ZiplineTextInput({
	value,
	onValueChange = () => {},
	onPasswordToggle = () => {},
	onCopy = () => {},
	disabled = false,
	disableContext = false,
	title,
	password = false,
	type = "default",
	placeholder,
	copy = false,
	buttonColor,
	props,
}: Props) {
	const [displayPassword, setDisplayPassword] = useState<boolean>(false);

	if (password)
		return (
			<View>
				{title && (
					<Text
						style={{
							...styles.inputHeader,
							...(disabled && styles.inputHeaderDisabled),
						}}
					>
						{title}
					</Text>
				)}
				<View style={styles.inputContainer}>
					<TextInput
						{...props}
						secureTextEntry={true}
						style={{
							...styles.textInput,
							...styles.textInputSideButton,
							...(disabled && styles.textInputDisabled),
						}}
						editable={!disabled}
						contextMenuHidden={disableContext}
						onChangeText={onValueChange}
						value={value}
						keyboardType={displayPassword ? "visible-password" : "default"}
						placeholder={placeholder}
						placeholderTextColor="#222c47"
					/>
					<Pressable
						style={{
							...styles.sideButton,
							...(buttonColor && { backgroundColor: buttonColor }),
						}}
						onPress={() => {
							onPasswordToggle(!displayPassword);
							setDisplayPassword((prev) => !prev);
						}}
					>
						<MaterialIcons
							name={displayPassword ? "visibility" : "visibility-off"}
							color="white"
							size={15}
						/>
					</Pressable>
				</View>
			</View>
		);

	if (copy)
		return (
			<View>
				{title && (
					<Text
						style={{
							...styles.inputHeader,
							...(disabled && styles.inputHeaderDisabled),
						}}
					>
						{title}
					</Text>
				)}
				<View style={styles.inputContainer}>
					<TextInput
						{...props}
						secureTextEntry={true}
						style={{
							...styles.textInput,
							...styles.textInputSideButton,
							...(disabled && styles.textInputDisabled),
						}}
						editable={!disabled}
						contextMenuHidden={disableContext}
						onChangeText={onValueChange}
						value={value}
						keyboardType={type}
						placeholder={placeholder}
						placeholderTextColor="#222c47"
					/>
					<Pressable
						style={{
							...styles.sideButton,
							...(buttonColor && { backgroundColor: buttonColor }),
						}}
						onPress={() => {
							onCopy();
						}}
					>
						<MaterialIcons
							name={displayPassword ? "visibility" : "visibility-off"}
							color="white"
							size={15}
						/>
					</Pressable>
				</View>
			</View>
		);

	return (
		<View>
			{title && (
				<Text
					style={{
						...styles.inputHeader,
						...(disabled && styles.inputHeaderDisabled),
					}}
				>
					{title}
				</Text>
			)}
			<TextInput
				{...props}
				secureTextEntry={true}
				style={{
					...styles.textInput,
					...(disabled && styles.textInputDisabled),
				}}
				editable={!disabled}
				contextMenuHidden={disableContext}
				onChangeText={onValueChange}
				value={value}
				keyboardType={type}
				placeholder={placeholder}
				placeholderTextColor="#222c47"
			/>
		</View>
	);
}
