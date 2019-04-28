package com.example.yhj.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.utils.ToastUtils;
import com.example.yhj.mobilesafe.view.SettingItemView;

/**
 * 第二个设置向导页面
 * */
public class Setup2Activity extends BaseSetup {

    private SettingItemView sivSim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
         sivSim= (SettingItemView) findViewById(R.id.siv_sim);
        final String sim=mPref.getString("sim",null);
        if(!TextUtils.isEmpty(sim)){
            sivSim.setChecked(true);
        }else {
            sivSim.setChecked(false);
        }

        sivSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sivSim.isChecked()){
                    sivSim.setChecked(false);
                    mPref.edit().remove("sim").apply();
                }else {
                    sivSim.setChecked(true);
                    //保存sim卡信息
                    TelephonyManager tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber=tm.getSimSerialNumber();//获取sim卡序列号
                    mPref.edit().putString("sim",simSerialNumber).apply();//将sim卡序列号保存到sp中
                }
            }
        });
    }

    @Override
    public void showPreviousPage() {//上一页
        startActivity(new Intent(Setup2Activity.this,Setup1Activity.class));
        finish();
        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in,R.anim.tran_previous_out);//进入和退出动画
    }

    @Override
    public void showNextPage() {//下一页
        //如果sim卡没有绑定，不允许进入下一个页面
        String sim=mPref.getString("sim",null);
        if(TextUtils.isEmpty(sim)){
            ToastUtils.showToast(this,"没有绑定sim卡");
            return;
        }
        startActivity(new Intent(Setup2Activity.this,Setup3Activity.class));
        finish();
        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);//进入退出动画
    }




}
