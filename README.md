# AndroidApplication
安卓学习代码

#OfflineUpdateDemo
在线升级demo

使用过程存在的问题：
 java.lang.NoClassDefFoundError: Failed resolution of: Lorg/apache/http/protocol/BasicHttpContext;
 使用 "<uses-library android:name="org.apache.http.legacy" android:required="false" />"

 Android N 7.0 上 报错：android.os.FileUriExposedException
 //注：Android 7.0以上版本跳转，要使用FileProvider.getUriForFile(),在注册文件里注册provider。
 // 7.0以下继续使用Uri.fromFile()
 参照博客：https://blog.csdn.net/yy1300326388/article/details/52787853

