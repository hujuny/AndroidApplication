package com.example.yhj.mobilesafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhj.mobilesafe.activity.HomeActivity;
import com.example.yhj.mobilesafe.bean.Virus;
import com.example.yhj.mobilesafe.db.AntivirusDao;
import com.example.yhj.mobilesafe.utils.StreamUtils;
import com.example.yhj.mobilesafe.utils.ToastUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

/*
* 闪屏页面(版本更新)
* */

public class SplashActivity extends AppCompatActivity {



    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_URL_ERROR = 1;
    private static final int CODE_NET_ERROR = 2;
    private static final int CODE_JSON_ERROR = 3;
    private static final int CODE_ENTER_HOME = 4;

    private TextView tvVersion;
    private TextView tvProgress;//下载进度

    private SharedPreferences mPref;//自动更新

    private RelativeLayout rlRoot;

    //服务器返回的信息
    private String mVersionName;//版本名
    private String mDesc;//版本描述
    private int mVersionCode;//版本号
    private String mDownloadUrl;//下载链接

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
            }
        }
    };
    private AntivirusDao dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);

        tvVersion.setText("版本号:" + getVersionName());

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        copyDB("address.db");
        copyDB("antivirus.db");

        //创建快捷方式
        createShortcut();
        //更新病毒数据库
        updateVirus();

        //判断是否需要自动更新
        boolean autoUpdate = mPref.getBoolean("auto_update", true);
        if (autoUpdate) {
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);//设置延迟两秒发送消息
        }

        //设置闪屏页渐变动画效果
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(2000);
        rlRoot.startAnimation(anim);


    }

    /**
     * 创建快捷方式
     */
    private void createShortcut() {

        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //如果设置为true，表示可以创建重复的快捷方式
        intent.putExtra("duplicate",false);

        /**
         * 1 干什么事情
         * 2 你叫什么名字
         * 3你长成什么样子
         */
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.mipmap.bind));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机卫士");

        /**
         * 干什么事情
         * 这个地方不能使用显示意图
         * 必须使用隐式意图，因为桌面不清楚this是什么东西
         */
        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction("aaa");
        shortcutIntent.addCategory("android.intent.category.DEFAULT");

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortcutIntent);
        sendBroadcast(intent);
    }

    /*
    * 获取版本名以便输出到闪屏页
    * */
    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);//获取包信息
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {//没有包名会走此异常
            e.printStackTrace();
        }
        return "";
    }

    /*
   * 获取本地版本号
   * */
    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);//获取包信息
            int versionCode = packageInfo.versionCode;

            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {//没有包名会走此异常
            e.printStackTrace();
        }
        return -1;
    }

    /*
    * 从服务器获取版本信息进行校验
    * */
    private void checkVersion() {
        final long startTimes = System.currentTimeMillis();
        new Thread() {//启动子线程异步加载数据
            @Override
            public void run() {

                Message msg = Message.obtain();//拿到一个消息
                HttpURLConnection conn = null;
                try {
                    /*
                     * 请求网络
                     */
                    URL url = new URL("http://172.24.38.95:8080/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");//设置请求方法
                    conn.setConnectTimeout(5000);//设置连接超时
                    conn.setReadTimeout(5000);//设置响应超时
                    conn.connect();//连接服务器
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String result = StreamUtils.readFromStream(inputStream);

                        //解析json
                        JSONObject jo = new JSONObject(result);
                        mVersionName = jo.getString("versionName");
                        mDesc = jo.getString("description");
                        mVersionCode = jo.getInt("versionCode");
                        mDownloadUrl = jo.getString("downloadUrl");

                        if (mVersionCode > getVersionCode()) {//服务器版本号大于本地版本号，可更新。弹出升级对话框
                            msg.what = CODE_UPDATE_DIALOG;
                        } else {
                            //已是最新版本
                            msg.what = CODE_ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {//url错误异常
                    msg.what = CODE_URL_ERROR;
                    enterHome();
                    e.printStackTrace();
                } catch (IOException e) {//网络错误异常
                    msg.what = CODE_NET_ERROR;
                    enterHome();
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = CODE_JSON_ERROR;
                    enterHome();
                    e.printStackTrace();
                } finally {
                    long endTimes = System.currentTimeMillis();
                    long usedTimes = endTimes - startTimes;//访问网络花费的时间
                    if (usedTimes < 2000) {
                        try {//由于网速过快，让其休眠一段时间，保证显示闪屏页
                            Thread.sleep(2000 - usedTimes);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                    if (conn != null) {
                        conn.disconnect();//关闭网络连接
                    }
                }
            }


        }.start();
    }

    /*
    * 升级对话框
    * */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本" + mVersionName);
        builder.setMessage(mDesc);
        //builder.setCancelable(false);//设置返回键无效,用户体验差。
        builder.setNegativeButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(SplashActivity.this,"升级成功",Toast.LENGTH_SHORT).show();
                download();
            }
        });

        builder.setPositiveButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });

        /*
        * 设置取消侦听,用户点击返回键触发
        * */
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();//显示对话框
    }

    /*
    * 进入主页面
    * */

    public void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();//直接退出
    }

    /*
    * 下载新版apk，借用xutils框架
    * */
    public void download() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            tvProgress.setVisibility(View.VISIBLE);//显示下载进度
            String target = Environment.getExternalStorageDirectory() + "/update.apk";
            //xUtils,可在主线程中加载。框架已封装好
            HttpUtils utils = new HttpUtils();
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
                @Override//下载成功
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //Toast.makeText(SplashActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
                    //跳转到安装页面
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result), "application/vnd.android.package-archive");
                    //startactivity(intent);
                    startActivityForResult(intent, 0);
                }

                @Override//下载失败
                public void onFailure(HttpException e, String s) {
                    //System.out.println(mDownloadUrl);
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    enterHome();
                }

                @Override//下载进度
                public void onLoading(long total, long current, boolean isUploading) {
                    //System.out.println(current*100/total);
                    super.onLoading(total, current, isUploading);
                    tvProgress.setText("下载进度" + current * 100 / total + "%");
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, "抱歉，没有内存卡", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * 如果用户取消安装，会返回结果，回调onActivityResult
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    * 拷贝数据库
    * */
    private void copyDB(String dbName) {
        File destFile = new File(getFilesDir(), dbName);// 要拷贝的目标地址
        if (destFile.exists()) {
            return;
        }
        FileOutputStream out = null;
        InputStream in = null;
        try {
            in = getAssets().open(dbName);
            out = new FileOutputStream(destFile);
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null && out != null) {
                    in.close();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新数据库
     */
    private void updateVirus(){
        dao = new AntivirusDao();
        //联网从服务器获取最新的数据的md5的特征码
        HttpUtils utils = new HttpUtils();
        utils.send(HttpMethod.GET, "http://172.28.145.103:8080//virus.json", new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                //解析json
                try {
                    /*JSONObject jsonObject = new JSONObject(responseInfo.result);
                    String md5 = jsonObject.getString("md5");
                    String desc = jsonObject.getString("desc");
                    AntivirusDao.addVirus(md5,desc);*/

                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    Gson gson = new Gson();
                    Virus virus=gson.fromJson(responseInfo.result, Virus.class);
                    AntivirusDao.addVirus(virus.md5,virus.desc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                ToastUtils.showToast(SplashActivity.this,"病毒数据库更新失败，请检查你的网络！");
            }
        });
    }

}
