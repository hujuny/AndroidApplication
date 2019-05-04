package com.example.yhj.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yhj.mobilesafe.R;

/**
 * 修改归属地位置
 * */
public class DragViewActivity extends AppCompatActivity {

    private TextView tvTop;
    private TextView tvBottom;

    private ImageView ivDrag;

    private int startX;
    private int startY;
    public SharedPreferences mPref;

    long[] mHits=new long[2];//数组长度表示要点击的次数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);

        mPref=getSharedPreferences("config",MODE_PRIVATE);

        tvTop= (TextView) findViewById(R.id.tv_top);
        tvBottom= (TextView) findViewById(R.id.tv_bottom);
        ivDrag= (ImageView) findViewById(R.id.iv_drag);

        int lastX=mPref.getInt("lastX",0);
        int lastY=mPref.getInt("lastY",0);

        //获取屏幕宽高
        final int winWidth=getWindowManager().getDefaultDisplay().getWidth();
        final int winHeight=getWindowManager().getDefaultDisplay().getHeight();
        if (lastY>winHeight/2){//上边显示，下边隐藏
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        }else{//上边隐藏，下边显示
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }

        RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();//获取布局对象
        layoutParams.leftMargin=lastX;//设置左边距
        layoutParams.topMargin=lastY;//设置top边距

        ivDrag.setLayoutParams(layoutParams);//重新设置位置

        ivDrag.setOnClickListener(new View.OnClickListener() {//设置双击居中
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                mHits[mHits.length-1]= SystemClock.uptimeMillis();//开机后计算的时间
                if (mHits[0]>=(SystemClock.uptimeMillis()-500)){
                    //把图片居中
                    ivDrag.layout(winWidth/2-ivDrag.getWidth()/2,ivDrag.getTop(),winWidth/2+ivDrag.getWidth()/2,ivDrag.getBottom());
                }
            }
        });

        ivDrag.setOnTouchListener(new View.OnTouchListener() {//设置触摸监听
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //初始化起点坐标
                        startX= (int) event.getRawX();
                        startY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX= (int) event.getRawX();
                        int endY=(int) event.getRawY();

                        //计算移动偏移量
                        int dx=endX-startX;
                        int dy=endY-startY;

                        //更新左上下右距离
                        int l=ivDrag.getLeft()+dx;
                        int r=ivDrag.getRight()+dx;

                        int t=ivDrag.getTop()+dy;
                        int b=ivDrag.getBottom()+dy;

                        //判断是否超出屏幕界面。注意状态栏的高度
                        if (l<0||r>winWidth||t<0||b>winHeight-10){
                            break;
                        }

                        //根据图片位置，确定显示隐藏
                        if (t>winHeight/2){//上边显示，下边隐藏
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        }else{
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        }

                        //更新界面
                        ivDrag.layout(l,t,r,b);

                        //重新初始化起点坐标
                        startX= (int) event.getRawX();
                        startY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //记录坐标点
                        SharedPreferences.Editor edit=mPref.edit();
                        edit.putInt("lastX",ivDrag.getLeft());
                        edit.putInt("lastY",ivDrag.getTop());
                        edit.apply();
                        break;
                }
                return false;//事件要向下传递，让onclick事件可以响应
            }
        });
    }
}
