package com.graduationdesign.gm.graduationdesign.dao;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.graduationdesign.gm.graduationdesign.provider.SmsProvider;
import com.graduationdesign.gm.graduationdesign.utils.NickUtils;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by Administrator on 2016/5/7.
 */
public class SmsDao {
    private Context context;
    private Uri uri = Uri.parse("content://com.graduationdesign.gm.graduationdesign.provider.SmsProvider/sms");

    public SmsDao(Context context) {
        this.context = context;
    }
    public void savaSendMessage(Message msg) {
        ContentValues values=new ContentValues();
        String account=msg.getFrom();
        values.put(SmsProvider.SMS.FROM_ID, account);
        String nick= NickUtils.getNick(context, account);
        String sessionName=NickUtils.getNick(context, msg.getTo());
        values.put(SmsProvider.SMS.FROM_NICK, nick);
        values.put(SmsProvider.SMS.FROM_AVATAR, 0);
        values.put(SmsProvider.SMS.BODY, msg.getBody());
        values.put(SmsProvider.SMS.TIME, System.currentTimeMillis());
        values.put(SmsProvider.SMS.TYPE, Message.Type.chat.toString());
        values.put(SmsProvider.SMS.STATUS, "");
        values.put(SmsProvider.SMS.UNREAD, 0);
        values.put(SmsProvider.SMS.SESSION_ID, msg.getTo());
        values.put(SmsProvider.SMS.SESSION_NAME, sessionName);

        this.context.getContentResolver().insert(uri, values);

    }

    public void savaReceiveMessage(Message msg) {
        String from = NickUtils.filterAccount(msg.getFrom());



        ContentValues values=new ContentValues();

        System.out.println("account -- smsdao  "+from);
        values.put(SmsProvider.SMS.FROM_ID, from);
        String nick= NickUtils.getNick(context, from);
        values.put(SmsProvider.SMS.FROM_NICK, nick);
        values.put(SmsProvider.SMS.FROM_AVATAR, 0);
        values.put(SmsProvider.SMS.BODY, msg.getBody());
        values.put(SmsProvider.SMS.TIME, System.currentTimeMillis());
        values.put(SmsProvider.SMS.TYPE, Message.Type.chat.toString());
        values.put(SmsProvider.SMS.STATUS, "");
        values.put(SmsProvider.SMS.UNREAD, 0);
        values.put(SmsProvider.SMS.SESSION_ID, from);
        values.put(SmsProvider.SMS.SESSION_NAME, nick);

        this.context.getContentResolver().insert(uri, values);
    }
}
