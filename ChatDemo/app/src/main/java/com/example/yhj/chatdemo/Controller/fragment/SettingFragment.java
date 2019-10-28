package com.example.yhj.chatdemo.Controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.yhj.chatdemo.Controller.activity.LoginActivity;
import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class SettingFragment extends Fragment {


    @BindView(R.id.btn_logout)
    Button btnLogout;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);

        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btn_logout)
    public void loginOut() {
        Model.getInstance().getGlobalThreadPool().execute(() -> EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                //关闭数据库
                Model.getInstance().getDbManager().close();
                getActivity().runOnUiThread(() -> {

                    Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                });
            }

            @Override
            public void onError(int code, String error) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "退出失败" + error, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        }));
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnLogout.setText("退出登录(" + EMClient.getInstance().getCurrentUser() + ")");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
