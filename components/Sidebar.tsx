import React, { useEffect, useRef } from "react";
import { Animated, StyleSheet, View, Dimensions, Text } from "react-native";
import { styles } from "@/styles/components/sidebar";
import { usePathname } from "expo-router"

interface Props {
    open: boolean;
    paddingTop: number;
}

export default function Sidebar({ open = false, paddingTop = 10 }: Props) {
    const screenWidth = Dimensions.get("window").width;
    const translateX = useRef(new Animated.Value(-screenWidth)).current;
    
    const pathname = usePathname()

    useEffect(() => {
        Animated.timing(translateX, {
            toValue: open ? 0 : -screenWidth,
            duration: 300,
            useNativeDriver: true,
        }).start();
        
        console.log(styles, open);
    }, [open, screenWidth]);

    return (
        <Animated.View style={[styles.sidebar, { transform: [{ translateX }], width: screenWidth, paddingTop }]}>
            {/* Your sidebar content goes here */}
            <View style={styles.sidebarContent}>
                <Text style={{color: "white"}}>{pathname}</Text>
            </View>
        </Animated.View>
    );
}