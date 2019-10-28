package com.example.yhj.chatdemo.Controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hyphenate.chat.EMGroupManager.EMGroupStyle;


//新建群
public class NewGroupActivity extends AppCompatActivity {

    @BindView(R.id.et_new_group_name)
    EditText etNewGroupName;
    @BindView(R.id.et_new_group_desc)
    EditText etNewGroupDesc;
    @BindView(R.id.cb_new_group_public)
    CheckBox cbNewGroupPublic;
    @BindView(R.id.cb_new_group_invite)
    CheckBox cbNewGroupInvite;
    @BindView(R.id.bt_new_group_create)
    Button btNewGroupCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        ButterKnife.bind(this);

        initListener();
    }

    private void initListener() {
        btNewGroupCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    /**
     * 创建群
     *
     * @param members
     */
    private void createGroup(String[] members) {
        //群名称
        String groupName = etNewGroupName.getText().toString().trim();
        //群描述
        String groupDesc = etNewGroupDesc.getText().toString().trim();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器创建群
                //参数一：群名称；参数二：群描述；参数三:群成员;参数四：原因；参数五：参数设置
                EMGroupOptions options = new EMGroupOptions();
                options.maxUsers = 200;
                EMGroupStyle groupStyle = null;
                if (cbNewGroupPublic.isChecked()) {
                    if (cbNewGroupInvite.isChecked()) {
                        groupStyle = EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {
                        groupStyle = EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                } else {
                    if (cbNewGroupInvite.isChecked()) {
                        groupStyle = EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    } else {
                        groupStyle = EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }
                options.style = groupStyle;//创建群的类型
                try {
                    EMClient.getInstance().groupManager().createGroup(groupName, groupDesc, members, "申请加入群", options);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群成功！", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
