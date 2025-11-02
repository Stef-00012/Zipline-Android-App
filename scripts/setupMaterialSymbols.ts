import axios from "axios";
import fs from "fs";
import path from "path";

const assetsFolder = path.join(__dirname, "..", "assets");

const fontUrl =
	"https://github.com/google/material-design-icons/raw/refs/heads/master/variablefont/MaterialSymbolsRounded%5BFILL,GRAD,opsz,wght%5D.ttf";
const codepointsUrl =
	"https://raw.githubusercontent.com/google/material-design-icons/refs/heads/master/variablefont/MaterialSymbolsRounded%5BFILL%2CGRAD%2Copsz%2Cwght%5D.codepoints";

const fontOutputPath = path.join(assetsFolder, "material-symbols.ttf");
const codepointsOutputPath = path.join(
	assetsFolder,
	"materialSymbolsRoundedMap.json",
);

async function downloadFont() {
	const response = await axios.get(fontUrl, { responseType: "arraybuffer" });

	fs.writeFileSync(fontOutputPath, Buffer.from(response.data));

	console.log(`Font downloaded at ${fontOutputPath}`);
}

/*
    Script to parse the codepoints was taken from:
    https://github.com/terros-inc/expo-material-symbols
*/

async function downloadCodepoints() {
	const res = await axios.get(codepointsUrl);
	const codepoints = res.data;

	const codepointsArray = codepoints.split("\n");

	const codepointsMap = codepointsArray.reduce(
		(
			acc: { [x: string]: number },
			line: { split: (arg0: string) => [any, any] },
		) => {
			const [key, value] = line.split(" ");

			if (key && value) {
				acc[key] = parseInt(value, 16);
			}

			return acc;
		},
		{},
	);

	fs.writeFileSync(
		codepointsOutputPath,
		JSON.stringify(codepointsMap, null, 2),
	);

	console.log(`Glyph map generated at ${codepointsOutputPath}`);
}

try {
	await downloadCodepoints();
	await downloadFont();
} catch (e) {
	console.error("Failed to download font or codepoints:", e);
}
