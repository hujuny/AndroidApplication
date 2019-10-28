package com.example.yhj.chatdemo.Controller.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yhj.chatdemo.Controller.activity.AddContactActivity;
import com.example.yhj.chatdemo.Controller.activity.ChatActivity;
import com.example.yhj.chatdemo.Controller.activity.GroupListActivity;
import com.example.yhj.chatdemo.Controller.activity.InviteActivity;
import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.Model.bean.UserInfo;
import com.example.yhj.chatdemo.R;
import com.example.yhj.chatdemo.Utils.Constant;
import com.example.yhj.chatdemo.Utils.SpUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContactListFragment extends EaseContactListFragment {


    private ImageView iv_contact_red;
    private LocalBroadcastManager mLBM;
    private LinearLayout ll_contact_invitation;
    private String mHxid;

    @Override
    protected void initView() {
        super.initView();
        titleBar.setRightImageResource(R.mipmap.em_add);
        //添加头布局
        View headerView = View.inflate(getActivity(), R.layout.header_fragment_contact, null);

        listView.addHeaderView(headerView);

        //获取红点对象
        iv_contact_red = headerView.findViewById(R.id.iv_invitation_notif);
        //获取邀请信息条目的对象
        ll_contact_invitation = headerView.findViewById(R.id.ll_contact_invitation);
        //设置ListView条目的监听
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                if (user == null) {
                    return;
                }
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());
                startActivity(intent);
            }
        });
        LinearLayout ll_group_item = headerView.findViewById(R.id.ll_group_item);
        //跳转到群组列表页面
        ll_group_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setUpView() {
        titleBar.setRightLayoutClickListener(view -> startActivity(new Intent(getActivity(), AddContactActivity.class)));
        //初始化红点显示
        boolean isNewInvite = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INVITE, false);
        iv_contact_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);
        ll_contact_invitation.setOnClickListener(view -> {
            iv_contact_red.setVisibility(View.GONE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, false);
            startActivity(new Intent(getActivity(), InviteActivity.class));
        });

        //注册广播
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(ContactInviteChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(ContactChangeReceiver, new IntentFilter(Constant.CONTACT_CHANGED));
        mLBM.registerReceiver(GroupChangeReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
        //从环信服务器获取所有人的联系人信息
        getContactFromHxServer();
        //绑定ListView和contextMenu
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);
        mHxid = easeUser.getUsername();

        //添加布局
        getActivity().getMenuInflater().inflate(R.menu.delete, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_contact) {
            //删除选中联系人
            deleteContact();
            return true;
        }
        return super.onContextItemSelected(item);

    }

    private void deleteContact() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                EMClient.getInstance().contactManager().deleteContact(mHxid);
                //本地数据库更新
                Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//Toast提示
                        Toast.makeText(getActivity(), "删除" + mHxid + "成功", Toast.LENGTH_SHORT).show();
                        //刷新页面
                        refreshContact();
                    }
                });
            } catch (HyphenateException e) {
                e.printStackTrace();
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//Toast提示
                        Toast.makeText(getActivity(), "删除" + mHxid + "失败", Toast.LENGTH_SHORT).show();
                        //刷新页面
                        refreshContact();
                    }
                });
            }
        });
    }

    private void getContactFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                //获取到所有好友的环信Id
                List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();
                //校验
                if (hxids != null && hxids.size() >= 0) {
                    List<UserInfo> contacts = new ArrayList<>();
                    //转换
                    for (String hxid : hxids) {
                        UserInfo userInfo = new UserInfo(hxid);
                        contacts.add(userInfo);
                    }
                    //保存好友信息到本地数据库
                    Model.getInstance().getDbManager().getContactTableDao().saveContacts(contacts, true);
                    //刷新页面
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshContact();
                        }
                    });
                }


            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        });
    }

    private void refreshContact() {
        //获取数据
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
        //校验
        if (contacts != null && contacts.size() >= 0) {
            //设置数据
            Map<String, EaseUser> contactsMap = new HashMap<>();
            //转换
            for (UserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getHxId());
                contactsMap.put(contact.getHxId(), easeUser);
            }
            setContactsMap(contactsMap);
            //刷新页面
            refresh();
        }
    }

    private BroadcastReceiver ContactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

        }
    };
    private BroadcastReceiver ContactChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新页面
            refreshContact();

        }
    };
    //群邀请广播
    private BroadcastReceiver GroupChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //显示红点
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(ContactInviteChangeReceiver);
        mLBM.unregisterReceiver(ContactChangeReceiver);
        mLBM.unregisterReceiver(GroupChangeReceiver);
    }
}
