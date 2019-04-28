package com.example.yhj.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by yhj on 2019/4/28.
 */

public class ToastUtils {
    public static void showToast(Context con, String text){
        Toast.makeText(con,text,Toast.LENGTH_SHORT).show();
    }
}
