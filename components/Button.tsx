import { getRippleColor } from "@/functions/util";
import { styles } from "@/styles/components/button";
import { MaterialIcons } from "@expo/vector-icons";
import { type ColorValue, type DimensionValue, Pressable, Text } from "react-native";

interface Props {
    onPress: () => unknown | Promise<unknown>;
    disabled?: boolean;
    color: ColorValue;
    textColor?: ColorValue;
    text?: string;
    width?: DimensionValue;
    height?: DimensionValue;
    icon?: keyof typeof MaterialIcons.glyphMap;
    iconColor?: ColorValue;
    borderWidth?: number;
    borderColor?: ColorValue;
    iconSize?: number;
    padding?: number;
    rippleColor?: ColorValue;
    margin?: {
        top?: DimensionValue;
        bottom?: DimensionValue;
        left?: DimensionValue;
        right?: DimensionValue;
    },
    flex?: number
}

export default function Button({
    onPress = () => {},
    disabled = false,
    color,
    textColor = "white",
    text,
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
    flex
}: Props) {
    return (
        <Pressable
            onPress={onPress}
            disabled={disabled}
            android_ripple={{
                color: rippleColor || getRippleColor(color as string)
            }}
            style={{
                ...styles.button,
                width: width,
                ...(height && { height: height }),
                backgroundColor: color,
                borderWidth: borderWidth,
                borderColor: borderColor,
                padding: padding - borderWidth,
                ...(margin.left && { marginLeft: margin.left }),
                ...(margin.right && { marginRight: margin.right }),
                ...(margin.top && { marginTop: margin.top }),
                ...(margin.bottom && { marginBottom: margin.bottom }),
                ...(flex && { flex })
            }}
        >
            {icon && (
                <MaterialIcons
                    name={icon}
                    size={iconSize}
                    color={iconColor}
                />
            )}
            {text && (
                <Text style={{
                    ...styles.buttonText,
                    color: textColor,
                    ...(icon && { marginLeft: 5 })
                }}>{text}</Text>
            )}
        </Pressable>
    )
}