package com.example.yhj.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * 设置引导页的基类,不需要在清单文件中注册，因为不需要界面展示
 */

public abstract class BaseSetup extends AppCompatActivity {

    private GestureDetector mDetector;
    public SharedPreferences mPref;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPref=getSharedPreferences("config",MODE_PRIVATE);
        //手势识别器
        mDetector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override//监听手势滑动事件，e1表示滑动的起点，e2表示滑动终点。velocityX表示水平速度，velocityY表示垂直速度
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //判断纵向滑动幅度是否过大，过大的话不允许切换界面
                if(Math.abs(e2.getRawY()-e1.getRawY())>100){
                    Toast.makeText(BaseSetup.this,"不能这样滑哦！",Toast.LENGTH_SHORT).show();
                    return true;
                }
                //判断滑动是否过慢
                if(Math.abs(velocityX)<100){
                    Toast.makeText(BaseSetup.this,"滑动的太慢了",Toast.LENGTH_SHORT).show();
                    return true;
                }

                //向右滑，上一页
                if(e2.getRawY()-e1.getRawX()>200){
                    showPreviousPage();
                    return true;
                }
                //向左滑，下一页
                if(e1.getRawX()-e2.getRawX()>200){
                    showNextPage();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /*
    * 展示下一页，子类必须实现
    * */
    public abstract void showPreviousPage();

    /*
    * 展示下一页，子类必须实现
    * */
    public abstract void showNextPage();

    //点击下一页按钮
    public void next(View v){
        showNextPage();
    }
    //点击上一页按钮
    public void previous(View v){
        showPreviousPage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);//委托手势识别器处理触摸事件
        return super.onTouchEvent(event);
    }
}
