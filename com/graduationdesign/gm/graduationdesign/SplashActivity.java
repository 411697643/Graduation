package com.graduationdesign.gm.graduationdesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import org.jivesoftware.smack.SmackAndroid;

/**
 * Created by Administrator on 2016/5/6.
 */
public class SplashActivity extends Activity {

    private ImageView mImageView;
    private AnimationSet mSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mImageView = (ImageView) findViewById(R.id.iv_kb);
        SmackAndroid.init(getApplicationContext());
        init();
        setListener();
    }

    private void init() {
        mSet = new AnimationSet(true);
        ScaleAnimation mScale = new ScaleAnimation(0,1,0,1, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mScale.setDuration(500);
        mSet.addAnimation(mScale);
        AlphaAnimation mAlpha = new AlphaAnimation(0,1.0f);
        mAlpha.setDuration(500);
        mSet.addAnimation(mAlpha);
        mImageView.setAnimation(mSet);
    }

    private void setListener() {
        mSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
