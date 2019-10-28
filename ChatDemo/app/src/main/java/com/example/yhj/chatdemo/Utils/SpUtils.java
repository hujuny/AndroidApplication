package com.example.yhj.chatdemo.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.yhj.chatdemo.IMApplication;

//SharedPreferences工具类,保存获取数据
public class SpUtils {

    public static String IS_NEW_INVITE = "is_new_invite";//新的邀请标记
    private static SharedPreferences mSp;
    private static SpUtils instance = new SpUtils();

    public SpUtils() {
    }

    //单例
    public static SpUtils getInstance() {
        if (mSp == null) {
            mSp = IMApplication.getGlobalApplication().getSharedPreferences("im", Context.MODE_PRIVATE);
        }
        return instance;
    }

    //保存
    public void save(String key, Object value) {
        if (value instanceof String) {
            mSp.edit().putString(key, (String) value).apply();
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).apply();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value).apply();
        }
    }

    //获取数据
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    //获取boolean型数据
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    //获取int型数据
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }
}
