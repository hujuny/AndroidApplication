package com.example.yhj.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yhj on 2019/5/21.
 */

public class SharedPreferencesUtils {
    public static final String SP_NAME="config";

    public static void saveBoolean(Context context,String key, boolean value){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).apply();
    }

    public static boolean getBoolean(Context context,String key,boolean defValue){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key,defValue);
    }
}
