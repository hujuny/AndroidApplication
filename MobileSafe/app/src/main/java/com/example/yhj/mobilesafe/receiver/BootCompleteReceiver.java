package com.example.yhj.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

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
                    sms.sendTextMessage(phone,null,"sim have changed!!!",null,null);

                    //System.out.println("手机不安全");
                }
            }
        }
    }
}
