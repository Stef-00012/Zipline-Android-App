import { isAuthenticated } from "@/functions/zipline/auth";
import { getSettings } from "@/functions/zipline/settings";
import { getURLs } from "@/functions/zipline/urls";
import { styles } from "@/styles/urls";
import type { APISettings, APIURLs, DashURL } from "@/types/zipline";
import { Link, useFocusEffect, useRouter } from "expo-router";
import { useShareIntentContext } from "expo-share-intent";
import { useEffect, useState } from "react";
import { Linking, Pressable, ScrollView, Text, View } from "react-native";
import { Row, Table } from "react-native-table-component";
import * as db from "@/functions/database";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";

export default function Page() {
    const router = useRouter();
    const { hasShareIntent } = useShareIntentContext();

    // biome-ignore lint/correctness/useExhaustiveDependencies: .
    useEffect(() => {
        if (hasShareIntent) {
            router.replace({
                pathname: "/shareintent",
            });
        }
    }, [hasShareIntent]);

    useFocusEffect(() => {
        (async () => {
            const authenticated = await isAuthenticated();

            if (!authenticated) return router.replace("/login");
        })();
    });

    const [urls, setUrls] = useState<APIURLs | null>(null);
    const [settings, setSettings] = useState<APISettings | null>(null);

    const dashUrl = db.get("url") as DashUrl | null

    useEffect(() => {
        (async () => {
            const urls = await getURLs();
            const settings = await getSettings()

            setUrls(urls);
            setSettings(settings)
        })()
    })

    return (
        <View style={styles.mainContainer}>
            <View style={styles.mainContainer}>

                {urls && settings && dashUrl ? (
                    <View style={{ flex: 1 }}>
                        <View style={styles.header}>
                            <Text style={styles.headerText}>Files</Text>
                            <View style={styles.headerButtons}>
                                <Pressable style={styles.headerButton} onPress={() => {
                                    router.replace("/shorten")
                                }}>
                                    <MaterialIcons name="add-link" size={30} color={styles.headerButton.color} />
                                </Pressable>
                            </View>
                        </View>
                        
                        <View style={{ ...styles.urlsContainer, flex: 1 }}>
                            <ScrollView horizontal={true}>
                                <View>
                                    <Table>
                                        <Row
                                            data={["Code", "Vanity", "URL", "Views", "Max Views"]}
                                            widthArr={[80, 100, 200, 100, 100]}
                                            style={styles.tableHeader}
                                            textStyle={styles.rowText}
                                        />
                                    </Table>
                                    <ScrollView style={styles.tableVerticalScroll}>
                                        <Table>
                                            {
                                                urls.map((url, index) => {
                                                    const code = (
                                                        <Link
                                                            key={url.id}
                                                            href={`${dashUrl}${settings.urlsRoute === "/" ? "" : settings.urlsRoute}/${url.code}`}
                                                            style={{
                                                                ...styles.rowText,
                                                                ...styles.link
                                                            }}
                                                        >
                                                            {url.code}
                                                        </Link>
                                                    )
                                                    
                                                    const vanity = (
                                                        <Link
                                                            key={url.id}
                                                            href={`${dashUrl}${settings.urlsRoute === "/" ? "" : settings.urlsRoute}/${url.vanity}`}
                                                            style={{
                                                                ...styles.rowText,
                                                                ...styles.link
                                                            }}
                                                        >
                                                            {url.vanity}
                                                        </Link>
                                                    )
                                                    
                                                    const noVanity = (
                                                        <Text style={styles.rowText}>
                                                            None
                                                        </Text>
                                                    )
                                                    
                                                    const destination = (
                                                        <Link
                                                            key={url.id}
                                                            href={url.destination}
                                                            style={{
                                                                ...styles.rowText,
                                                                ...styles.link
                                                            }}
                                                        >
                                                            {url.destination}
                                                        </Link>
                                                    )
                                                    
                                                    const views = (
                                                        <Text style={styles.rowText}>
                                                            {url.views}
                                                        </Text>
                                                    )
                                                    
                                                    const maxViews = (
                                                        <Text style={styles.rowText}>
                                                            {url.maxViews || "Unlimited"}
                                                        </Text>
                                                    )
                                                    
                                                    let rowStyle = styles.row
                                                    
                                                    if (index === 0) rowStyle = {
                                                        ...styles.row,
                                                        ...styles.firstRow
                                                    }
                                                    
                                                    if (index === urls.length - 1) rowStyle = {
                                                        ...styles.row,
                                                        ...styles.lastRow
                                                    }
    
                                                    return (
                                                        <Row
                                                            key={url.id}
                                                            data={
                                                                [
                                                                    code,
                                                                    url.vanity ? vanity : noVanity,
                                                                    destination,
                                                                    views,
                                                                    maxViews
                                                                ]
                                                            }
                                                            widthArr={[80, 100, 200, 100, 100]}
                                                            style={rowStyle}
                                                            textStyle={styles.text}
                                                        />
                                                    )
                                                })
                                            }
                                        </Table>
                                    </ScrollView>
                                </View>
                            </ScrollView>
                        </View>
                    </View>
                ) : (
                    <View style={styles.loadingContainer}>
                        <Text style={styles.loadingText}>Loading...</Text>
                    </View>
                )}
            </View>
        </View>
    )
}