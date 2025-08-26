import { styles } from "@/styles/components/textInput";
import { type ReactNode, useState } from "react";
import Button from "@/components/Button";
import {
	type TextInputSubmitEditingEventData,
	type TextInputChangeEventData,
	TextInput as NativeTextInput,
	type NativeSyntheticEvent,
	type ReturnKeyTypeOptions,
	type KeyboardType,
	type ColorValue,
	type TextStyle,
	View,
	Text,
} from "react-native";
import type MaterialSymbols from "./MaterialSymbols";

interface Props {
	value?: string;
	defaultValue?: string;
	title?: string;
	description?: string | ReactNode;
	id?: string;
	onPasswordToggle?: (
		visibile: boolean,
		id?: Props["id"],
	) => void | Promise<void>;
	onValueChange?: (newValue: string, id?: Props["id"]) => void | Promise<void>;
	disabled?: boolean;
	disableContext?: boolean;
	showDisabledStyle?: boolean;
	multiline?: boolean;
	password?: boolean;
	keyboardType?: KeyboardType;
	placeholder?: string;
	sideButtonColor?: ColorValue;
	sideButtonIconColor?: ColorValue;
	onSideButtonPress?: (id?: Props["id"]) => void | Promise<void>;
	sideButtonIcon?: keyof typeof MaterialSymbols.glyphMap;
	maxLength?: number;
	inputStyle?: TextStyle;
	showSoftInputOnFocus?: boolean;
	onChange?: (
		event: NativeSyntheticEvent<TextInputChangeEventData>,
		id?: Props["id"],
	) => void | Promise<void>;
	onSubmitEditing?: (
		event: NativeSyntheticEvent<TextInputSubmitEditingEventData>,
		id?: Props["id"],
	) => void | Promise<void>;
	returnKeyType?: ReturnKeyTypeOptions;
}

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
	description,
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
	id,
	defaultValue,
}: Props) {
	const [displayPassword, setDisplayPassword] = useState<boolean>(false);

	if (password)
		return (
			<View>
				{title && (
					<>
						<Text
							style={{
								...styles.inputHeader,
								...(!description && {
									marginBottom: 5,
								}),
								...(disabled &&
									showDisabledStyle &&
									styles.inputHeaderDisabled),
							}}
						>
							{title}
						</Text>

						{description && (
							<Text style={styles.inputDescription}>{description}</Text>
						)}
					</>
				)}
				<View
					style={{
						...styles.inputContainer,
					}}
				>
					<NativeTextInput
						showSoftInputOnFocus={showSoftInputOnFocus}
						multiline={multiline}
						secureTextEntry={!displayPassword}
						onChange={(event) => onChange(event, id)}
						onSubmitEditing={(event) => onSubmitEditing(event, id)}
						maxLength={maxLength}
						style={{
							...styles.textInput,
							...styles.textInputSideButton,
							...(disabled && showDisabledStyle && styles.textInputDisabled),
							...inputStyle,
						}}
						editable={!disabled}
						contextMenuHidden={disableContext}
						onChangeText={(text) => onValueChange(text, id)}
						value={value}
						placeholder={placeholder}
						placeholderTextColor="#222c47"
						returnKeyType={returnKeyType}
						defaultValue={defaultValue}
					/>
					<Button
						onPress={() => {
							onPasswordToggle(!displayPassword, id);
							onSideButtonPress(id);
							setDisplayPassword((prev) => !prev);
						}}
						icon={displayPassword ? "visibility_off" : "visibility"}
						color={sideButtonColor}
						margin={{
							left: 10,
						}}
					/>
				</View>
			</View>
		);

	if (onSideButtonPress && sideButtonIcon)
		return (
			<View>
				{title && (
					<>
						<Text
							style={{
								...styles.inputHeader,
								...(!description && {
									marginBottom: 5,
								}),
								...(disabled &&
									showDisabledStyle &&
									styles.inputHeaderDisabled),
							}}
						>
							{title}
						</Text>

						{description && (
							<Text style={styles.inputDescription}>{description}</Text>
						)}
					</>
				)}
				<View
					style={{
						...styles.inputContainer,
					}}
				>
					<NativeTextInput
						onChange={(event) => onChange(event, id)}
						onSubmitEditing={(event) => onSubmitEditing(event, id)}
						showSoftInputOnFocus={showSoftInputOnFocus}
						multiline={multiline}
						maxLength={maxLength}
						style={{
							...styles.textInput,
							...styles.textInputSideButton,
							...(disabled && showDisabledStyle && styles.textInputDisabled),
							...inputStyle,
						}}
						editable={!disabled}
						contextMenuHidden={disableContext}
						onChangeText={(text) => onValueChange(text, id)}
						value={value}
						keyboardType={keyboardType}
						placeholder={placeholder}
						placeholderTextColor="#222c47"
						returnKeyType={returnKeyType}
						defaultValue={defaultValue}
					/>
					<Button
						onPress={() => {
							onSideButtonPress(id);
						}}
						icon={sideButtonIcon}
						iconColor={sideButtonIconColor}
						color={sideButtonColor}
						margin={{
							left: 10,
						}}
					/>
				</View>
			</View>
		);

	return (
		<View>
			{title && (
				<>
					<Text
						style={{
							...styles.inputHeader,
							...(!description && {
								marginBottom: 5,
							}),
							...(disabled && showDisabledStyle && styles.inputHeaderDisabled),
						}}
					>
						{title}
					</Text>

					{description && (
						<Text style={styles.inputDescription}>{description}</Text>
					)}
				</>
			)}
			<NativeTextInput
				onChange={(event) => onChange(event, id)}
				onSubmitEditing={(event) => onSubmitEditing(event, id)}
				showSoftInputOnFocus={showSoftInputOnFocus}
				multiline={multiline}
				maxLength={maxLength}
				style={{
					...styles.textInput,
					...(disabled && showDisabledStyle && styles.textInputDisabled),
					...inputStyle,
				}}
				editable={!disabled}
				contextMenuHidden={disableContext}
				onChangeText={(text) => onValueChange(text, id)}
				value={value}
				keyboardType={keyboardType}
				placeholder={placeholder}
				placeholderTextColor="#222c47"
				returnKeyType={returnKeyType}
				defaultValue={defaultValue}
			/>
		</View>
	);
}
