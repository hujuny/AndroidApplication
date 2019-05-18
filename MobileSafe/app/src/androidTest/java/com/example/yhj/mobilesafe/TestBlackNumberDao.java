package com.example.yhj.mobilesafe;

import android.content.Context;
import android.test.AndroidTestCase;

import com.example.yhj.mobilesafe.bean.BlackNumberInfo;
import com.example.yhj.mobilesafe.db.BlackNumberDao;


import java.util.List;
import java.util.Random;


/**
 * safe.db数据库CURD测试
 */

public class TestBlackNumberDao extends AndroidTestCase {
    private Context mContext;
    private BlackNumberDao blackNumberDao;

    @Override
    protected void setUp() throws Exception {
        this.mContext=getContext();
        super.setUp();
    }

    public void testInsert(){
         blackNumberDao = new BlackNumberDao(mContext);
        Random random = new Random();
        for(int i=0;i<100;i++){
            Long number = 13300000000L +i;
            blackNumberDao.insert(number+"", String.valueOf(random.nextInt(3)+1));
        }
    }

    public void  testDelete(){
         blackNumberDao = new BlackNumberDao(mContext);
        boolean delete = blackNumberDao.delete("13300000006");
        assertEquals(true,delete);
    }

    public void testUpdate(){
        blackNumberDao=new BlackNumberDao(mContext);
        boolean update = blackNumberDao.update("13300000007", "2");
        assertEquals(true,update);
    }

    public void testSelectAll(){
        blackNumberDao=new BlackNumberDao(mContext);
        List<BlackNumberInfo> blackNumberInfos = blackNumberDao.selectAll();
        for (BlackNumberInfo blackNumberInfo:blackNumberInfos) {
            System.out.println("号码是："+blackNumberInfo.getNumber()+"拦截方式："+blackNumberInfo.getMode());
        }
    }
}
