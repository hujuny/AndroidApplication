package com.example.yhj.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密算法
 * MD5FileNameGenerator()同样效果
 */

public class MD5Utils {
    public static String encode(String password){


        try {
            MessageDigest instance=MessageDigest.getInstance("MD5");//获取MD5加密算法对象
            byte[] digest=instance.digest(password.getBytes());
            StringBuffer sb=new StringBuffer();
            for (byte b:digest) {
                int i=b&0xff;//获取字节的低八位有效值
                String hexString=Integer.toHexString(i);
                if(hexString.length()<2){
                    hexString="0"+hexString;//如果是一位的话，补零
                }
                sb.append(hexString);
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();//没有该算法时，抛出异常
        }
        return "";


    }
}
