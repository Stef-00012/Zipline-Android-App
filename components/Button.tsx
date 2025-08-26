import { styles } from "@/styles/components/button";
import { getRippleColor } from "@/functions/util";
import type { RefObject } from "react";
import {
	type AnimatableNumericValue,
	type ColorValue,
	type DimensionValue,
	type View,
	Pressable,
	Text,
} from "react-native";
import MaterialSymbols from "./MaterialSymbols";

interface Props {
	onPress: () => unknown | Promise<unknown>;
	disabled?: boolean;
	color: ColorValue;
	textColor?: ColorValue;
	text?: string;
	textJsx?: (disabled: boolean, hasIcon: boolean) => JSX.Element;
	width?: DimensionValue;
	height?: DimensionValue;
	icon?: keyof typeof MaterialSymbols.glyphMap;
	iconColor?: ColorValue;
	borderWidth?: number;
	borderColor?: ColorValue;
	borderRadius?: string | AnimatableNumericValue;
	iconSize?: number;
	padding?: number;
	rippleColor?: ColorValue;
	margin?: {
		top?: DimensionValue;
		bottom?: DimensionValue;
		left?: DimensionValue;
		right?: DimensionValue;
	};
	flex?: number;
	position?: "left" | "center" | "right";
	bold?: boolean;
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
	width,
	height,
	icon,
	iconColor = "white",
	borderWidth = 0,
	borderColor,
	iconSize = 20,
	padding = 10,
	margin = {},
	rippleColor,
	flex,
	borderRadius,
	position,
	bold = true,
	open,
	ref,
}: Props) {
	const flexPositions: {
		[key in "left" | "center" | "right"]: "center" | "flex-start" | "flex-end";
	} = {
		center: "center",
		left: "flex-start",
		right: "flex-end",
	};

	return (
		<Pressable
			ref={ref}
			onPress={onPress}
			disabled={disabled}
			android_ripple={{
				color: rippleColor || getRippleColor(color as string),
			}}
			style={{
				...styles.button,
				width: width,
				backgroundColor: color,
				borderWidth: borderWidth,
				borderColor: borderColor,
				padding: padding - borderWidth,
				justifyContent: flexPositions[position || "center"],
				marginLeft: margin.left,
				marginRight: margin.right,
				marginTop: margin.top,
				marginBottom: margin.bottom,
				flex,
				...(!Number.isNaN(Number(height)) && { height }),
				...(!Number.isNaN(Number(borderRadius)) && { borderRadius }),
			}}
		>
			{icon && <MaterialSymbols name={icon} size={iconSize} color={iconColor} />}

			{textJsx?.(disabled, !!icon)}

			{text && !textJsx && (
				<Text
					style={{
						...styles.buttonText,
						color: textColor,
						fontWeight: bold ? "bold" : "normal",
						...(icon && { marginLeft: 5 }),
					}}
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
	);
}
