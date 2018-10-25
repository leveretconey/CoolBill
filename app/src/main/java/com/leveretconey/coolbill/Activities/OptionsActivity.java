package com.leveretconey.coolbill.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leveretconey.coolbill.R;

import java.io.IOException;
import java.security.InvalidParameterException;

import Share.Options;
import Share.Util;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ipLayout;
    private TextView portView;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.options_cancel:
                finish();
                break;
            case R.id.options_confirm:
                if (saveOptions())
                    finish();
                break;
            default:
                break;
        }

    }
    void loadDefaultOption(){
        Options options=Options.getInstance();
        String[] ipPart=options.getIpAddress().split("\\.");
        int port=options.getPort();
        for(int i=0;i<4;i++){
            TextView textView=(TextView) (ipLayout.getChildAt(1+2*i));
            textView.setText(ipPart[i]);
        }
        portView.setText(String.valueOf(port));
    }
    boolean saveOptions(){
        boolean result=true;
        try{

            int[] ipPart=new int[4];
            for(int i=0;i<4;i++){
                TextView textView=(TextView) (ipLayout.getChildAt(1+2*i));
                ipPart[i]=Integer.parseInt(textView.getText().toString());
            }
            Options.getInstance().setAddress(ipPart)
                    .setPort(Integer.parseInt(portView.getText().toString()))
                    .save(this);
        }catch (InvalidParameterException e){
            Util.showSimpleAlert(this,"错误","数据格式错误");
            e.printStackTrace();
            result=false;
        }
        return result;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);


        portView=(TextView)findViewById(R.id.option_port);
        ipLayout=(LinearLayout)findViewById(R.id.option_address);

        bindOnClickListeners();
        loadDefaultOption();
    }
    void bindOnClickListeners(){
        ((Button)findViewById(R.id.options_cancel)).setOnClickListener(this);
        ((Button)findViewById(R.id.options_confirm)).setOnClickListener(this);
    }
    public static void startAction(Context context){
        Intent intent=new Intent(context,OptionsActivity.class);
        context.startActivity(intent);
    }
}
