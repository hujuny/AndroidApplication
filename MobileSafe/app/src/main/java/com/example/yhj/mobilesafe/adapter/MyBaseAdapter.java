package com.example.yhj.mobilesafe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import static android.R.id.list;

/**
 * Created by yhj on 2019/5/6.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    public List<T> lists;
    public Context mConText;

    public MyBaseAdapter(List<T> lists, Context mConText) {
        this.lists = lists;
        this.mConText = mConText;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
