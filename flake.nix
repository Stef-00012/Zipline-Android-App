{
    inputs = {
        nixpkgs.url = "github:NixOS/nixpkgs/23.11";
        flake-utils.url = "github:numtide/flake-utils";
    };

    outputs = { self, nixpkgs, flake-utils }:
        flake-utils.lib.eachDefaultSystem (system:
            let
                pkgs = import nixpkgs {
                    inherit system;
                    config = {
                        android_sdk.accept_license = true;
                        allowUnfree = true;
                    };
                };

                androidComposition = pkgs.androidenv.composeAndroidPackages {
                    buildToolsVersions = [ "34.0.0" "33.0.1" ];
                    platformVersions = [ "34"];
                    abiVersions = [ "x86_64" ];
                    includeEmulator = true;
                    emulatorVersion = "33.1.20";
                    includeSystemImages = true;
                    systemImageTypes = [ "google_apis" ];
                    includeNDK = true;
                    ndkVersions = ["25.1.8937393"];
                    cmakeVersions = [ "3.22.1" ];
                };

                androidSdk = androidComposition.androidsdk;
            in
            {
                devShell =
                    with pkgs; mkShell {
                        ANDROID_SDK_ROOT = "${androidSdk}/libexec/android-sdk";
                        GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${androidSdk}/libexec/android-sdk/build-tools/34.0.0/aapt2";
                        LD_LIBRARY_PATH="${libglvnd}/lib";
                        buildInputs = [
                            androidSdk

                            nodejs
                            corepack
                            jdk17
                            crudini
                        ];
                    };
            }
        );
}