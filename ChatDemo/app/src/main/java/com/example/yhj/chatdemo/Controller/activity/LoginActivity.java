package com.example.yhj.chatdemo.Controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.Model.bean.UserInfo;
import com.example.yhj.chatdemo.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {


    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_login)
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_register, R.id.btn_login})//注册
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register://注册
                String userName = etUserName.getText().toString().trim();
                String password = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "账号或密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Model.getInstance().getGlobalThreadPool().execute(() -> {
                    try {
                        //去环信服务器创建用户账号
                        EMClient.getInstance().createAccount(userName, password);
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "注册成功！", Toast.LENGTH_SHORT).show());
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "注册失败！", Toast.LENGTH_SHORT).show());
                    }
                });
                break;
            case R.id.btn_login://登录
                String loginUserName = etUserName.getText().toString().trim();
                String loginPassword = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(loginUserName) || TextUtils.isEmpty(loginPassword)) {
                    Toast.makeText(this, "账号或密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                Model.getInstance().getGlobalThreadPool().execute(() -> EMClient.getInstance().login(loginUserName, loginPassword, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        //对模型层数据的处理
                        Model.getInstance().loginSuccess(new UserInfo(loginUserName));
                        //保存用户账号信息到本地数据库
                        Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginUserName));
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        });

                    }

                    @Override
                    public void onError(int code, String error) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "登录失败！" + error, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                }));
                break;
        }

    }
}
