package com.example.yhj.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.utils.ToastUtils;
import com.lidroid.xutils.cache.MD5FileNameGenerator;

public class EnterPwdActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_pwd;
    private Button bt_0;
    private Button bt_1;
    private Button bt_2;
    private Button bt_3;
    private Button bt_4;
    private Button bt_5;
    private Button bt_6;
    private Button bt_7;
    private Button bt_8;
    private Button bt_9;

    private Button bt_clean_all;
    private Button bt_delete;

    private Button bt_ok;

    private String str;

    private SharedPreferences mPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        mPref = getSharedPreferences("config", MODE_PRIVATE);

        initUI();
    }

    private void initUI() {
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        //隐藏当前的键盘
        et_pwd.setInputType(InputType.TYPE_NULL);

        bt_0 = (Button) findViewById(R.id.bt_0);
        bt_1 = (Button) findViewById(R.id.bt_1);
        bt_2 = (Button) findViewById(R.id.bt_2);
        bt_3 = (Button) findViewById(R.id.bt_3);
        bt_4 = (Button) findViewById(R.id.bt_4);
        bt_5 = (Button) findViewById(R.id.bt_5);
        bt_6 = (Button) findViewById(R.id.bt_6);
        bt_7 = (Button) findViewById(R.id.bt_7);
        bt_8 = (Button) findViewById(R.id.bt_8);
        bt_9 = (Button) findViewById(R.id.bt_9);

        bt_clean_all = (Button) findViewById(R.id.bt_clean_all);
        bt_delete = (Button) findViewById(R.id.bt_delete);

        bt_ok = (Button) findViewById(R.id.btn_ok);

        bt_0.setOnClickListener(this);
        bt_1.setOnClickListener(this);
        bt_2.setOnClickListener(this);
        bt_3.setOnClickListener(this);
        bt_4.setOnClickListener(this);
        bt_5.setOnClickListener(this);
        bt_6.setOnClickListener(this);
        bt_7.setOnClickListener(this);
        bt_8.setOnClickListener(this);
        bt_9.setOnClickListener(this);


        bt_ok.setOnClickListener(this);
        bt_clean_all.setOnClickListener(this);
        bt_delete.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bt_0:
                str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_0.getText().toString());
                break;
            case R.id.bt_1:
                 str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_1.getText().toString());
                break;
            case R.id.bt_2:
                str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_2.getText().toString());
                break;
            case R.id.bt_3:
                str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_3.getText().toString());
                break;
            case R.id.bt_4:
                str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_4.getText().toString());
                break;
            case R.id.bt_5:
                str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_5.getText().toString());
                break;
            case R.id.bt_6:
                str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_6.getText().toString());
                break;
            case R.id.bt_7:
                str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_7.getText().toString());
                break;
            case R.id.bt_8:
                str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_8.getText().toString());
                break;
            case R.id.bt_9:
                str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_9.getText().toString());
                break;
            case R.id.bt_clean_all:
                et_pwd.setText("");
                break;
            case R.id.bt_delete:
                str = et_pwd.getText().toString();

                if (str.length() == 0) {
                    return;
                }
                et_pwd.setText(str.substring(0, str.length() - 1));
                break;
            case R.id.btn_ok:
                str=et_pwd.getText().toString();
                String savePassword = mPref.getString("password", null);
                if (new MD5FileNameGenerator().generate(str).equals(savePassword)){
                    ToastUtils.showToast(this,"密码输入正确");
                }else {
                    ToastUtils.showToast(this,"密码输入错误");
                }
                break;


        }
    }
}
