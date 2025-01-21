import mimetypesJSON from "@/assets/mimetypes.json";
import type { Mimetypes } from "@/types/mimetypes";

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

export function convertToBlob(data: string): Blob {
	const base64Data = data.split(",")[1];
	const mimetype = data.split(":")[1].split(";").shift();
	const string = atob(base64Data);
	const length = string.length;
	const bytes = new Uint8Array(length);

	for (let i = 0; i < length; i++) {
		bytes[i] = string.charCodeAt(i);
	}

	const blob = new Blob([bytes], { type: mimetype || "image/png" });

	return blob;
}
