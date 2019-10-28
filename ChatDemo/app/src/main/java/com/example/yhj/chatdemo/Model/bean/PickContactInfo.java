package com.example.yhj.chatdemo.Model.bean;

/**
 * author : yhj
 * date   : 2019/10/22
 * desc   :选择联系人Bean类
 */
public class PickContactInfo {

    private UserInfo userInfo;//联系人
    private boolean isChecked;//是否被选择的标记

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public PickContactInfo(UserInfo userInfo, boolean isChecked) {
        this.userInfo = userInfo;
        this.isChecked = isChecked;
    }

    public PickContactInfo() {
    }
}
