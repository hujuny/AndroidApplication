package com.example.yhj.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhj.mobilesafe.R;
import com.lidroid.xutils.cache.MD5FileNameGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Build.VERSION_CODES.O;


/*
* 主页面
* */
public class HomeActivity extends AppCompatActivity {

    private GridView gvHome;

    private SharedPreferences mPref;


    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] mPics = new int[]{R.mipmap.home_safe,
            R.mipmap.home_callmsgsafe, R.mipmap.home_apps,
            R.mipmap.home_taskmanager, R.mipmap.home_netmanager,
            R.mipmap.home_trojan, R.mipmap.home_sysoptimize,
            R.mipmap.home_tools, R.mipmap.home_settings};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        //TextView tvElli= (TextView) findViewById(R.id.tv_elli);
        //tvElli.setSelected(true);//设置TextView的焦点
        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());

        //设置监听
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://手机防盗
                        showPasswordDialog();
                        break;
                    case 7://高级工具
                        startActivity(new Intent(HomeActivity.this,AToolsActivity.class));
                        break;
                    case 8://设置中心
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                }
            }
        });
    }

    /*
    * 显示密码弹窗
    * */
    private void showPasswordDialog() {
        //判断是否设置了密码
        String savePassword = mPref.getString("password", null);
        if (!TextUtils.isEmpty(savePassword)) {
            //输入密码弹窗
            showPasswordInputDialog();
        } else {
            //如果没有设置过，弹出设置密码的弹窗
            showPasswordSetDialog();
        }
    }

    /*
    * 输入密码弹窗
    * */
    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_input_password, null );
        dialog.setView(view, 0, 0, 0, 0);//设置边距为0
        final EditText etPassword = view.findViewById(R.id.et_password);
        Button btnOK = view.findViewById(R.id.btn_ok);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                if (!TextUtils.isEmpty(password)) {
                    String savePassword = mPref.getString("password", null);
                    if (new MD5FileNameGenerator().generate(password).equals(savePassword)) {
                        dialog.dismiss();
                        //跳转到手机防盗页面
                        startActivity(new Intent(HomeActivity.this,LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//隐藏对话框
            }
        });
        dialog.show();
    }

    /*
    * 设置密码弹窗
    * */
    private void showPasswordSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dialog_set_password, null);
        dialog.setView(view, 0, 0, 0, 0);
        final EditText etPassword=view.findViewById(R.id.et_password);
         final EditText etPasswordConfirm=view.findViewById(R.id.et_password_confirm);
        Button btnOK=view.findViewById(R.id.btn_ok);
        Button btnCancel=view.findViewById(R.id.btn_cancel);
         btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=etPassword.getText().toString();
                String passwordConfirm=etPasswordConfirm.getText().toString();
                if(!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(passwordConfirm)){
                    if(password.equals(passwordConfirm)){
                        //将密码保存起来,md5加密
                        String md5=new MD5FileNameGenerator().generate(password);//MD5加密算法
                        mPref.edit().putString("password",md5).apply();
                        dialog.dismiss();
                        //跳转到手机防盗页面
                        startActivity(new Intent(HomeActivity.this,LostFindActivity.class));
                    }else{
                        Toast.makeText(HomeActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(HomeActivity.this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
            ImageView ivItem = view.findViewById(R.id.iv_item);
            TextView tvItem = view.findViewById(R.id.tv_item);
            tvItem.setText(mItems[position]);
            ivItem.setImageResource(mPics[position]);

            return view;
        }
    }


}
