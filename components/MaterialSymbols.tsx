import glyphMap from "@/assets/materialSymbolsRoundedMap.json";
import createIconSet from '@expo/vector-icons/createIconSet';

const MaterialSymbols = createIconSet(glyphMap, 'MaterialSymbols', require('../assets/material-symbols.ttf'));

export default MaterialSymbols;