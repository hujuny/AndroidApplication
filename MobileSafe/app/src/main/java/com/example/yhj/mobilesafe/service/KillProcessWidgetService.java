package com.example.yhj.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.receiver.MyAppWidgetReceiver;
import com.example.yhj.mobilesafe.utils.ServiceStatusUtils;

import java.util.Timer;
import java.util.TimerTask;

public class KillProcessWidgetService extends Service {

    private Timer timer;
    private TimerTask timerTask;
    private AppWidgetManager widgetManager;

    public KillProcessWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //桌面小控件的管理者
        widgetManager = AppWidgetManager.getInstance(this);
        //初始化定时器
        //每隔五秒更新界面
        timer = new Timer();
        //初始化一个定时任务
        timerTask = new TimerTask(){
            @Override
            public void run() {
                //初始化一个远程view
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);

                int processCount = ServiceStatusUtils.getProcessCount(getApplicationContext());
                remoteViews.setTextViewText(R.id.process_count,"正在运行的软件:"+processCount);

                long availMem = ServiceStatusUtils.getAvailMem(getApplicationContext());
                remoteViews.setTextViewText(R.id.process_memory,"可用内存:"+ Formatter.formatFileSize(getApplicationContext(),availMem));

                Intent intent = new Intent();
                //发送一个隐式意图
                intent.setAction("com.example.yhj.mobilesafe");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                //设置点击事件
                remoteViews.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);


                //第二个参数表示当前哪一个广播去处理当前的桌面小控件
                ComponentName componentName = new ComponentName(getApplicationContext(), MyAppWidgetReceiver.class);

                //更新桌面
                widgetManager.updateAppWidget(componentName,remoteViews);
            }
        };
        timer.schedule(timerTask,0,5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
