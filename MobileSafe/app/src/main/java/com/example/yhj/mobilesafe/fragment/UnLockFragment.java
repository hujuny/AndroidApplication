package com.example.yhj.mobilesafe.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.bean.AppInfo;
import com.example.yhj.mobilesafe.db.AppLockDao;
import com.example.yhj.mobilesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;


public class UnLockFragment extends Fragment {

    private ListView list_view;
    private List<AppInfo> appInfos;
    private TextView tv_unlock;
    private List<AppInfo> unLockList;
    private AppLockDao dao;
    private UnLockAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_un_lock, null);
        list_view = view.findViewById(R.id.list_view);
        tv_unlock =  view.findViewById(R.id.tv_unlock);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appInfos = AppInfos.getAppInfos(getActivity());

        dao = new AppLockDao(getActivity());
        unLockList = new ArrayList<>();
        for (AppInfo appInfo: appInfos){
            //判断当前的程序是否在程序锁是数据库里面
            if (dao.find(appInfo.getApkPackageName())){
                
            }else {
                //如果不在说明没有在加锁数据库里面
                unLockList.add(appInfo);
            }
        }

        adapter = new UnLockAdapter();
        list_view.setAdapter(adapter);
    }

    private class UnLockAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            tv_unlock.setText("未加锁"+"("+unLockList.size()+")"+"个");
            return unLockList.size();
        }

        @Override
        public Object getItem(int position) {
            return unLockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final View view;
            ViewHolder holder=null;
            final AppInfo appInfo ;

            if (convertView==null){
                 view = View.inflate(getActivity(), R.layout.item_unlock, null);
                holder=new ViewHolder();
                holder.iv_icon=  view.findViewById(R.id.iv_icon);
                holder.iv_unlock=view.findViewById(R.id.iv_unlock);
                holder.tv_name=view.findViewById(R.id.tv_name);
                view.setTag(holder);
            }else {
                view=convertView;
                holder = (ViewHolder) view.getTag();
            }
            appInfo=unLockList.get(position);

            holder.iv_icon.setImageDrawable(unLockList.get(position).getIcon());
            holder.tv_name.setText(unLockList.get(position).getApkName());

            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //初始化一个位移动画
                    TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
                    animation.setDuration(3000);
                    view.startAnimation(animation);

                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            //动画不在主线程中，需要线程等待
                            SystemClock.sleep(3000);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //添加到加锁数据库
                                    dao.add(appInfo.getApkPackageName());
                                    //移除未加锁显示的app
                                    unLockList.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();
                }
            });
            return view;
        }
    }
    class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_unlock;
    }
}
