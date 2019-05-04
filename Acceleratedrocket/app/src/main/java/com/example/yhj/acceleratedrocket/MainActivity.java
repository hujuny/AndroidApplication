package com.example.yhj.acceleratedrocket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startRocket(View v){//启动火箭
        startService(new Intent(this,RocketService.class));
        finish();
    }

    public void stopRocket(View v){//停止火箭
        startService(new Intent(this,RocketService.class));
        finish();
    }
}
