import glyphMap from "@/assets/materialSymbolsRoundedMap.json";
import createIconSet from '@expo/vector-icons/createIconSet';

const MaterialSymbols = createIconSet(glyphMap, 'MaterialSymbols', '../assets/material-symbols.ttf');

export default MaterialSymbols;