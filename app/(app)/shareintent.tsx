import ShareIntentShorten from "@/components/ShareIntentShorten";
import { useShareIntentContext } from "expo-share-intent";
import UploadFile from "@/app/(app)/(files)/upload/file";
import UploadText from "@/app/(app)/(files)/upload/text";
import { useFocusEffect, useRouter } from "expo-router";
import { styles } from "@/styles/shareIntent";
import { useAuth } from "@/hooks/useAuth";
import { View } from "react-native";

export default function ShareIntent() {
	const router = useRouter();
	const { hasShareIntent, shareIntent, error, resetShareIntent } =
		useShareIntentContext();

	useAuth();

	useFocusEffect(() => {
		if (!hasShareIntent) {
			resetShareIntent();
			router.replace("/");

			return;
		}

		if (error) {
			console.error(error);

			resetShareIntent();
			return router.replace("/");
		}

		if (
			(!shareIntent.files || shareIntent.files.length <= 0) &&
			!shareIntent.text &&
			!shareIntent.webUrl
		) {
			resetShareIntent();
			return router.replace("/");
		}
	});

	return (
		<View style={styles.mainContainer}>
			{shareIntent.files && (
				<UploadFile
					fromShareIntent
					defaultFiles={shareIntent.files.map((file) => ({
						name: file.fileName,
						uri: file.path,
						size: file.size || undefined,
						mimetype: file.mimeType,
					}))}
					showFileSelector={false}
				/>
			)}

			{shareIntent.text && !shareIntent.webUrl && (
				<UploadText
					fromShareIntent
					defaultText={shareIntent.text}
					showFileSelector={false}
				/>
			)}

			{shareIntent.webUrl && (
				<ShareIntentShorten defaultUrl={shareIntent.webUrl} />
			)}
		</View>
	);
}
