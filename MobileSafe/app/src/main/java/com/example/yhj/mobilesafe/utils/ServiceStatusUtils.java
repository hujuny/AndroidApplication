package com.example.yhj.mobilesafe.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

    /**
     * 返回进程的总个数
     * @param context
     * @return
     */
    public static int getProcessCount(Context context){
        //得到进程管理者
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取到当前手机上面运行的进程
        List<RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
        //获取手机上面一共有多少个进程
        Log.d("ServiceStatusUtils","进程是多少个"+runningAppProcesses.size());
        return runningAppProcesses.size();
    }

    /**
     * 返回剩余的内存
     * @param context
     * @return
     */
    public static long getAvailMem(Context context){
        //得到进程管理者
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        //获取到内存的基本信息
        manager.getMemoryInfo(memoryInfo);
        //获取到剩余内存
        return memoryInfo.availMem;
    }

    /**
     * 获取到总内存
     * @param context
     * @return
     */
    public static long getTotalMem(Context context){
        /*
        * 这个地方不能直接跑到低版本的手机上面
        * 这个方法memoryInfo.totalMem()
        * */

        try {
            FileInputStream fileInputStream = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = bufferedReader.readLine();
            StringBuilder buffer = new StringBuilder();

            for (char c:line.toCharArray()) {
                if (c>='0'&&c<='9'){
                    buffer.append(c);
                }
            }

            return Long.parseLong(buffer.toString())*1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
