package com.example.yhj.jnitset;

/**
 * Created by yhj on 2019/4/8.
 */

public class JNITest {

    static {
        System.loadLibrary("JNITest");
    }

    //生成一个native方法
    public native static String get();
}
