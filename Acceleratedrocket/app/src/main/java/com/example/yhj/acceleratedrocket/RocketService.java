package com.example.yhj.acceleratedrocket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import static android.R.attr.startX;
import static android.os.Build.VERSION_CODES.M;

public class RocketService extends Service {

    private WindowManager.LayoutParams params;
    private int winWidth;
    private int winHeight;
    private WindowManager mWM;
    private View view;

    private int startX;
    private int startY;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        // 获取屏幕宽高
        winWidth = mWM.getDefaultDisplay().getWidth();
        winHeight = mWM.getDefaultDisplay().getHeight();
        System.out.println("高度："+winWidth+"!!!宽度："+winHeight);

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

        view = View.inflate(this, R.layout.rocket, null);// 初始化火箭布局

        // 初始化火箭帧动画
        ImageView ivRocket = (ImageView) view.findViewById(R.id.iv_rocket);
        ivRocket.setBackgroundResource(R.drawable.anim_rocket);
        AnimationDrawable anim = (AnimationDrawable) ivRocket.getBackground();
        anim.start();

        mWM.addView(view, params);

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

                        System.out.println("x:" + params.x + ";y:" + params.y);

                        mWM.updateViewLayout(view, params);

                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if ( params.y > winHeight - 150) {
                            System.out.println("发射火箭!!!");
                            sendRocket();

                            // 启动烟雾效果
                            Intent intent = new Intent(RocketService.this,
                                    BackgroundActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 启动一个栈来存放activity
                            startActivity(intent);
                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 更新火箭位置
            int y = msg.arg1;
            params.y = y;
            mWM.updateViewLayout(view, params);
        }
    };

    /**
     * 发射火箭
     */
    protected void sendRocket() {
        // 设置火箭居中
        params.x = winWidth / 2 - view.getWidth() / 2;
        mWM.updateViewLayout(view, params);

        new Thread() {
            @Override
            public void run() {
                int pos = 400;// 移动总距离
                for (int i = 0; i <= 10; i++) {

                    // 等待一段时间再更新位置,用于控制火箭速度
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int y = pos - 40 * i;

                    Message msg = Message.obtain();
                    msg.arg1 = y;

                    mHandler.sendMessage(msg);
                }
            }
        }.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWM != null && view != null) {
            mWM.removeView(view);
            view = null;
        }
    }
}
