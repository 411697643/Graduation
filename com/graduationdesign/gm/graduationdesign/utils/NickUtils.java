package com.graduationdesign.gm.graduationdesign.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.graduationdesign.gm.graduationdesign.dao.MyHelper;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * Created by GM on 2015/12/21.
 * 传入汉字，返回拼音
 */
public class NickUtils {

    public static String getNick(String nick) {
        //传入汉字，分隔符，格式
       return PinyinHelper.convertToPinyinString(nick, "", PinyinFormat.WITHOUT_TONE).toUpperCase();
    }

    public static String getNick(Context context,String account) {
        String nick = "";
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.graduationdesign.gm.graduationdesign.provider.ContactProvider/contacts"), null, MyHelper.CONTACT.ACCOUNT + "=?", new String[]{account}, null);
        if (cursor.moveToFirst()) {
            nick = cursor.getString(cursor.getColumnIndex(MyHelper.CONTACT.NICK));
        }
        cursor.close();
        if (nick == null||"".equals(nick)) {
            nick = account.substring(0, account.indexOf("@"));
        }
        return nick;
    }
    //gm@gm.com/Spack 2.63
    public static String filterAccount(String account) {
        account = account.substring(0, account.indexOf("@"));
        account = account + "@gm.seven";
        return account;
    }
}
