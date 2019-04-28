package com.example.yhj.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;
/**
 * 手机防盗页面
 * */

public class LostFindActivity extends AppCompatActivity {

    private SharedPreferences mPref;
    private TextView tvSafePhone;
    private ImageView ivLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPref=getSharedPreferences("config",MODE_PRIVATE);
        boolean configed=mPref.getBoolean("configed",false);//判断是否进入过设置向导，默认没有
        if(configed){
            setContentView(R.layout.activity_lost_find);
            //更新sp，设置安全号码
            tvSafePhone= (TextView) findViewById(R.id.tv_safe_phone);
            String phone=mPref.getString("safe_phone","");
            tvSafePhone.setText(phone);
            //更新sp，设置图片锁
            ivLock= (ImageView) findViewById(R.id.iv_lock );
            Boolean protect=mPref.getBoolean("protect",false);
            if(protect){
                ivLock.setImageResource(R.mipmap.lock);
            }else {
                ivLock.setImageResource(R.mipmap.unlock);
            }

        }else{//跳转设置向导页面
            startActivity(new Intent(LostFindActivity.this,Setup1Activity.class));
            finish();
        }
    }

    /*
    * 重新进入向导页面
    * */
    public void reEnter(View v){
        startActivity(new Intent(LostFindActivity.this,Setup1Activity.class));
        finish();
    }

}
