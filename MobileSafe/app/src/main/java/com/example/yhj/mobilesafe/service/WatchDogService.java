package com.example.yhj.mobilesafe.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.yhj.mobilesafe.activity.EnterPwdActivity;
import com.example.yhj.mobilesafe.db.AppLockDao;

import java.util.List;

public class WatchDogService extends Service {

    private ActivityManager activityManager;
    private AppLockDao dao;

    private String packageName = "";
    private String tempStopProtectPackageName;
    private List<String> appLockInfos;
    private WatchDogReceiver receiver;

    public WatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }


    private class WatchDogReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.yhj.mobilesafe.stopprotect")){
                //获取到临时停止保护的对象
                tempStopProtectPackageName = intent.getStringExtra("packageName");
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                tempStopProtectPackageName=null;
                flag=false;
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //狗开始干活
                if (!flag){
                    startWatchDog();
                }
            }
        }
    }

    private class AppLockContentObserver extends ContentObserver{


        public AppLockContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            appLockInfos = dao.findAll();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getContentResolver().registerContentObserver(Uri.parse("content://com.example.yhj.mobilesafe.change"),true,new AppLockContentObserver(new Handler()));

        dao = new AppLockDao(WatchDogService.this);

        //注册广播接受者
        receiver = new WatchDogReceiver();
        IntentFilter filter = new IntentFilter();
        //停止保护
        filter.addAction("com.example.yhj.mobilesafe.stopprotect");

        /*
         * 注册一个锁屏的广播
         * 屏幕锁住，看门狗休息
         * 屏幕解锁的时候，看门狗信息
         */
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        registerReceiver(receiver,filter);

        //获取到进程管理器
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        //1.获取到当前的任务栈
        //2.取任务栈最上面的任务
        startWatchDog();

    }
    //标记当前的看萌狗是否停下来
    private boolean flag = false;

    private void startWatchDog() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                flag=true;
                while (flag) {
                   //由于这个狗一直在后台运行，防止阻塞
                    //获取到当前正在运行的任务栈
                    List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
                    //获取最上面的进程
                    RunningTaskInfo taskInfo = runningTasks.get(0);
                    //获取最顶端应用程序的包名
                    String packageName = taskInfo.topActivity.getPackageName();
                    //System.out.println("包名时候说呢么"+packageName);
                    SystemClock.sleep(30);
                    if (appLockInfos.contains(packageName)){
                        if(packageName.equals(tempStopProtectPackageName)){

                        }else {
                            Intent intent = new Intent(WatchDogService.this, EnterPwdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //停止要保护的对象
                            intent.putExtra("packageName",packageName);
                            startActivity(intent);
                        }
                    }
                    if(dao.find(packageName)){
                        System.out.println("你进拉了没诶诶额");
                        Intent intent = new Intent(WatchDogService.this, EnterPwdActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }


                }


            }

        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag=false;
        unregisterReceiver(receiver);
        receiver=null;
    }
}
