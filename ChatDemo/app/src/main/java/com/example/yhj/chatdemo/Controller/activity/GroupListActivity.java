package com.example.yhj.chatdemo.Controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yhj.chatdemo.Controller.adapter.GroupListAdapter;
import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//群组列表页面
public class GroupListActivity extends AppCompatActivity {

    @BindView(R.id.lv_grouplist)
    ListView lvGrouplist;
    private GroupListAdapter groupListAdapter;
    private LinearLayout ll_grouplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        ButterKnife.bind(this);

        View headerView = View.inflate(this, R.layout.header_grouplist, null);
        lvGrouplist.addHeaderView(headerView);
        ll_grouplist = headerView.findViewById(R.id.ll_grouplist);
        initData();
        initListener();
    }

    private void initListener() {
        lvGrouplist.setOnItemClickListener((adapterView, view, position, id) -> {
            if (position == 0) {
                return;
            }
            Intent intent = new Intent(GroupListActivity.this, InviteActivity.class);
            //传递会话类型
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
            EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position - 1);
            //群id
            intent.putExtra(EaseConstant.EXTRA_USER_ID, emGroup.getGroupId());
            startActivity(intent);

        });

        ll_grouplist.setOnClickListener(view -> startActivity(new Intent(GroupListActivity.this, NewGroupActivity.class)));
    }

    private void initData() {
        groupListAdapter = new GroupListAdapter(this);
        lvGrouplist.setAdapter(groupListAdapter);
        //从环信服务器获取所有群的消息
        getGroupsFromServer();
    }

    private void getGroupsFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                List<EMGroup> mGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                //更新页面
                runOnUiThread(() -> {
                    Toast.makeText(GroupListActivity.this, "加载群信息成功！", Toast.LENGTH_SHORT).show();
                    refresh();
                });

            } catch (HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(GroupListActivity.this, "加载群信息失败！", Toast.LENGTH_SHORT).show();
                    //刷新
                    groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
                });
            }
        });
    }

    public void refresh() {
        //刷新
        groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }
}
