package com.example.yhj.mobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.example.yhj.mobilesafe.bean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * safe.db数据库CURD
 */


public class BlackNumberDao {
    private BlackNumberDBOpenHelper helper;
    private SQLiteDatabase db;
    private ContentValues values;

    public BlackNumberDao(Context context) {
        helper = new BlackNumberDBOpenHelper(context);
    }

    public boolean insert(String number, String mode) {
        db = helper.getWritableDatabase();
        values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        long rawId = db.insert("blacknumber", null, values);
        return rawId != -1;
    }


    /*
    * 通过黑名单号码删除
    * */
    public boolean delete(String number) {
        db = helper.getWritableDatabase();
        int rowNumber = db.delete("blacknumber", "number=?", new String[]{number});
        return rowNumber != 0;
    }

    /*
    * 通过黑名单号码修改拦截模式
    * */
    public boolean update(String number, String mode) {
        db = helper.getWritableDatabase();
        values=new ContentValues();
        values.put("mode",mode);
        int rawNumber = db.update("blacknumber", values, "number=?", new String[]{number});
        return rawNumber != 0;

    }

    /*
    * 通过黑名单号码查询拦截模式
    * */
    public String select(String number) {
        String mode = "";
        db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    /*
    * 查询所有的黑名单
    * */
    public  List<BlackNumberInfo> selectAll(){
        SQLiteDatabase db=helper.getReadableDatabase();
        List<BlackNumberInfo> lists = new ArrayList<>();
        Cursor cursor = db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            lists.add(info);
        }
        cursor.close();
        db.close();
        SystemClock.sleep(3000);
        return lists;
    }


    /*
    * 分页加载数据
    * pageNumber表示当前是第几页
    * pageSize表示每一页有多少条数据
    * limit表示限制当前有多少数据
    * offset表示跳过，从第几条开始
    * */
    public List<BlackNumberInfo> findPar(int pageNumber,int pageSize){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{String.valueOf(pageSize), String.valueOf(pageNumber * pageSize)});
        ArrayList<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    /**
     * 分批加载数据
     * @param startIndex 开始的位置
     * @param maxCount 每页展示的最大条目
     * @return
     */
    public List<BlackNumberInfo> findPar2(int startIndex,int maxCount){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{String.valueOf(maxCount), String.valueOf(startIndex)});
        ArrayList<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }


    /*
    * 获取总的记录数
    * */
    public int getTotalNumber(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}
