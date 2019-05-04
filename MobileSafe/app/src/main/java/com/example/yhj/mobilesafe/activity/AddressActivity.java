package com.example.yhj.mobilesafe.activity;

import android.animation.Animator;
import android.os.Vibrator;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.db.AddressDao;

/**
 * 归属地查询页面
 * */
public class AddressActivity extends AppCompatActivity {

    private EditText etNumber;
    private TextView tvResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

         etNumber= (EditText) findViewById(R.id.et_number);
         tvResult= (TextView) findViewById(R.id.tv_result);

        etNumber.addTextChangedListener(new TextWatcher() {//文本框监听器
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address= AddressDao.getAddress(s.toString());
                tvResult.setText(address);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*
    * 归属地查询
    * */
    public void query(View v){
        String number=etNumber.getText().toString().trim();
        if(!TextUtils.isEmpty(number)){
            String address= AddressDao.getAddress(number);
            tvResult.setText(address);
        }else {
            Animation shake= AnimationUtils.loadAnimation(this,R.anim.shake);
            etNumber.startAnimation(shake);
            vibrate();
        }
    }

    /*
    * 手机震动（有权限）
    * */
    private void vibrate() {
        Vibrator vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //vibrator.vibrate(2000);震动2秒
        //先等待一秒，再震动2秒，再等待1秒，再震动2秒；参数2表示只执行一次，不循环；等于0表示从头循环；参数2表示从第几个位置开始循环
        vibrator.vibrate(new long[]{1000,2000,1000,3000},-1);
        //vibrator.cancel();关闭震动
    }

}
