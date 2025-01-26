import mimetypesJSON from "@/assets/mimetypes.json";
import type { Mimetypes } from "@/types/mimetypes";
import * as FileSystem from "expo-file-system";

const mimetypes = mimetypesJSON as Mimetypes;

export function guessMimetype(
	mimetype: keyof Mimetypes,
): Mimetypes[keyof Mimetypes] {
	if (!mimetype) return "so";

	const mime = mimetypes[mimetype];
	if (!mime) return "so";

	return mime;
}

export function guessExtension(
	mimetype: Mimetypes[keyof Mimetypes],
): keyof Mimetypes {
	if (!mimetype) return "application/octet-stream";

	const mime = Object.entries(mimetypes).find(
		([key, value]) => value === mimetype,
	) as [keyof Mimetypes, Mimetypes[keyof Mimetypes]] | undefined;
	if (!mime) return "application/octet-stream";

	return mime[0];
}

export async function getFileDataURI(filePath: string): Promise<string | null> {
	const base64Data = await FileSystem.readAsStringAsync(filePath, {
		encoding: FileSystem.EncodingType.Base64,
	});

	const extension = filePath.split(".").pop();

	const mimetype = guessExtension(extension as Mimetypes[keyof Mimetypes]);

	const dataURI = `data:${mimetype};base64,${base64Data}`;

	return dataURI;
}

export function timeDifference(currentDate: Date, targetDate: Date) {
	const msPerMinute = 60 * 1000;
	const msPerHour = msPerMinute * 60;
	const msPerDay = msPerHour * 24;
	const msPerMonth = msPerDay * 30;
	const msPerYear = msPerDay * 365;

	const elapsed = targetDate.getTime() - currentDate.getTime();
	const isFuture = elapsed > 0;

	const absElapsed = Math.abs(elapsed);

	if (absElapsed < msPerMinute) {
		const difference = Math.round(absElapsed / 1000);
		return `${isFuture ? "in " : ""}${difference} second${difference !== 1 ? "s" : ""}${isFuture ? "" : " ago"}`;
	}

	if (absElapsed < msPerHour) {
		const difference = Math.round(absElapsed / msPerMinute);
		return `${isFuture ? "in " : ""}${difference} minute${difference !== 1 ? "s" : ""}${isFuture ? "" : " ago"}`;
	}

	if (absElapsed < msPerDay) {
		const difference = Math.round(absElapsed / msPerHour);
		return `${isFuture ? "in " : ""}${difference} hour${difference !== 1 ? "s" : ""}${isFuture ? "" : " ago"}`;
	}

	if (absElapsed < msPerMonth) {
		const difference = Math.round(absElapsed / msPerDay);
		return `${isFuture ? "in " : ""}${difference} day${difference !== 1 ? "s" : ""}${isFuture ? "" : " ago"}`;
	}

	if (absElapsed < msPerYear) {
		const difference = Math.round(absElapsed / msPerMonth);
		return `${isFuture ? "in " : ""}${difference} month${difference !== 1 ? "s" : ""}${isFuture ? "" : " ago"}`;
	}

	const difference = Math.round(absElapsed / msPerYear);
	return `${isFuture ? "in " : ""}${difference} year${difference !== 1 ? "s" : ""}${isFuture ? "" : " ago"}`;
}

export function colorHash(str: string) {
	let hash = 0;
	for (let i = 0; i < str.length; i++) {
		hash = str.charCodeAt(i) + ((hash << 5) - hash);
	}

	let color = "#";
	for (let i = 0; i < 3; i++) {
		const value = (hash >> (i * 8)) & 0xff;
		color += `00${value.toString(16)}`.substr(-2);
	}

	return color;
}

