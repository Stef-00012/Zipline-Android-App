import * as SecureStore from "expo-secure-store";

export function set(key: string, value: string): void {
	SecureStore.setItem(key, value);
}

export function get(key: string): string | null {
	const result = SecureStore.getItem(key);

	if (result) {
		return result;
	}

	return null;
}

export async function del(key: string): Promise<boolean> {
	try {
		await SecureStore.deleteItemAsync(key);
		
		return true;
	} catch(e) {
		return false;
	}
}
