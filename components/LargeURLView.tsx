import { type ExternalPathString, Link } from "expo-router";
import { styles } from "@/styles/components/largeURLView";
import type { APIURL, DashURL } from "@/types/zipline";
import type { URLActions } from "@/app/(app)/urls";
import { timeDifference } from "@/functions/util";
import Dropdown from "@/components/Dropdown";
import { Text, View } from "react-native";

interface Props {
	url: APIURL;
	dashUrl: DashURL;
	urlsRoute: string;
	onAction: (type: URLActions, url: APIURL) => Promise<void> | void;
}

export default function LargeURLView({
	url,
	urlsRoute,
	dashUrl,
	onAction,
}: Props) {
	return (
		<View style={styles.mainContainer}>
			<View style={styles.titleContainer}>
				{url.enabled ? (
					<Link
						href={`${dashUrl}${urlsRoute === "/" ? "" : urlsRoute}/${url.vanity || url.code}`}
						style={{
							...styles.urlCode,
							...styles.link,
						}}
					>
						{url.vanity || url.code}
					</Link>
				) : (
					<Text style={styles.urlCode}>{url.vanity || url.code}</Text>
				)}

				<Dropdown
					icon="more-horiz"
					data={[
						{
							name: "Copy Short Link",
							id: `${url.id}-copyShortLink`,
							icon: "content-copy",
							onPress: () => {
								onAction("copyShortLink", url);
							},
						},
						{
							name: "Copy Destination",
							id: `${url.id}-copyDestinatin`,
							icon: "content-copy",
							onPress: async () => {
								onAction("copyDestination", url);
							},
						},
						{
							name: "Edit",
							id: `${url.id}-edit`,
							icon: "edit",
							onPress: () => {
								onAction("edit", url);
							},
						},
						{
							name: "Delete",
							id: `${url.id}-delete`,
							color: "#e65f59",
							iconColor: "#e65f59",
							icon: "delete",
							onPress: () => {
								onAction("delete", url);
							},
						},
					]}
				/>
			</View>

			<View style={styles.divider} />

			<View style={styles.keysContainer}>
				<Text style={styles.key}>
					<Text style={styles.keyName}>Views</Text>: <Text>{url.views}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Max Views</Text>:{" "}
					<Text>{url.maxViews || "Unlimited"}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Enabled</Text>:{" "}
					<Text>{url.enabled ? "Yes" : "No"}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Created</Text>:{" "}
					<Text>{timeDifference(new Date(), new Date(url.createdAt))}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Updated</Text>:{" "}
					<Text>{timeDifference(new Date(), new Date(url.updatedAt))}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Destination</Text>:{" "}
					<Link
						href={url.destination as ExternalPathString}
						style={styles.link}
					>
						{url.destination}
					</Link>
				</Text>

				{url.vanity && (
					<Text style={styles.key}>
						<Text style={styles.keyName}>Code</Text>:{" "}
						<Link
							href={`${dashUrl}${urlsRoute === "/" ? "" : urlsRoute}/${url.code}`}
							style={styles.link}
						>
							{url.code}
						</Link>
					</Text>
				)}

				<Text style={styles.key}>
					<Text style={styles.keyName}>ID</Text>: <Text>{url.id}</Text>
				</Text>
			</View>
		</View>
	);
}
