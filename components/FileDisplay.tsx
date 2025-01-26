import type { Mimetypes } from "@/types/mimetypes";
import { guessExtension } from "@/functions/util";
import { Image as NativeImage, Text, View } from "react-native";
import { Image } from "expo-image";
import { useEffect, useState } from "react";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { styles } from "@/styles/components/fileDisplay";

interface Props {
	uri: string;
	name: string;
	alt?: string;
	width: number;
	height?: number;
	mimetype?: string;
	autoHeight?: boolean;
	passwordProtected?: boolean;
	originalName?: string | null;
	openable?: boolean;
}

export default function FileDisplay({
	uri,
	alt,
	name,
	mimetype,
	width = 200,
	originalName,
	height = width,
	openable = true,
	autoHeight = false,
	passwordProtected = false,
}: Props) {
	if (passwordProtected) {
		return (
			<View
			style={{
				...styles.nonDisplayableFileContainer,
				height,
				width,
			}}
			>
				<MaterialIcons name="lock" size={60} color={"white"} />
				<Text
					style={styles.nonDisplayableFileText}
				>
					{openable ? "Click to view password " : ""}{originalName || name}
				</Text>
			</View>
		)
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

	const [imageHeight, setImageHeight] = useState<number>(height);

	if (autoHeight) {
		useEffect(() => {
			(async () => {
				const size = await NativeImage.getSize(uri);

				const scaledHeight = (width * size.height) / size.width;

				setImageHeight(scaledHeight);
			})();
		}, [uri, width]);
	}

	if (displayableMimetypes.includes(mimetype))
		return (
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
		);

	return (
		<View
			style={{
				...styles.nonDisplayableFileContainer,
				height,
				width,
			}}
		>
			<MaterialIcons name="description" size={60} color={"white"} />
			<Text
				style={styles.nonDisplayableFileText}
			>
				{openable ? "Click to view " : ""}{originalName || name}
			</Text>
		</View>
	);
}
