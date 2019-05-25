package com.example.yhj.mobilesafe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.db.AntivirusDao;
import com.example.yhj.mobilesafe.utils.MD5Utils;

import java.util.List;

public class AntivirusActivity extends AppCompatActivity {

    private static final int BEGING = 1;
    private static final int SCANNING = 2;
    private static final int FINISH = 3;
    private TextView tv_init_virus;
    private ImageView iv_scanning;
    private ProgressBar pb;
    private LinearLayout ll_content;
    private Message message;
    private ScrollView scrollView;
    private ScanInfo scanInfo;
    private Button btn_ok;
    private Button btn_cancel;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);

        initUI();
        initData();
    }

    /**
     * 卸载广播
     */
    private class UninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("AppManagerActivity", "onReceive: 接收到卸载的广播了");
            initData();
        }
    }

    private void initUI() {

        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        tv_init_virus = (TextView) findViewById(R.id.tv_init_virus);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        scrollView = (ScrollView) findViewById(R.id.scrollView);


        UninstallReceiver receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver,filter);
        /*
         * fromDegrees 第一个参数表示开始的角度
         * toDegrees 第二个参数表示结束的角度
         * pivotXType第三个参数表示参照自己，初始化旋转动画
         * pivotXValue
         * pivotYType
         * pivotYValue
         */
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //设置动画的时间
        rotateAnimation.setDuration(5000);
        //设置无限循环
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        //开始动画
        iv_scanning.setAnimation(rotateAnimation);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BEGING:
                    tv_init_virus.setText("初始化八核引擎");
                    break;
                case SCANNING:
                    //病毒扫描中
                    TextView textView = new TextView(AntivirusActivity.this);
                    scanInfo = (ScanInfo) msg.obj;
                    //如果是true表示有病毒
                    if (scanInfo.desc){
                        textView.setTextColor(Color.RED);
                        textView.setText(scanInfo.appName+"有病毒");
                        //卸载病毒软件
                        virusUnload();
                    }else {
                        textView.setTextColor(Color.BLACK);
                        textView.setText(scanInfo.appName+"扫描安全");
                    }
                    ll_content.addView(textView,0);
                    //自动滚动
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            //一直往下面滚动
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }

                    });
                    break;
                case FINISH:
                    //当扫描结束的时候，停止动画
                    iv_scanning.clearAnimation();
                    scanInfo = (ScanInfo) msg.obj;


                    break;

            }
        }
    };

    /**
     * 卸载病毒软件
     */
    private void virusUnload() {
       //弹出卸载对话框

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("病毒警告");
        builder.setMessage("有病毒软件，请尽快卸载！！！");
        //builder.setCancelable(false);//设置返回键无效,用户体验差。
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent uninstallLocalIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:com.example.yhj.mobilesafe.test"));
                startActivity(uninstallLocalIntent);
            }
        });

        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /*
        * 设置取消侦听,用户点击返回键触发
        * */
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.show();//显示对话框

    }


    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                message = Message.obtain();
                message.what = BEGING;

                PackageManager packageManager = getPackageManager();
                //获取到所有安装的应用程序
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                //返回手机安装了多少应用程序
                int size = installedPackages.size();
                //设置滚动条的最大值
                pb.setMax(size);

                int progress = 0;

                for (PackageInfo packageInfo : installedPackages) {
                    ScanInfo scanInfo = new ScanInfo();

                    //获取到当前手机上面app名字
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    scanInfo.appName = appName;
                    String packageName = packageInfo.applicationInfo.packageName;
                    scanInfo.packageName = packageName;

                    //首先获取到每个程序的目录
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    //获取到到文件的md5
                    String md5 = MD5Utils.getFileMd5(sourceDir);
                    //判断当前的文件是否在病毒库里面
                    String desc = AntivirusDao.checkFileVirus(md5);

                    //desc==null表示没有病毒
                    if (desc == null) {
                        scanInfo.desc = false;
                    } else {
                        scanInfo.desc = true;
                    }
                    progress++;
                    SystemClock.sleep(100);
                    pb.setProgress(progress);

                    message = Message.obtain();
                    message.what = SCANNING;
                    message.obj = scanInfo;
                    handler.sendMessage(message);

                }
                message=Message.obtain();
                message.what=FINISH;
                message.obj = scanInfo;
                handler.sendMessage(message);


            }
        }.start();


    }


    static class ScanInfo {
        boolean desc;
        String appName;
        String packageName;
    }
}
