package com.graduationdesign.gm.graduationdesign.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.graduationdesign.gm.graduationdesign.activity.MyApp;
import com.graduationdesign.gm.graduationdesign.dao.MyHelper;
import com.graduationdesign.gm.graduationdesign.utils.NickUtils;
import com.graduationdesign.gm.graduationdesign.utils.ThreadUtils;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

/**
 * Created by Administrator on 2016/5/7.
 */
public class ImService extends Service {
    private static final String TAG = "ImService";
    private Roster roster;
    private Uri uri;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(ImService.this, "后台服务打开", Toast.LENGTH_SHORT).show();
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
//                roster = MyApp.conn.getRoster();
//                Collection<RosterGroup> groups = roster.getGroups();
//                for (RosterGroup group : groups) {
//                    Collection<RosterEntry> entries = group.getEntries();
//                    for (RosterEntry entry : entries) {
//                        System.out.println(entry.getUser());
//                        System.out.println(entry.getName());
//                    }
//                }
                //获取所有的联系人
                roster = MyApp.conn.getRoster();
                roster.addRosterListener(getRosterListener());
                Collection<RosterEntry> entries = roster.getEntries();
                Log.i(TAG, "roster:"+entries.toString());
                for (RosterEntry person : entries) {
                    saveOrUpdateRosterEntry(person);
                }

            }

        });
    }

    @NonNull
    private RosterListener getRosterListener() {
        return new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> collection) {
                System.out.println("添加");
                System.out.println(collection.toString());
                for (String person : collection) {
                    RosterEntry entry = roster.getEntry(person);
                    saveOrUpdateRosterEntry(entry);
                }
            }

            @Override
            public void entriesUpdated(Collection<String> collection) {
                System.out.println("更改");
                System.out.println(collection.toString());
                for (String person : collection) {
                    RosterEntry entry = roster.getEntry(person);
                    saveOrUpdateRosterEntry(entry);
                }
            }

            @Override
            public void entriesDeleted(Collection<String> collection) {
                System.out.println("删除");
                //lollection
                System.out.println(collection.toString());
                for (String person : collection) {
                    getContentResolver().delete(uri, MyHelper.CONTACT.ACCOUNT + "=?", new String[]{person});
                }
            }

            @Override
            public void presenceChanged(Presence presence) {
                System.out.println(presence.toString());
            }
        };
    }

    /**
     * 保存或者更新roster实体
     *
     * @param person
     */
    private void saveOrUpdateRosterEntry(RosterEntry person) {
        String username = person.getUser();
        Log.i(TAG, "saveOrUpdateRosterEntry: " +username);
        uri = Uri.parse("content://com.graduationdesign.gm.graduationdesign.provider.ContactProvider/contacts");
        ContentValues values = new ContentValues();
        values.put(MyHelper.CONTACT.ACCOUNT, username);

        String nickName = person.getName();
        //判断是否有昵称
        if (nickName == null) {
//            System.out.println("userName = " + username);
            nickName = username.substring(0, username.indexOf("@"));
        }
        values.put(MyHelper.CONTACT.NICK, nickName);
        values.put(MyHelper.CONTACT.SORT, NickUtils.getNick(nickName));
        values.put(MyHelper.CONTACT.AVATAR, 0);
        int update = getContentResolver().update(uri, values, MyHelper.CONTACT.ACCOUNT + "=?", new String[]{username});
        //证明没有数据需要更新，需要往数据库中添加数据
        if (update < 1) {
            getContentResolver().insert(uri, values);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(ImService.this, "后台服务关闭", Toast.LENGTH_SHORT).show();
    }
}
