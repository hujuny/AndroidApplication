package com.example.yhj.chatdemo;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.Model.bean.GroupInfo;
import com.example.yhj.chatdemo.Model.bean.InvationInfo;
import com.example.yhj.chatdemo.Model.bean.UserInfo;
import com.example.yhj.chatdemo.Utils.Constant;
import com.example.yhj.chatdemo.Utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;

import java.util.List;

//全局事件监听类
public class EventListener {


    private Context mContext;
    private final LocalBroadcastManager mLBM;

    public EventListener(Context context) {
        mContext = context;

        //创建一个发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(mContext);

        //注册一个联系人的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);

        //注册一个群消息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangeListener);

    }

    private final EMContactListener emContactListener = new EMContactListener() {
        @Override
        public void onContactAdded(String hxid) {
            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new UserInfo(hxid), true);
            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        @Override
        public void onContactDeleted(String hxid) {
            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(hxid);
            Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxid);
            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));

        }

        @Override
        public void onContactInvited(String hxid, String reason) {
            //数据库更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setUser(new UserInfo(hxid));
            invationInfo.setReason(reason);
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_INVITE);//新邀请
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        @Override
        public void onFriendRequestAccepted(String hxid) {
            //数据库更新的处理
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setUser(new UserInfo(hxid));
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_INVITE);//新邀请
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送邀请信息的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        @Override
        public void onFriendRequestDeclined(String username) {
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送邀请信息的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

    };

    private final EMGroupChangeListener emGroupChangeListener = new EMGroupChangeListener() {

        //收到群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //数据更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupName, groupId, inviter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到群申请通知
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
            //数据更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupName, groupId, applicant));
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到群申请被接受
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
            //数据更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(accepter);
            invationInfo.setGroup(new GroupInfo(groupName, groupId, accepter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到群邀请被拒绝
        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            //数据更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupName, groupId, decliner));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到群申请被接受
        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {
            //数据更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(invitee, groupId, reason));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            //数据更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(invitee, groupId, reason));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //删除用户
        @Override
        public void onUserRemoved(String groupId, String groupName) {


            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //删除群
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        //自动同意群邀请
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            //数据更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(inviteMessage);
            invationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //有成员被禁言
        @Override
        public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire) {

        }

        //有成员从禁言列表中移除，恢复发言权限
        @Override
        public void onMuteListRemoved(String groupId, List<String> mutes) {

        }

        //添加成员管理员权限
        @Override
        public void onAdminAdded(String groupId, String administrator) {

        }

        //转移群组所有者权限
        @Override
        public void onAdminRemoved(String groupId, String administrator) {

        }

        //转移群组所有者权限
        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {

        }

        //群组加入新成员事件
        @Override
        public void onMemberJoined(String groupId, String member) {

        }

        //群组成员主动退出事件
        @Override
        public void onMemberExited(String groupId, String member) {

        }

        //群公告更改事件
        @Override
        public void onAnnouncementChanged(String groupId, String announcement) {

        }

        //群组增加共享文件事件
        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {

        }

        //群组删除共享文件事件
        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {

        }
    };


}
