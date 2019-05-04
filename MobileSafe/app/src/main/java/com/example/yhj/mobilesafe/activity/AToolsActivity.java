package com.example.yhj.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.yhj.mobilesafe.R;

/**
 * 高级设置页面
 * */
public class AToolsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /*
    * 归属地查询
    * */
    public void numberAddressQuery(View v){
        startActivity(new Intent(AToolsActivity.this,AddressActivity.class));
    }
}
