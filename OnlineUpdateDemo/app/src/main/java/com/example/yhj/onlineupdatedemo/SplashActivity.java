package com.example.yhj.onlineupdatedemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_URL_ERROR = 1;
    private static final int CODE_NET_ERROR = 2;
    private static final int CODE_JSON_ERROR = 3;
    private static final int CODE_ENTER_HOME = 4;

    private TextView tvProgress;//下载进度

    //服务器返回的信息
    private String mVersionName;//版本名
    private String mDesc;//版本描述
    private int mVersionCode;//版本号
    private String mDownloadUrl;//下载链接
    private ProgressBar pbNumber;
    private RelativeLayout rlRoot;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    rlRoot.setVisibility(View.VISIBLE);
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
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //权限处理
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(SplashActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this, permissions, 1);
            }
        }

        pbNumber = findViewById(R.id.pb_number);
        tvProgress = findViewById(R.id.tv_progress);
        rlRoot = findViewById(R.id.rl_root);

        checkVersion();
    }

    /**
     * 从服务器获取版本信息进行校验
     */
    private void checkVersion() {
        final long startTimes = System.currentTimeMillis();
        new Thread() {//启动子线程异步加载数据
            @Override
            public void run() {
                Message msg = Message.obtain();//拿到一个消息
                HttpURLConnection conn = null;
                try {
                    //请求网络
                    URL url = new URL("http://192.168.10.198:8080/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");//设置请求方法
                    conn.setConnectTimeout(5000);//设置连接超时
                    conn.setReadTimeout(5000);//设置响应超时
                    conn.connect();//连接服务器
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String result = readFromStream(inputStream);
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
                    if (usedTimes < 3000) {
                        try {//由于网速过快，让其休眠一段时间，保证显示闪屏页
                            Thread.sleep(3000 - usedTimes);
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

    /**
     * 读取流
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static String readFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        String result = out.toString();
        in.close();
        out.close();

        return result;
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
            Log.i("yhj", "版本号" + versionCode);
            return versionCode;
        } catch (PackageManager.NameNotFoundException | NoSuchMethodError e) {//没有包名会走此异常
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 升级对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本" + mVersionName);
        builder.setMessage(mDesc);
        builder.setNegativeButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                download();
            }
        });
        builder.setPositiveButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });

        //设置取消侦听,用户点击返回键触发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();//显示对话框
    }

    /**
     * 下载新版apk，xUtils3框架
     */
    public void download() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            tvProgress.setVisibility(View.VISIBLE);//显示下载进度
            String target = Environment.getExternalStorageDirectory() + "/update.apk";
            //xUtils,可在主线程中加载。框架已封装好
            HttpUtils utils = new HttpUtils();
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
                @Override//下载成功
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //判断是否是AndroidN以及更高的版本
                    //注：Android 7.0以上版本跳转，要使用FileProvider.getUriForFile(),在注册文件里注册provider。
                    // 7.0以下继续使用Uri.fromFile()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri contentUri = FileProvider.getUriForFile(SplashActivity.this, "com.example.yhj.onlineupdatedemo.fileProvider", responseInfo.result);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else {
                        intent.setDataAndType(Uri.fromFile(responseInfo.result), "application/vnd.android.package-archive");
                    }
                    startActivityForResult(intent, 0);
                }

                @Override//下载失败
                public void onFailure(HttpException e, String s) {
                    //System.out.println(mDownloadUrl);
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    Log.i("yhj", "失败原因:" + e.toString());
                    enterHome();
                }

                @Override//下载进度
                public void onLoading(long total, long current, boolean isUploading) {
                    //System.out.println(current*100/total);
                    super.onLoading(total, current, isUploading);
                    int max = Integer.parseInt(String.valueOf(total));
                    pbNumber.setMax(max);
                    int present = Integer.parseInt(String.valueOf(current));
                    pbNumber.setProgress(present);
                    tvProgress.setText("下载进度" + current * 100 / total + "%");
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, "抱歉，没有内存卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 进入主页面
     */
    public void enterHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();//直接退出
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(SplashActivity.this, "请授予该应用权限！", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }


}
