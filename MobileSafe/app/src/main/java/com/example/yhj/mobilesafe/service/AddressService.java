package com.example.yhj.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.db.AddressDao;

import static android.R.attr.endX;
import static android.R.attr.startX;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.yhj.mobilesafe.db.AddressDao.getAddress;

/**
 * 来电提醒的服务
 * */

public class AddressService extends Service {

    private TelephonyManager tm;
    private myListener listener;
    private OutCallReceiver receiver;

    private WindowManager mWM;
    private View view;
    private SharedPreferences mPref;

    private int startX;
    private int startY;
    private WindowManager.LayoutParams params;
    private int winWidth;
    private int winHeight;

    public AddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public void onCreate() {

        mPref = getSharedPreferences("config", MODE_PRIVATE);

         tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener=new myListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);//监听来电状态

        receiver=new OutCallReceiver();
        IntentFilter filter=new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver,filter);//动态注册广播

        super.onCreate();
    }

    private class myListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://电话铃响
                    String address=AddressDao.getAddress(incomingNumber);
                    //Toast.makeText(AddressService.this,address,Toast.LENGTH_LONG).show();
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://电话状态空闲
                    if (view!=null&&mWM!=null){
                        mWM.removeViewImmediate(view);
                        view=null;
                        break;
                    }

            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /*
    * 监听去电的广播接收者，需要权限PROCESS_OUTGOING_CALLS
    * */
    private class OutCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String number=getResultData();//获取去电号码
            String address=AddressDao.getAddress(number);
            //Toast.makeText(context,address,Toast.LENGTH_LONG).show();
            showToast(address);

        }
    }

    /*
    * 自定义归属地悬浮窗，权限SYSTEM_ALERT_WINDOW
    * */
    private void showToast(String text) {
        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        // 获取屏幕宽高
        winWidth = mWM.getDefaultDisplay().getWidth();
        winHeight = mWM.getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;// 电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
        params.gravity = Gravity.LEFT + Gravity.TOP;// 将重心位置设置为左上方,
        // 也就是(0,0)从左上方开始,而不是默认的重心位置
        params.setTitle("Toast");

        int lastX = mPref.getInt("lastX", 0);
        int lastY = mPref.getInt("lastY", 0);

        // 设置浮窗的位置, 基于左上方的偏移量
        params.x = lastX;
        params.y = lastY;

        // view = new TextView(this);
        view = View.inflate(this, R.layout.toast_address, null);

        int[] bgs = new int[] { R.mipmap.call_locate_white,
                R.mipmap.call_locate_orange, R.mipmap.call_locate_blue,
                R.mipmap.call_locate_gray, R.mipmap.call_locate_green };
        int style = mPref.getInt("address_style", 0);

        view.setBackgroundResource(bgs[style]);// 根据存储的样式更新背景

        TextView tvText = (TextView) view.findViewById(R.id.tv_number);
        tvText.setText(text);

        mWM.addView(view, params);// 将view添加在屏幕上(Window)

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        // 计算移动偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;

                        // 更新浮窗位置
                        params.x += dx;
                        params.y += dy;

                        // 防止坐标偏离屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        // 防止坐标偏离屏幕
                        if (params.x > winWidth - view.getWidth()) {
                            params.x = winWidth - view.getWidth();
                        }

                        if (params.y > winHeight - view.getHeight()) {
                            params.y = winHeight - view.getHeight();
                        }

                        // System.out.println("x:" + params.x + ";y:" + params.y);

                        mWM.updateViewLayout(view, params);

                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 记录坐标点
                        SharedPreferences.Editor edit = mPref.edit();
                        edit.putInt("lastX", params.x);
                        edit.putInt("lastY", params.y);
                        edit.apply();
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public void onDestroy() {
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);//停止来电监听
        unregisterReceiver(receiver);//注销广播
        super.onDestroy();
    }
}
