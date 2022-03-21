package com.armdri.myracle;

import com.armdri.myracle.DateUtil.Format;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";

    Fragment chatFragment;
    Fragment calendarFragment;
    BottomNavigationView bottomNavigationView;
    RecordDatabaseHelper recordDBHelper;

    public boolean updateFlag;

    public void setUpdateFlag(boolean set){ updateFlag = set; }
    public boolean getUpdateFlag(){ return updateFlag; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(recordDBHelper == null) recordDBHelper = new RecordDatabaseHelper(this);
        chatFragment = new ChatFragment();
        //calendarFragment = new CalendarFragment();
        initCalendar();

        //제일 처음 띄워줄 뷰를 세팅해줍니다.
        getSupportFragmentManager().beginTransaction().replace(R.id.container, chatFragment).commit();
        getSupportActionBar().hide();
        // 바텀 네비게이션 객체 선언
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 바텀 네비게이션 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case  R.id.fragment_chat:
                        // replace(프레그먼트를 띄워줄 frameLayout, 교체할 fragment 객체)
                        if(chatFragment == null) {
                            chatFragment = new ChatFragment();
                            getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().show(chatFragment).commit();
                        }

                        if(calendarFragment != null) getSupportFragmentManager().beginTransaction().hide(calendarFragment).commit();
                        getSupportActionBar().hide();
                        return  true;
                    case  R.id.fragment_calendar:
                        if(calendarFragment == null) {
                            calendarFragment = new CalendarFragment();
                            getSupportFragmentManager().beginTransaction().add(R.id.container, calendarFragment).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().show(calendarFragment).commit();
                        }

                        if(chatFragment != null) getSupportFragmentManager().beginTransaction().hide(chatFragment).commit();
                        getSupportActionBar().show();
                        return  true;
                    default:
                        return false;
                }

            }
        });

//        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
//            @Override
//            public void onToggleSoftKeyboard(boolean isVisible) {
//                Log.d("keyboard", "keyboard visible: "+isVisible);
//                if(isVisible) hideBottomNav();
//                else          showBottomNav();
//            }
//        });
    }

    public void showBottomNav() { bottomNavigationView.setVisibility(View.VISIBLE); }

    public void hideBottomNav() { bottomNavigationView.setVisibility(View.GONE); }

    //오늘자 캘린더 초기화
    public void initCalendar(){
        if(recordDBHelper == null) recordDBHelper = new RecordDatabaseHelper(this);
        Format today = new DateUtil.Format();

        Cursor cursor = recordDBHelper.readRecordData();

        if(cursor.getCount() == 0) {
            Record todayRecord = new Record(today, 0);
            addRecordDB(todayRecord);
            setUpdateFlag(true);
            return;
        }

        Format lastDay = readLastRecord(recordDBHelper).dateFormat;

        while(!(lastDay.stringFormat).equals(today.stringFormat)){
            lastDay = new Format(DateUtil.getNextDate(lastDay.stringFormat));
            Record lastRecord = new Record(lastDay, 0);
            addRecordDB(lastRecord);
        }
        setUpdateFlag(true);

    }

    //오늘자 레코드 갱신
    public boolean updateTodayCalendar(){
        Record lastRecord = readLastRecord(recordDBHelper);
        Format today = new Format();

        if(lastRecord == null) {
            addRecordDB(new Record(today, 1));
            setUpdateFlag(true);
            return true;
        }

        if((lastRecord.dateFormat.stringFormat).equals(today.stringFormat)) {
            Log.d(TAG, "DB의 마지막 레코드가 오늘 날짜와 동일");
            if((lastRecord.color) == 0) {
                Log.d(TAG, "오늘자 레코드 color 업데이트하고 DB에 저장");

                Cursor cursor = recordDBHelper.readRecordData();
                if(cursor.getCount() < 2){
                    (lastRecord.color) = 1;
                } else {
                    cursor.moveToLast();
                    cursor.moveToPrevious();
                    (lastRecord.color) = Math.min( cursor.getInt(2) + 1, 4 );
                }

                updateRecordDB(lastRecord);
                printLastDB();
                setUpdateFlag(true);
                return true;
            }
        }

        Log.d(TAG, "오늘자 레코드 color 업데이트 안됨");
        return false;
    }

    //데이터베이스 추가하기
    public void addRecordDB(Record record){
        Format format = record.dateFormat;
        int color = record.color;

        boolean isInserted = recordDBHelper.insertRecordData(format.stringFormat, Integer.toString(color));
        if(isInserted) Log.d(TAG, "레코드 데이터 추가 성공 (" + format.stringFormat + ")");
        else Log.d(TAG, "레코드 데이터 추가 실패 (" + format.stringFormat + ")");
    }

    //데이터베이스 읽어오기
    public ArrayList<Record> readAllRecordDB(){
        ArrayList<Record> res = new ArrayList<>();
        Cursor cursor = recordDBHelper.readRecordData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "레코드 기록이 비어있습니다", Toast.LENGTH_LONG).show();
            return null;
        }

        while (cursor.moveToNext()) {
            String stringFormat = cursor.getString(1);
            int color = cursor.getShort(2);
            Record record = new Record(new DateUtil.Format(stringFormat), color);
            res.add(record);
        }
        return res;
    }

    // 마지막 레코드 데이터 읽어오기
    public Record readLastRecord(RecordDatabaseHelper recordDBHelper){
        Record lastRecord = new Record();
        Cursor cursor = recordDBHelper.readRecordData();
        if(cursor.getCount() == 0) return null;

        cursor.moveToLast();
        lastRecord.dateFormat = new DateUtil.Format(cursor.getString(1));
        lastRecord.color = cursor.getInt(2);
        return lastRecord;
    }

    // 마지막 레코드 id 읽어오기
    public int readLastId(RecordDatabaseHelper recordDBHelper){
        Cursor cursor = recordDBHelper.readRecordData();
        if(cursor.getCount() == 0) return -1;

        cursor.moveToLast();
        return cursor.getInt(0);
    }

    //데이터베이스 다 지우기
    public void deleteAllRecordDB(){
        Cursor cursor = recordDBHelper.readRecordData();
        if(cursor.getCount() == 0) return;
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            String date = cursor.getString(1);
            recordDBHelper.deleteRecordData(date);
            cursor.moveToNext();
        }
    }

    //데이터베이스 업데이트하기
    public void updateRecordDB(String DATE, String COLOR){
        recordDBHelper.updateRecordData(DATE, COLOR);
    }
    public void updateRecordDB(Record record){
        updateRecordDB(record.dateFormat.stringFormat, Integer.toString(record.color));
    }

    //데이터베이스 로그 찍기
    public void printLogDB(String TAG){
        Cursor cursor = recordDBHelper.readRecordData();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.d(TAG, "ID : " + Integer.toString(cursor.getInt(0))
                    + ", DATE : " + cursor.getString(1)
                    + ", COLOR : " + cursor.getString(2));
            cursor.moveToNext();
        }
    }

    //마지막 데이터베이스 로그 찍기
    public void printLastDB(){
        String TAG = "LAST ";
        Log.d(TAG, "id " + Integer.toString(readLastId(recordDBHelper)) +
                ", date " + readLastRecord(recordDBHelper).dateFormat.stringFormat +
                ", color " + Integer.toString(readLastRecord(recordDBHelper).color));
    }
}