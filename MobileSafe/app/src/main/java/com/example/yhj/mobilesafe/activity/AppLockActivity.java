package com.example.yhj.mobilesafe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.fragment.LockFragment;
import com.example.yhj.mobilesafe.fragment.UnLockFragment;

/**
 * 程序锁
 */
public class AppLockActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_lock;
    private TextView tv_unlock;
    private FrameLayout fl_content;
    private FragmentManager fragmentManager;
    private UnLockFragment unLockFragment;
    private LockFragment lockFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        initUI();
    }

    private void initUI() {

        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);

        tv_lock.setOnClickListener(this);
        tv_unlock.setOnClickListener(this);

        //获取到fragment的管理者
        fragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        unLockFragment = new UnLockFragment();
        lockFragment = new LockFragment();
        /*
         * 1.替换界面
         * 2.初始的fragment界面
         */
        transaction.replace(R.id.fl_content,unLockFragment).commit();
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (v.getId()){
            case R.id.tv_lock:
                tv_lock.setBackgroundResource(R.mipmap.tab_left_pressed);
                tv_unlock.setBackgroundResource(R.mipmap.tab_right_default);
                ft.replace(R.id.fl_content,lockFragment);
                break;
            case R.id.tv_unlock:
                tv_lock.setBackgroundResource(R.mipmap.tab_left_default);
                tv_unlock.setBackgroundResource(R.mipmap.tab_right_pressed);
                ft.replace(R.id.fl_content,unLockFragment);
                break;
        }
            ft.commit();
    }
}
