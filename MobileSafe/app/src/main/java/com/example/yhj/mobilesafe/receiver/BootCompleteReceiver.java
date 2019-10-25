package com.example.yhj.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 开机检测SIM卡是否发生变动
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        Boolean protect=sp.getBoolean("protect",true);
        //只有在防盗保护开启的前提下才进行sim卡判断
        if (protect){
            String sim=sp.getString("sim",null);
            if(!TextUtils.isEmpty(sim)){
                //获得当前手机端sim卡
                TelephonyManager tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String currentSim=tm.getSimSerialNumber();
                if(sim.equals(currentSim)){
                    //System.out.println("手机安全");
                }else{
                    String phone=sp.getString("safe_phone","");
                    SmsManager sms=SmsManager.getDefault();
                    //第一个参数，对方手机号码，参数二，短信中心号码，一般为null，第三个参数，短信内容，
                    //第四个参数，setIntent判断短信是否发送(动作)成功，参数五，deliveryIntent，强调发送后的结果
                    sms.sendTextMessage(phone,null,"sim have changed!!!",null,null);


                    //System.out.println("手机不安全");
                }
            }
        }
    }
}
