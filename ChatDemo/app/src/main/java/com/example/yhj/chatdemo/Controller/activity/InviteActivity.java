package com.example.yhj.chatdemo.Controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yhj.chatdemo.Controller.adapter.InviteAdapter;
import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.Model.bean.InvationInfo;
import com.example.yhj.chatdemo.R;
import com.example.yhj.chatdemo.Utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//邀请信息列表页面
public class InviteActivity extends Activity {

    @BindView(R.id.lv_invite)
    ListView lvInvite;


    private InviteAdapter.OnInviteListener mOnInviteListener = new InviteAdapter.OnInviteListener() {
        @Override
        public void accept(InvationInfo invationInfo) {
            //环信服务器
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invationInfo.getUser().getHxId());
                        //数据库更新
                        Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvationInfo.InvitationStatus.NEW_INVITE, invationInfo.getUser().getHxId());
                        //页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受了邀请！", Toast.LENGTH_SHORT).show();
                                //刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void reject(InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invationInfo.getUser().getHxId());
                        //数据库变化
                        Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(invationInfo.getUser().getHxId());
                        //页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请成功！", Toast.LENGTH_SHORT).show();
                                //刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onInviteAccept(InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //告诉环信服务器接受了邀请
                        EMClient.getInstance().groupManager().acceptInvitation(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson());
                        //本地数据更新
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

                        //内存数据变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onInviteReject(InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().declineInvitation(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson(), invationInfo.getReason());
                        //本地数据更新
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
                        //内存数据变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请失败", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onApplicationAccept(InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().acceptApplication(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson());
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受申请", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受申请失败", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onApplicationReject(InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().acceptApplication(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson());
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝申请", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝申请失败", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });
                    }
                }
            });
        }
    };
    private InviteAdapter inviteAdapter;
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver InviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新页面
            refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        inviteAdapter = new InviteAdapter(this, mOnInviteListener);
        lvInvite.setAdapter(inviteAdapter);
        //刷新方法
        refresh();
        //注册邀请信息变化的广播
        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    private void refresh() {
        //获取数据库中的所有邀请信息
        List<InvationInfo> invitations = Model.getInstance().getDbManager().getInviteTableDao().getInvitations();
        //刷新适配器
        inviteAdapter.refresh(invitations);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(InviteChangedReceiver);
    }


}
