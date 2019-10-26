package com.example.yhj.onlineupdatedemo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        TextView tvVersion = findViewById(R.id.tv_version);

        getVersionCode();
        tvVersion.setText("当前版本号：" + getVersionCode());
    }

    /**
     * 获取本地版本号
     *
     * @return
     */
    private long getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);//获取包信息
            long versionCode = packageInfo.getLongVersionCode();

            return versionCode;
        } catch (PackageManager.NameNotFoundException | NoSuchMethodError e) {//没有包名会走此异常
            e.printStackTrace();
        }
        return -1;
    }


}
