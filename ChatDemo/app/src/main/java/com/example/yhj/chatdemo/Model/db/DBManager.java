package com.example.yhj.chatdemo.Model.db;

import android.content.Context;

import com.example.yhj.chatdemo.Model.dao.ContactTableDao;
import com.example.yhj.chatdemo.Model.dao.InviteTableDao;

//联系人和邀请信息表的操作类的管理类
public class DBManager {

    private final InviteTableDao inviteTableDao;
    private final ContactTableDao contactTableDao;
    private final DBHelper dbHelper;

    public DBManager(Context context, String name) {
        dbHelper = new DBHelper(context, name);

        contactTableDao = new ContactTableDao(dbHelper);
        inviteTableDao = new InviteTableDao(dbHelper);

    }

    //获取联系人表的操作类对象
    public ContactTableDao getContactTableDao() {
        return contactTableDao;
    }

    //获取邀请信息表的操作类对象
    public InviteTableDao getInviteTableDao() {
        return inviteTableDao;
    }

    //关闭数据库的方法
    public void close() {
        dbHelper.close();
    }
}
