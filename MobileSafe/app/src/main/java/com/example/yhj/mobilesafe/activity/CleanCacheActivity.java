package com.example.yhj.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.utils.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CleanCacheActivity extends AppCompatActivity {

    private PackageManager packageManager;
    private List<CacheInfo> cacheLists;
    private ListView list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);

        initUI();
    }

    private void initUI() {

        list_view = (ListView) findViewById(R.id.list_view);
        //缓存的集合
        cacheLists = new ArrayList<>();
        packageManager = getPackageManager();

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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            CacheAdapter adapter = new CacheAdapter();
            list_view.setAdapter(adapter);
            super.handleMessage(msg);

        }
    };

    private class CacheAdapter extends BaseAdapter {
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            final CacheInfo cacheInfo = new CacheInfo();

            View view = null;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(CleanCacheActivity.this, R.layout.item_clean_cache, null);
                holder = new ViewHolder();
                holder.icon = view.findViewById(R.id.iv_icon);
                holder.appName = view.findViewById(R.id.tv_name);
                holder.cacheSize = view.findViewById(R.id.tv_cache_size);
                holder.del=view.findViewById(R.id.iv_del);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.icon.setImageDrawable(cacheLists.get(position).icon);
            holder.appName.setText(cacheLists.get(position).appName);
            holder.cacheSize.setText("缓存大小:" + Formatter.formatFileSize(CleanCacheActivity.this, cacheLists.get(position).cacheSize));

           holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent();
                    detailIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    detailIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    detailIntent.setData(Uri.parse("package:" + cacheLists.get(position).packageName));

                    startActivity(detailIntent);
                }
            });
            return view;
        }
    }

    class ViewHolder {
        ImageView icon;
        TextView appName;
        TextView cacheSize;
        ImageView del;
    }

    //获取到缓存大大小
    private void getCacheSize(PackageInfo packageInfo) {
        try {
            //Class<?> clazz = getClassLoader().loadClass("packageManager");
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            //第一个参数表示当前的方法由于调用的；第二个参数表示包名
            method.invoke(packageManager, packageInfo.applicationInfo.packageName, new MyIPackageStatsObserver(packageInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

        private PackageInfo packageInfo;

        private MyIPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
        }


        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            //获取到当前手机应用的缓存大小
            long cacheSize = pStats.cacheSize;
            if (cacheSize > 0) {
                CacheInfo cacheInfo = new CacheInfo();
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                cacheInfo.icon = icon;
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                cacheInfo.appName = appName;
                cacheInfo.cacheSize = cacheSize;
                String packageName = packageInfo.applicationInfo.packageName;
                cacheInfo.packageName = packageName;
                cacheLists.add(cacheInfo);

                Log.d("TAG", "onClick:报上名来over "+cacheInfo.packageName);
            }
        }
    }

    static class CacheInfo {
        Drawable icon;
        long cacheSize;
        String appName;
        String packageName;
    }

    /**
     * 全部清理
     * @param v
     */
    public void  clearAll(View v){

        //获取当前应用程序内的所有方法
        Method[] methods = PackageManager.class.getMethods();
        for(Method method:methods){
            if (method.getName().equals("freeStorageAndNotify")){
                try {
                    method.invoke(packageManager, Integer.MAX_VALUE, new MyIPackageDataObserver());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

            }

        }
        ToastUtils.showToast(CleanCacheActivity.this,"清理成功");
    }

    private class MyIPackageDataObserver extends IPackageDataObserver.Stub{
        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }
}
