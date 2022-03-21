package com.armdri.myracle;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class RecordDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "RecordDatabaseHelper";
    private static RecordDatabaseHelper recordDatabaseHelper;

    public static String DATABASE_NAME = "record.db";
    public static String TABLE_NAME = "record_table";

    //테이블 항목
    public static final String COL_1 = "ID";
    public static final String COL_2 = "DATE";
    public static final String COL_3 = "COLOR";

    public RecordDatabaseHelper(@Nullable Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
//        db.execSQL("create table " + TABLE_NAME +
//                "(ID INTEGER PRIMARY KEY, DATE TEXT, COLOR TEXT)");
        db.execSQL("create table " + TABLE_NAME + "(" + COL_1 + ", " + COL_2 + ", " + COL_3
            + ", PRIMARY KEY (" + COL_1 + ", " + COL_2 + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //데이터베이스 추가하기 insert
    public boolean insertRecordData(String date, String color){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, date);
        contentValues.put(COL_3, color);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    //데이터베이스 항목 읽어오기 read
    public Cursor readRecordData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    //데이터베이스 삭제하기 delete
    public Integer deleteRecordData(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "DATE = ?", new String[]{ date });
    }

    //데이터베이스 수정하기
    public boolean updateRecordData(String date, String color){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, date);
        contentValues.put(COL_3, color);
        db.update(TABLE_NAME, contentValues, "DATE = ?", new String[]{ date });
        return true;
    }
}
