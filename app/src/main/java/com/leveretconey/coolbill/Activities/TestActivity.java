package com.leveretconey.coolbill.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.leveretconey.coolbill.R;

import java.util.ArrayList;
import java.util.List;

import Share.MonthItemAdapter;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.year_item_layout);

        initializeForTest();
    }
    void initializeForTest(){
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.months_recycle_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<Integer> months= new ArrayList<Integer>();
        months.add(1);
        months.add(2);
        months.add(3);
        months.add(4);
        months.add(5);
        MonthItemAdapter monthItemAdapter=new MonthItemAdapter(months);
        recyclerView.setAdapter(monthItemAdapter);
    }
}
