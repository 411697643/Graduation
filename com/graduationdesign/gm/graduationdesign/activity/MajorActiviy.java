package com.graduationdesign.gm.graduationdesign.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.graduationdesign.gm.graduationdesign.ExitApplication;
import com.graduationdesign.gm.graduationdesign.R;
import com.graduationdesign.gm.graduationdesign.fragment.ContactFragment;
import com.graduationdesign.gm.graduationdesign.fragment.SessionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class MajorActiviy extends FragmentActivity implements View.OnClickListener {

    private TextView title;
    private ViewPager viewpager;
    private TextView session;
    private TextView contact;
    private LinearLayout tablayout;
    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major);
        ExitApplication.getInstance().addActivity(this);
        initView();
        title.setText("会话");
        final List<Fragment> pages = new ArrayList<>();
        pages.add(new SessionFragment());
        pages.add(new ContactFragment());

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pages.get(position);
            }

            @Override
            public int getCount() {
                return pages.size();
            }
        };
        viewpager.setAdapter(adapter);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    title.setText("会话");
                    session.setTextColor(Color.rgb(26,113,244));
                    contact.setTextColor(Color.rgb(43,43,43));
                } else {
                    title.setText("好友");
                    contact.setTextColor(Color.rgb(26,113,244));
                    session.setTextColor(Color.rgb(43,43,43));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        session = (TextView) findViewById(R.id.session);
        contact = (TextView) findViewById(R.id.contact);
        tablayout = (LinearLayout) findViewById(R.id.tablayout);
        session.setOnClickListener(this);
        contact.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact:
                viewpager.setCurrentItem(1);
                break;
            case R.id.session:
                viewpager.setCurrentItem(0);
                break;
        }
    }
}
