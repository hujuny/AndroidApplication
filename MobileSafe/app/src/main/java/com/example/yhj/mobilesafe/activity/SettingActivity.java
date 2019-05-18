package com.example.yhj.mobilesafe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.service.AddressService;
import com.example.yhj.mobilesafe.service.CallSafeService;
import com.example.yhj.mobilesafe.utils.ServiceStatusUtils;
import com.example.yhj.mobilesafe.view.SettingClickView;
import com.example.yhj.mobilesafe.view.SettingItemView;

import static java.lang.reflect.Array.getInt;


/*
* 设置中心
* */
public class SettingActivity extends AppCompatActivity {

    private SharedPreferences mPref;
    private SettingItemView sivUpdate;//设置升级
    private SettingItemView sivAddress;//设置归属地显示

    private SettingClickView scvAddressStyle;//修改风格
    private SettingClickView scvAddressLocation;//修改归属地位置
    private SettingItemView sivCallSafe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mPref=getSharedPreferences("config",MODE_PRIVATE);

        initUpdateView();
        initAddressView();
        initAddressStyle();
        initAddressLocation();
        initBlackView();
    }

    /**
     * 初始化黑名单
     */
    private void initBlackView() {
        sivCallSafe = (SettingItemView) findViewById(R.id.siv_call_safe);

        //根据黑名单服务是否运行来更新checkbox
        boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, "com.example.yhj.mobilesafe.service.CallSafeService");
        if (serviceRunning){
            sivCallSafe.setChecked(true);
        }else {
            sivCallSafe.setChecked(false);
        }
        sivCallSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivCallSafe.isChecked()){
                    sivCallSafe.setChecked(false);
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));
                }else {
                    sivCallSafe.setChecked(true);
                    startService(new Intent(SettingActivity.this,CallSafeService.class));
                    System.out.println("开启了服务");
                }
            }
        });

    }


    /*
    * 初始化自动更新开关
    * */
    private void initUpdateView() {

        sivUpdate= (SettingItemView) findViewById(R.id.siv_update);
        boolean autoUpdate=mPref.getBoolean("auto_update",true);
        if (autoUpdate){//设置默认不勾选
            sivUpdate.setChecked(false);
        }else{
            sivUpdate.setChecked(true);

        }
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    /*
    * 初始化归属地开关
    * */
    private void initAddressView() {
        sivAddress= (SettingItemView) findViewById(R.id.siv_address);

        //根据归属地服务来判断是否更新服务
        boolean serviceRunning= ServiceStatusUtils.isServiceRunning(this,"com.example.yhj.mobilesafe.service.AddressService");
        if (serviceRunning){
            sivAddress.setChecked(true);
        }else {
            sivAddress.setChecked(false);

        }

        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivAddress.isChecked()){
                    sivAddress.setChecked(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                }else {
                    sivAddress.setChecked(true);
                    startService(new Intent(SettingActivity.this,AddressService.class));
                }
            }
        });
    }


    final String[] items = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
    /*
    * 修改提示框风格
    * */
    private void initAddressStyle(){
        scvAddressStyle= (SettingClickView) findViewById(R.id.scv_address_style);
        scvAddressStyle.setTitle("归属地提示框风格");
        int style=mPref.getInt("address_style",0);//读取保存的style
        scvAddressStyle.setDesc(items[style]);
        scvAddressStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChooseDialog();
            }
        });

    }

    /*
    * 弹出选择风格的单选框
    * */
    private void showSingleChooseDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");
        int style=mPref.getInt("address_style",0);//读取保存的style
        builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPref.edit().putInt("address_style",which).apply();
                dialog.dismiss();//单选框消失
                scvAddressStyle.setDesc(items[which]);
            }
        });
        builder.setPositiveButton("取消",null);
        builder.show();
    }

    /*
    * 修改归属地位置
    * */
    private void initAddressLocation(){
        scvAddressLocation= (SettingClickView) findViewById(R.id.address_location);
        scvAddressLocation.setTitle("归属地提示框显示位置");
        scvAddressLocation.setDesc("设置归属地提示框的显示位置");

        scvAddressLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,DragViewActivity.class));
            }
        });
    }
}
