package com.graduationdesign.gm.graduationdesign;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.graduationdesign.gm.graduationdesign.activity.LoginActivity;
import com.graduationdesign.gm.graduationdesign.activity.MyApp;
import com.graduationdesign.gm.graduationdesign.service.ChatService;
import com.graduationdesign.gm.graduationdesign.service.ImService;
import com.graduationdesign.gm.graduationdesign.service.PushService;
import com.graduationdesign.gm.graduationdesign.utils.SPUtils;

import org.jivesoftware.smack.SmackException;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private TextView loginMark;//登录标记
    private FrameLayout mainFrame;//主要替换的布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mToolbar.setTitle(R.string.toolbar_name);
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_menu, R.string.close_menu);
        //相互绑定
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mainFrame = (FrameLayout) findViewById(R.id.fl_main);

        loginMark = (TextView) findViewById(R.id.whethorlogin);
        SPUtils.addInfo("loginMark","1",this);//0没登录，1登录过
        if (Integer.parseInt(SPUtils.getInfo("loginMark", this)) == 0) {
            loginMark.setVisibility(View.VISIBLE);
        } else if (Integer.parseInt(SPUtils.getInfo("loginMark",this))==1) {
            loginMark.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_one:
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                break;
            case R.id.item_two:

                stopService(new Intent(MainActivity.this, ChatService.class));
                stopService(new Intent(MainActivity.this, ImService.class));
                stopService(new Intent(MainActivity.this, PushService.class));
                try {
                    MyApp.conn.disconnect();
                    MyApp.conn = null;

                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                ExitApplication.getInstance().exit();

                break;
            case R.id.item_three:
                break;
            case R.id.item_four:
                break;
            case R.id.item_five:
                break;
            case R.id.item_six:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
