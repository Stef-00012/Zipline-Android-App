# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.stefdp.zipline.network.models.** { *; }
-keep interface com.stefdp.zipline.network.** { *; }

-keep class com.stefdp.zipline.network.backendapi.models.** { *; }
-keep interface com.stefdp.zipline.network.backendapi.** { *; }

-keep class com.stefdp.zipline.screens.** { *; }
-keep interface com.stefdp.zipline.screens.** { *; }

-keep class com.stefdp.zipline.BuildConfig { *; }

-keep class androidx.lifecycle.ViewTreeLifecycleOwner { *; }
-keep class androidx.lifecycle.ViewTreeViewModelStoreOwner { *; }
-keep class androidx.savedstate.ViewTreeSavedStateRegistryOwner { *; }