package com.leveretconey.coolbill.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.leveretconey.coolbill.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import Util.AppContext;
import Util.BillItem;
import Util.DBHelper;
import Util.YearItemAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "MainActivity";
    TreeMap<Integer,TreeSet<Integer>> yearMonthAvailable
            =new TreeMap<Integer, TreeSet<Integer>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //todo
        AppContext.appInitialization(this);

        initializeOnce();
        refreshAll();
    }
    private RecyclerView yearsRecycleView;
    void initializeOnce(){
        yearsRecycleView=(RecyclerView)findViewById(R.id.years_recycle_view);
        binOnClickListeners();
    }
    void refreshAll() {
        refreshYearMonthAvailable();
        refreshDateSelectionLayout();
        //todo
        tryLogAllDataInDataBase();
    }
    void binOnClickListeners(){
        ((FloatingActionButton)findViewById(R.id.add_bill_button)).setOnClickListener(this);
    }
    void refreshDateSelectionLayout(){
        yearsRecycleView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        YearItemAdapter yearItemAdapter=new YearItemAdapter(yearMonthAvailable);
        yearsRecycleView.setAdapter(yearItemAdapter);
    }
    void refreshYearMonthSelections(){

    }
    private void tryLogAllDataInDataBase(){
        try{
            SQLiteDatabase db=AppContext.dbHelper.getReadableDatabase();
            Cursor cursor=db.rawQuery("select * from BillItem;",null);
            List<BillItem> billItems=getAllBillItemsFromCursor(cursor);
            for(BillItem billItem:billItems){
                Log.d(TAG, "tryLogAllDataInDataBase: "+billItem);
            }
        }catch (Exception e){
            Log.d(TAG, "tryLogAllDataInDataBase: error");
        }

    }
    private static final String QUERY_YEAR_MONTH="select year,month from BillItem " +
                                                    "group by year,month";
    private int latestYear,latestMonth;
    void refreshYearMonthAvailable(){
        yearMonthAvailable.clear();
        latestMonth=latestYear=0;
        SQLiteDatabase db= AppContext.dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(QUERY_YEAR_MONTH,null);
        int yearIndex=cursor.getColumnIndex("year");
        int monthIndex=cursor.getColumnIndex("month");
        while(cursor.moveToNext()){
            int year=cursor.getInt(yearIndex);
            int month=cursor.getInt(monthIndex);
            if(!yearMonthAvailable.containsKey(year)){
                yearMonthAvailable.put(year,new TreeSet<Integer>());
            }
            yearMonthAvailable.get(year).add(month);
            if(year >latestYear || (year == latestYear && month > latestMonth) ){
                latestYear=year;
                latestMonth=month;
            }
        }
        cursor.close();
    }
    List<BillItem> getAllBillItemsFromCursor(Cursor cursor){
        List<BillItem> billItems=new ArrayList<BillItem>();
        if (cursor!=null){
            while (cursor.moveToNext()){
                int amount=cursor.getColumnIndex("amount");
                int year=cursor.getColumnIndex("year");
                int month=cursor.getColumnIndex("month");
                int day=cursor.getColumnIndex("day");
                int mainType=cursor.getColumnIndex("mainType");
                int subType=cursor.getColumnIndex("subType");
                int description=cursor.getColumnIndex("description");

                billItems.add(new BillItem(
                        cursor.getInt(year),
                        cursor.getInt(month) ,
                        cursor.getInt(day),
                        cursor.getDouble(amount),
                        cursor.getString(mainType),
                        cursor.getString(subType),
                        cursor.getString(description)
                ));
            }
        }
        cursor.close();
        return  billItems;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_bill_button:
                AddActivity.startAction(MainActivity.this);
                break;
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case AddActivity.RESULT_CODE:

                boolean needToRefresh=data.getBooleanExtra(AddActivity.RESULT_NAME,false);
                if (needToRefresh){
                    refreshAll();
                }
                break;
            default:break;
        }
    }
}
