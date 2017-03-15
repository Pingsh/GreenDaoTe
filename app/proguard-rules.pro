# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\android\newsdk/tools/proguard/proguard-android.txt
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

# R文件可能被第三方lib通过发射调用
-keepclassmembers class **.R$* {
    public static <fields>;
}


# # -------------------------------------------
# #  ######## greenDao混淆  ##########
# # -------------------------------------------
-libraryjars libs/greendao-1.3.7.jar
-keep class de.greenrobot.dao.** {*;}
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static Java.lang.String TABLENAME;
}
-keep class **$Properties
