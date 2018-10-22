package com.leveretconey.coolbill.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.leveretconey.coolbill.R;

import java.util.Calendar;

import Util.AppContext;
import Util.Util;

public class AddActivity extends AppCompatActivity implements View.OnClickListener{


    EditText yearEditText,monthEditText,dayEditText,amountEditText,
            mainTypeEditText,subTypeEditText,descriptionEditText;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_confirm:
                tryToInsertBill();
                break;
            case R.id.add_cancel:
                showCancelAlertDialog();
                break;
            default:break;
        }
    }
    private void tryToInsertBill(){
        boolean isValid=true;
        StringBuilder sb=new StringBuilder();
        sb.append("发生错误:");
        String date=yearEditText.getText()+"/"+monthEditText.getText()+"/"+dayEditText.getText();
        if (!Util.isValidDate(date)){
            isValid=false;
            sb.append("\n日期格式错误");
        }
        if(amountEditText.getText().length()==0){
            isValid=false;
            sb.append("\n金额不能为空");
        }
        if(mainTypeEditText.getText().length()==0){
            isValid=false;
            sb.append("\n主类别不能为空");
        }
        if (isValid){
            try{
                SQLiteDatabase db= AppContext.dbHelper.getWritableDatabase();
                ContentValues contentValues=new ContentValues();
                contentValues.put("year",Integer.parseInt(yearEditText.getText().toString()));
                contentValues.put("month",Integer.parseInt(monthEditText.getText().toString()));
                contentValues.put("day",Integer.parseInt(dayEditText.getText().toString()));
                contentValues.put("amount",Double.parseDouble(amountEditText.getText().toString()));
                contentValues.put("mainType",amountEditText.getText().toString());
                contentValues.put("subType",amountEditText.getText().toString());
                contentValues.put("description",descriptionEditText.getText().toString());
                db.insert("BillItem",null,contentValues);

                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("成功")
                        .setMessage("新记录插入完成")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                returnMainActivity(true);
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();

            }catch (Exception e){
                e.printStackTrace();
                Util.showSimpleAlert(this,"失败","发生错误");
            }
        }else {
            Util.showSimpleAlert(this,"失败",sb.toString());
        }
    }
    void writeDefaultDate(){
        Calendar calendar=Calendar.getInstance();
        yearEditText.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        monthEditText.setText(String.valueOf(calendar.get(Calendar.MONTH)+1));
        dayEditText.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    }
    @Override
    public void onBackPressed() {
        showCancelAlertDialog();
    }

    private void showCancelAlertDialog(){
        new AlertDialog.Builder(AddActivity.this)
                .setTitle("退出")
                .setMessage("确定要退出吗？所有内容不会被保存")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        returnMainActivity(false);
                    }
                })
                .setNegativeButton("取消",null)
                .create().show();
    }
    private void returnMainActivity(boolean newBillInserted){
        Intent intent=new Intent();
        intent.putExtra(RESULT_NAME,newBillInserted);
        this.setResult(RESULT_CODE,intent);
        finish();
    }
    public static final String RESULT_NAME="inserted";
    public static final int RESULT_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ((Button)findViewById(R.id.add_cancel)).setOnClickListener(this);
        ((Button)findViewById(R.id.add_confirm)).setOnClickListener(this);
        bindViews();
        writeDefaultDate();
    }
    private void bindViews(){
        yearEditText=(EditText)findViewById(R.id.add_year);
        monthEditText=(EditText)findViewById(R.id.add_month);
        dayEditText=(EditText)findViewById(R.id.add_day);
        amountEditText=(EditText)findViewById(R.id.add_amount);
        mainTypeEditText=(EditText)findViewById(R.id.add_type_main);
        subTypeEditText=(EditText)findViewById(R.id.add_type_sub);
        descriptionEditText=(EditText)findViewById(R.id.add_description);
    }
    public static void startAction(Activity contextFrom){
        Intent intent=new Intent(contextFrom,AddActivity.class);
        contextFrom.startActivityForResult(intent,RESULT_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}
