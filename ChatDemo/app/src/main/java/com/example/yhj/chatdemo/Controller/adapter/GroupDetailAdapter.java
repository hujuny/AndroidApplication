package com.example.yhj.chatdemo.Controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yhj.chatdemo.Model.bean.UserInfo;
import com.example.yhj.chatdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author : yhj
 * date   : 2019/10/28
 * desc   :群详情适配器
 */
public class GroupDetailAdapter extends BaseAdapter {

    private Context mContext;
    private boolean mIsCanModify;//是否允许添加和删除群成员
    private List<UserInfo> mUsers = new ArrayList<>();

    //获取当前的删除模式
    public boolean ismIsDeleteModel() {
        return mIsDeleteModel;
    }

    public void setmIsDeleteModel(boolean mIsDeleteModel) {
        this.mIsDeleteModel = mIsDeleteModel;
    }

    private boolean mIsDeleteModel;//删除模式
    private OnGroupDetailListener mOnGroupDetailListener;

    public GroupDetailAdapter(Context context, boolean isCanModify, OnGroupDetailListener onGroupDetailListener) {
        mContext = context;
        mIsCanModify = isCanModify;
        mOnGroupDetailListener = onGroupDetailListener;
    }


    public void Refresh(List<UserInfo> users) {
        if (users != null && users.size() >= 0) {
            mUsers.clear();
            //添加加号和减号
            initUsers();
            mUsers.addAll(0, users);
        }

        notifyDataSetChanged();
    }

    private void initUsers() {
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");
        mUsers.add(delete);
        mUsers.add(0, add);
    }

    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return mUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = View.inflate(mContext, R.layout.item_groupdetail, null);
            viewHolder.name = view.findViewById(R.id.tv_member_name);
            viewHolder.delete = view.findViewById(R.id.iv_member_delete);
            viewHolder.photo = view.findViewById(R.id.iv_member_photo);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        UserInfo userInfo = mUsers.get(i);
        if (mIsCanModify) {
            if (i == getCount() - 1) {//减号
                if (mIsDeleteModel) {//删除模式
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                    viewHolder.photo.setImageResource(R.mipmap.em_smiley_minus_btn_pressed);
                    viewHolder.delete.setVisibility(View.GONE);
                    viewHolder.name.setVisibility(View.INVISIBLE);
                }
            } else if (i == getCount() - 2) {//加号
                if (mIsDeleteModel) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                    viewHolder.name.setVisibility(View.INVISIBLE);
                    viewHolder.photo.setImageResource(R.mipmap.em_smiley_add_btn_pressed);
                    viewHolder.delete.setVisibility(View.GONE);
                }
            } else {//群成员
                view.setVisibility(View.VISIBLE);
                viewHolder.name.setVisibility(View.VISIBLE);
                viewHolder.name.setText(userInfo.getName());
                viewHolder.photo.setImageResource(R.mipmap.em_default_avatar);
                if (mIsDeleteModel) {
                    viewHolder.delete.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.delete.setVisibility(View.GONE);
                }
            }

            if (i == getCount() - 1) {//减号
                viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mIsDeleteModel) {
                            mIsDeleteModel = true;
                            notifyDataSetChanged();
                        }
                    }
                });
            } else if (i == getCount() - 2) {
                viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            } else {
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnGroupDetailListener.onDeleteMembers(userInfo);
                    }
                });
            }

        } else {//普通的群成员
            if (i == getCount() - 1 || i == getCount() - 2) {
                view.setVisibility(View.GONE);

            } else {
                view.setVisibility(View.VISIBLE);
                viewHolder.name.setText(userInfo.getName());
                viewHolder.photo.setImageResource(R.mipmap.em_default_avatar);
                viewHolder.delete.setVisibility(View.GONE);

            }
        }
        return view;
    }

    private class ViewHolder {
        private ImageView photo;
        private ImageView delete;
        private TextView name;
    }

    public interface OnGroupDetailListener {
        //添加群成员方法
        void onAddMembers();

        //删除群成员方法
        void onDeleteMembers(UserInfo user);
    }
}
