import React, { useEffect, useRef, useState } from "react";
import { Animated, StyleSheet, View, Dimensions, Text, Pressable } from "react-native";
import { styles } from "@/styles/components/sidebar";
import { usePathname } from "expo-router"
import { getSettings } from "@/functions/zipline/settings";
import { getCurrentUser } from "@/functions/zipline/user";
import { sidebarOptions } from "@/constants/sidebar"
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { useRouter } from "expo-router"

interface Props {
    open: boolean;
    paddingTop: number;
    setOpen: Dispatch<SetStateAction<boolean>>
}

export default function Sidebar({ open = false, paddingTop = 0, setOpen }: Props) {
    const router = useRouter()
    
    const screenWidth = Dimensions.get("window").width;
    const translateX = useRef(new Animated.Value(-screenWidth)).current;
    
    const [invitesEnabled, setInvitesEnabled] = useState<boolean>(false)
    const [isAdmin, setIsAdmin] = useState<boolean>(false)
    
    useEffect(() => {
        (async () => {
            const settings = await getSettings()
            const user = await getCurrentUser()
            
            if (user && ["ADMIN", "SUPERADMIN"].includes(user.role)) setIsAdmin(true)
            if (settings && settings.invitesEnabled) setInvitesEnabled(true)
        })()
    })
    
    const pathname = usePathname()

    useEffect(() => {
        Animated.timing(translateX, {
            toValue: open ? 0 : -screenWidth,
            duration: 300,
            useNativeDriver: true,
        }).start();
    }, [open, screenWidth]);

    return (
        <Animated.View style={[styles.sidebar, { transform: [{ translateX }], width: screenWidth, paddingTop }]}>
            {/* Your sidebar content goes here */}
            <View style={styles.sidebarContent}>
                {sidebarOptions.map((option) => {
                    const isActive = pathname === option.route;
                    
                    if (option.type === "button") return (
                        <Pressable key={option.route} onPress={() => {
                            setOpen(false)
                            router.replace(option.route)
                        }} href={option.route} style={{
                            ...styles.sidebarOption,
                            ...(isActive && styles.sidebarOptionActive)
                        }}>
                            <MaterialIcons name={option.icon} size={20} color={isActive ? styles.sidebarOptionTextActive.color : styles.sidebarOptionText.color} />
                            <Text style={{
                                ...styles.sidebarOptionText,
                                ...(isActive && styles.sidebarOptionTextActive)
                            }}>{option.name}</Text>
                        </Pressable>
                    )
                })}
            </View>
        </Animated.View>
    );
}