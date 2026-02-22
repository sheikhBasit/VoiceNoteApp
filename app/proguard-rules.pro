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

# Preservation for GSON / Retrofit Models
-keep class com.abstudio.voicenote.data.models.** { *; }

# Firebase Messaging
-keep class com.google.firebase.** { *; }

# Hilt
-keep class androidx.hilt.** { *; }
-keep class com.google.dagger.** { *; }

# Room
-keep class androidx.room.** { *; }

# Keep line numbers for Crashlytics/Debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile