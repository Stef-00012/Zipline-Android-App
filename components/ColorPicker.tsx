import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { styles } from "@/styles/components/colorPicker";
import { View, Pressable, Text } from "react-native";
import { isLightColor } from "@/functions/color";
import TextInput from "@/components/TextInput";
import { useEffect, useState } from "react";
import Button from "@/components/Button";
import Popup from "@/components/Popup";
import NativeColorPicker, {
	type ColorFormatsObject,
	HSLSaturationSlider,
	BrightnessSlider,
	LuminanceSlider,
	OpacitySlider,
	InputWidget,
	HueSlider,
	Swatches,
	Preview,
	Panel1,
	Panel2,
	Panel3,
	Panel4,
	Panel5,
} from "reanimated-color-picker";

type PanelType = "saturation" | "brightness" | "hsl-saturation";
type PanelComponent = "panel1" | "panel2" | "panel3" | "panel4" | "panel5";

interface CustomColorPickerProps {
	defaultInputFormats?: ("HEX" | "RGB" | "HSL" | "HWB" | "HSV")[];
	onSelectColor: (color: ColorFormatsObject) => void;
	previewHideInitialColor?: boolean;
	defaultColors?: string[];
	showHSLSaturation?: boolean;
	previewHideText?: boolean;
	showBrightness?: boolean;
	showLuminance?: boolean;
	disableAlpha?: boolean;
	initialColor?: string;
	showOpacity?: boolean;
	showPreview?: boolean;
	description?: string;
	showInput?: boolean;
	disabled?: boolean;
	showHue?: boolean;
	title?: string;
	previewColorFormat?:
		| "hex"
		| "rgb"
		| "rgba"
		| "hsl"
		| "hsla"
		| "hsv"
		| "hsva"
		| "hwb"
		| "hwba";
	panel?:
		| "panel1"
		| "panel2"
		| "panel2_saturation"
		| "panel2_brightness"
		| "panel2_hsl-saturation"
		| "panel3"
		| "panel3_saturation"
		| "panel3_brightness"
		| "panel3_hsl-saturation"
		| "panel4"
		| "panel5";
}

export default function ColorPicker({
	showHSLSaturation = false,
	initialColor = "#ffffff",
	previewHideInitialColor,
	showBrightness = false,
	showLuminance = false,
	disableAlpha = false,
	defaultInputFormats,
	defaultColors = [],
	showOpacity = true,
	showPreview = true,
	previewColorFormat,
	showInput = true,
	disabled = false,
	previewHideText,
	showHue = true,
	onSelectColor,
	description,
	title,
	panel,
}: CustomColorPickerProps) {
	const [showPicker, setShowPicker] = useState<boolean>(false);
	const [color, setColor] = useState<string>(initialColor || "#ffffff");
	const [selectedColorData, setSelectedColorData] = useState<ColorFormatsObject>();
	const [panelType, setPanelType] = useState<PanelType>();
	const [panelComponent, setPanelComponent] =
		useState<PanelComponent>("panel1");

	useEffect(() => {
		if (panel) {
			const panelData = panel.split("_") as [
				PanelComponent,
				PanelType | undefined,
			];

			setPanelComponent(panelData[0]);
			setPanelType(panelData[1]);
		} else {
			setPanelComponent("panel1");
		}
	}, [panel]);

	useEffect(() => {
		setColor(initialColor || "#ffffff");
	}, [initialColor]);

	return (
		<>
			{title && (
				<>
					<Text
						style={{
							...styles.inputHeader,
							...(!description && {
								marginBottom: 5,
							}),
							...(disabled && styles.inputHeaderDisabled),
						}}
					>
						{title}
					</Text>

					{description && (
						<Text style={styles.inputDescription}>{description}</Text>
					)}
				</>
			)}
			<Pressable
				disabled={disabled}
				onPress={() => setShowPicker(true)}
				style={styles.input}
			>
				<TextInput
					value={color.toUpperCase()}
					disabled
					disableContext
					placeholder="#000000"
					inputStyle={{
						borderWidth: 0,
						color: disabled ? "gray" : "white",
					}}
					showDisabledStyle={false}
				/>
				<View
					style={{
						...styles.inputPreview,
						borderColor: isLightColor(color) ? "black" : "#ccc",
						backgroundColor: color,
					}}
				/>
			</Pressable>

			<Popup hidden={!showPicker} onClose={() => setShowPicker(false)}>
				<KeyboardAwareScrollView>
					<NativeColorPicker
						thumbAnimationDuration={400}
						value={color}
						thumbSize={24}
						thumbShape="circle"
						boundedThumb
						onCompleteJS={(color) => {
							setSelectedColorData(color);
						}}
					>
						{showPreview && (
							<Preview
								colorFormat={previewColorFormat}
								hideInitialColor={previewHideInitialColor}
								hideText={previewHideText}
								style={styles.pickerPreview}
							/>
						)}

						{panelComponent === "panel1" && <Panel1 style={styles.panel} />}

						{panelComponent === "panel2" && (
							<Panel2 style={styles.panel} verticalChannel={panelType} />
						)}

						{panelComponent === "panel3" && (
							<Panel3 style={styles.panel} centerChannel={panelType} />
						)}

						{panelComponent === "panel4" && <Panel4 style={styles.panel} />}

						{panelComponent === "panel5" && <Panel5 style={styles.panel} />}

						{showHue && <HueSlider style={styles.slider} />}

						{showOpacity && <OpacitySlider style={styles.slider} />}

						{showBrightness && <BrightnessSlider style={styles.slider} />}

						{showHSLSaturation && <HSLSaturationSlider style={styles.slider} />}

						{showLuminance && <LuminanceSlider style={styles.slider} />}

						{defaultColors.length > 0 && (
							<Swatches
								style={styles.swatchesContainer}
								swatchStyle={styles.swatches}
								colors={defaultColors}
							/>
						)}

						{showInput && (
							<View style={styles.colorInputMainContainer}>
								<InputWidget
									formats={defaultInputFormats}
									disableAlphaChannel={disableAlpha}
									containerStyle={styles.colorInputContainer}
									inputStyle={styles.colorInput}
									iconColor="#2d3f70"
								/>
							</View>
						)}
					</NativeColorPicker>

					<Button
						color={selectedColorData ? "#323ea8" : "#373d79"}
						textColor={selectedColorData ? "white" : "gray"}
						disabled={!selectedColorData}
						onPress={() => {
							if (!selectedColorData) return;

							onSelectColor(selectedColorData);
							setColor(selectedColorData.hex);
							setShowPicker(false);
						}}
						text="Done"
					/>
				</KeyboardAwareScrollView>
			</Popup>
		</>
	);
}
