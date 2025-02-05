import {
	View,
	Text,
	Pressable,
	TextInput as NativeTextInput,
	type ColorValue,
} from "react-native";
import { styles } from "@/styles/components/textInput";
import { useState } from "react";
import { MaterialIcons } from "@expo/vector-icons";
import type { KeyboardType, NativeSyntheticEvent, ReturnKeyTypeOptions, TextInputChangeEventData, TextInputSubmitEditingEventData, TextStyle } from "react-native";

interface Props {
	value?: string;
	title?: string;
	onPasswordToggle?: (visibile: boolean) => void | Promise<void>;
	onValueChange?: (newValue: string) => void | Promise<void>;
	disabled?: boolean;
	disableContext?: boolean;
	showDisabledStyle?: boolean;
	multiline?: boolean;
	password?: boolean;
	keyboardType?: KeyboardType // | "visible-password"
	placeholder?: string;
	sideButtonColor?: ColorValue;
	sideButtonIconColor?: ColorValue;
	onSideButtonPress?: () => void | Promise<void>;
	sideButtonIcon?: keyof typeof MaterialIcons.glyphMap;
	maxLength?: number;
	inputStyle?: TextStyle;
	showSoftInputOnFocus?: boolean;
	onChange?: (event: NativeSyntheticEvent<TextInputChangeEventData>) => void | Promise<void>;
	onSubmitEditing?: (event: NativeSyntheticEvent<TextInputSubmitEditingEventData>) => void | Promise<void>;
	returnKeyType?: ReturnKeyTypeOptions;

}

const sideButtonSize = 17

export default function TextInput({
	value,
	onValueChange = () => {},
	onPasswordToggle = () => {},
	onSideButtonPress = () => {},
	disabled = false,
	showDisabledStyle = true,
	disableContext = false,
	multiline = false,
	title,
	password = false,
	keyboardType = "default",
	placeholder,
	sideButtonColor = "#323ea8",
	sideButtonIcon,
	sideButtonIconColor = "white",
	maxLength,
	inputStyle,
	showSoftInputOnFocus = true,
	onChange = () => {},
	onSubmitEditing = () => {},
	returnKeyType,
}: Props) {
	const [displayPassword, setDisplayPassword] = useState<boolean>(false);

	if (password)
		return (
			<View>
				{title && (
					<Text
						style={{
							...styles.inputHeader,
							...((disabled && showDisabledStyle) && styles.inputHeaderDisabled),
						}}
					>
						{title}
					</Text>
				)}
				<View style={{
					...styles.inputContainer,
				}}>
					<NativeTextInput
						showSoftInputOnFocus={showSoftInputOnFocus}
						multiline={multiline}
						secureTextEntry
						onChange={onChange}
						onSubmitEditing={onSubmitEditing}
						maxLength={maxLength}
						style={{
							...styles.textInput,
							...styles.textInputSideButton,
							...((disabled && showDisabledStyle) && styles.textInputDisabled),
							...inputStyle,
						}}
						editable={!disabled}
						contextMenuHidden={disableContext}
						onChangeText={onValueChange}
						value={value}
						keyboardType={displayPassword ? "visible-password" : "default"}
						placeholder={placeholder}
						placeholderTextColor="#222c47"
						returnKeyType={returnKeyType}
					/>
					<Pressable
						style={{
							...styles.sideButton,
							...(sideButtonColor && { backgroundColor: sideButtonColor }),
						}}
						onPress={() => {
							onPasswordToggle(!displayPassword);
							onSideButtonPress()
							setDisplayPassword((prev) => !prev);
						}}
					>
						<MaterialIcons
							name={displayPassword ? "visibility-off" : "visibility"}
							color="white"
							size={sideButtonSize}
						/>
					</Pressable>
				</View>
			</View>
		);

	if (onSideButtonPress && sideButtonIcon)
		return (
			<View>
				{title && (
					<Text
						style={{
							...styles.inputHeader,
							...((disabled && showDisabledStyle) && styles.inputHeaderDisabled),
						}}
					>
						{title}
					</Text>
				)}
				<View style={{
					...styles.inputContainer,
				}}>
					<NativeTextInput
						onChange={onChange}
						onSubmitEditing={onSubmitEditing}
						showSoftInputOnFocus={showSoftInputOnFocus}
						multiline={multiline}
						maxLength={maxLength}
						style={{
							...styles.textInput,
							...styles.textInputSideButton,
							...((disabled && showDisabledStyle) && styles.textInputDisabled),
							...inputStyle,
						}}
						editable={!disabled}
						contextMenuHidden={disableContext}
						onChangeText={onValueChange}
						value={value}
						keyboardType={keyboardType}
						placeholder={placeholder}
						placeholderTextColor="#222c47"
						returnKeyType={returnKeyType}
					/>
					<Pressable
						style={{
							...styles.sideButton,
							...(sideButtonColor && { backgroundColor: sideButtonColor }),
						}}
						onPress={() => {
							onSideButtonPress();
						}}
					>
						<MaterialIcons
							name={sideButtonIcon}
							color={sideButtonIconColor}
							size={sideButtonSize}
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
						...((disabled && showDisabledStyle) && styles.inputHeaderDisabled),
					}}
				>
					{title}
				</Text>
			)}
			<NativeTextInput
				onChange={onChange}
				onSubmitEditing={onSubmitEditing}
				showSoftInputOnFocus={showSoftInputOnFocus}
				multiline={multiline}
				maxLength={maxLength}
				style={{
					...styles.textInput,
					...((disabled && showDisabledStyle) && styles.textInputDisabled),
					...inputStyle,
				}}
				editable={!disabled}
				contextMenuHidden={disableContext}
				onChangeText={onValueChange}
				value={value}
				keyboardType={keyboardType}
				placeholder={placeholder}
				placeholderTextColor="#222c47"
				returnKeyType={returnKeyType}
			/>
		</View>
	);
}
