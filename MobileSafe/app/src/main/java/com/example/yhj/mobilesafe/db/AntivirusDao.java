package com.example.yhj.mobilesafe.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by yhj on 2019/5/24.
 */

public class AntivirusDao {

    /**
     * 检查当前的MD5值是否在病毒数据库
     * @param md5
     * @return
     */
    public static String checkFileVirus(String md5){

        String desc = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase("data/data/com.example.yhj.mobilesafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        //判断当前传过来的MD5是否在病毒数据库中
        Cursor cursor = db.rawQuery("select desc from datable where md5=?", new String[]{md5});
        if (cursor.moveToNext()){
            desc=cursor.getString(0);
        }
        cursor.close();
        return desc;
    }

    /**
     * 更新病毒数据库
     * @param md5 特征码
     * @param desc 描述信息
     */
    public static void addVirus(String md5,String desc){
        SQLiteDatabase db = SQLiteDatabase.openDatabase("data/data/com.example.yhj.mobilesafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("md5",md5);
        values.put("type",6);
        values.put("name","Android.Troj.AirAD.a");
        values.put("desc",desc);
        db.insert("datable",null,values);
        db.close();
    }
}
