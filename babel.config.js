module.exports = (api) => {
	api.cache(true);

	return {
		presets: ["babel-preset-expo"],
		plugins: [
			"react-native-worklets/plugin", // THIS HAS TO BE LAST IN THIS ARRAY
		],
	};
};
