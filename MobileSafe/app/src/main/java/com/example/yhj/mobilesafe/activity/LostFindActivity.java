package com.example.yhj.mobilesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.receiver.AdminReceiver;

/**
 * 手机防盗页面
 */

public class LostFindActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //激活设备管理器
       //devicePolicyManager();

        SharedPreferences mPref = getSharedPreferences("config", MODE_PRIVATE);
        boolean configed = mPref.getBoolean("configed", false);//判断是否进入过设置向导，默认没有
        if (configed) {
            setContentView(R.layout.activity_lost_find);
            //更新sp，设置安全号码
            TextView tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
            String phone = mPref.getString("safe_phone", "");
            tvSafePhone.setText(phone);
            //更新sp，设置图片锁
            ImageView ivLock = (ImageView) findViewById(R.id.iv_lock);
            Boolean protect = mPref.getBoolean("protect", false);
            if (protect) {
                ivLock.setImageResource(R.mipmap.lock);
            } else {
                ivLock.setImageResource(R.mipmap.unlock);
            }

        } else {//跳转设置向导页面
            startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
            finish();
        }
    }

    /*
    * 重新进入向导页面
    * */
    public void reEnter(View v) {
        startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
        finish();
    }

    public void devicePolicyManager(){
        ComponentName mDeviceAdminSample = new ComponentName(LostFindActivity.this, AdminReceiver.class);//设备管理组件
        DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);//获取设备策略服务
        if (mDPM.isAdminActive(mDeviceAdminSample)) {
            //System.out.println("已经开启了手机设备管理器");
        } else {
            //激活设备管理器
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "哈哈哈，我们有了设备管理器，好夺目，好炫彩！！！");
            startActivity(intent);
        }
    }
}
