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


public class LockFragment extends Fragment {


    private TextView tv_lock;
    private ListView list_view;
    private LockAdapter adapter;
    private List<AppInfo> lockLists;
    private AppLockDao dao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock, null);

        tv_lock = view.findViewById(R.id.tv_lock);
        list_view = view.findViewById(R.id.list_view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        List<AppInfo> appInfos = AppInfos.getAppInfos(getActivity());
        dao = new AppLockDao(getActivity());
        lockLists = new ArrayList<>();
        for (AppInfo appInfo:appInfos){
            if (dao.find(appInfo.getApkPackageName())){
                lockLists.add(appInfo);
            }
        }

        adapter = new LockAdapter();
        list_view.setAdapter(adapter);

    }

    private class LockAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            tv_lock.setText("已加锁("+lockLists.size()+")个");
            return lockLists.size();
        }

        @Override
        public Object getItem(int position) {
            return lockLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view ;
            ViewHolder holder = null;
            final AppInfo appInfo;

            if (convertView==null){
                holder=new ViewHolder();
                 view = View.inflate(getActivity(), R.layout.item_lock,null);
                holder.iv_icon=view.findViewById(R.id.iv_icon);
                holder.tv_name=view.findViewById(R.id.tv_name);
                holder.iv_lock=view.findViewById(R.id.iv_lock);

                view.setTag(holder);
            }else {
                view=convertView;
                 holder = (ViewHolder) view.getTag();
            }
             appInfo = lockLists.get(position);
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getApkName());

            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //位移动画
                    TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    animation.setDuration(3000);
                    view.startAnimation(animation);

                    new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            SystemClock.sleep(3000);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dao.delete(appInfo.getApkPackageName());
                                    lockLists.remove(position);
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
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }
}
