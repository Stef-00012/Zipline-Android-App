import { type ExternalPathString, Link } from "expo-router";
import { Text } from "react-native";
import type { ReactNode } from "react";
import type { StyleProp, TextStyle } from "react-native";

export function parseMarkdownLinks(
	input: string,
	urlStyle?: StyleProp<TextStyle>,
): ReactNode {
	const regex = /\[(?<text>[^\]]+)\]\((?<url>http(s)?:\/\/[^\)]+\.[^\)]+)\)/gim;
	const parts: ReactNode[] = [];
	let lastIndex = 0;

	let match: RegExpExecArray | null;
	// biome-ignore lint/suspicious/noAssignInExpressions: .
	while ((match = regex.exec(input)) !== null) {
		const { text, url } = match.groups as {
			text: string;
			url: string;
		};

		// Add text before the match
		if (match.index > lastIndex) {
			parts.push(input.slice(lastIndex, match.index));
		}

		// Add the Link component
		parts.push(
			<Link
				key={url + match.index}
				href={url as ExternalPathString}
				style={urlStyle}
			>
				{text}
			</Link>,
		);

		lastIndex = regex.lastIndex;
	}

	// Add remaining text after the last match
	if (lastIndex < input.length) {
		parts.push(input.slice(lastIndex));
	}

	return <Text>{parts}</Text>;
}
