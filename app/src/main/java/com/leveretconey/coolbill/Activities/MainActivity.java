package com.leveretconey.coolbill.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.leveretconey.coolbill.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import Util.BillItem;
import Util.DBHelper;
import Util.YearItemAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DBHelper dbHelper;
    private static final String TAG = "MainActivity";
    TreeMap<Integer,TreeSet<Integer>> yearMonthAvailable
            =new TreeMap<Integer, TreeSet<Integer>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper=new DBHelper(MainActivity.this,
                "BillItem.db",null,1);
        initializeOnce();
        refreshAll();
    }
    private RecyclerView yearsRecycleView;
    void initializeOnce(){
        yearsRecycleView=(RecyclerView)findViewById(R.id.years_recycle_view);
    }
    void refreshAll() {
        refreshYearMonthAvailable();
        refreshDateSelectionLayout();
    }

    void refreshDateSelectionLayout(){
        yearsRecycleView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        YearItemAdapter yearItemAdapter=new YearItemAdapter(yearMonthAvailable);
        yearsRecycleView.setAdapter(yearItemAdapter);
    }
    void refreshYearMonthSelections(){

    }
    private static final String QUERY_YEAR_MONTH="select year,month from BillItem " +
                                                    "group by year,month";
    private int latestYear,latestMonth;
    void refreshYearMonthAvailable(){
        yearMonthAvailable.clear();
        latestMonth=latestYear=0;
        SQLiteDatabase db=dbHelper.getReadableDatabase();
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
        //Log.d(TAG, "refreshYearMonthAvailable: "+yearMonthAvailable);
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
                Calendar calendar=Calendar.getInstance();
                calendar.set(
                        cursor.getInt(year),
                        cursor.getInt(month) ,
                        cursor.getInt(day));
                billItems.add(new BillItem(
                        calendar,
                        cursor.getDouble(amount),
                        cursor.getString(mainType),
                        cursor.getString(subType),
                        cursor.getString(description)
                ));
            }
        }
        return  billItems;
    }

    @Override
    public void onClick(View v) {

    }
}
