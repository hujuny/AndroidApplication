package com.example.yhj.mobilesafe.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by yhj on 2019/5/17.
 * 短信备份的工具类
 */

public class SmsUtils {

    public interface BackUpCallBackSms{

        public void before(int count);
        public void onBackUpSms(int process);
    }

    public static boolean backUp(Context context,BackUpCallBackSms callback){



        /**
         * 判断当前用户的手机是否有sd卡
         * 权限--使用内容观察者
         * 写短信到sd卡
         */

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
            int count = cursor.getCount();
            //
            callback.before(count);

            //进度条默认为0
            int process=0;
            try {
                //写短信
                File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
                //写文件
                FileOutputStream outputStream = new FileOutputStream(file);
                //得到序列化器，在安卓系统中所有有关xml的解析都是pull解析
                XmlSerializer serializer = Xml.newSerializer();
                //把短信序列化到sd卡并且添加编码格式
                serializer.setOutput(outputStream, "utf-8");
                //第二个参数表示当前的xml文件是否是独立文件standalone,true表示独立
                serializer.startDocument("utf-8", true);
                //设置开始的节点，第一个参数是命名空间，第二个参数是节点的名字
                serializer.startTag(null, "smss");
                //设置smss节点的属性，第二个参数是名字，第三个参数是值
                serializer.attribute(null,"size", String.valueOf(count));

                while (cursor.moveToNext()) {
                    System.err.println("----------------------------");
                    System.out.println("address = " + cursor.getString(0));
                    System.out.println("date = " + cursor.getString(1));
                    System.out.println("type = " + cursor.getString(2));
                    System.out.println("body = " + Crypto.decrypt("123",cursor.getString(3)));
                    serializer.startTag(null, "sms");

                    serializer.startTag(null, "address");
                    //设置节点文本
                    serializer.text(cursor.getString(0));
                    serializer.endTag(null, "address");

                    serializer.startTag(null, "date");
                    serializer.text(cursor.getString(1));
                    serializer.endTag(null, "date");

                    serializer.startTag(null, "type");
                    serializer.text(cursor.getString(2));
                    serializer.endTag(null, "type");

                    serializer.startTag(null, "body");
                    serializer.text(cursor.getString(3));
                    serializer.endTag(null, "body");

                    serializer.endTag(null, "sms");
                    process++;
                    callback.onBackUpSms(process);

                    SystemClock.sleep(200);
                }
                    serializer.endTag(null, "smss");
                    serializer.endDocument();

                cursor.close();
                outputStream.flush();
                outputStream.close();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            return false;
    }

}
