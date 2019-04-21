package com.example.yhj.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 读取流的工具
 */

public class StreamUtils {

    /*
    * 将输入流读取成String返回
    * */
    public static String readFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        int len;
        byte[] buffer=new byte[1024];
        while ((len=in.read(buffer))!=-1){
            out.write(buffer,0,len);
        }
        String result=out.toString();
        in.close();
        out.close();

        return result;
    }
}
