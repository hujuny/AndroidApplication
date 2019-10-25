package com.example.yhj.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.example.yhj.mobilesafe.db.BlackNumberDao;

import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;

public class CallSafeService extends Service {

    private BlackNumberDao blackNumberDao;
    private TelephonyManager tm;

    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        blackNumberDao = new BlackNumberDao(this);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyPhoneStateListener listener = new MyPhoneStateListener();
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);

        //初始化短信广播
        CallSafeReceiver callSafeReceiver = new CallSafeReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(callSafeReceiver,intentFilter);
    }

    /**
     * 电话监听
     */
    private class MyPhoneStateListener extends PhoneStateListener{

       /* @see TelephonyManager#CALL_STATE_IDLE
        @see TelephonyManager#CALL_STATE_RINGING
        @see TelephonyManager#CALL_STATE_OFFHOOK*/
        //电话状态改变的监听
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch(state){
                case TelephonyManager.CALL_STATE_RINGING://响铃
                    String mode = blackNumberDao.select(incomingNumber);

                    if (mode.equals("1")||mode.equals("2")){
                        Uri uri = Uri.parse("content://call_log/calls");

                        getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler(),incomingNumber ));
                    }

                    endCall();
                    break;

            }
        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        try {
            //通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);

            //使用aidl
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
            Log.d(TAG, "endCall: 进入了挂断电话");



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private class MyContentObserver extends ContentObserver {
        String incomingNumber;
        public MyContentObserver(Handler handler,String incomingNumber) {
            super(handler);
           this.incomingNumber=incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
        }
    }

    /**
     * 删除通话记录
     */
    private void deleteCallLog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});
    }

    /**
     * 黑名单服务
     */
    private class CallSafeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //Android设备接收到的SMS是以pdu形式的(protocol description unit)。所以从intent提取数据时就会遇到pdus。
            Object[] objects = (Object[]) intent.getExtras().get("pdus");

            for (Object object : objects) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();//短信来源号码
                String messageBody = message.getMessageBody();//短信内容
                //通过短信号码查询拦截方式
                String mode = blackNumberDao.select(originatingAddress);


                /*
                  黑名单拦截模式
                  1.全部拦截
                  2.电话拦截
                  3.短信拦截
                 */
                /*if (mode.equals("1")||mode.equals("3")){
                    abortBroadcast();
                }*/
                if(mode.equals("1")){
                    System.out.println("拦截方式是"+mode);
                    abortBroadcast();
                }else if(mode.equals("3")){
                    abortBroadcast();
                    System.out.println("拦截方式是"+mode);
                }

                //智能拦截模式
                if (messageBody.equals("fapiao")){
                    abortBroadcast();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
