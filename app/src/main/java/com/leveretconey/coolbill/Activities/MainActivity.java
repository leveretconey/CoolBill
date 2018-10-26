package com.leveretconey.coolbill.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leveretconey.coolbill.R;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import Share.BillItemDetailAdapter;
import Share.Global;
import Share.BillItem;
import Share.HttpUtil;
import Share.Options;
import Share.Util;
import Share.YearItemAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "MainActivity";
    private List<BillItem> queriedBillItems;
    TreeMap<Integer, TreeSet<Integer>> yearMonthAvailable
            = new TreeMap<Integer, TreeSet<Integer>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Global.appInitialization(this);
        initializeOnce();
        refreshAll();
    }

    private RecyclerView yearsRecycleView;
    private RecyclerView billItemDetailRecycleView;
    private TextView billItemDetailNoneTextView;
    private LinearLayout bottomRelativeLayout;
    private TextView descriptionTextView;
    private YearItemAdapter yearItemAdapter;

    @SuppressWarnings("all")
    void initializeOnce() {
        yearsRecycleView = (RecyclerView) findViewById(R.id.years_recycle_view);
        billItemDetailRecycleView = (RecyclerView) findViewById(R.id.bill_detail_recycle_view);
        billItemDetailNoneTextView = (TextView) findViewById(R.id.main_find_nothing);
        bottomRelativeLayout = (LinearLayout) findViewById(R.id.main_bottom_board);
        descriptionTextView = (TextView) findViewById(R.id.main_description_text_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        bindListeners();
        monthSelected=yearSelected=Integer.MIN_VALUE;
    }

    public void refreshAll() {
        refreshBillItemDetail();
        refreshDateSelectionLayout();
    }
    public void refreshBillItemDetail(){
        queryBillItemsUsingConditions();
        refreshBillItemsDetailRecycleView();
        switchSelectedBillItemDetail(null, null);
    }
    final String[] columns=new String[]{"id","year","month","day","amount",
            "mainType","subType","description"};
    private void queryBillItemsUsingConditions() {

        SQLiteDatabase db = Global.dbHelper.getReadableDatabase();
        String selections=null;
        String[] selectionArgs=null;
        if(monthSelected!=Integer.MIN_VALUE){
            selections="year=? and month=?";
            selectionArgs=new String[]{String.valueOf(yearSelected)
                                    ,String.valueOf(monthSelected)};
        }else if (yearSelected!=Integer.MIN_VALUE){
            selections="year=?";
            selectionArgs=new String[]{String.valueOf(yearSelected)};
        }
        Cursor cursor = db.query("BillItem",columns,selections,selectionArgs,
                null,null,"year,month,day");
        queriedBillItems = getAllBillItemsFromCursor(cursor);
    }

    private void refreshBillItemsDetailRecycleView() {
        if (queriedBillItems.size() == 0) {
            billItemDetailNoneTextView.setVisibility(View.VISIBLE);
            billItemDetailRecycleView.setVisibility(View.GONE);
            yearItemAdapter = null;
        } else {
            billItemDetailNoneTextView.setVisibility(View.GONE);
            billItemDetailRecycleView.setVisibility(View.VISIBLE);
            billItemDetailRecycleView.setLayoutManager(new LinearLayoutManager(this));
            billItemDetailRecycleView.setAdapter(new BillItemDetailAdapter(this, queriedBillItems));
        }
    }

    @SuppressWarnings("all")
    void bindListeners() {
        bindDrawerEvents();

        ((Button) findViewById(R.id.main_modify_button)).setOnClickListener(this);
        ((Button) findViewById(R.id.main_delete_button)).setOnClickListener(this);


        billItemDetailRecycleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    switchSelectedBillItemDetail(null, null);
                }
                return false;
            }
        });
        yearsRecycleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    resetSelect();
                }
                return false;
            }
        });
    }
    void bindDrawerEvents(){
        DrawerLayout drawer=findViewById(R.id.main_draw);
        //todo
    }
    void refreshDateSelectionLayout() {
        selectYearMonth(Integer.MIN_VALUE,Integer.MIN_VALUE);
        refreshYearMonthAvailable();
        yearsRecycleView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        yearItemAdapter = new YearItemAdapter(yearMonthAvailable, this);
        yearsRecycleView.setAdapter(yearItemAdapter);

    }
    private static final String QUERY_YEAR_MONTH = "select year,month from BillItem " +
            "group by year,month";

    void refreshYearMonthAvailable() {
        yearMonthAvailable.clear();
        SQLiteDatabase db = Global.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_YEAR_MONTH, null);
        int yearIndex = cursor.getColumnIndex("year");
        int monthIndex = cursor.getColumnIndex("month");
        while (cursor.moveToNext()) {
            int year = cursor.getInt(yearIndex);
            int month = cursor.getInt(monthIndex);
            if (!yearMonthAvailable.containsKey(year)) {
                yearMonthAvailable.put(year, new TreeSet<Integer>());
            }
            yearMonthAvailable.get(year).add(month);
        }
        cursor.close();
    }

    List<BillItem> getAllBillItemsFromCursor(Cursor cursor) {
        List<BillItem> billItems = new ArrayList<BillItem>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getColumnIndex("id");
                int amount = cursor.getColumnIndex("amount");
                int year = cursor.getColumnIndex("year");
                int month = cursor.getColumnIndex("month");
                int day = cursor.getColumnIndex("day");
                int mainType = cursor.getColumnIndex("mainType");
                int subType = cursor.getColumnIndex("subType");
                int description = cursor.getColumnIndex("description");

                billItems.add(new BillItem(
                        cursor.getInt(id),
                        cursor.getInt(year),
                        cursor.getInt(month),
                        cursor.getInt(day),
                        cursor.getDouble(amount),
                        cursor.getString(mainType),
                        cursor.getString(subType),
                        cursor.getString(description)
                ));
            }
        }
        cursor.close();
        return billItems;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_modify_button:
                if (selectedBillItem != null) {
                    ModifyActivity.startAction(this, selectedBillItem);
                }
                break;
            case R.id.main_delete_button:
                deleteSelectedBillItem();
                break;
            default:
                break;
        }
    }

    private void deleteSelectedBillItem() {
        if (selectedBillItem != null) {
            new AlertDialog.Builder(this)
                    .setTitle("删除")
                    .setMessage("确认要删除吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                SQLiteDatabase db = Global.dbHelper.getWritableDatabase();
                                db.delete("BillItem", "id=?"
                                        , new String[]{String.valueOf(selectedBillItem.getId())});
                                switchSelectedBillItemDetail(null, null);
                                refreshDetailAndRefreshDateIFNecessary();
                            } catch (Exception e) {
                                Util.showSimpleAlert(MainActivity.this,
                                        "失败", "发生错误");
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create().show();
        }
    }
    private void refreshDetailAndRefreshDateIFNecessary(){
        queryBillItemsUsingConditions();
        if(queriedBillItems.size()==0){
            resetSelect();
        }
        refreshBillItemDetail();
        refreshDateSelectionLayout();
    }
    private void resetSelect(){
        selectYearMonth(Integer.MIN_VALUE,Integer.MIN_VALUE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ModifyActivity.RESULT_CODE:

                boolean needToRefresh = data.getBooleanExtra(ModifyActivity.RESULT_NAME, true);
                if (needToRefresh) {
                    refreshDetailAndRefreshDateIFNecessary();
                }
                break;
            default:
                break;
        }
    }

    private View selectedBillItemView = null;
    private BillItem selectedBillItem = null;

    public void switchSelectedBillItemDetail(View view, BillItem billItem) {
        if (selectedBillItem != null) {
            selectedBillItemView.setBackgroundColor(getResources().getColor(R.color.colorNotSelect));
        }

        if (selectedBillItemView == null && view != null) {
            bottomRelativeLayout.setVisibility(View.VISIBLE);
        } else if (selectedBillItemView != null && view == null) {
            bottomRelativeLayout.setVisibility(View.GONE);
        }
        selectedBillItemView = view;
        selectedBillItem = billItem;
        if (selectedBillItem != null) {
            selectedBillItemView.setBackgroundColor(getResources().getColor(R.color.colorSelect));
            String description = selectedBillItem.getDescription();
            if (description != null && description.length() > 0) {
                descriptionTextView.setText(description);
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setText("");
                descriptionTextView.setVisibility(View.GONE);
            }
        }
    }

    public void toggleYearItem(int year) {
        LinearLayout yearItemLayout = yearItemAdapter.getYearItemLayout(year);
        if (yearItemLayout != null) {
            Button button = (Button) yearItemLayout.findViewById(R.id.year_toggle);
            LinearLayout monthLayout = (LinearLayout)
                    yearItemLayout.findViewById(R.id.year_months_layout);
            if (monthLayout.getVisibility() == View.GONE) {
                monthLayout.setVisibility(View.VISIBLE);
                button.setBackground(getDrawable(R.drawable.up));
            } else {
                monthLayout.setVisibility(View.GONE);
                button.setBackground(getDrawable(R.drawable.down));
            }
        }
    }

    private int yearSelected ;
    private int monthSelected ;

    public void selectYearMonth(int year, int month) {
        if (yearSelected != Integer.MIN_VALUE) {
            RelativeLayout yearLayout = (RelativeLayout)yearItemAdapter
                    .getYearItemLayout(yearSelected).getChildAt(0);
            if (yearLayout != null) {
                yearLayout.setBackground(getDrawable(R.drawable.month_item_background));
                if (monthSelected != Integer.MIN_VALUE) {
                    TextView monthLayout = yearItemAdapter.getMonthTextView(yearSelected, monthSelected);
                    if (monthLayout != null) {
                        monthLayout.setBackground(getDrawable(R.drawable.month_item_background));
                    }
                }
            }
        }

        if (!(year == Integer.MIN_VALUE       ||
             (month==Integer.MIN_VALUE && year == yearSelected)))
        {
            RelativeLayout yearLayout = (RelativeLayout) yearItemAdapter
                    .getYearItemLayout(year).getChildAt(0);
            if (yearLayout != null) {
                yearLayout.setBackground(getDrawable(R.drawable.month_item_selected_background));
            }
            if (month != Integer.MIN_VALUE    &&
               (monthSelected!=month || year!=yearSelected) ){
                TextView monthLayout = yearItemAdapter.getMonthTextView(year, month);
                monthSelected=month;
                if (monthLayout != null) {
                    monthLayout.setBackground(getDrawable(R.drawable.month_item_selected_background));
                }
            }else {
                monthSelected=Integer.MIN_VALUE;
            }
            yearSelected=year;
        }else {
            yearSelected=Integer.MIN_VALUE;
        }
        if (yearSelected==Integer.MIN_VALUE)
            monthSelected=Integer.MIN_VALUE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_add_bill_button:
                ModifyActivity.startAction(MainActivity.this, null);
                break;
            case R.id.main_statistics:
                showStatistics();
                break;
            case R.id.main_backup:
                backupData();
                break;
            case R.id.main_recover:
                recoverData();
                break;
            case R.id.main_options:
                OptionsActivity.startAction(this);
                break;
            case R.id.main_history:
                showHistory();
                break;
            default:
                break;
        }
        return true;
    }
    private void showHistory(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.history_layout,null);
        final Dialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        ((ImageView)view.findViewById(R.id.history_image)).setImageBitmap(makeHistoryGraph());
        ((TextView)view.findViewById(R.id.history_quit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }
    public static final int COUNT_YEAR_IN_HISTORY=3;
    private Bitmap makeHistoryGraph(){
        HashMap<Integer,HashMap<Integer,Double>> data=prepareHistoryData();
        double maxSum=0.0;
        List<Double> sums=new ArrayList<>();
        List<Integer> years=new ArrayList<>();
        List<Integer> months=new ArrayList<>();
        for (HashMap.Entry<Integer,HashMap<Integer,Double>> entry1:data.entrySet()){
            for (HashMap.Entry<Integer,Double> entry2:entry1.getValue().entrySet()){
                sums.add(entry2.getValue());
                years.add(entry1.getKey());
                months.add(entry2.getKey());
            }
        }
        int length=years.size();
        if (maxSum==0.0){
            maxSum=100.0;
        }

        final int HEIGHT=300;
        final int textSize=15;
        final int MONTH_OCCUPY_WIDTH=80;
        final int TOP=(int)(HEIGHT*0.1),DOWN=(int)(HEIGHT*0.80);
        final int WIDTH=length * MONTH_OCCUPY_WIDTH;
        final int COUNT_REFERENCE_LINE=5;
        final int INTERVAL=15;

        Bitmap bitmap=Bitmap.createBitmap(WIDTH,HEIGHT,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);

        Paint linePaint=new Paint();
        linePaint.setColor(getResources().getColor(R.color.colorPrimary));
        linePaint.setAntiAlias(true);

        Paint bgPaint=new Paint();
        bgPaint.setAlpha(100);
        bgPaint.setColor(getResources().getColor(R.color.colorGrey));

        Paint textPaint=new Paint();
        textPaint.setColor(getResources().getColor(R.color.colorBlack));
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawLine(0,DOWN,WIDTH,DOWN,textPaint);

        float dy=(DOWN-TOP)/(COUNT_REFERENCE_LINE);
        for(int i=1;i<=COUNT_REFERENCE_LINE;i++){
            float y=-i * dy + DOWN;
            canvas.drawLine(0,y,WIDTH,y,bgPaint);
        }
        double pixelPerSum=(DOWN-TOP)/maxSum;
        for(int i=0;i<length;i++){
            float x1=i * MONTH_OCCUPY_WIDTH+MONTH_OCCUPY_WIDTH/2.0f;
            float y1=DOWN-(float) (sums.get(i) * pixelPerSum);
            canvas.drawCircle(x1,y1,4,linePaint);
            canvas.drawText(String.valueOf(sums.get(i)),x1,y1-INTERVAL,textPaint);
            canvas.drawText(String.valueOf(years.get(i)+"年"),x1,DOWN+INTERVAL+3,textPaint);
            canvas.drawText(String.valueOf(months.get(i)+"月"),x1,DOWN+2*INTERVAL+5,textPaint);
            if(i > 0){
                float x2=x1-MONTH_OCCUPY_WIDTH;
                float y2=DOWN-(float) (sums.get(i-1) * pixelPerSum);
                canvas.drawLine(x1,y1,x2,y2,linePaint);
            }

        }
        return bitmap;
    }
    HashMap<Integer,HashMap<Integer,Double>> prepareHistoryData(){
        HashMap<Integer,HashMap<Integer,Double>> map=new HashMap<>();
        Calendar today=Calendar.getInstance();
        int beginYear,beginMonth;
        int todayYear=today.get(Calendar.YEAR);
        int todayMonth=today.get(Calendar.MONTH)+1;
        if (todayMonth ==12){
            beginYear=todayYear-COUNT_YEAR_IN_HISTORY+1;
            beginMonth=1;
        }else {
            beginYear=todayYear-COUNT_YEAR_IN_HISTORY;
            beginMonth=todayMonth+1;
        }
        String sql="select year,month,sum(amount) as sum from BillItem group by" +
                " year,month order by year,month;";
        SQLiteDatabase database=Global.dbHelper.getReadableDatabase();
        Cursor cursor=database.rawQuery(sql,null);
        boolean first=true;
        int columnYear=cursor.getColumnIndex("year");
        int columnMonth=cursor.getColumnIndex("month");
        int columnSum=cursor.getColumnIndex("sum");
        while (cursor.moveToNext()){
            int year,month;
            double sum;
            year=cursor.getInt(columnYear);
            month=cursor.getInt(columnMonth);
            sum=cursor.getDouble(columnSum);
            if (year < beginYear || year== beginYear && month <beginMonth){
                continue;
            }else if (year > todayYear || (year==todayYear && month >todayMonth)){
                continue;
            }
            if (first){
                first=false;
                beginYear=year;
                beginMonth=month;
                if (beginYear==todayYear){
                    map.put(beginYear,new HashMap<Integer, Double>());
                    for(int i=beginMonth;i<=todayMonth;i++){
                        map.get(beginYear).put(i,0.0);
                    }
                }else {
                    for(int i=beginYear;i<=todayYear;i++){
                        map.put(i,new HashMap<Integer, Double>());
                        for(int j=1;j<=12;j++){
                            if(i ==beginYear && j >=beginMonth){
                                map.get(i).put(j,0.0);
                            }else if (i==todayYear && j<=todayMonth){
                                map.get(i).put(j,0.0);
                            }else if( i>beginYear && i<todayYear){
                                map.get(i).put(j,0.0);
                            }
                        }
                    }
                }
            }
            map.get(year).put(month,sum);
        }
        if (first){
            map.put(todayYear,new HashMap<Integer, Double>());
            map.get(todayYear).put(todayMonth,0.0);
        }
        cursor.close();
        return map;
    }
    private final static String SELECT_ALL="select * from BillItem;";
    private  void backupData(){
        //手机和电脑连接不了，先不弄这个功能了
        //todo
        final Context context=this;
        WaitingActivity.startAction(context);
        SQLiteDatabase database=Global.dbHelper.getReadableDatabase();
        Cursor cursor=database.rawQuery(SELECT_ALL,null);
        List<BillItem> billItems=getAllBillItemsFromCursor(cursor);
        HttpUtil.sendOKHttpRequestPOST(
            "http://"+Options.getInstance().getBackupServerUrl() + "/cool_bill_server/main",
            BillItem.toJson(billItems),
            new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    new AlertDialog.Builder(MainActivity.this)
                        .setTitle("失败")
                        .setMessage("备份失败")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                WaitingActivity.endAction(MainActivity.this);
                            }
                        });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d(TAG, "onResponse: succeed");
                    WaitingActivity.endAction(MainActivity.this);
                }
            }
        );
        Log.d(TAG, "backupData: request send");
    }
    private void recoverData(){
        //todo
    }
    private HashMap<String,Double> getAmountAccordingToMainType(List<BillItem> billItems){
        HashMap<String,Double> mainType2sum=new HashMap<>();
        for (BillItem billItem : billItems) {
            String mainType = billItem.getMainType();

            if (!mainType2sum.containsKey(mainType)) {
                mainType2sum.put(mainType, 0.0);
            }
            mainType2sum.put(mainType, mainType2sum.get(mainType) + billItem.getAmount());
        }
        return mainType2sum;
    }
    private void showStatistics(){
        StringBuilder title=new StringBuilder(),message=new StringBuilder();

        if(queriedBillItems.size()==0){
            title.append("提示");
            message.append("查找不到任何记录哦");
        }else {
            if (yearSelected==Integer.MIN_VALUE){
                int  earlyYear=Integer.MAX_VALUE,earlyMonth=Integer.MAX_VALUE
                    ,lateYear=Integer.MIN_VALUE,lateMonth=Integer.MIN_VALUE;
                for(BillItem billItem :queriedBillItems) {
                    int year = billItem.getYear(), month = billItem.getMonth();
                    if (year<earlyYear ||(year==earlyYear && month < earlyMonth)){
                        earlyMonth=month;
                        earlyYear=year;
                    }
                    if (year > lateYear | (year==lateYear && month > lateMonth)){
                        lateMonth=month;
                        lateYear=year;
                    }
                }
                title.append(earlyYear).append("年").append(earlyMonth).append("月")
                     .append("---")
                     .append(lateYear).append("年").append(lateMonth).append("月");
            }else{
                title.append(yearSelected).append("年");
                if (monthSelected !=Integer.MIN_VALUE){
                    title.append(monthSelected).append("月");
                }
            }
            HashMap<String,Double> mainType2sum=getAmountAccordingToMainType(queriedBillItems);
            for(Map.Entry<String,Double> entry : mainType2sum.entrySet()){
                message.append(entry.getKey()).append(":").append(entry.getValue())
                        .append("\n");
            }
        }
        Share.Util.showSimpleAlert(this,title.toString(),message.toString());
    }
}