package com.graduationdesign.gm.graduationdesign.activity;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.graduationdesign.gm.graduationdesign.BaseActivity;
import com.graduationdesign.gm.graduationdesign.R;
import com.graduationdesign.gm.graduationdesign.dao.SmsDao;
import com.graduationdesign.gm.graduationdesign.provider.SmsProvider;
import com.graduationdesign.gm.graduationdesign.utils.MyTime;
import com.graduationdesign.gm.graduationdesign.utils.ThreadUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by Administrator on 2016/5/7.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener {
    private String toAccount = "";
    private String toNick = "";
    private Uri uri = Uri.parse("content://com.graduationdesign.gm.graduationdesign.provider.SmsProvider/sms");
    private TextView title;
    private ListView chatlistview;
    private EditText input;

    private Chat currChat;


    private MyObserver myObserver = new MyObserver(new Handler());
    private SmsDao dao;
    private Button send;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                final String body = input.getText().toString().trim();
                if ("".equals(body)) {
                    Toast.makeText(getBaseContext(), "消息不以为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                input.setText("");
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Message msg = new Message();
                            msg.setType(Message.Type.chat);
                            msg.setBody(body);
                            msg.setFrom(MyApp.account);
                            msg.setTo(toAccount);
                            //发送时保存到数据库中
                            dao.savaSendMessage(msg);
                            currChat.sendMessage(msg);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }

    private class MyObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyObserver(Handler handler) {
            super(handler);
        }
        //老版本
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            setAdapterOrNotify();
        }
        //新版本
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            setAdapterOrNotify();
        }
    }

    private CursorAdapter adapter = null;
    private void setAdapterOrNotify() {
        if (adapter == null) {
            final Cursor c = getContentResolver().query(uri, null, null, null, SmsProvider.SMS.TIME + " ASC");
            if (c.getCount() < 1) {
                return;
            }

            adapter = new CursorAdapter(this, c) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    int type = getItemViewType(position);
                    if (type == 0) {//发送
                        ViewHolder holder1 = null;
                        if (convertView == null) {
                            convertView = View.inflate(getBaseContext(), R.layout.item_chat_send, null);
                            holder1 = new ViewHolder();
                            holder1.time = (TextView) convertView.findViewById(R.id.time);
                            holder1.head = (ImageView) convertView.findViewById(R.id.head);
                            holder1.content = (TextView) convertView.findViewById(R.id.content);
                            convertView.setTag(holder1);
                        } else {
                            holder1 = (ViewHolder) convertView.getTag();
                        }

                        c.moveToPosition(position);
                        long time = c.getLong(c.getColumnIndex(SmsProvider.SMS.TIME));
                        String timeStr = MyTime.getTime(time);
                        String body = c.getString(c.getColumnIndex(SmsProvider.SMS.BODY));
                        holder1.time.setText(timeStr);
                        holder1.content.setText(body);
                        return convertView;
                    } else if (type == 1) {//接收
                        ViewHolder holder2 = null;
                        if (convertView == null) {
                            convertView = View.inflate(getBaseContext(), R.layout.item_chat_receive, null);
                            holder2 = new ViewHolder();
                            holder2.time = (TextView) convertView.findViewById(R.id.time);
                            holder2.head = (ImageView) convertView.findViewById(R.id.head);
                            holder2.content = (TextView) convertView.findViewById(R.id.content);
                            convertView.setTag(holder2);
                        } else {
                            holder2 = (ViewHolder) convertView.getTag();
                        }
                        c.moveToPosition(position);
                        long time = c.getLong(c.getColumnIndex(SmsProvider.SMS.TIME));
                        String timeStr = MyTime.getTime(time);
                        String body = c.getString(c.getColumnIndex(SmsProvider.SMS.BODY));
                        holder2.time.setText(timeStr);
                        holder2.content.setText(body);
                        return convertView;

                    }

                    return convertView;
                }

                @Override
                public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    return null;
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {

                }
                @Override
                public int getItemViewType(int position) {
                    c.moveToPosition(position);
                    String fromId = c.getString(c.getColumnIndex(SmsProvider.SMS.FROM_ID));
                    if (fromId.equals(MyApp.account)) {
                        return 0;
                    }
                    return 1;
                }

                //返回2种视图，只有01两种
                @Override
                public int getViewTypeCount() {
                    return 2;
                }
            };

            chatlistview.setAdapter(adapter);
        } else {
            adapter.getCursor().requery();
        }

        // 行数设置
        if (adapter.getCursor().getCount() >= 1) {
            chatlistview.setSelection(adapter.getCursor().getCount() - 1);
        }

    }

    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getContentResolver().registerContentObserver(uri,true,myObserver);
        initView();

        dao = new SmsDao(getBaseContext());
        Intent intent = getIntent();
        toAccount = intent.getStringExtra("toAccount");
        Log.i(TAG, "传递过来的toAccount："+toAccount);
        toNick = intent.getStringExtra("toNick");
        Log.i(TAG, "传递过来的toNick："+toNick);
        title.setText("与"+toNick+"聊天中...");
        //创建chat工具
        //创建chatmanager
        //创建message
        //发送chat sendmessage

        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                if (currChat ==null) {
                    ChatManager chatManager = ChatManager.getInstanceFor(MyApp.conn);

                    chatManager.addChatListener(new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean b) {
                            chat.addMessageListener(listener);
                        }
                    });
                    currChat = chatManager.createChat(toAccount, listener);
                }
            }
        });
        setAdapterOrNotify();
    }

    private MessageListener listener = new MessageListener() {
        @Override
        public void processMessage(Chat chat, final Message message) {
            final String body = message.getBody();
            Log.i(TAG, "processMessage: "+"ChatActivity中的聊天监听打开");
            Log.i(TAG, "processMessage: "+"ChatActivity Type-->"+message.getType().toString());
            if (Message.Type.chat.equals(message.getType())) {
                Log.i(TAG, "processMessage: body-->"+body);
                if (!TextUtils.isEmpty(body)) {
                    dao.savaReceiveMessage(message);
                    Log.i(TAG, "processMessage: "+"ChatActivity已经保存聊天数据");
                }
            }
//            ThreadUtils.runUIThread(new Runnable() {
//                @Override
//                public void run() {
//                    //这里的message.getFrom()获取的数据末尾有Spack +版本号
//                    //获取这里要过滤掉，就是在savaReceiveMessage
////                    System.out.println("message.toxml+++"+message.toXML());
//                    if (!TextUtils.isEmpty(body)) {
//                        //注掉一下代码是因为服务中已经存了接收过来的数据，这里没必要再存一次
////                        getContentResolver().query(uri, new String[]{SmsProvider.SMS.BODY,});
//
//                    }
//                }
//            });

        }
    };

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        chatlistview = (ListView) findViewById(R.id.chatlistview);
        input = (EditText) findViewById(R.id.input);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);
    }

    public static class ViewHolder{
        TextView time;
        TextView content;
        ImageView head;
    }
}
