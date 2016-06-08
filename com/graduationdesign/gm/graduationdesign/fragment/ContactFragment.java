package com.graduationdesign.gm.graduationdesign.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.graduationdesign.gm.graduationdesign.dao.MyHelper;

/**
 * Created by Administrator on 2016/5/7.
 */
public class ContactFragment extends BaseFragment {

    private ListView contactlistview;
    private Uri uri;
    private MyObserver mMyObserver = new MyObserver(new Handler());

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_contact, null);
        contactlistview = (ListView) view.findViewById(R.id.contactlistview);
        setAdapterOrNotify();
        getActivity().getContentResolver().registerContentObserver(uri,true,mMyObserver);
        return view;
    }

    private CursorAdapter adapter = null;
    private void setAdapterOrNotify() {
        uri = Uri.parse("content://com.graduationdesign.gm.graduationdesign.provider.ContactProvider/contacts");

        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, MyHelper.CONTACT.SORT + " ASC");
        if (cursor.getCount() < 1) {
            return;
        } else {
            if (adapter == null) {
                adapter = new CursorAdapter(getActivity(), cursor) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder holder = null;
                        if (convertView == null) {
                            convertView = View.inflate(getActivity(), R.layout.item_buddy, null);
                            holder = new ViewHolder();
                            holder.account = (TextView) convertView.findViewById(R.id.account);
                            holder.nick = (TextView) convertView.findViewById(R.id.nick);
                            holder.head = (ImageView) convertView.findViewById(R.id.head);
                            convertView.setTag(holder);
                        } else {
                            holder = (ViewHolder) convertView.getTag();
                        }
                        Cursor cursor = adapter.getCursor();
                        cursor.moveToPosition(position);
                        String account = cursor.getString(cursor.getColumnIndex(MyHelper.CONTACT.ACCOUNT));
                        String nick = cursor.getString(cursor.getColumnIndex(MyHelper.CONTACT.NICK));
                        holder.account.setText(account);
                        holder.nick.setText(nick);
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

                contactlistview.setAdapter(adapter);
                //点击条目
                contactlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        adapter.getCursor().moveToPosition(position);
                        //获取好友的账户和昵称
                        String toAccount = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(MyHelper.CONTACT.ACCOUNT));
                        String toNick = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(MyHelper.CONTACT.NICK));
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("toAccount", toAccount);
                        intent.putExtra("toNick", toNick);
                        startActivity(intent);
                    }
                });


            } else {
                adapter.getCursor().requery();
            }
        }


    }
    public class ViewHolder {

        public ImageView head;

        public TextView nick;

        public TextView account;


    }
    @Override
    public void onDestroy() {
        getActivity().getContentResolver().unregisterContentObserver(mMyObserver);
        super.onDestroy();
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
        //低版本
        @Override
        public void onChange(boolean selfChange) {
            setAdapterOrNotify();
        }
        //高版本
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            setAdapterOrNotify();
        }
    }
}
