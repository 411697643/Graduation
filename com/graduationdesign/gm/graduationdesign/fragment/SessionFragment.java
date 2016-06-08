package com.graduationdesign.gm.graduationdesign.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.graduationdesign.gm.graduationdesign.R;
import com.graduationdesign.gm.graduationdesign.activity.ChatActivity;
import com.graduationdesign.gm.graduationdesign.provider.SmsProvider;
import com.graduationdesign.gm.graduationdesign.utils.NickUtils;

/**
 * Created by Administrator on 2016/5/7.
 */
public class SessionFragment extends BaseFragment {
    private static final String TAG = "SessionFragment";

    private ListView sessionlistview;
    private CursorAdapter adapter = null;
    private Uri uri;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  View.inflate(getActivity(), R.layout.fragment_session, null);
        sessionlistview = (ListView) view.findViewById(R.id.sessionlistview);
        setAdapterOrNotify();
        getActivity().getContentResolver().registerContentObserver(Uri.parse("content://com.graduationdesign.gm.graduationdesign.provider.SmsProvider/sms"), true, myObserver);

        return view;
    }
    private MyObserver myObserver = new MyObserver(new Handler());
    private class MyObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            setAdapterOrNotify();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            setAdapterOrNotify();
        }
    }

    private void setAdapterOrNotify() {
        uri = Uri.parse("content://com.graduationdesign.gm.graduationdesign.provider.SmsProvider/session");
        final Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null,null);
        if (cursor.getCount() < 1) {
            return;
        } else {
            if (adapter == null) {
                adapter = new CursorAdapter(getActivity(), cursor) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder holder = null;
                        if (convertView == null) {
                            convertView = View.inflate(getActivity(), R.layout.item_session, null);
                            holder = new ViewHolder();
                            holder.account = (TextView) convertView.findViewById(R.id.account);
                            holder.nick = (TextView) convertView.findViewById(R.id.nick);
                            holder.head = (ImageView) convertView.findViewById(R.id.head);
                            convertView.setTag(holder);
                        } else {
                            holder = (ViewHolder) convertView.getTag();
                        }
                        //优化
                        Cursor cursor = adapter.getCursor();
                        cursor.moveToPosition(position);
                        String session_id = cursor.getString(cursor.getColumnIndex(SmsProvider.SMS.SESSION_ID));
                        String body = cursor.getString(cursor.getColumnIndex(SmsProvider.SMS.BODY));
                        String nick = NickUtils.getNick(getActivity(), session_id);
                        holder.nick.setText(nick);
                        holder.account.setText(nick);
                        return convertView;
                    }

                    @Override
                    public View newView(Context context, Cursor cursor, ViewGroup parent) {
                        return null;
                    }

                    @Override
                    public void bindView(View view, Context context, Cursor cursor) {

                    }
                };
                sessionlistview.setAdapter(adapter);
                sessionlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        adapter.getCursor().moveToPosition(position);
                        String session_id = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(SmsProvider.SMS.SESSION_ID));
                        Log.i(TAG, "查询的Session_id："+session_id);
//                        String nick = NickUtils.filterAccount(session_id);
                        Intent intent = new Intent(getActivity(), ChatActivity.class);

                        intent.putExtra("toAccount", session_id);
                        String nick = NickUtils.getNick(getActivity(), session_id);
                        Log.i(TAG, "查询的昵称: "+nick);
//                        Bundle bundle = new Bundle();
//
//                        bundle.putString("toAccount", session_id);
//                        bundle.putString("toNick",nick);
                        intent.putExtra("toNick",nick);
//                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                //同步完成在对提供者监听
                //第二个参数true代表自路径也接收通知
                //false全路径匹配
                //注意：这里传入的uri是sms的并不是session的，因为增删都是对sms的数据库删除，session只不过是查询结果不同而已

            } else {
                adapter.getCursor().requery();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(myObserver);
    }

    public  class ViewHolder {

        public ImageView head;

        public TextView nick;

        public TextView account;

    }
}
