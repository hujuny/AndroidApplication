package com.example.yhj.chatdemo.Controller.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yhj.chatdemo.Controller.adapter.GroupDetailAdapter;
import com.example.yhj.chatdemo.Model.Model;
import com.example.yhj.chatdemo.Model.bean.UserInfo;
import com.example.yhj.chatdemo.R;
import com.example.yhj.chatdemo.Utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.widget.EaseExpandGridView;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


//群详情页面
public class GroupDetailActivity extends AppCompatActivity {

    @BindView(R.id.gv_member_list)
    EaseExpandGridView gvMemberList;
    @BindView(R.id.btn_exit_group)
    Button btnExitGroup;
    private EMGroup mGroup;

    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        @Override
        public void onAddMembers() {
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);
            intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

            startActivityForResult(intent, 2);
        }

        @Override
        public void onDeleteMembers(UserInfo user) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), user.getHxId());
                        //更新数据
                        getMembersFromHxServer();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除失败！" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };
    private GroupDetailAdapter groupDetailAdapter;
    private List<UserInfo> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        ButterKnife.bind(this);

        getData();

        initData();

        initListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        gvMemberList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (groupDetailAdapter.ismIsDeleteModel()) {
                            //切换非删除模式
                            groupDetailAdapter.setmIsDeleteModel(false);
                            groupDetailAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void initData() {
        //初始化button演示
        initButtonDisPlay();
        //初始化gridView
        initGridView();

        //从环信服务器获取所有的群成员
        getMembersFromHxServer();
    }

    private void getMembersFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());
                    List<String> members = mGroup.getMembers();

                    if (members != null && members.size() > 0) {
                        mUsers = new ArrayList<>();
                        for (String member : members) {
                            UserInfo userInfo = new UserInfo(member);
                            mUsers.add(userInfo);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            groupDetailAdapter.Refresh(mUsers);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, "获取群信息失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initGridView() {
        //当前用户是群组||群公开了
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();
        groupDetailAdapter = new GroupDetailAdapter(this, isCanModify, mOnGroupDetailListener);
        gvMemberList.setAdapter(groupDetailAdapter);
    }

    private void initButtonDisPlay() {
        //是否是群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {
            btnExitGroup.setText("解散群");
            btnExitGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            //解散群
                            try {
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());
                                //退群广播
                                exitGroupBroadCast();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功！", Toast.LENGTH_SHORT).show();
                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败！" + e.toString(), Toast.LENGTH_SHORT).show();
                                        //结束当前页面
                                        finish();
                                    }
                                });
                            }

                        }
                    });
                }
            });
        } else {
            btnExitGroup.setText("退群");
            btnExitGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());
                                exitGroupBroadCast();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群成功！", Toast.LENGTH_SHORT).show();
                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群失败！", Toast.LENGTH_SHORT).show();
                                        //结束当前页面
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    private void exitGroupBroadCast() {
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(GroupDetailActivity.this);
        Intent intent = new Intent(Constant.EXIT_GROUP);
        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());
        mLBM.sendBroadcast(intent);
    }

    private void getData() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra(Constant.GROUP_ID);
        if (groupId == null) {
            return;
        } else {
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            //获取返回的准备邀请的群成员信息
            String[] members = data.getStringArrayExtra("members");

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //去环信服务器，发送邀请信息
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(), members);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请失败！" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}
