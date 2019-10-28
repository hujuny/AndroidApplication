package com.example.yhj.chatdemo.Controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.Model.bean.UserInfo;
import com.example.yhj.chatdemo.R;
import com.hyphenate.chat.EMClient;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.sendMessageDelayed(Message.obtain(), 2000);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            toMainOrLogin();
        }
    };


    private void toMainOrLogin() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            //是否登录过
            if (EMClient.getInstance().isLoggedInBefore()) {
                //获取用户信息
                Log.i("yhj", "当前用户名：" + EMClient.getInstance().getCurrentUser());
                String sss = EMClient.getInstance().getCurrentUser();
                UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxId(sss);
                if (account == null) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                } else {//登录成功
                    Model.getInstance().loginSuccess(account);
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                //主页面
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {//未登录
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
