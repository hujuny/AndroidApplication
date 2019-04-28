package com.example.yhj.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.utils.ToastUtils;

/**
 * 第三个设置向导页面
 * */
public class Setup3Activity extends BaseSetup {

    private EditText etPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        etPhone= (EditText) findViewById(R.id.et_phone);

        String phone=mPref.getString("safe_phone","");
        etPhone.setText(phone);
    }

    @Override
    public void showPreviousPage() {//上一页
        startActivity(new Intent(Setup3Activity.this,Setup2Activity.class));
        finish();
        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in,R.anim.tran_previous_out);//进入和退出动画
    }

    @Override
    public void showNextPage() {//下一页
        String phone=etPhone.getText().toString().trim();//过滤空格
        if(TextUtils.isEmpty(phone)){
            ToastUtils.showToast(this,"安全号码不能为空");
            return;
        }
        mPref.edit().putString("safe_phone",phone).apply();//保存安全号码

        startActivity(new Intent(Setup3Activity.this,Setup4Activity.class));
        finish();
        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);//进入退出动画
    }
    /*
    * 选择联系人
    * */
    public void insertCon(View v){
       Intent intent=new Intent(this,ContactActivity.class);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            String phone=data.getStringExtra("phone");
            phone=phone.replaceAll("-","").replaceAll(" ","");//替换空格和-
            etPhone.setText(phone);//将电话号码传给输入框
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
