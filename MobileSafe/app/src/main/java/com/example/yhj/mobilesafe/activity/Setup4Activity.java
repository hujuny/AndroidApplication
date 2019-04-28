package com.example.yhj.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.yhj.mobilesafe.R;

/**
 * 第四个设置向导页面
 * */
public class Setup4Activity extends BaseSetup {


    private CheckBox cbProtect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        cbProtect= (CheckBox) findViewById(R.id.cb_protect);
        boolean protect=mPref.getBoolean("protect",false);
        //根据sp保存的状态，更新内容
        if(protect){
            cbProtect.setText("防盗保护已经开启");
            cbProtect.setChecked(true);
        }else {
            cbProtect.setText("防盗保护没有开启");
            cbProtect.setChecked(false);
        }
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbProtect.setText("防盗保护已经开启");
                    mPref.edit().putBoolean("protect",true).apply();
                }else{
                    cbProtect.setText("防盗保护没有开启");
                    mPref.edit().putBoolean("protect",false).apply();
                }
            }
        });
    }

    @Override//上一页
    public void showPreviousPage() {
        startActivity(new Intent(Setup4Activity.this,Setup3Activity.class));
        finish();
        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in,R.anim.tran_previous_out);//进入和退出动画
    }

    @Override//下一页
    public void showNextPage() {
        startActivity(new Intent(Setup4Activity.this,LostFindActivity.class));
        finish();
        mPref.edit().putBoolean("configed",true).apply();//更新sp，表示已经设置过手机向导，下次进来就不展示了
        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);//进入退出动画
    }


}
