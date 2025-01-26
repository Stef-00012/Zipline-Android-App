import mimetypesJSON from "@/assets/mimetypes.json";
import type { Mimetypes } from "@/types/mimetypes";
import axios from "axios";
import * as FileSystem from "expo-file-system";

const mimetypes = mimetypesJSON as Mimetypes;

export function generateRandomString(): string {
	return Math.random().toString(36).substring(2, 6);
}

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

	const mime = Object.entries(mimetypes).find(([key, value]) => value === mimetype) as [keyof Mimetypes, Mimetypes[keyof Mimetypes]] | undefined;
	if (!mime) return "application/octet-stream";

	return mime[0];
}

// export function convertToBlob(data: string): Blob {
// 	console.debug("ctb", 1)

// 	const base64Data = data.split(",")[1];
// 	// console.debug("ctb", 2, base64Data)
// 	const mimetype = data.split(":")[1].split(";").shift();
// 	console.debug("ctb", 3, mimetype)
// 	const string = atob(base64Data);
// 	const length = string.length;
// 	console.debug("ctb", 4, length)
// 	const bytes = new Uint8Array(length);

// 	for (let i = 0; i < length; i++) {
// 		console.debug("ctb", 4.5, i, length)
// 		bytes[i] = string.charCodeAt(i);
// 	}

// 	const blob = new Blob([bytes], { type: mimetype || "image/png" });

// 	console.debug("ctb", 5, blob)

// 	return blob;
// }

// export function convertToBlob(utf8Data: string, mimetype?: string): Blob {
// 	console.debug("ctb", 1)
//     const byteCharacters = new TextEncoder().encode(utf8Data);
// 	console.debug(byteCharacters, byteCharacters.length)
// 	console.debug("ctb", 2)
	
// 	const blob = new Blob([byteCharacters], { type: mimetype });
// 	console.debug("ctb", 3)

// 	console.debug(blob)

//     return blob;
// }

export async function convertToBlob(fileURI: string): Promise<Blob | null> {
	try {
		const res = await axios.get(fileURI, {
			responseType: "blob"
		})

		return res.data;
	} catch(e) {
		return null;
	}
}

export async function getFileDataURI(filePath: string): Promise<string | null> {
	const base64Data = await FileSystem.readAsStringAsync(filePath, {
		encoding: FileSystem.EncodingType.Base64,
	});

	const extension = filePath.split(".").pop();

	const mimetype = guessExtension(extension as Mimetypes[keyof Mimetypes])

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