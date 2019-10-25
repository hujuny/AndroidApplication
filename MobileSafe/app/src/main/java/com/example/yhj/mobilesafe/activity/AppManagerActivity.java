package com.example.yhj.mobilesafe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.bean.AppInfo;
import com.example.yhj.mobilesafe.engine.AppInfos;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AdapterView.OnItemClickListener;


public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;

    @ViewInject(R.id.tv_rom)
    private TextView tvRom;
    @ViewInject(R.id.tv_sd)
    private TextView tvSD;
    @ViewInject(R.id.list_View)
    private ListView listView;
    @ViewInject(R.id.tv_app)
    private TextView tvApp;

    private PopupWindow popUpWindow;
    private AppInfo clickInfo;
    private AppManagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            adapter = new AppManagerAdapter();
            listView.setAdapter(adapter);
            super.handleMessage(msg);
        }
    };

    private void initData() {

        new Thread() {
            @Override
            public void run() {
                //获取到所有安装到手机上面的应用程序
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);
                //appInfos集合拆成用户程序集合和系统程序集合
                //用户程序的集合
                userAppInfos = new ArrayList<>();
                //系统程序的集合
                systemAppInfos = new ArrayList<>();

                for (AppInfo appInfo : appInfos) {
                    //  用户程序
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
                super.run();
            }
        }.start();
    }

    private void initUI() {
        ViewUtils.inject(this);//xUtils框架，相当于findViewById

        long Rom = Environment.getDataDirectory().getFreeSpace();//获取手机内存剩余空间
        long SD = Environment.getExternalStorageDirectory().getFreeSpace();//获取手机sd卡剩余空间

        tvRom.setText("内存可用：" + Formatter.formatFileSize(this, Rom));
        tvSD.setText("sd卡可用：" + Formatter.formatFileSize(this, SD));

        UninstallReceiver receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * @param view
             * @param firstVisibleItem 第一次条目可见的位置
             * @param visibleItemCount 一共可以展示多少条目
             * @param totalItemCount 总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                popupWindowDismiss();

                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size() + 1) {
                        tvApp.setText("系统程序(" + systemAppInfos.size() + ")");
                    } else {
                        tvApp.setText("用户程序(" + userAppInfos.size() + ")");
                    }
                }
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取到当前点击的的item对象
                Object obj = listView.getItemAtPosition(position);
                if (obj != null && obj instanceof AppInfo) {

                    clickInfo = (AppInfo) obj;

                    View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);

                    LinearLayout llUninstall = contentView.findViewById(R.id.ll_uninstall);
                    LinearLayout llDetail = contentView.findViewById(R.id.ll_detail);
                    LinearLayout llStart = contentView.findViewById(R.id.ll_start);
                    LinearLayout llShare = contentView.findViewById(R.id.ll_share);

                    llDetail.setOnClickListener(AppManagerActivity.this);
                    llUninstall.setOnClickListener(AppManagerActivity.this);
                    llShare.setOnClickListener(AppManagerActivity.this);
                    llStart.setOnClickListener(AppManagerActivity.this);

                    popupWindowDismiss();
                    //-2表示包裹内容
                    popUpWindow = new PopupWindow(contentView, -2, -2);
                    //需要注意，popupwindow必须设置背景，不然没有动画
                    popUpWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    int[] location = new int[2];
                    //获取view展示到窗体的位置
                    view.getLocationInWindow(location);
                    popUpWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 70, location[1]);

                    //缩放动画；
                    //Animation.RELATIVE_TO_SELF(相对于自身)、Animation.RELATIVE_TO_PARENT(相对于父控件(容器)）。
                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(2000);
                    contentView.startAnimation(sa);
                }
            }
        });
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_share://分享
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.SUBJECT", "分享");
                shareIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + clickInfo.getApkName() + "下载地址:" + "https://play.google.com/store/apps/details?id=" + clickInfo.getApkPackageName());
                this.startActivity(Intent.createChooser(shareIntent, "分享"));
                popupWindowDismiss();
                break;
            case R.id.ll_detail://详情
                Intent detailIntent = new Intent();
                detailIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detailIntent.addCategory(Intent.CATEGORY_DEFAULT);
                detailIntent.setData(Uri.parse("package:" + clickInfo.getApkPackageName()));
                startActivity(detailIntent);
                break;
            case R.id.ll_start://运行
                Intent startLocalIntent = this.getPackageManager().getLaunchIntentForPackage(clickInfo.getApkPackageName());
                startActivity(startLocalIntent);
                popupWindowDismiss();
                break;
            case R.id.ll_uninstall://卸载

                Intent uninstallLocalIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + clickInfo.getApkPackageName()));
                startActivity(uninstallLocalIntent);
                popupWindowDismiss();
                break;

        }
    }

    /**
     * 取消对话框
     */
    private void popupWindowDismiss() {
        if (popUpWindow != null && popUpWindow.isShowing()) {
            popUpWindow.dismiss();
            popUpWindow = null;
        }
    }


    private class AppManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userAppInfos.size() + 1) {
                return null;
            }
            AppInfo appInfo;
            if (position < userAppInfos.size() + 1) {
                //把多出来的条目减掉
                appInfo = userAppInfos.get(position - 1);
            } else {
                int location = userAppInfos.size() + 2;
                appInfo = systemAppInfos.get(position - location);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //如果当前的position是0，表示应用程序
            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("用户程序(" + userAppInfos.size() + ")");
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                return textView;

            } else if (position == userAppInfos.size() + 1) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("系统程序(" + systemAppInfos.size() + ")");
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                return textView;
            }

            AppInfo appInfo;

            if (position < userAppInfos.size() + 1) {
                //把多出来的条目减掉
                appInfo = userAppInfos.get(position - 1);
            } else {
                int location = userAppInfos.size() + 2;
                appInfo = systemAppInfos.get(position - location);
            }
            View view = null;
            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();

            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);

                holder = new ViewHolder();

                holder.ivIcon = view.findViewById(R.id.iv_icon);
                holder.tvApkSize = view.findViewById(R.id.tv_apk_size);
                holder.tvLocation = view.findViewById(R.id.tv_location);
                holder.tvName = view.findViewById(R.id.tv_name);
                view.setTag(holder);
            }


            holder.ivIcon.setBackground(appInfo.getIcon());
            holder.tvName.setText(appInfo.getApkName());
            holder.tvApkSize.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));
            holder.tvName.setText(appInfo.getApkName());

            if (appInfo.isRom()) {
                holder.tvLocation.setText("手机内存");
            } else {
                holder.tvLocation.setText("外部内存");

            }
            return view;
        }
    }


    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvApkSize;
        TextView tvLocation;
        TextView tvName;
    }

    @Override
    protected void onDestroy() {
        popupWindowDismiss();
        super.onDestroy();
    }


}
