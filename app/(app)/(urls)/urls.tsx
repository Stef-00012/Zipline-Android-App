import { isAuthenticated } from "@/functions/zipline/auth";
import { getSettings } from "@/functions/zipline/settings";
import { getURLs } from "@/functions/zipline/urls";
import { styles } from "@/styles/urls";
import type { APISettings, APIURLs } from "@/types/zipline";
import { Link, useFocusEffect, useRouter } from "expo-router";
import { useShareIntentContext } from "expo-share-intent";
import { useEffect, useState } from "react";
import { Linking, Pressable, ScrollView, Text, View } from "react-native";
import { Row, Table } from "react-native-table-component";
import * as db from "@/functions/database";

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

    const dashUrl = db.get("url")

    useEffect(() => {
        (async () => {
            const urls = await getURLs();
            const settings = await getSettings()

            console.log(urls)

            setUrls(urls);
            setSettings(settings)
        })()
    })

    return (
        <View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<View style={styles.header}>
					<Text style={styles.headerText}>URLs</Text>
				</View>

                {urls && settings && dashUrl ? (
                    <View style={styles.urlsContainer}>
                        <ScrollView horizontal={true}>
                            <View>
                                <Table borderStyle={{borderRadius: 10}}>
                                    <Row data={["Code", "Vanity", "URL", "Views", "max Views"]} widthArr={[80, 100, 200, 100, 100]} style={styles.tableHeader} textStyle={styles.text}/>
                                </Table>
                                <ScrollView style={styles.dataWrapper}>
                                    <Table borderStyle={{borderRadius: 10, borderBottomLeftRadius: 10}}>
                                        {
                                            urls.map((url) => {

                                                return (
                                                    <Row
                                                    key={url.id}
                                                    data={[
                                                        (<Link key={url.id} href={`${dashUrl}${settings.urlsRoute === "/" ? "" : settings.urlsRoute}/${url.code}`}>{url.code}</Link>),
                                                        url.vanity ? (<Link key={url.id} href={`${dashUrl}${settings.urlsRoute === "/" ? "" : settings.urlsRoute}/${url.vanity}`}>{url.vanity}</Link>) : "",
                                                        (<Link key={url.id} href={url.destination}>{url.destination}</Link>),
                                                        url.views, url.maxViews || 0]}
                                                    widthArr={[80, 100, 200, 100, 100]}
                                                    style={[styles.row]}
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
                ) : (
                    <View style={styles.loadingContainer}>
                        <Text style={styles.loadingText}>Loading...</Text>
                    </View>
                )}
			</View>
		</View>
    )
}