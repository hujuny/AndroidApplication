package com.example.yhj.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 黑名单数据库
 */

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

    BlackNumberDBOpenHelper(Context context) {
        super(context, "safe.db", null, 1);
    }

    /*
    * blacknumber：表名
    * number：电话号
    * mode：拦截模式（电话拦截，短信拦截,全部拦截）
    * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement,number varchar(20),mode varchar(2))");
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
