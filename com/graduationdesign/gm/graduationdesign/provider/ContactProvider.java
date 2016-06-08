package com.graduationdesign.gm.graduationdesign.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.graduationdesign.gm.graduationdesign.dao.MyHelper;

/**
 * Created by Administrator on 2016/5/7.
 */
public class ContactProvider extends ContentProvider {
    private static final String CONTACT_AUTHORITY = "com.graduationdesign.gm.graduationdesign.provider.ContactProvider";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int SUCCESS = 1;

    static {
        uriMatcher.addURI(CONTACT_AUTHORITY,"contacts",SUCCESS);
    }
    private MyHelper myHelper = null;
    private int update;
    private int delete;

    @Override
    public boolean onCreate() {
        myHelper = new MyHelper(getContext());
        return myHelper == null ? false:true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == SUCCESS) {
            SQLiteDatabase db = myHelper.getWritableDatabase();
            return db.query(MyHelper.TABLE,projection,selection,selectionArgs,null,null,sortOrder);
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
        SQLiteDatabase db = myHelper.getWritableDatabase();
        if (uriMatcher.match(uri) == SUCCESS) {
            long insert = db.insert(MyHelper.TABLE, null, values);
            //返回最新插入的行
            if (insert != -1) {
                //插入的位置添加到uri末尾，返回
                uri = ContentUris.withAppendedId(uri, insert);
                getContext().getContentResolver().notifyChange(uri,null);
            }
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == SUCCESS) {
            SQLiteDatabase db = myHelper.getWritableDatabase();
            delete = db.delete(MyHelper.TABLE, selection, selectionArgs);
            if (delete >0) {
                getContext().getContentResolver().notifyChange(uri, null);//所有观察这个uri的观察者
            }

        }
        return delete;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == SUCCESS) {
            SQLiteDatabase db = myHelper.getWritableDatabase();
            update = db.update(MyHelper.TABLE, values, selection, selectionArgs);
            if (update >0) {
                getContext().getContentResolver().notifyChange(uri,null);
            }
        }
        return update;
    }
}
