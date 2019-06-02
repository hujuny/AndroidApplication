package com.example.yhj.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.utils.SmsUtils;
import com.example.yhj.mobilesafe.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 高级设置页面
 * */
public class AToolsActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    @ViewInject(R.id.sms_progressBar)
    private ProgressBar smsProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        ViewUtils.inject(AToolsActivity.this);
    }

    /*
    * 归属地查询
    * */
    public void numberAddressQuery(View v){
        startActivity(new Intent(AToolsActivity.this,AddressActivity.class));
    }

    /**
     * 短信备份
     * @param v
     */
    public void backUpSms(View v){

        //初始化一个进度条的对话框
        dialog = new ProgressDialog(AToolsActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("稍安勿躁，正在备份，马上就好。。。");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();

        new Thread(){
            @Override
            public void run() {
                super.run();
                boolean backUp = SmsUtils.backUp(AToolsActivity.this, new SmsUtils.BackUpCallBackSms() {
                    @Override
                    public void before(int count) {
                        dialog.setMax(count);
                        smsProgressBar.setMax(count);
                    }

                    @Override
                    public void onBackUpSms(int process) {
                        dialog.setProgress(process);
                        smsProgressBar.setProgress(process);
                    }
                });
                if (backUp){
                    ToastUtils.showToast(AToolsActivity.this,"备份成功");
                }else {
                    ToastUtils.showToast(AToolsActivity.this,"备份失败");
                }
                dialog.dismiss();
            }
        }.start();


    }

    /**
     * 程序锁
     * @param v
     */
    public void appLock(View v){
        startActivity(new Intent(AToolsActivity.this,AppLockActivity.class));
    }

    /**
     * 二维码扫描
     * @param v
     */
    public void qrCode(View v){
        startActivity(new Intent(AToolsActivity.this,QRcodeActivity.class));
    }
}
