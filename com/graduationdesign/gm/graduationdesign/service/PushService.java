package com.graduationdesign.gm.graduationdesign.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.graduationdesign.gm.graduationdesign.activity.MyApp;
import com.graduationdesign.gm.graduationdesign.utils.ThreadUtils;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * Created by Administrator on 2016/5/7.
 */
public class PushService extends Service {
    private static final String TAG = "PushService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(PushService.this, "推送服务打开", Toast.LENGTH_SHORT).show();
        //Packet：推送数据包，包含推送消息
        //message from to
        MyApp.conn.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(final Packet packet) throws SmackException.NotConnectedException {
                System.out.println(packet.toXML());
                final Message msg = (Message) packet;
                Log.i(TAG, "processPacket: "+packet.getFrom());
                if ("gm.seven".equals(packet.getFrom())) {
//                    Log.d(TAG, "processPacket: "+packet.getFrom());

                    ThreadUtils.runUIThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "商城消息："+msg.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }, null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(PushService.this, "推送服务关闭", Toast.LENGTH_SHORT).show();
    }
}
