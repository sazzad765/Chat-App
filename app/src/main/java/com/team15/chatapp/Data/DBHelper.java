package com.team15.chatapp.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyDBName.db";
    private static final String CHAT_DATA_TABLE_NAME = "chat_data";
    private static final String ID = "id";
    private static final String TITLE = "title";
//    private static final String DESCRIPTION = "description";

    private static final String CREATE_TABLE_CHAT_DATA = "CREATE TABLE "
            + CHAT_DATA_TABLE_NAME + "(" + ID + " integer primary key," + TITLE + " TEXT);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CHAT_DATA);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CHAT_DATA_TABLE_NAME);

        onCreate(db);
    }

    //insert product purchase list......
    public boolean insert(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, title);
//        contentValues.put(DESCRIPTION, description);

        db.insert(CHAT_DATA_TABLE_NAME, null, contentValues);
        return true;
    }



    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from chat_data where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CHAT_DATA_TABLE_NAME);
        return numRows;
    }

    public boolean updateData(Integer id, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, title);
//        contentValues.put(DESCRIPTION, description);

        db.update(CHAT_DATA_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }





    public Integer deleteItem(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CHAT_DATA_TABLE_NAME,
                "id = ? ",
                new String[]{Integer.toString(id)});
    }


    public ArrayList<ArrayList<String>> getAllCart() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CHAT_DATA_TABLE_NAME);
        ArrayList<ArrayList<String>> array_list = new ArrayList<>(numRows);
        Cursor res = db.rawQuery("select * from " + CHAT_DATA_TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(0, res.getString(res.getColumnIndex(ID)));
            list.add(1, res.getString(res.getColumnIndex(TITLE)));
//            list.add(2, res.getString(res.getColumnIndex(DESCRIPTION)));
            array_list.add(list);
            res.moveToNext();
        }
        return array_list;
    }
    public ArrayList<String> getList (){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CHAT_DATA_TABLE_NAME);
        ArrayList<String> list = new ArrayList<String>(numRows);
        Cursor res = db.rawQuery("select * from " + CHAT_DATA_TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            list.add(res.getString(res.getColumnIndex(TITLE)));
            res.moveToNext();
        }
        return list;
    }


    public void delete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + CHAT_DATA_TABLE_NAME);
    }



}