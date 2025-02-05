import { getRippleColor } from "@/functions/util";
import { styles } from "@/styles/components/button";
import { MaterialIcons } from "@expo/vector-icons";
import { type ColorValue, type DimensionValue, Pressable, Text } from "react-native";

interface Props {
    onPress: () => void | Promise<void>;
    disabled?: boolean;
    color: ColorValue;
    disabledColor?: ColorValue;
    textColor?: ColorValue;
    disabledTextColor?: ColorValue;
    text?: string;
    width?: DimensionValue;
    height?: DimensionValue;
    icon?: keyof typeof MaterialIcons.glyphMap;
    iconColor?: ColorValue;
    borderWidth?: number;
    borderColor?: ColorValue;
}

export default function Button({
    onPress = () => {},
    disabled = false,
    color,
    disabledColor,
    textColor = "white",
    disabledTextColor = "gray",
    text,
    width,
    height,
    icon,
    iconColor = "white",
    borderWidth = 0,
    borderColor,
}: Props) {
    return (
        <Pressable
            onPress={onPress}
            disabled={disabled}
            android_ripple={{
                color: disabled
                    ? getRippleColor(disabledColor as string || "#323244")
                    : getRippleColor(color as string),
            }}
            style={{
                ...styles.button,
                width: width,
                ...(height && { height: height }),
                backgroundColor: disabled ? disabledColor : color,
                borderWidth: borderWidth,
                borderColor: borderColor,
                padding: 10 - borderWidth
            }}
        >
            {icon && (
                <MaterialIcons
                    name={icon}
                    size={20}
                    color={iconColor}
                />
            )}
            {text && (
                <Text style={{
                    ...styles.buttonText,
                    color: disabled ? disabledTextColor : textColor,
                    ...(icon && { marginLeft: 5 })
                }}>{text}</Text>
            )}
        </Pressable>
    )
}