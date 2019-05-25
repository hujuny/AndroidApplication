package com.example.yhj.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.yhj.mobilesafe.service.KillProcessWidgetService;

/**
 *  清理所有的进程
 */
public class MyAppWidgetReceiver extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);
    }

    /**
     * 第一次创建的时候才会调用
     * 当前的广播生命周期只有10秒
     * 不能做耗时的操作
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.startService(intent);
    }

    /**
     * 删除小部件时调用
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }


    /**
     * 当所有的小部件删除时，调用
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context,KillProcessWidgetService.class));
    }

    /**
     * 每次有新的桌面小控件都会调用
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}
