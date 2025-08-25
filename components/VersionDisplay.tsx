import { styles } from "@/styles/components/versionDisplay";
import { type ExternalPathString, Link } from "expo-router";
import type { /*APIUser,*/ APIVersion } from "@/types/zipline";
import { /*useContext, useEffect,*/ useState } from "react";
import { Pressable, Text, View } from "react-native";
// import { ZiplineContext } from "@/contexts/ZiplineProvider";
import { openURL } from "expo-linking";
import Button from "./Button";
import Popup from "./Popup";
// import Table from "./Table";

interface Props {
	versionData: APIVersion;
}

export default function VersionDisplay({ versionData }: Props) {
	// const [versionChecking, setVersionChecking] = useState(false);
	const [popupOpen, setPopupOpen] = useState(false);

	// const { webSettings } = useContext(ZiplineContext)

	// useEffect(() => {
	// 	(async () => {
	// 		if ("version" in versionData) {
	// 			setVersionChecking(
	// 				webSettings
	// 					? webSettings.config.features.versionChecking
	// 					: false
	// 			);
	// 		}
	// 	})();
	// }, [versionData, webSettings]);

	if ("version" in versionData) {
		return (
			<View style={styles.versionContainer}>
				<Text style={styles.versionText}>{versionData.version}</Text>
			</View>
		);
	}

	const version = versionData.data;

	return (
		<View>
			<Popup hidden={!popupOpen} onClose={() => setPopupOpen(false)}>
				<View>
					<Text style={styles.headerText}>Zipline Version</Text>

					{version.isLatest && (
						<Text style={styles.text}>
							Running the latest version of Zipline.
						</Text>
					)}

					{version.isUpstream && (
						<Text style={styles.text}>
							You are running an <Text style={styles.boldText}>unstable</Text>{" "}
							version of Zipline. Upstream versions are not fully tested and may
							contain bugs.
						</Text>
					)}

					{!version.isLatest && !version.isUpstream && version.isRelease && (
						<Text style={styles.text}>
							You are running an <Text style={styles.boldText}>outdated</Text>{" "}
							version of Zipline. It is recommended to update to the{" "}
							<Link href={version.latest.url as ExternalPathString}>
								latest version
							</Link>
							.
						</Text>
					)}

					<View style={styles.currentVersionContainer}>
						<Text style={styles.headerText}>Current Version</Text>
						<View
							style={{
								...styles.largeUpstreamVersionIndicator,
								backgroundColor: versionData.data.isLatest
									? "green"
									: "#910000",
							}}
						/>
					</View>

					<View style={styles.versionDataContainer}>
						<View style={styles.versionDataRow}>
							<Text style={styles.versionDataKey}>Version</Text>
							<Link
								href={`https://github.com/diced/zipline/releases/${version.version.tag}`}
								style={styles.versionDataLink}
							>
								{version.version.tag}
							</Link>
						</View>

						<View style={styles.versionDataRow}>
							<Text style={styles.versionDataKey}>Commit</Text>
							<Link
								href={`https://github.com/diced/zipline/commit/${version.version.sha}`}
								style={styles.versionDataLink}
							>
								{version.version.sha}
							</Link>
						</View>

						<View style={styles.versionDataRow}>
							<Text style={styles.versionDataKey}>Upstream?</Text>
							<Text style={styles.versionDataValue}>
								{version.isUpstream ? "Yes" : "No"}
							</Text>
						</View>
					</View>

					{!version.isLatest && version.isUpstream && version.latest.commit && (
						<>
							<Text style={styles.headerText}>Latest Commit Available</Text>
							<Text style={styles.subHeaderText}>
								This is only visible when running an upstream version.
							</Text>

							<View style={styles.versionDataContainer}>
								<View style={styles.versionDataRow}>
									<Text style={styles.versionDataKey}>Commit</Text>
									<Link
										href={`https://github.com/diced/zipline/commit/${version.latest.commit.sha}`}
										style={styles.versionDataLink}
									>
										{version.latest.commit.sha.slice(0, 7)}
									</Link>
								</View>

								<View style={styles.versionDataRow}>
									<Text style={styles.versionDataKey}>Available to update</Text>
									<Text style={styles.versionDataValue}>
										{version.latest.commit.pull ? "Yes" : "No"}
									</Text>
								</View>
							</View>
						</>
					)}

					{!version.isLatest && version.isRelease && (
						<>
							<Text style={styles.headerText}>
								{version.latest.tag} is available
							</Text>

							<Button
								color="#323ea8"
								text={`Changelog for ${version.latest.tag}`}
								margin={{
									top: 5,
									bottom: 5,
								}}
								onPress={() => {
									openURL(version.latest.url);
								}}
							/>

							<Button
								color="#323ea8"
								text={`Update to ${version.latest.tag}`}
								margin={{
									top: 5,
									bottom: 5,
								}}
								onPress={() => {
									openURL(
										"https://zipline.diced.sh/docs/get-started/docker#updating",
									);
								}}
							/>
						</>
					)}
				</View>
			</Popup>

			<Pressable
				onPress={() => setPopupOpen(true)}
				style={styles.versionContainer}
			>
				<View
					style={{
						...styles.upstreamVersionIndicator,
						backgroundColor: versionData.data.isLatest ? "green" : "#910000",
					}}
				/>
				<Text style={styles.versionText}>{versionData.details.version}</Text>
			</Pressable>
		</View>
	);
}
