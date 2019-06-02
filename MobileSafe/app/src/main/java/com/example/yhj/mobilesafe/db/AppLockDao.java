package com.example.yhj.mobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhj on 2019/5/25.
 */

public class AppLockDao  {

    private final AppLockOpenHelper helper;
    private Context context;

    public AppLockDao(Context context) {
        this.context=context;
        helper = new AppLockOpenHelper(context);
    }

    /**
     * 添加到程序里面
     * @param packageName 包名
     */
    public void add(String packageName){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename",packageName);
        db.insert("info",null,values);
        db.close();
        //自定义一个内容观察者
        context.getContentResolver().notifyChange(Uri.parse("content://com.example.yhj.mobilesafe.change"),null);
    }

    public void delete(String packageName){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("info","packagename=?",new String[]{packageName});
        db.close();
        //自定义一个内容观察者
        context.getContentResolver().notifyChange(Uri.parse("content://com.example.yhj.mobilesafe.change"),null);
    }

    public boolean find(String packageName){
        boolean result=false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", null, "packagename=?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()){
            result=true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部锁定的包名
     * @return
     */
    public List<String> findAll(){
        boolean result=false;
        SQLiteDatabase db = helper.getReadableDatabase();
        List<String> appLockList = new ArrayList<>();
        Cursor cursor = db.query("info", new String[]{"packagename"}, null, null, null, null, null);
        while (cursor.moveToNext()){
           /* AppInfo appInfo = new AppInfo();
            appInfo.setApkPackageName(cursor.getString(0));
            appLockList.add(appInfo);*/

           appLockList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return appLockList;
    }
}
