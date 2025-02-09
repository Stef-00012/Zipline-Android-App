# Zipline Android App

> [!IMPORTANT]
> iOS is not supported

This is an android app made to manage your [Zipline V4](https://github.com/diced/zipline/tree/v4) (selfhosted) server and also upload files or shorten URLs. You can also share files to the app to upload them.

# Download

To download the app, just go to the [latest release](https://github.com/Stef-00012/zipline-android-app/releases/latest) and download the `app-release.apk` file.\
Or directly download it from [here](https://github.com/Stef-00012/zipline-android-app/releases/latest/download/app-release.apk).

# Creating a development build

> [!IMPORTANT]
> Requires an android device connected to the laptop and ADB

1. `bun install`
2. `bun run prebuild`
3. `bun run run:android`

(this will create a development apk and automatically install in your device)

> Building an apk

> [!NOTE]
> add ` --local` flag if you don't have a Expo EAS account or want to build it locally on your PC\
> add `--output /path/to/file.apk` to save the apk in that path

1. `bun install`
2. `bun install --global eas-cli`
3. `eas build --clear-cache --platform android --profile preview`

This will create an apk but won't automatically install

# TODO

- [ ] App update checking + update button (maybe, idk)