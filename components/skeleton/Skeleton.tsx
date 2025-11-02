import { colors } from "@/constants/skeleton";
import { Skeleton as NativeSkeleton } from "moti/skeleton";
import type { ReactElement } from "react";
import { type DimensionValue, View } from "react-native";

interface Props {
	children?: ReactElement | null;
	disableAnimation?: boolean;
	width?: DimensionValue;
	height?: DimensionValue;
	radius?: "round" | "square" | number;
}

function Skeleton({
	children,
	disableAnimation,
	width,
	height,
	radius,
}: Props) {
	if (disableAnimation) {
		if (radius === "square") radius = 0;
		else if (radius === "round") radius = 9999;

		return (
			<View
				style={{
					width: width,
					height: height,
					borderRadius: radius || 8,
					backgroundColor: colors[1],
				}}
			>
				{children}
			</View>
		);
	}

	return (
		<NativeSkeleton
			colors={colors}
			width={width}
			height={height}
			radius={radius}
		>
			{children}
		</NativeSkeleton>
	);
}

Skeleton.Group = NativeSkeleton.Group;

export default Skeleton;
