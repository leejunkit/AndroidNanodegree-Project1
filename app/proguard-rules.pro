# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/kit/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# The AbstractProvider searches for inner classes annotated with @Table
# but proguard flattens nested classes. This line makes sure the structure
# is preserved.
-keepattributes InnerClasses

# Don't obfuscate the FieldType enum as those values are added
# to the CREATE TABLE statement directly
-keep enum de.triplet.simpleprovider.Column$FieldType { public *; }

# Do not obfuscate anything that is annotated with @Table or @Column
# so AbstractProvider can automatically generate the table and column names.
# (This line might be optional if you're fine with a table called 'a' and columns
# called 'a', 'b', 'c', 'd' and so forth).
-keep @de.triplet.simpleprovider.Table class * { @de.triplet.simpleprovider.Column *; }
