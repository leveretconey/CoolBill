package com.leveretconey.coolbill.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leveretconey.coolbill.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import Share.BillItemDetailAdapter;
import Share.Global;
import Share.BillItem;
import Share.Util;
import Share.YearItemAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "MainActivity";
    private List<BillItem> queriedBillItems;
    TreeMap<Integer,TreeSet<Integer>> yearMonthAvailable
            =new TreeMap<Integer, TreeSet<Integer>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //todo
        Global.appInitialization(this);
        initializeOnce();
        refreshAll();
    }
    private RecyclerView yearsRecycleView;
    private RecyclerView billItemDetailRecycleView;
    private TextView billItemDetailNoneTextView;
    private LinearLayout bottomRelativeLayout;
    private TextView descriptionTextView ;
    private FloatingActionButton addButton;
    @SuppressWarnings("all")
    void initializeOnce(){
        yearsRecycleView=(RecyclerView)findViewById(R.id.years_recycle_view);
        billItemDetailRecycleView=(RecyclerView)findViewById(R.id.bill_detail_recycle_view);
        billItemDetailNoneTextView=(TextView)findViewById(R.id.main_find_nothing) ;
        bottomRelativeLayout=(LinearLayout) findViewById(R.id.main_bottom_board) ;
        descriptionTextView=(TextView)findViewById(R.id.main_description_text_view);
        addButton=(FloatingActionButton)findViewById(R.id.main_add_bill_button);


        Toolbar toolbar=(Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        bindOnClickListeners();
    }
    void refreshAll() {
        refreshYearMonthAvailable();
        refreshSelectedBillItems();

        refreshAllDetailSelection();

        refreshDateSelectionLayout();
        refreshBillItemsDetail();


        switchSelectedBillItemDetail(null,null);
        //todo
        tryLogAllDataInDataBase();
    }
    void refreshSelectedBillItems(){
        //todo
        SQLiteDatabase db= Global.dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from BillItem;",null);
        queriedBillItems =getAllBillItemsFromCursor(cursor);
    }
    void refreshBillItemsDetail(){
        if (queriedBillItems.size()==0){
            billItemDetailNoneTextView.setVisibility(View.VISIBLE);
            billItemDetailRecycleView.setVisibility(View.GONE);
        }else {
            billItemDetailNoneTextView.setVisibility(View.GONE);
            billItemDetailRecycleView.setVisibility(View.VISIBLE);
            billItemDetailRecycleView.setLayoutManager(new LinearLayoutManager(this));
            billItemDetailRecycleView.setAdapter(new BillItemDetailAdapter(this, queriedBillItems));
        }
    }
    @SuppressWarnings("all")
    void bindOnClickListeners(){
        addButton.setOnClickListener(this);
        ((Button)findViewById(R.id.main_modify_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.main_delete_button)).setOnClickListener(this);




        billItemDetailRecycleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    switchSelectedBillItemDetail (null,null);
                }
                return false;
            }
        });

    }
    void refreshDateSelectionLayout(){
        yearsRecycleView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        YearItemAdapter yearItemAdapter=new YearItemAdapter(yearMonthAvailable);
        yearsRecycleView.setAdapter(yearItemAdapter);
    }
    void refreshAllDetailSelection(){

    }
    private void tryLogAllDataInDataBase(){
        try{
            SQLiteDatabase db= Global.dbHelper.getReadableDatabase();
            Cursor cursor=db.rawQuery("select * from BillItem;",null);
            List<BillItem> billItems=getAllBillItemsFromCursor(cursor);
            for(BillItem billItem:billItems){
                Log.d(TAG, "tryLogAllDataInDataBase: "+billItem);
            }
            Log.d(TAG, "tryLogAllDataInDataBase: \n\n\n\n");
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
        SQLiteDatabase db= Global.dbHelper.getReadableDatabase();
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
                int id=cursor.getColumnIndex("id");
                int amount=cursor.getColumnIndex("amount");
                int year=cursor.getColumnIndex("year");
                int month=cursor.getColumnIndex("month");
                int day=cursor.getColumnIndex("day");
                int mainType=cursor.getColumnIndex("mainType");
                int subType=cursor.getColumnIndex("subType");
                int description=cursor.getColumnIndex("description");

                billItems.add(new BillItem(
                        cursor.getInt(id),
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
            case R.id.main_add_bill_button:
                ModifyActivity.startAction(MainActivity.this,null);
                break;
            case R.id.main_modify_button:
                if(selectedBillItem!=null){
                    ModifyActivity.startAction(this,selectedBillItem);
                }
                break;
            case R.id.bill_detail_recycle_view:
                Log.d(TAG, "onClick: click!!");
                switchSelectedBillItemDetail(null,null);
                break;
            case R.id.main_delete_button:
                deleteSelectedBillItem();
                break;
            default:break;
        }
    }
    void deleteSelectedBillItem(){
        if(selectedBillItem!=null){
            new AlertDialog.Builder(this)
                .setTitle("删除")
                .setMessage("确认要删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            SQLiteDatabase db=Global.dbHelper.getWritableDatabase();
                            db.delete("BillItem","id=?"
                                    ,new String[]{String.valueOf(selectedBillItem.getId())});
                            switchSelectedBillItemDetail(null,null);
                            refreshAll();
                        }
                        catch (Exception e){
                            Util.showSimpleAlert(MainActivity.this,
                                    "失败","发生错误");
                        }
                    }
                })
                .setNegativeButton("取消",null)
                .create().show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case ModifyActivity.RESULT_CODE:

                boolean needToRefresh=data.getBooleanExtra(ModifyActivity.RESULT_NAME,false);
                if (needToRefresh){
                    refreshAll();
                }
                break;
            default:break;
        }
    }
    private View selectedBillItemView=null;
    private BillItem selectedBillItem=null;
    public void switchSelectedBillItemDetail(View view,BillItem billItem){
        if(selectedBillItem !=null){
            selectedBillItemView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        }

        if(selectedBillItemView ==null && view!=null){
            bottomRelativeLayout.setVisibility(View.VISIBLE);
        }else   if(selectedBillItemView !=null && view==null){
            bottomRelativeLayout.setVisibility(View.GONE);
        }
        selectedBillItemView=view;
        selectedBillItem=billItem;
        if (selectedBillItem!=null) {
            selectedBillItemView.setBackgroundColor(getResources().getColor(R.color.colorGrey));
            String description=selectedBillItem.getDescription();
            if(description!=null && description.length()>0){
                descriptionTextView.setText(description);
                descriptionTextView.setVisibility(View.VISIBLE);
            }else {
                descriptionTextView.setText("");
                descriptionTextView.setVisibility(View.GONE);
            }
        }
    }
}
