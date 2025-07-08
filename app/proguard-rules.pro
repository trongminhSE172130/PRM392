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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Socket.IO rules
-keep class io.socket.** { *; }
-keep class com.github.nkzawa.** { *; }
-keep class okio.** { *; }
-keep class okhttp3.** { *; }

# Keep WebSocket classes
-keep class org.java_websocket.** { *; }

# Keep JSON parsing classes
-keep class org.json.** { *; }

# Keep our model classes for JSON parsing
-keep class com.fpt.project.data.model.** { *; }

# Keep Retrofit and Gson classes
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }