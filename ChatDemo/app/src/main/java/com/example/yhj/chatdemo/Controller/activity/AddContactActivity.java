package com.example.yhj.chatdemo.Controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.Model.bean.UserInfo;
import com.example.yhj.chatdemo.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * 添加联系人页面
 */
public class AddContactActivity extends AppCompatActivity {

    @BindView(R.id.tv_add_find)
    TextView tvAddFind;
    @BindView(R.id.et_add_name)
    EditText etAddName;
    @BindView(R.id.tv_add_name)
    TextView tvAddName;
    @BindView(R.id.bt_add_add)
    Button btAddAdd;
    @BindView(R.id.rl_add)
    RelativeLayout rlAdd;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        ButterKnife.bind(this);

        PromptDialog promptDialog = new PromptDialog(this);
        promptDialog.showLoading("正在登陆");

    }

    @OnClick({R.id.tv_add_find, R.id.bt_add_add})
    public void viewClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_add_find://查找
                String name = etAddName.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(AddContactActivity.this, "输入的用户名称不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Model.getInstance().getGlobalThreadPool().execute(() -> {
                    userInfo = new UserInfo(name);
                    runOnUiThread(() -> {
                        rlAdd.setVisibility(View.VISIBLE);
                        tvAddName.setText(userInfo.getName());
                    });
                });
                break;
            case R.id.bt_add_add://添加联系人
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //去环信服务器添加好友
                            EMClient.getInstance().contactManager().addContact(userInfo.getName(), "yhj");
                            runOnUiThread(() -> Toast.makeText(AddContactActivity.this, "发送添加好友邀请成功！", Toast.LENGTH_SHORT).show());
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(AddContactActivity.this, "发送添加好友邀请失败！" + e.toString(), Toast.LENGTH_SHORT).show());
                        }
                    }
                });
                break;
        }
    }

}
