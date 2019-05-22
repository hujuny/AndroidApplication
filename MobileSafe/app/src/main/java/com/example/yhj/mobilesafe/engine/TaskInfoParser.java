package com.example.yhj.mobilesafe.engine;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhj on 2019/5/18.
 * 进程管理解析器
 */

public class TaskInfoParser {

    public static List<TaskInfo> getTaskInfos(Context context){
        PackageManager packageManager = context.getPackageManager();

        List<TaskInfo> TaskInfos = new ArrayList<TaskInfo>();

        // 获取到进程管理器
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // 获取到手机上面所有运行的进程
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();

        for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {

            TaskInfo taskInfo = new TaskInfo();

            // 获取到进程的名字
            String processName = runningAppProcessInfo.processName;

            taskInfo.setPackageName(processName);

            try {
                // 获取到内存基本信息
                /**
                 * 这个里面一共只有一个数据
                 */
                MemoryInfo[] memoryInfo = activityManager
                        .getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
                // Dirty弄脏
                // 获取到总共弄脏多少内存(当前应用程序占用多少内存)
                int totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty() * 1024;


//				System.out.println("==========="+totalPrivateDirty);

                taskInfo.setMemorySize(totalPrivateDirty);

                PackageInfo packageInfo = packageManager.getPackageInfo(
                        processName, 0);

                // /获取到图片
                Drawable icon = packageInfo.applicationInfo
                        .loadIcon(packageManager);

                taskInfo.setIcon(icon);
                // 获取到应用的名字
                String appName = packageInfo.applicationInfo.loadLabel(
                        packageManager).toString();

                taskInfo.setAppName(appName);

                System.out.println("-------------------");
                System.out.println("processName="+processName);
                System.out.println("appName="+appName);
                //获取到当前应用程序的标记
                //packageInfo.applicationInfo.flags 我们写的答案
                //ApplicationInfo.FLAG_SYSTEM表示老师的该卷器
                int flags = packageInfo.applicationInfo.flags;
                //ApplicationInfo.FLAG_SYSTEM 表示系统应用程序
                if((flags & ApplicationInfo.FLAG_SYSTEM) != 0 ){
                    //系统应用
                    taskInfo.setUserApp(false);
                }else{
//					/用户应用
                    taskInfo.setUserApp(true);

                }




            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                // 系统核心库里面有些系统没有图标。必须给一个默认的图标

                taskInfo.setAppName(processName);
                taskInfo.setIcon(context.getResources().getDrawable(
                        R.mipmap.ic_launcher));
            }

            TaskInfos.add(taskInfo);
        }

        return TaskInfos;
    }
}
