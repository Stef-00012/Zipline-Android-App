import type { ReactNode } from "react";
import { colors } from "@/constants/skeleton";
import { Skeleton as NativeSkeleton } from "moti/skeleton";

interface Props {
    children?: ReactNode;
    disableAnimation?: boolean;
    width?: Size;
    height?: Size;
    radius?: "round" | "square" | number;
}

function Skeleton({
    children,
    disableAnimation,
    width,
    height,
    radius,
}) {
    if (disableAnimation) {
        if (radius === "square") radius = 0;
        else if (radius === "round") radius = "50%"
        
        return (
            <View
                style={{
                    width: width,
                    height: height,
                    borderRadius: radius|| 8,
                    backgroundColor: colors[1]
                }}
            >
                {children}
            </View>
        )
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
    )
}

Skeleton.Group = NativeSkeleton.Group;

export default Skeleton;