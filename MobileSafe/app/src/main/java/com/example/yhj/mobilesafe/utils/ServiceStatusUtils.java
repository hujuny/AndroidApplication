package com.example.yhj.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.res.AssetManager;

import java.util.List;

/**
 * 检查服务是否正在运行
 */

public class ServiceStatusUtils {

    public static boolean isServiceRunning(Context ctx,String serviceName){
        ActivityManager am= (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningServices=am.getRunningServices(100);//获取系统运行的服务，最多一百个
        for (RunningServiceInfo runningServiceInfo: runningServices) {
            String className=runningServiceInfo.service.getClassName();//获取服务的名称
            if (className.equals(serviceName)){//服务存在
                System.out.println("开启了服务");
                return true;
            }
        }

        return false;
    }
}
