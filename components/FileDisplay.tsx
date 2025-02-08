import type { Mimetypes } from "@/types/mimetypes";
import { guessExtension } from "@/functions/util";
import { Image as NativeImage, Pressable, Text, View } from "react-native";
import { Image } from "expo-image";
import { useEffect, useState } from "react";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { styles } from "@/styles/components/fileDisplay";
import type { APIFile, DashURL } from "@/types/zipline";
import * as db from "@/functions/database";
import { type ExternalPathString, useRouter } from "expo-router";

interface Props {
	uri: string;
	name: string;
	alt?: string;
	width: number;
	file?: APIFile;
	height?: number;
	mimetype?: string;
	openable?: boolean;
	maxHeight?: number;
	autoHeight?: boolean;
	passwordProtected?: boolean;
	originalName?: string | null;
	onPress?: () => void | Promise<void>;
}

export default function FileDisplay({
	uri,
	alt,
	name,
	file,
	mimetype,
	maxHeight,
	width = 200,
	originalName,
	height = width,
	openable = true,
	autoHeight = false,
	passwordProtected = false,
	onPress,
}: Props) {
	const router = useRouter();
	const dashUrl = db.get("url") as DashURL | null;

	const [imageHeight, setImageHeight] = useState<number>(height);

	useEffect(() => {
		if (autoHeight) {
			(async () => {
				const size = await NativeImage.getSize(uri);

				let scaledHeight = (width * size.height) / size.width;

				if (maxHeight && scaledHeight > maxHeight) scaledHeight = maxHeight;

				setImageHeight(scaledHeight);
			})();
		}
	}, [uri, width, maxHeight, autoHeight]);

	if (file) {
		mimetype = file.type;
		uri =
			mimetype.startsWith("video/") && file.thumbnail
				? file.thumbnail
				: `${dashUrl}/raw/${file.name}`;
		name = file.name;
		originalName = file.originalName;
		passwordProtected = file.password;
	}

	if (passwordProtected) {
		return (
			<Pressable onPress={onPress || onPressDefault}>
				<View
					style={{
						...styles.nonDisplayableFileContainer,
						height,
						width,
					}}
				>
					<MaterialIcons name="lock" size={60} color={"white"} />
					<Text style={styles.nonDisplayableFileText}>
						{openable ? "Click to view password " : ""}
						{originalName || name}
					</Text>
				</View>
			</Pressable>
		);
	}

	if (!mimetype) {
		const uriExtension = uri.split(".").pop();
		const nameExtension = name.split(".").pop();
		const originalNameExtension = originalName?.split(".").pop();

		const isOctetStream = [
			uriExtension,
			nameExtension,
			originalNameExtension,
		].some((extension) => extension === "so");

		mimetype = guessExtension(uriExtension as Mimetypes[keyof Mimetypes]);

		if (
			mimetype === "application/octet-stream" &&
			!isOctetStream &&
			nameExtension
		)
			mimetype = guessExtension(nameExtension as Mimetypes[keyof Mimetypes]);

		if (
			mimetype === "application/octet-stream" &&
			!isOctetStream &&
			originalNameExtension
		)
			mimetype = guessExtension(
				originalNameExtension as Mimetypes[keyof Mimetypes],
			);
	}

	const displayableMimetypes = [
		"image/webp",
		"image/apng",
		"image/png",
		"image/avif",
		"image/heic",
		"image/jpeg",
		"image/gif",
		"image/x-icon",
		"image/svg+xml",
	];

	if (
		displayableMimetypes.includes(mimetype) ||
		(mimetype.startsWith("video/") && file?.thumbnail)
	)
		return (
			<Pressable onPress={onPress || onPressDefault}>
				<Image
					source={{
						uri: uri,
					}}
					style={{
						height: imageHeight,
						width: width,
					}}
					alt={alt || originalName || name}
					contentFit="contain"
				/>
			</Pressable>
		);

	return (
		<Pressable onPress={onPress || onPressDefault}>
			<View
				style={{
					...styles.nonDisplayableFileContainer,
					height,
					width,
				}}
			>
				<MaterialIcons name="description" size={60} color={"white"} />
				<Text style={styles.nonDisplayableFileText}>
					{openable ? "Click to view " : ""}
					{originalName || name}
				</Text>
			</View>
		</Pressable>
	);

	function onPressDefault() {
		if (file) router.replace(`${dashUrl}${file.url}` as ExternalPathString);
	}
}
