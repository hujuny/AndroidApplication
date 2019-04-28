package com.example.yhj.mobilesafe.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.view.SettingItemView;



/*
* 设置中心
* */
public class SettingActivity extends AppCompatActivity {

    private SharedPreferences mPref;
    private SettingItemView sivUpdate;//设置升级
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mPref=getSharedPreferences("config",MODE_PRIVATE);
        sivUpdate= (SettingItemView) findViewById(R.id.siv_update);
        boolean autoUpdate=mPref.getBoolean("auto_update",true);
        if (autoUpdate){//设置默认不勾选
            sivUpdate.setChecked(false);
        }else{
            sivUpdate.setChecked(true);

        }
    }

    /**
     * 对自动更新的全局点击
     * */
    public void click(View v){

        // 判断当前的勾选状态
        if (sivUpdate.isChecked()) {
            // 设置勾选
            sivUpdate.setChecked(false);
            // 更新sp
            mPref.edit().putBoolean("auto_update", true).apply();
        } else {
            sivUpdate.setChecked(true);
            // sivUpdate.setDesc("自动更新已开启");
            // 更新sp
            mPref.edit().putBoolean("auto_update", false).apply();
        }
    }
}
