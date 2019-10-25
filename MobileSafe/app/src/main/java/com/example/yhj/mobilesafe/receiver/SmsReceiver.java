package com.example.yhj.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.service.LocationService;

/**
 * 拦截短信
 * */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objects= (Object[]) intent.getExtras().get("pdus");
        for(Object object: objects){//短信最多140字节，超出的话会按多条短信发送，
            // 所以是一个数组，因为我们的短信指令有限，所以for循环执行一次
            SmsMessage smsMessage=SmsMessage.createFromPdu((byte[]) object);
            String smsOrign=smsMessage.getOriginatingAddress();//短信的发送号码
            String smsBody=smsMessage.getMessageBody();

            DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);//获取设备策略服务
            ComponentName mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);//设备管理组件
            if("#*alarm*#".equals(smsBody)){
                //播放报警音乐,手机静音无效
                MediaPlayer mPlayer=MediaPlayer.create(context, R.raw.alarm);
                mPlayer.setVolume(1f,1f);
                mPlayer.setLooping(true);
                mPlayer.start();
            }else if("#*location*#".equals(smsBody)){
                //获取经纬度坐标
                context.startService(new Intent(context, LocationService.class));//开启定位服务
                SharedPreferences sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);//
                String location=sp.getString("location","");
                abortBroadcast();//中断短信传递，从而系统短信app 就收不到内容了
                Log.d("SmsReceiver.class","位置"+location);
            }else if ("#*wipedata*#".equals(smsBody)){
                if (mDPM.isAdminActive(mDeviceAdminSample)) {
                    mDPM.wipeData(0);//清除数据，恢复出厂设置；参数：只清除手机内部数据，不包括sd卡
                } else {
                    Toast.makeText(context, "请先激活设备管理器", Toast.LENGTH_SHORT).show();
                }
                abortBroadcast();//中断短信传递，从而系统短信app 就收不到内容了
            }else if ("#*lockscreen*#".equals(smsBody)){
                if (mDPM.isAdminActive(mDeviceAdminSample)) {//判断是否激活了设备管理器
                    mDPM.lockNow();//立即锁屏
                    mDPM.resetPassword("123456", 0);//参数2：多个设备管理器，后台不重新设置密码
                } else {
                    Toast.makeText(context, "请先激活设备管理器", Toast.LENGTH_SHORT).show();
                }
                abortBroadcast();//中断短信传递，从而系统短信app 就收不到内容了
            }
        }
    }

}
