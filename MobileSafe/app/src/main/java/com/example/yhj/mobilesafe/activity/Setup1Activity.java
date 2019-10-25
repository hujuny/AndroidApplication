package com.example.yhj.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.yhj.mobilesafe.R;

/**
 * 第一个设置向导页面
 * */
public class Setup1Activity extends BaseSetup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showPreviousPage() {

    }

    @Override
    public void showNextPage() {//下一页
        startActivity(new Intent(Setup1Activity.this,Setup2Activity.class));
        finish();
        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);//进入和退出动画
    }


}
