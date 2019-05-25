package com.example.yhj.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
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

    /**
     * 获取到文件的md5（病毒特征码）
     * @param sourceDir
     * @return
     */
    public static String getFileMd5(String sourceDir) {

        File file=new File(sourceDir);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len=-1;
            //获取到数字摘要
            MessageDigest messageDigest = MessageDigest.getInstance("md5");//获取MD5加密算法对象
            while ((len=fis.read(buffer))!=-1){
                messageDigest.update(buffer,0,len);
            }
            byte[] result = messageDigest.digest();
            StringBuffer sb = new StringBuffer();

            for (byte b:result){
                int number=b&0xff;
                String hex = Integer.toHexString(number);
                if (hex.length()==1){
                    sb.append("0").append(hex);
                }else{
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
