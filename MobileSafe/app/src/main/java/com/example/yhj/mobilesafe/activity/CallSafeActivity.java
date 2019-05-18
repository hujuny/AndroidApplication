package com.example.yhj.mobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.adapter.MyBaseAdapter;
import com.example.yhj.mobilesafe.bean.BlackNumberInfo;
import com.example.yhj.mobilesafe.db.BlackNumberDao;
import com.example.yhj.mobilesafe.utils.ToastUtils;

import java.util.List;


public class CallSafeActivity extends AppCompatActivity {

    private ListView lv;
    private LinearLayout ll_pb;
    private BlackNumberDao blackNumberDao;
    private List<BlackNumberInfo> blackNumberInfos;
    private TextView tvBlackNumber;
    private TextView tvMode;
    private CallSafeAdapter adapter;

    //private int mPageSize = 20;//每页展示20条数据
    //一共有多少条页面
    private int totalPage;
    private int totalNumber;

    /*private int mCurrentPageNumber;//当前页面
    private TextView tvPageNumber;
    private EditText etPageNumber;
    private int pageNum;
    private ImageView ivDelete;*/

    private int mStartIndex=0;//开始的位置
    private int maxCount=20;//每页展示20条数据



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safectivity2);
        initUI();
        initData();

    }

    /*
     * */
    private void initUI() {
        ll_pb = (LinearLayout) findViewById(R.id.ll_pb);
        ll_pb.setVisibility(View.VISIBLE);
        lv = (ListView) findViewById(R.id.list_view);
        /*etPageNumber = (EditText) findViewById(R.id.et_page_number);
        tvPageNumber = (TextView) findViewById(R.id.tv_page_number);*/
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             *  SCROLL_STATE_IDLE 闲置状态
                SCROLL_STATE_FLING 惯性
                SCROLL_STATE_TOUCH_SCROLL 手指触摸时的状态
             */
            @Override//状态改变时回调的方法
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        int lastVisiblePosition = lv.getLastVisiblePosition();
                        if (lastVisiblePosition==blackNumberInfos.size()-1){
                            mStartIndex+=maxCount;
                            System.out.println("生产垃圾"+mStartIndex);
                            if (mStartIndex>=totalNumber){
                                System.out.println("生产垃圾"+mStartIndex);
                                Toast.makeText(CallSafeActivity.this, "没有更多的数据了亲，么么哒。", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            initData();
                        }
                        break;
                }
            }

            @Override//ListView滚动时调用的方法
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_pb.setVisibility(View.INVISIBLE);
           /* pageNum = totalPage / mPageSize;
            if (totalPage % mPageSize != 0) {
                pageNum++;
            }

            tvPageNumber.setText(mCurrentPageNumber + 1 + "/" + pageNum);*/
            adapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
            lv.setAdapter(adapter);

            super.handleMessage(msg);
        }
    };

    /*
    * 初始化数据
    * */
    private void initData() {

        blackNumberDao = new BlackNumberDao(CallSafeActivity.this);
        totalNumber = blackNumberDao.getTotalNumber();
        new Thread() {
            @Override
            public void run() {

                //blackNumberInfos = blackNumberDao.selectAll();//查询所有
                //blackNumberInfos = blackNumberDao.findPar(mCurrentPageNumber, mPageSize);//分页加载
                //分页加载数据
                if (blackNumberInfos==null){
                    blackNumberInfos=blackNumberDao.findPar2(mStartIndex,maxCount);
                }else {
                    //把后面的数据，追加到blackNumberInfos集合里面，防止黑名单被覆盖
                    blackNumberInfos.addAll(blackNumberDao.findPar2(mStartIndex,maxCount));
                }

                mHandler.sendEmptyMessage(0);
                super.run();
            }
        }.start();


    }
    /*
    * 添加黑名单
    * */
    public void addBlackNumber(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_add_black_number, null);
        final EditText etNumber = view.findViewById(R.id.et_number);
        final CheckBox cbSms = view.findViewById(R.id.cb_sms);
        final CheckBox cbPhone = view.findViewById(R.id.cb_phone);
        Button btnCancel = view.findViewById(R.id.bt_cancel);
        Button btnOK = view.findViewById(R.id.bt_ok);

        //添加或取消黑名单
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addNumber = etNumber.getText().toString().trim();
                if (TextUtils.isEmpty(addNumber)){
                    //Toast.makeText(CallSafeActivity.this, "。", Toast.LENGTH_SHORT).show();
                    ToastUtils.showToast(CallSafeActivity.this,"请输入黑名单号码");
                    return;
                }

                String mode="";
                if (cbPhone.isChecked()&&cbSms.isChecked()){
                    mode="1";
                }else if (cbPhone.isChecked()){
                    mode="2";
                }else if (cbSms.isChecked()){
                    mode="3";
                }else {
                    ToastUtils.showToast(CallSafeActivity.this,"请勾选拦截模式");
                    return;
                }

                BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                blackNumberInfo.setNumber(addNumber);
                blackNumberInfo.setMode(mode);
                blackNumberInfos.add(0,blackNumberInfo);
                //把电话号码和拦截模式添加到数据库
                blackNumberDao.insert(addNumber,mode);

                if (adapter==null){
                    new CallSafeAdapter(blackNumberInfos,CallSafeActivity.this);
                    lv.setAdapter(adapter);
                }else {
                    adapter.notifyDataSetChanged();//刷新界面
                }
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();

    }


    /*上一页*//*
    public void prePage(View v) {

        if (mCurrentPageNumber <= 0) {
            Toast.makeText(CallSafeActivity.this, "已经是第一页了亲，么么哒。", Toast.LENGTH_SHORT).show();
        }
        mCurrentPageNumber--;
        initData();
    }

    *//*下一页*//*
    public void nextPage(View v) {
        if (mCurrentPageNumber >= pageNum - 1) {
            Toast.makeText(CallSafeActivity.this, "已经是最后一页了亲，么么哒。", Toast.LENGTH_SHORT).show();
        } else {
            mCurrentPageNumber++;
            initData();
        }

    }

    *//*跳转*//*
    public void jump(View v) {
        String pageNumber = etPageNumber.getText().toString().trim();
        if (TextUtils.isEmpty(pageNumber)) {
            Toast.makeText(CallSafeActivity.this, "请输入正确的页码亲，么么哒。", Toast.LENGTH_SHORT).show();
        } else {
            int number = Integer.parseInt(pageNumber);
            if (number > 0 && number <= pageNum) {
                mCurrentPageNumber = number - 1;
                initData();
            } else {
                Toast.makeText(CallSafeActivity.this, "请输入正确的页码亲，么么哒。", Toast.LENGTH_SHORT).show();
            }

        }
    }*/

    /*
    * 黑名单数据适配器
    * */
    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {

        private CallSafeAdapter(List<BlackNumberInfo> lists, Context mConText) {
            super(lists, mConText);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
                holder = new ViewHolder();
                holder.tvBlackNumber = convertView.findViewById(R.id.tv_black_number);
                holder.tvMode = convertView.findViewById(R.id.tv_mode);
                holder.ivDelete = convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
                //System.out.println("数据打印出来了没");
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String number = lists.get(position).getNumber();
            //System.out.println("电话号为："+number);
            holder.tvBlackNumber.setText(number);
            String mode = lists.get(position).getMode();
            switch (mode) {
                case "1":
                    holder.tvMode.setText("来电拦截+短信");
                    break;
                case "2":
                    holder.tvMode.setText("电话拦截");
                    break;
                case "3":
                    holder.tvMode.setText("短信拦截");
                    break;
            }
            final BlackNumberInfo info = lists.get(position);
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = info.getNumber();
                    boolean del = blackNumberDao.delete(phone);
                    if (del) {
                        Toast.makeText(CallSafeActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                        lists.remove(info);
                        adapter.notifyDataSetChanged();//更新页面
                    } else {
                        Toast.makeText(CallSafeActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertView;
        }
    }

    private static class ViewHolder {
        TextView tvBlackNumber;
        TextView tvMode;
        ImageView ivDelete;
    }


}
