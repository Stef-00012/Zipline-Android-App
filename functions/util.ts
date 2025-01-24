import mimetypesJSON from "@/assets/mimetypes.json";
import type { Mimetypes } from "@/types/mimetypes";
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

export async function getFileDataURI(filePath: string): Promise<string | null> {
	const base64Data = await FileSystem.readAsStringAsync(filePath, {
		encoding: FileSystem.EncodingType.Base64,
	});

	const dataURI = `data:image/jpg;base64,${base64Data}`;

	return dataURI;
}

export function timeDifference(current: Date, previous: Date) {
    const msPerMinute = 60 * 1000;
    const msPerHour = msPerMinute * 60;
    const msPerDay = msPerHour * 24;
    const msPerMonth = msPerDay * 30;
    const msPerYear = msPerDay * 365;

    const elapsed = current.getTime() - previous.getTime();

    if (elapsed < msPerMinute) {
         const difference = Math.round(elapsed / 1000)
         
        return difference + ` second${difference > 1 ? 's' : ''} ago`;
    }

    else if (elapsed < msPerHour) {
         const difference = Math.round(elapsed / msPerMinute)
         
        return difference + ` minute${difference > 1 ? 's' : ''} ago`;
    }

    else if (elapsed < msPerDay ) {
        const difference = Math.round(elapsed / msPerHour)
         
        return difference + ` hour${difference > 1 ? 's' : ''} ago`;
    }

    else if (elapsed < msPerMonth) {
        const difference = Math.round(elapsed / msPerDay)
         
        return difference + ` day${difference > 1 ? 's' : ''} ago`;
    }

    else if (elapsed < msPerYear) {
        const difference = Math.round(elapsed / msPerMonth)
         
        return difference + ` month${difference > 1 ? 's' : ''} ago`;
    }

    else {
        const difference = Math.round(elapsed / msPerYear)
         
        return difference + ` year${difference > 1 ? 's' : ''} ago`;
    }
}