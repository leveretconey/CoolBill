package com.leveretconey.coolbill.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.leveretconey.coolbill.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "WelcomeActivity";
    public static final int TOTAL_TIME_FOR_SKIP=5;
    int remainingTime =TOTAL_TIME_FOR_SKIP;

    Timer timer;

    private Button button;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        button=(Button)findViewById(R.id.welcome_skip_button);
        imageView=(ImageView) findViewById(R.id.welcome_image);
        button.setOnClickListener(this);
        updateButtonText();

        setWelcomeImage();
        timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                remainingTime--;
                if(remainingTime == 0){
                    startMainActivity();
                }else {
                    updateButtonText();
                }
            }
        };
        timer.schedule(task,1000,1000);
    }
    void setWelcomeImage(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    Glide.with(WelcomeActivity.this)
                            .load("https://cn.bing.com/az/hprichbg/rb/VallettaMalta_ZH-CN11321825930_1920x1080.jpg")
                            .into(imageView);
                }
                catch (Exception e){
                    Glide.with(WelcomeActivity.this).load(R.drawable.waifu).into(imageView);
                }
            }
        });
    }
    void updateButtonText(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setText("跳过 "+ remainingTime);
            }
        });
    }
    void startMainActivity(){
        Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.welcome_skip_button:
                startMainActivity();
                break;
            default:break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
