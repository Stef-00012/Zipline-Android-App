import Button from "@/components/Button";
import { useRef, useState } from "react";
import {
    Dimensions,
    Modal,
    Pressable,
    ScrollView,
    TouchableOpacity,
    View,
    type ColorValue,
    type DimensionValue,
    type StyleProp,
    type ViewStyle,
} from "react-native";
import MaterialSymbols from "./MaterialSymbols";

interface Item {
	name: string;
	id: string;
	color?: ColorValue;
	iconColor?: ColorValue;
	disabled?: boolean;
	icon?: keyof typeof MaterialSymbols.glyphMap;
	onPress: () => Promise<void> | void;
}

interface DropdownPosition {
	top: number;
	left: number;
}

interface Props {
	data: Item[];

	containerStyle?: StyleProp<ViewStyle>;

	width?: DimensionValue;
	height?: DimensionValue;
	iconColor?: ColorValue;
	iconSize?: number;
	icon: keyof typeof MaterialSymbols.glyphMap;
	margin?: {
		top?: DimensionValue;
		bottom?: DimensionValue;
		left?: DimensionValue;
		right?: DimensionValue;
	};
	dropdown?: {
		width?: number;
		maxHeight?: number;
		margin?: {
			top?: DimensionValue;
			bottom?: DimensionValue;
			left?: DimensionValue;
			right?: DimensionValue;
		};
	};
}

const minimumMargin = 15;

export default function Dropdown({
	data,
	containerStyle,
	height,
	width,
	icon,
	iconSize = 25,
	iconColor = "#575DB5",
	dropdown,
	margin = {},
}: Props) {
	const [visible, setVisible] = useState(false);
	const [dropdownPosition, setDropdownPosition] = useState<DropdownPosition>({
		top: 0,
		left: 0,
	});
	const dropdownButtonRef = useRef<View>(null);

	const screen = Dimensions.get("window");

	const screenWidth = screen.width;
	const dropdownWidth = dropdown?.width || screenWidth * 0.4;

	return (
		<View
			style={[
				{
					position: "relative",
					width: width,
					height: height,
					marginTop: margin.top,
					marginBottom: margin.bottom,
					marginLeft: margin.left,
					marginRight: margin.right,
				},
				containerStyle,
			]}
		>
			<Pressable
				android_ripple={{
					color: "#464953",
				}}
				ref={dropdownButtonRef}
				style={{
					borderWidth: 2,
					borderColor: "#222c47",
					borderRadius: 7,
				}}
				onPress={() => {
					if (visible) {
						setVisible(false);
					} else {
						dropdownButtonRef.current?.measure(
							(
								_fx: number,
								_fy: number,
								_elementWidth: number,
								elementHeight: number,
								px: number,
								py: number,
							) => {
								let left = px;
								const spaceBelow = screen.height - (py + elementHeight);
								const spaceAbove = py;
								const dropdownHeight =
									(data.length * 50 > (dropdown?.maxHeight || 325)
										? dropdown?.maxHeight || 325
										: data.length * 50) +
									(typeof height === "number" ? height : 40) +
									minimumMargin;

								let top = py + elementHeight;
								if (spaceAbove > spaceBelow && spaceAbove > dropdownHeight) {
									top = py - dropdownHeight;
								}

								if (left < minimumMargin) {
									left = minimumMargin;
								} else if (left + dropdownWidth > screenWidth - minimumMargin) {
									left = screenWidth - dropdownWidth - minimumMargin;
								}

								setDropdownPosition({ top: top, left: left });
								setVisible(true);
							},
						);
					}
				}}
			>
				<MaterialSymbols name={icon} size={iconSize} color={iconColor} />
			</Pressable>

			<Modal visible={visible} transparent animationType="none">
				<TouchableOpacity
					style={{
						width: "100%",
						height: "100%",
						backgroundColor: "#0000004d",
					}}
					activeOpacity={1}
					onPress={() => setVisible(false)}
				>
					<ScrollView
						style={[
							{
								position: "absolute",
								borderRadius: 8,
								overflow: "hidden",
								shadowColor: "#000",
								shadowOffset: {
									width: 0,
									height: 4,
								},
								shadowOpacity: 0.25,
								shadowRadius: 8,
								elevation: 8,
								maxHeight: dropdown?.maxHeight || 325,
							},
							{
								backgroundColor: "#1a202c",
								borderColor: "#2d3748",
								borderWidth: 1,
							},
							{
								top: dropdownPosition.top,
								left: dropdownPosition.left,
								width: dropdownWidth,
							},
						]}
					>
						{data.map((item) => (
							<Button
								disabled={item.disabled}
								position="left"
								bold={false}
								text={item.name}
								icon={item.icon}
								color="#191b27"
								textColor={item.color}
								onPress={() => {
									item.onPress();
									setVisible(false);
								}}
								key={item.id}
								iconColor={item.iconColor}
							/>
						))}
					</ScrollView>
				</TouchableOpacity>
			</Modal>
		</View>
	);
}
