import * as SecureStore from "expo-secure-store";

export function set(key: string, value: string) {
	SecureStore.setItem(key, value);
}

export function get(key: string) {
	const result = SecureStore.getItem(key);

	if (result) {
		return result;
	}

	return null;
}
