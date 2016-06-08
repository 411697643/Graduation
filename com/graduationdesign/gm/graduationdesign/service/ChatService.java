package com.graduationdesign.gm.graduationdesign.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.graduationdesign.gm.graduationdesign.activity.MyApp;
import com.graduationdesign.gm.graduationdesign.dao.SmsDao;
import com.graduationdesign.gm.graduationdesign.utils.NickUtils;
import com.graduationdesign.gm.graduationdesign.utils.ThreadUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by Administrator on 2016/5/7.
 */
public class ChatService extends Service {
    private static final String TAG = "ChatService";
    private ChatManager chatManager;
    private SmsDao dao = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(ChatService.this, "聊天服务打开", Toast.LENGTH_SHORT).show();
        chatManager = ChatManager.getInstanceFor(MyApp.conn);
        chatManager.addChatListener(cmListener);
        dao = new SmsDao(getBaseContext());
    }

    private ChatManagerListener cmListener = new ChatManagerListener() {
        @Override
        public void chatCreated(Chat chat, boolean b) {
//            chat.addMessageListener(listener);
            //b=false 别人找你聊天
            if (!b) {
                //主动找别人聊天
                chat.addMessageListener(listener);
                Log.i(TAG, "chatCreated: "+"服务中的聊天监听打开了");
            }



        }
    };

    private MessageListener listener = new MessageListener() {
        @Override
        public void processMessage(Chat chat, Message message) {
            //判断message类型是否是chat类型
            if (!Message.Type.chat.equals(message.getType())) {
                return;
            }
            //保存到数据库中
            String fromUserAccount = NickUtils.filterAccount(message.getFrom());
            //发送人的id跟自己id不同时保存
            if (message.getBody()!=null) {
                if (!MyApp.account.equals(fromUserAccount)) {
                    dao.savaReceiveMessage(message);
                    Log.i(TAG, "processMessage: "+"ChatService中的聊天数据已经保存");
                }
            }

            final String body = message.getBody();
            ThreadUtils.runUIThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(body)) {
                        Toast.makeText(ChatService.this, body, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(ChatService.this, "聊天服务关闭", Toast.LENGTH_SHORT).show();
        if (chatManager != null) {
            chatManager.removeChatListener(cmListener);
        }
    }

}
