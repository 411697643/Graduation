package com.graduationdesign.gm.graduationdesign.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.graduationdesign.gm.graduationdesign.BaseActivity;
import com.graduationdesign.gm.graduationdesign.R;
import com.graduationdesign.gm.graduationdesign.service.ChatService;
import com.graduationdesign.gm.graduationdesign.service.ImService;
import com.graduationdesign.gm.graduationdesign.service.PushService;
import com.graduationdesign.gm.graduationdesign.utils.SPUtils;
import com.graduationdesign.gm.graduationdesign.utils.ThreadUtils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

/**
 * Created by Administrator on 2016/5/6.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private Button login;
    private EditText account;
    private EditText pwd;
    private String username;
    private String password;
    private boolean flag = false;
    private XMPPTCPConnection conn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();


        ThreadUtils.runInThread(new Runnable() {

            @Override
            public void run() {

                try {
                    ConnectionConfiguration configuration = new ConnectionConfiguration(MyApp.HOST, MyApp.PORT);
                    configuration.setDebuggerEnabled(true);
                    configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                    configuration.setReconnectionAllowed(true);
                    SASLAuthentication.supportSASLMechanism("PLAIN",0);
                    conn = new XMPPTCPConnection(configuration);
                    conn.connect();//连接
                    Log.i(TAG, "run: conn.isConnected() :----->"+conn.isConnected());
                    MyApp.conn = conn;
                    int i = 0;
                    while (!conn.isConnected()) {
                        Log.i(TAG, "run: conn.isConnected() :----->"+conn.isConnected());
                        conn.connect();
                        i++;
                        Log.i(TAG, "run: ------------------>"+i);
                    }
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        login = (Button) findViewById(R.id.login);
        account = (EditText) findViewById(R.id.account);
        pwd = (EditText) findViewById(R.id.pwd);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:

                //获取账号密码
                username = account.getText().toString().trim();
                password = pwd.getText().toString().trim();
                SPUtils.addInfo("username",username,LoginActivity.this);
                SPUtils.addInfo("password",password,LoginActivity.this);
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (conn.isConnected()) {
                                conn.login(username, password);
                                flag = true;
                            } else {
                                flag = false;
                            }

                        } catch (XMPPException e) {
                            e.printStackTrace();
                            flag = false;
                        } catch (SmackException e) {
                            e.printStackTrace();
                            flag = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                            flag = false;
                        }

                        ThreadUtils.runUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (flag) {
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    MyApp.username = username;
                                    MyApp.account = username + "@" + MyApp.SERVER_NAME;
                                    //开启操作数据库的服务
                                    startService(new Intent(LoginActivity.this, ChatService.class));
                                    startService(new Intent(LoginActivity.this, ImService.class));
                                    startService(new Intent(LoginActivity.this, PushService.class));
                                    startActivity(new Intent(LoginActivity.this, MajorActiviy.class));
                                } else {
                                    Toast.makeText(LoginActivity.this, "登陆失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
//                finish();


                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
