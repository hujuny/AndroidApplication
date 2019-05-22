package com.example.yhj.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.service.KillProcessService;
import com.example.yhj.mobilesafe.utils.ServiceStatusUtils;
import com.example.yhj.mobilesafe.utils.SharedPreferencesUtils;

public class TaskManagerSettingActivity extends AppCompatActivity {

    private CheckBox cb_status_kill_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
        initUI();
    }



    private void initUI() {
        CheckBox cb_status = (CheckBox) findViewById(R.id.cb_status);
        //设置是否选中
        cb_status.setChecked(SharedPreferencesUtils.getBoolean(TaskManagerSettingActivity.this,"is_show_system",false));

        cb_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBoolean(TaskManagerSettingActivity.this,"is_show_system",isChecked);
            }
        });

        //定时清理进程
        cb_status_kill_process = (CheckBox) findViewById(R.id.cb_status_kill_process);
        final Intent intent = new Intent(TaskManagerSettingActivity.this, KillProcessService.class);
        cb_status_kill_process.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    startService(intent);
                }else {
                    stopService(intent);
                }
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        if (ServiceStatusUtils.isServiceRunning(TaskManagerSettingActivity.this,"com.example.yhj.mobilesafe.service.KillProcessService")){
            cb_status_kill_process.setChecked(true);
        }else {
            cb_status_kill_process.setChecked(false);
        }
    }
}
