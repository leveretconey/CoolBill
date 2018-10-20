package com.leveretconey.coolbill.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.leveretconey.coolbill.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Util.BillItem;
import Util.DBHelper;

public class MainActivity extends AppCompatActivity {

    List<BillItem> billItems=new ArrayList<BillItem>();
    DBHelper dbHelper;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllBillItems();
        dbHelper=new DBHelper(MainActivity.this,
                "BillItem.db",null,1);
        Log.d(TAG, "onCreate: "+billItems);
    }
    void getAllBillItems(){
        billItems.clear();
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from BillItem",null);
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
                        new Date(
                          cursor.getInt(year),
                          cursor.getInt(month) ,
                          cursor.getInt(day)
                        ),
                        cursor.getDouble(amount),
                        cursor.getString(mainType),
                        cursor.getString(subType),
                        cursor.getString(description)
                ));
            }
        }
    }
}
