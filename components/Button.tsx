import { getRippleColor } from "@/functions/util";
import { styles } from "@/styles/components/button";
import type { JSX, RefObject } from "react";
import {
	type ColorValue,
	Pressable,
	type StyleProp,
	Text,
	type TextStyle,
	View,
	type ViewStyle,
} from "react-native";
import MaterialSymbols from "./MaterialSymbols";

interface Props {
	onPress: () => unknown | Promise<unknown>;
	disabled?: boolean;
	color: ColorValue;
	textColor?: ColorValue;
	text?: string;
	textJsx?: (
		disabled: boolean,
		hasIcon: boolean,
		style?: StyleProp<TextStyle>,
	) => JSX.Element;
	textStyle?: StyleProp<TextStyle>;
	buttonStyle?: StyleProp<ViewStyle>;
	containerStyle?: StyleProp<ViewStyle>;
	iconStyle?: StyleProp<TextStyle>;
	icon?: keyof typeof MaterialSymbols.glyphMap;
	iconColor?: ColorValue;
	iconSize?: number;
	rippleColor?: ColorValue;
	open?: boolean;
	ref?: RefObject<View>;
}

export default function Button({
	onPress = () => {},
	disabled = false,
	color,
	textColor = "white",
	text,
	textJsx,
	icon,
	iconColor = "white",
	iconSize = 20,
	rippleColor,
	open,
	ref,
	buttonStyle,
	containerStyle,
	textStyle,
	iconStyle,
}: Props) {
	return (
		<View
			style={[
				styles.buttonContainer,
				{ backgroundColor: color },
				containerStyle,
			]}
		>
			<Pressable
				ref={ref}
				onPress={onPress}
				disabled={disabled}
				android_ripple={{
					color: rippleColor || getRippleColor(color as string),
					foreground: true,
					borderless: true,
				}}
				style={[styles.button, buttonStyle]}
			>
				{icon && (
					<MaterialSymbols
						name={icon}
						size={iconSize}
						color={iconColor}
						style={iconStyle}
					/>
				)}

				{textJsx?.(disabled, !!icon, textStyle)}

				{text && !textJsx && (
					<Text
						style={[
							styles.buttonText,
							{ color: textColor },
							textStyle,
						]}
					>
						{text}
					</Text>
				)}

				{typeof open === "boolean" && (
					<MaterialSymbols
						name={open ? "expand_more" : "expand_less"}
						size={20}
						color="white"
					/>
				)}
			</Pressable>
		</View>
	);
}
