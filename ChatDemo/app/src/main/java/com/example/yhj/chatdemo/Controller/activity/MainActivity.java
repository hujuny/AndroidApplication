package com.example.yhj.chatdemo.Controller.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.RadioGroup;

import com.example.yhj.chatdemo.Controller.fragment.ChatFragment;
import com.example.yhj.chatdemo.Controller.fragment.ContactListFragment;
import com.example.yhj.chatdemo.Controller.fragment.SettingFragment;
import com.example.yhj.chatdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    @BindView(R.id.rg_main)
    RadioGroup rgMain;
    private ChatFragment chatFragment;
    private ContactListFragment contactListFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initData();
        initListener();

    }

    private void initListener() {
        rgMain.setOnCheckedChangeListener((radioGroup, i) -> {
            Fragment fragment = null;
            switch (i) {
                case R.id.rb_main_chat:
                    fragment = chatFragment;
                    break;
                case R.id.rb_main_contact:
                    fragment = contactListFragment;
                    break;
                case R.id.rb_main_setting:
                    fragment = settingFragment;
                    break;
            }
            switchFragment(fragment);
        });
        //默认fragment
        rgMain.check(R.id.rb_main_chat);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main, fragment).commit();
    }

    private void initData() {
        chatFragment = new ChatFragment();
        contactListFragment = new ContactListFragment();
        settingFragment = new SettingFragment();
    }


}
