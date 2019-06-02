package com.example.yhj.mobilesafe.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class TrafficStatisticsActivity extends AppCompatActivity {

    private List<AppInfo> appInfos;
    private ListView list_view;
    private PackageInfo packageInfo;
    private PackageManager packageManager;
    private List<CacheInfo> cacheLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_statistics);

        initUI();
        initData();


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TrafficAdapter adapter = new TrafficAdapter();
            list_view.setAdapter(adapter);
        }
    };

    private void initData() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                //安装到手机上面的应用程序
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);


                for (PackageInfo packageInfo : installedPackages) {

                    getCacheSize(packageInfo);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();


    }

    private void getCacheSize(PackageInfo packageInfo) {
        CacheInfo cacheInfo = new CacheInfo();
        Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
        cacheInfo.icon = icon;
        String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
        cacheInfo.appName = appName;
        /*// 1.获取一个包管理器
        PackageManager pm = getPackageManager();*/

        // 2.遍历手机操作系统，获取所有的应用程序的uid
        List<ApplicationInfo> appLicationInfos = packageManager.getInstalledApplications(0);
        //下载
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        cacheInfo.mobileRxBytes = mobileRxBytes;
        //上传
        long mobileTxBytes = TrafficStats.getMobileTxBytes();
        cacheInfo.mobileTxBytes = mobileTxBytes;

        cacheLists.add(cacheInfo);
    }

    static class CacheInfo {
        Drawable icon;
        String appName;
        long  mobileRxBytes;
        long mobileTxBytes;
        /*long uidTxBytes;
        long uidRxBytes;*/
    }

    private void initUI() {
        list_view = (ListView) findViewById(R.id.list_view);
        cacheLists = new ArrayList<>();
        packageManager = getPackageManager();


    }

    private class TrafficAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cacheLists.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final CacheInfo cacheInfo = new CacheInfo();
            View view = null;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(TrafficStatisticsActivity.this, R.layout.item_trafice_statistics, null);
                holder = new ViewHolder();

                holder.ivIcon = view.findViewById(R.id.iv_app_icon);
                holder.tvName = view.findViewById(R.id.tv_app_name);
                holder.tvUpload = view.findViewById(R.id.tv_upload);
                holder.tvDownload = view.findViewById(R.id.tv_download);
                holder.tvSum = view.findViewById(R.id.tv_sum);

                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            holder.ivIcon.setImageDrawable(cacheLists.get(position).icon);
            holder.tvName.setText(cacheLists.get(position).appName);


            holder.tvUpload.setText("上传 " + Formatter.formatFileSize(TrafficStatisticsActivity.this, cacheLists.get(position).mobileTxBytes));
            holder.tvDownload.setText("下载 " + Formatter.formatFileSize(TrafficStatisticsActivity.this, cacheLists.get(position).mobileRxBytes));
            String s = Formatter.formatFileSize(TrafficStatisticsActivity.this, cacheLists.get(position).mobileTxBytes + cacheLists.get(position).mobileRxBytes);
            holder.tvSum.setText(s);

            return view;
        }
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvUpload;
        TextView tvDownload;
        TextView tvSum;
    }
}
