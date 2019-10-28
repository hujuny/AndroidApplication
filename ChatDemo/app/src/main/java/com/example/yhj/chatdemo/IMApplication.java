package com.example.yhj.chatdemo;

import android.app.Application;
import android.content.Context;

import com.example.yhj.chatdemo.Model.Model;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;


public class IMApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        EMOptions options = new EMOptions();
        options.setAutoAcceptGroupInvitation(false);//不自动接收群邀请消息
        options.setAcceptInvitationAlways(false);//不总是一直接受所有邀请
        //初始化easeui
        EaseUI.getInstance().init(this, options);
        Model.getInstance().init(this);

        mContext = this;
    }

    //获取全局上下文对象
    public static Context getGlobalApplication() {
        return mContext;
    }
}
