import Button from "@/components/Button";
import Skeleton from "@/components/skeleton/Skeleton";
import { styles } from "@/styles/components/textInput";
import type { ReactNode } from "react";
import { Text, View, type ColorValue, type DimensionValue } from "react-native";
import type MaterialSymbols from "../MaterialSymbols";

interface Props {
	title?: string;
	description?: string | ReactNode;
	sideButtonColor?: ColorValue;
	sideButtonIconColor?: ColorValue;
	sideButtonIcon?: keyof typeof MaterialSymbols.glyphMap;
	skeletonWidth?: DimensionValue;
	skeletonHeight?: DimensionValue;
	disableAnimation?: boolean;
}

export default function SkeletonTextInput({
	title,
	description,
	sideButtonColor = "#373d79",
	sideButtonIcon,
	sideButtonIconColor = "gray",
	skeletonHeight,
	skeletonWidth,
	disableAnimation,
}: Props) {
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
							...styles.inputHeaderDisabled,
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
				<View
					style={{
						borderWidth: 2,
						borderColor: "#222c47",
						height: 40,
						paddingHorizontal: 10,
						borderRadius: 6,
						justifyContent: "center",
						width: "100%",
						...(sideButtonIcon && {
							flex: 1,
						}),
					}}
				>
					<Skeleton
						disableAnimation={disableAnimation}
						height={skeletonHeight || 17}
						width={skeletonWidth || "60%"}
					/>
				</View>
				{sideButtonIcon && (
					<Button
						disabled
						onPress={() => {}}
						icon={sideButtonIcon}
						iconColor={sideButtonIconColor}
						color={sideButtonColor}
						containerStyle={{
							marginLeft: 10,
							height: 40,
							width: 40,
						}}
					/>
				)}
			</View>
		</View>
	);
}
