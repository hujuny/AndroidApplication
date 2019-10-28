package com.example.yhj.chatdemo.Controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yhj.chatdemo.Controller.adapter.PickContactAdapter;
import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.Model.bean.PickContactInfo;
import com.example.yhj.chatdemo.Model.bean.UserInfo;
import com.example.yhj.chatdemo.R;
import com.example.yhj.chatdemo.Utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//选择联系人
public class PickContactActivity extends AppCompatActivity {

    @BindView(R.id.tv_pick_contacts_save)
    TextView tvPickContactsSave;
    @BindView(R.id.lv_pick_contacts)
    ListView lvPickContacts;
    private List<PickContactInfo> mPicks;
    private PickContactAdapter pickContactAdapter;
    private List<String> mExistMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);

        ButterKnife.bind(this);

        getData();

        initData();
        initListener();

    }

    /**
     * 获取传递过来的数据
     */
    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);

        if (groupId != null) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            //获取群众已经存在的所有群成员
            mExistMembers = group.getMembers();
        }
        if (mExistMembers == null) {
            mExistMembers = new ArrayList<>();
        }
    }

    private void initListener() {
        lvPickContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox cb_pick = view.findViewById(R.id.cb_item_pick_contacts);
                cb_pick.setChecked(!cb_pick.isChecked());
                PickContactInfo pickContactInfo = mPicks.get(i);
                pickContactInfo.setChecked(cb_pick.isChecked());
                pickContactAdapter.notifyDataSetChanged();

            }
        });

        tvPickContactsSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取到已经选择的联系人
                List<String> names = pickContactAdapter.getPickContacts();

                //给启动页面返回数据
                Intent intent = new Intent();
                intent.putExtra("members", names.toArray(new String[0]));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initData() {
        //从本地数据库中获取所有的联系人信息
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
        mPicks = new ArrayList<>();
        if (contacts != null && contacts.size() >= 0) {
            //转换
            for (UserInfo contact : contacts) {
                PickContactInfo pickContactInfo = new PickContactInfo(contact, false);
                mPicks.add(pickContactInfo);
            }
        }
        pickContactAdapter = new PickContactAdapter(this, mPicks, mExistMembers);
        lvPickContacts.setAdapter(pickContactAdapter);

    }
}
