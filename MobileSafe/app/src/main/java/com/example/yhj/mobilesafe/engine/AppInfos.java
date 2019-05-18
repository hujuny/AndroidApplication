package com.example.yhj.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.yhj.mobilesafe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by yhj on 2019/5/14.
 * 读取用户程序和系统程序
 */

public class AppInfos {

    public static List<AppInfo> getAppInfos(Context context){
        List<AppInfo> packageInfos = new ArrayList<>();

        //获取到包的管理者
        PackageManager packageManager = context.getPackageManager();
        //获取到安装包
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo installedPackage : installedPackages) {
            AppInfo appInfo = new AppInfo();

            //获取到应用的图标
            Drawable drawable = installedPackage.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(drawable);

            //获取到应用程序的名字
            String apkName = installedPackage.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);

            //获取到应用程序的包名
            String packageName = installedPackage.packageName;
            appInfo.setApkPackageName(packageName);

            //获取到apk资源的路径
            String sourceDir = installedPackage.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            long apkSize = file.length();
            appInfo.setApkSize(apkSize);

            Log.d(TAG, "getAppInfos: 程序的名字"+apkName);
            Log.d(TAG, "getAppInfos: 程序的包名"+packageName);
            Log.d(TAG, "getAppInfos: 程序的大小"+apkSize);

            //获取到安装应用程序的标记
            int flags = installedPackage.applicationInfo.flags;

            if((flags& ApplicationInfo.FLAG_SYSTEM)!=0){
                //表示系统的app
                appInfo.setUserApp(false);
            }else {
                //表示用户的app
                appInfo.setUserApp(true);
            }

            if ((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
                //表示在sd卡
                appInfo.setRom(false);

            }else {
                //表示在内存
                appInfo.setRom(true);
            }

            packageInfos.add(appInfo);
        }

        return packageInfos;
    }
}
