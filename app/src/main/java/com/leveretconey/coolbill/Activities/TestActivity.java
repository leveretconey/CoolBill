package com.leveretconey.coolbill.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.leveretconey.coolbill.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.year_item_layout);

        initializeForTest();
    }
    void initializeForTest(){
    }
}
