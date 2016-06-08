package com.graduationdesign.gm.graduationdesign.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2016/5/7.
 */
public class SmsProvider extends ContentProvider{
    private static final String SMS_AUTHORITY = "com.graduationdesign.gm.graduationdesign.provider.SmsProvider";
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int SMS = 0;
    private static final int SESSION = 1;
    static {
        mUriMatcher.addURI(SMS_AUTHORITY,"sms",SMS);
        mUriMatcher.addURI(SMS_AUTHORITY,"session",SESSION);
    }
    public static class SMS implements BaseColumns {
        public static final String FROM_ID = "from_id";// 发送
        public static final String FROM_NICK = "from_nick";
        public static final String FROM_AVATAR = "from_avatar";
        public static final String BODY = "body";
        public static final String TYPE = "type";// chat
        public static final String TIME = "time";
        public static final String STATUS = "status";
        public static final String UNREAD = "unread";

        public static final String SESSION_ID = "session_id";
        public static final String SESSION_NAME = "session_name";
    }

    public static final String DB = "sms.db";
    public static final String TABLE = "sms";

    private SQLiteDatabase db = null;
    private MyOpenHelper mMyOpenHelper;
    private class MyOpenHelper extends SQLiteOpenHelper {

        public MyOpenHelper(Context context) {
            super(context, DB, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table sms (_id integer  primary key autoincrement,session_id text,session_name text,from_id text,from_nick text,from_avatar integer ,body text,type text ,unread integer,status text,time long)");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
    @Override
    public boolean onCreate() {
        mMyOpenHelper = new MyOpenHelper(getContext());
        return mMyOpenHelper == null ? false : true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (mUriMatcher.match(uri)==SMS) {
            SQLiteDatabase db = mMyOpenHelper.getWritableDatabase();
            Cursor cursor = db.query(TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            return cursor;
        }else if (mUriMatcher.match(uri)==SESSION) {
            SQLiteDatabase db = mMyOpenHelper.getWritableDatabase();
            Cursor session_id = db.query(TABLE, null, selection, selectionArgs, "session_id", null, "time desc");
            return session_id;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (mUriMatcher.match(uri) == SMS) {
            SQLiteDatabase db = mMyOpenHelper.getWritableDatabase();
            long insert = db.insert(TABLE, null, values);
            if (insert > 1) {
                //uri结尾添加操作列表
                uri = ContentUris.withAppendedId(uri, insert);
                getContext().getContentResolver().notifyChange(uri,null);
            }
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
