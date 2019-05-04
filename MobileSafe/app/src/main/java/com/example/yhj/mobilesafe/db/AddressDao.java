package com.example.yhj.mobilesafe.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 归属地查询工具
 */

public class AddressDao {
    //注意，该路径必须是data/data文件夹目录下的文件，否则数据库访问不到
    private static final String PATH="data/data/com.example.yhj.mobilesafe/files/address.db";

    public static String getAddress(String number){
        String address="未知号码";

        //获取数据库对象
        SQLiteDatabase database=SQLiteDatabase.openDatabase(PATH,null,SQLiteDatabase.OPEN_READWRITE);

        //手机号码特点： 1+（3，4,5,6,7,8,）+9位数字
        //正则表达式
        //^1[3-8]\d{9}$

        if (number.matches("^1[3-8]\\d{9}$")){//匹配手机号码；select location from data2 where id=(select outkey from data1 where id=?)
            Cursor cursor=database.rawQuery("select location from data2 where id =(select outkey from data1 where id=?)",new String[]{number.substring(0,7)});
            if (cursor.moveToNext()){
                address=cursor.getString(0);
            }
            cursor.close();
        }else if (number.matches("^\\d+$")){//匹配数字
            switch (number.length()){
                case 3:
                    address="报警电话";
                    break;
                case 4:
                    address="模拟器号码";
                    break;
                case 5:
                    address="客服号码";
                    break;
                case 7:
                case 8:
                    address="本地号码";
                    break;
                default:
                    if (number.startsWith("0")&&number.length()>10){//有可能是长途电话
                        //有些区号是三位，有些是四位，包含0

                        //先查询四位区号
                        Cursor cursor=database.rawQuery("select location from data2 where area=?",new String[]{number.substring(1,4)});
                        if (cursor.moveToNext()){
                            address=cursor.getString(0);
                        }else {
                            cursor.close();
                            //查询三位区号
                            database.rawQuery("select location from data2 where area=?",new String[]{number.substring(1,3)});
                            if (cursor.moveToNext()){
                                address=cursor.getString(0);
                            }
                            cursor.close();
                        }

                    }
                    break;
            }
        }
        return address;
    }
}

