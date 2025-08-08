{
  description = "Creates an environment in which you can create, build and emulate an expo app, and a script which creates, builds and emulates an expo app.";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?rev=55d15ad12a74eb7d4646254e13638ad0c4128776";
    #nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable&rev=55d15ad12a74eb7d4646254e13638ad0c4128776";

    android.url = "github:tadfisher/android-nixpkgs?rev=a21442ddcdf359be82220a6e82eff7432d9a4190";
    #android.url = "github:tadfisher/android-nixpkgs/stable?rev=a21442ddcdf359be82220a6e82eff7432d9a4190";
    android.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = { self, nixpkgs, ...}@inputs: 
  let pkgs = nixpkgs.legacyPackages.x86_64-linux; 
  in {
    packages.x86_64-linux.android-sdk = inputs.android.sdk.x86_64-linux (sdkPkgs: [
      sdkPkgs.cmdline-tools-latest
      sdkPkgs.cmake-3-22-1
      sdkPkgs.build-tools-34-0-0
      sdkPkgs.build-tools-35-0-0
      sdkPkgs.platform-tools
      sdkPkgs.platforms-android-35
      sdkPkgs.emulator
      sdkPkgs.system-images-android-35-google-apis-x86-64 
      sdkPkgs.ndk-27-1-12297006
    ]);


    packages.x86_64-linux.createAndEmulateExpoApp = pkgs.writeScriptBin "create-and-emulate-expo-app"
    ''
    #!/usr/bin/env sh
    date=$(date +%s)
    avd=myavd$date
    echo "using date $date"
    mkdir -p demo-app-$date/avds
    export ANDROID_AVD_HOME=$HOME/.config/.android/avd
    echo using $ANDROID_AVD_HOME as ANDROID_AVD_HOME
    echo "no" | avdmanager create avd -k 'system-images;android-35;google_apis;x86_64' -n $avd
    cd demo-app-$date
    echo "demo-app" | yarn create expo-app
    cd demo-app
    yarn expo run:android -d $avd
    '';

    packages.x86_64-linux.default = self.packages.x86_64-linux.createAndEmulateExpoApp;
    devShell.x86_64-linux = pkgs.mkShell ( rec { 
     JAVA_HOME=pkgs.corretto17.home;
     ANDROID_HOME="${self.packages.x86_64-linux.android-sdk}/share/android-sdk";
     ANDROID_SDK_ROOT="${self.packages.x86_64-linux.android-sdk}/share/android-sdk";
     GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${ANDROID_SDK_ROOT}/build-tools/34.0.0/aapt2";
     ANDROID_AVD_HOME = "~/.config/.android/avd";

     nativeBuildInputs = [ 
        pkgs.nodejs_18 
        pkgs.yarn
        pkgs.bun
        pkgs.watchman
        pkgs.corretto17
        pkgs.aapt
        self.packages.x86_64-linux.createAndEmulateExpoApp
        self.packages.x86_64-linux.android-sdk
      ];
    });

  };
}