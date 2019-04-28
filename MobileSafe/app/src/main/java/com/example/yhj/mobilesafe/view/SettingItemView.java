package com.example.yhj.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;


/**
 * 设置中心的自定义组合控件
 */

public class SettingItemView extends RelativeLayout {

    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox cbStatus;

    private String mTitle;
    private String mDescOn;
    private String mDescOff;
    protected static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";

    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //根据属性名称，获取属性的值
        mTitle = attrs.getAttributeValue(NAMESPACE, "title");
        mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
        mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
        initView();

    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //将自定义的布局文件设置给当前的settingItemView
        View.inflate(getContext(), R.layout.view_setting_item, this);
        tvTitle = findViewById(R.id.tv_title);
        tvDesc = findViewById(R.id.tv_desc);
        cbStatus = findViewById(R.id.cb_status);
        setTitle(mTitle);// 设置标题
    }

    private void setTitle(String title) {
        tvTitle.setText(title);
    }

    private void setDesc(String desc) {
        tvDesc.setText(desc);
    }

    /*
    * 返回勾选状态
    * */
    public boolean isChecked() {
        return cbStatus.isChecked();
    }

    public void setChecked(boolean check) {
        cbStatus.setChecked(check);
        //根据选择的状态，更新文本描述
        if (check) {
            setDesc(mDescOff);
        } else {
            setDesc(mDescOn);

        }
    }
}
