package com.example.yhj.mobilesafe.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;
import com.example.yhj.mobilesafe.bean.TaskInfo;
import com.example.yhj.mobilesafe.engine.TaskInfoParser;
import com.example.yhj.mobilesafe.utils.ServiceStatusUtils;
import com.example.yhj.mobilesafe.utils.SharedPreferencesUtils;
import com.example.yhj.mobilesafe.utils.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;


public class TaskManagerActivity extends AppCompatActivity {


    @ViewInject(R.id.tv_task_process_count)
    private TextView tv_task_process_count;
    @ViewInject(R.id.tv_task_memory)
    private TextView tv_task_memory;
    @ViewInject(R.id.list_view)
    private ListView list_view;
    private int processCount;
    private long availMem;
    private long totalMem;
    private List<TaskInfo> taskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;
    private TaskManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);


        initUI();
        initData();
    }


    private void initUI() {

        processCount = ServiceStatusUtils.getProcessCount(TaskManagerActivity.this);
        tv_task_process_count.setText("进程："+processCount+"个");
        availMem = ServiceStatusUtils.getAvailMem(TaskManagerActivity.this);
        totalMem = ServiceStatusUtils.getTotalMem(TaskManagerActivity.this);

        tv_task_memory.setText("剩余/总内存"+ Formatter.formatFileSize(TaskManagerActivity.this,availMem)+"/"+
                Formatter.formatFileSize(TaskManagerActivity.this,totalMem));

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //得到当前点击的ListView对象
                Object obj = list_view.getItemAtPosition(position);

                if (obj!=null&&obj instanceof TaskInfo){
                    TaskInfo taskInfo= (TaskInfo) obj;
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (taskInfo.getPackageName().equals(getPackageName())){
                        return;
                    }

                    /**
                     * 判断当前的item是否被选上
                     * 如果被勾选上了，就改成没有勾选
                     */

                    if (taskInfo.isChecked()){
                        taskInfo.setChecked(false);
                        holder.tv_app_status.setChecked(false);
                    }else {
                        taskInfo.setChecked(true);
                        holder.tv_app_status.setChecked(true);
                    }

                }
            }
        });
    }

    private void initData() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                taskInfos =  TaskInfoParser.getTaskInfos(TaskManagerActivity.this);
                userTaskInfos = new ArrayList<>();
                systemTaskInfos = new ArrayList<>();

                for (TaskInfo taskInfo :taskInfos) {

                    if (taskInfo.isUserApp()){
                        userTaskInfos.add(taskInfo);
                    }else {
                        systemTaskInfos.add(taskInfo);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new TaskManagerAdapter();
                        list_view.setAdapter(adapter);
                    }
                });
            }
        }.start();
    }
    private class TaskManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            /**
             * 判断当前用户是否需要展示系统进程
             */

            boolean result = SharedPreferencesUtils.getBoolean(TaskManagerActivity.this, "is_show_system", false);
            if (result){
                return userTaskInfos.size()+1+systemTaskInfos.size()+1;
            }else {
                return userTaskInfos.size()+1;
            }

        }

        @Override
        public Object getItem(int position) {
            if (position==0){
                return null;
            }else if (position==userTaskInfos.size()+1){
                return null;
            }

            TaskInfo taskInfo;
            if (position<(userTaskInfos.size()+1)){
                //  用户程序
                taskInfo=userTaskInfos.get(position-1);
            }else {
                //系统程序
                int location=position-1-userTaskInfos.size()-1;
                taskInfo=systemTaskInfos.get(location);
            }

            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           if (position==0){
               TextView textView = new TextView(getApplicationContext());
               textView.setText("用户程序："+userTaskInfos.size()+"个");
               textView.setTextColor(Color.WHITE);
               textView.setBackgroundColor(Color.GRAY);
               return textView;
           }

           if (position==(userTaskInfos.size()+1)){
               TextView textView = new TextView(getApplicationContext());
               textView.setText("系统程序："+systemTaskInfos.size()+"个");
               textView.setTextColor(Color.WHITE);
               textView.setBackgroundColor(Color.GRAY);
               return textView;
           }

           ViewHolder viewHolder;
            View view;

            //如果复用的view不为空,并且能转化为子类布局文件,获取viewHolder对象,否则加载布局文件,重新进行设置复用的view
            if (convertView!=null&&convertView instanceof LinearLayout){
                view=convertView;
                 viewHolder = (ViewHolder) view.getTag();
            }else {
                view  = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);
                viewHolder=new ViewHolder();

                viewHolder.iv_app_icon=view.findViewById(R.id.iv_app_icon);
                viewHolder.tv_app_name=view.findViewById(R.id.tv_app_name);
                viewHolder.tv_app_memory_size=view.findViewById(R.id.tv_app_memory_size);
                viewHolder.tv_app_status=view.findViewById(R.id.tv_app_status);

                view.setTag(viewHolder);

            }

            TaskInfo taskInfo;
            if (position<(userTaskInfos.size()+1)){
                //用户程序
                taskInfo=userTaskInfos.get(position-1);
            }else {
                //系统程序
                int location=position-1-userTaskInfos.size()-1;
                taskInfo=systemTaskInfos.get(location);
            }

            /**
             * 这个是设置图片大小
             */
            viewHolder.iv_app_icon.setImageDrawable(taskInfo.getIcon());
            viewHolder.tv_app_name.setText(taskInfo.getAppName());
            viewHolder.tv_app_memory_size.setText("内存占用:"+Formatter.formatFileSize(TaskManagerActivity.this,taskInfo.getMemorySize()));
            if (taskInfo.isChecked()){
                viewHolder.tv_app_status.setChecked(true);
            }else {
                viewHolder.tv_app_status.setChecked(false);
            }
            //判断当前显示是否是自己的程序，如果是隐藏
            if (taskInfo.getPackageName().equals(getPackageName())){
                //隐藏
                viewHolder.tv_app_status.setVisibility(View.INVISIBLE);
            }else {
                //显示
                viewHolder.tv_app_status.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_app_memory_size;
        CheckBox tv_app_status;
    }

    /**
     * 全选
     */
    public void selectAll(View v){
        for (TaskInfo taskInfo :userTaskInfos) {
            //判断当前的应用是不是自己
            if (taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(true);
        }

        for (TaskInfo taskInfo:systemTaskInfos){
            taskInfo.setChecked(true);
        }
        //数据发生改变更新
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     */

    public void selectOppsite(View v){
        for (TaskInfo taskInfo :userTaskInfos) {
            //判断当前的应用是不是自己
            if (taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }

            taskInfo.setChecked(!taskInfo.isChecked());
        }
        for (TaskInfo taskInfo:systemTaskInfos){
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        //数据发生改变更新
        adapter.notifyDataSetChanged();
    }

    /**
     * 清理进程
     */
    public void killProcess(View v){
        //想要杀死进程，必须先得到进程管理器
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //清理进程的集合
        List<TaskInfo> killList = new ArrayList<>();
        //清理的总共进程个数
        int totalCount=0;
        //清理的进程大小
        int killMem=0;

        for (TaskInfo taskInfo :userTaskInfos) {
            if (taskInfo.isChecked()){
                killList.add(taskInfo);
                totalCount++;
                killMem+=taskInfo.getMemorySize();
                //杀死进程，参数表示包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }

        for (TaskInfo taskInfo:systemTaskInfos){
            if (taskInfo.isChecked()){
                killList.add(taskInfo);
                totalCount++;
                killMem+=taskInfo.getMemorySize();
                //杀死进程，参数表示包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }

        for (TaskInfo taskInfo:killList){
            if (taskInfo.isUserApp()){
                userTaskInfos.remove(taskInfo);
                //杀死进程
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }else {
                systemTaskInfos.remove(taskInfo);
                //杀死进程
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }

        ToastUtils.showToast(TaskManagerActivity.this,"共清理"+totalCount+"个进程，释放"+
                Formatter.formatFileSize(TaskManagerActivity.this,killMem)+"内存");

        //刷新界面
        adapter.notifyDataSetChanged();
    }
    /**
     * 打开设置界面
     */

    public void openSetting(View v){
        startActivity(new Intent(TaskManagerActivity.this,TaskManagerSettingActivity.class));
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

}
