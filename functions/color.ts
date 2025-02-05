export interface RGBA {
	r: number;
	g: number;
	b: number;
	a: number;
}

function isHexColor(hex: string): boolean {
	const HEX_REGEXP = /^#?([0-9A-F]{3}){1,2}([0-9A-F]{2})?$/i;

	return HEX_REGEXP.test(hex);
}

function hexToRgba(color: string): RGBA {
	let hexString = color.replace("#", "");

	if (hexString.length === 3) {
		const shorthandHex = hexString.split("");
		hexString = [
			shorthandHex[0],
			shorthandHex[0],
			shorthandHex[1],
			shorthandHex[1],
			shorthandHex[2],
			shorthandHex[2],
		].join("");
	}

	if (hexString.length === 8) {
		const alpha = Number.parseInt(hexString.slice(6, 8), 16) / 255;

		return {
			r: Number.parseInt(hexString.slice(0, 2), 16),
			g: Number.parseInt(hexString.slice(2, 4), 16),
			b: Number.parseInt(hexString.slice(4, 6), 16),
			a: alpha,
		};
	}

	const parsed = Number.parseInt(hexString, 16);
	const r = (parsed >> 16) & 255;
	const g = (parsed >> 8) & 255;
	const b = parsed & 255;

	return {
		r,
		g,
		b,
		a: 1,
	};
}

function rgbStringToRgba(color: string): RGBA {
	const [r, g, b, a] = color
		.replace(/[^0-9,./]/g, "")
		.split(/[/,]/)
		.map(Number);

	return { r, g, b, a: a || 1 };
}

function hslStringToRgba(hslaString: string): RGBA {
	const hslaRegex =
		/^hsla?\(\s*(\d+)\s*,\s*(\d+%)\s*,\s*(\d+%)\s*(,\s*(0?\.\d+|\d+(\.\d+)?))?\s*\)$/i;

	const matches = hslaString.match(hslaRegex);
	if (!matches) {
		return {
			r: 0,
			g: 0,
			b: 0,
			a: 1,
		};
	}

	const h = Number.parseInt(matches[1], 10);
	const s = Number.parseInt(matches[2], 10) / 100;
	const l = Number.parseInt(matches[3], 10) / 100;
	const a = matches[5] ? Number.parseFloat(matches[5]) : undefined;

	const chroma = (1 - Math.abs(2 * l - 1)) * s;
	const huePrime = h / 60;
	const x = chroma * (1 - Math.abs((huePrime % 2) - 1));
	const m = l - chroma / 2;

	let r: number;
	let g: number;
	let b: number;

	if (huePrime >= 0 && huePrime < 1) {
		r = chroma;
		g = x;
		b = 0;
	} else if (huePrime >= 1 && huePrime < 2) {
		r = x;
		g = chroma;
		b = 0;
	} else if (huePrime >= 2 && huePrime < 3) {
		r = 0;
		g = chroma;
		b = x;
	} else if (huePrime >= 3 && huePrime < 4) {
		r = 0;
		g = x;
		b = chroma;
	} else if (huePrime >= 4 && huePrime < 5) {
		r = x;
		g = 0;
		b = chroma;
	} else {
		r = chroma;
		g = 0;
		b = x;
	}

	return {
		r: Math.round((r + m) * 255),
		g: Math.round((g + m) * 255),
		b: Math.round((b + m) * 255),
		a: a || 1,
	};
}

export function toRgba(color: string): RGBA {
	if (isHexColor(color)) {
		return hexToRgba(color);
	}

	if (color.startsWith("rgb")) {
		return rgbStringToRgba(color);
	}

	if (color.startsWith("hsl")) {
		return hslStringToRgba(color);
	}

	return {
		r: 0,
		g: 0,
		b: 0,
		a: 1,
	};
}

function gammaCorrect(c: number) {
	return c <= 0.03928 ? c / 12.92 : ((c + 0.055) / 1.055) ** 2.4;
}

function getLightnessFromOklch(oklchColor: string) {
	const match = oklchColor.match(/oklch\((.*?)%\s/);
	return match ? Number.parseFloat(match[1]) : null;
}

export function luminance(color: string): number {
	if (color.startsWith("oklch(")) {
		return (getLightnessFromOklch(color) || 0) / 100;
	}

	const { r, g, b } = toRgba(color);

	const sR = r / 255;
	const sG = g / 255;
	const sB = b / 255;

	const rLinear = gammaCorrect(sR);
	const gLinear = gammaCorrect(sG);
	const bLinear = gammaCorrect(sB);

	return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear;
}

export function isLightColor(color: string, luminanceThreshold = 0.179) {
	if (color.startsWith("var(")) {
		return false;
	}

	return luminance(color) > luminanceThreshold;
}

export function rgbaToHex(r: number, g: number, b: number, a = 1) {
	r = Math.min(255, Math.max(0, r));
    g = Math.min(255, Math.max(0, g));
    b = Math.min(255, Math.max(0, b));
    a = Math.min(1, Math.max(0, a));
	
	const alpha = a << 24
	const red = r << 16
	const green = g << 8
	const blue = b

    return `#${(alpha + red + green + blue).toString(16).slice(1).toUpperCase()}`;
}