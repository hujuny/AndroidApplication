package com.example.yhj.chatdemo.Controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.yhj.chatdemo.Model.bean.PickContactInfo;
import com.example.yhj.chatdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author : yhj
 * date   : 2019/10/22
 * desc   :选择联系人适配器
 */
public class PickContactAdapter extends BaseAdapter {

    private Context mContext;
    private List<PickContactInfo> mPicks = new ArrayList<>();
    private List<String> mExistMembers = new ArrayList<>();//保存群组中已经存在的成员集合

    public PickContactAdapter(Context context, List<PickContactInfo> picks, List<String> existMembers) {
        mContext = context;
        if (picks != null && picks.size() >= 0) {
            mPicks.clear();
            mPicks.addAll(picks);

        }
        //加载已经存在的成员集合
        mExistMembers.clear();
        mExistMembers.addAll(existMembers);
    }

    @Override
    public int getCount() {
        return mPicks == null ? 0 : mPicks.size();
    }

    @Override
    public Object getItem(int i) {
        return mPicks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_pick_contact, null);
            holder.cb = convertView.findViewById(R.id.cb_item_pick_contacts);
            holder.tv_name = convertView.findViewById(R.id.tv_item_pick_contacts_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PickContactInfo pickContactInfo = mPicks.get(i);
        holder.tv_name.setText(pickContactInfo.getUserInfo().getName());
        holder.cb.setChecked(pickContactInfo.isChecked());

        if (mExistMembers.contains(pickContactInfo.getUserInfo().getHxId())) {
            holder.cb.setChecked(true);
            pickContactInfo.setChecked(true);
        }
        return convertView;
    }

    //获取选择的联系人
    public List<String> getPickContacts() {
        List<String> picks = new ArrayList<>();
        for (PickContactInfo pick : mPicks) {
            if (pick.isChecked()) {
                picks.add(pick.getUserInfo().getName());
            }
        }
        return picks;
    }

    public class ViewHolder {
        private CheckBox cb;
        private TextView tv_name;
    }
}
