package com.graduationdesign.gm.graduationdesign.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 2016/5/7.
 */
public class MyHelper extends SQLiteOpenHelper{
    public static class CONTACT implements BaseColumns {
        //默认有_id _count
        public static final String ACCOUNT = "account";//账户
        public static final String NICK = "nick";//昵称
        public static final String AVATAR = "avatar";//头像
        public static final String SORT = "sort";//拼音

    }

    public static final String TABLE = "contacts";
    public static final String DB = "contact.db";
    private String sql = "create table contacts(_id integer primary key autoincrement,account varchar(40),nick varchar(40),avatar integer,sort varchar(40))";
    public MyHelper(Context context) {
        super(context, DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
