package com.leveretconey.coolbill.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.leveretconey.coolbill.R;

public class WaitingActivity extends AppCompatActivity {

    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;

    private static final String BROADCAST_NAME="com.leveretconey.collbill.FINISH_WAITING";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        setFinishBroadCast();
    }
    void setFinishBroadCast(){
        broadcastManager=LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BROADCAST_NAME);
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver,intentFilter);
    }
    public static void startAction(Context context){
        Intent intent=new Intent(context,WaitingActivity.class);
        context.startActivity(intent);
    }
    public static void endAction(Context context){
        Intent intent=new Intent(BROADCAST_NAME);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {}
}
