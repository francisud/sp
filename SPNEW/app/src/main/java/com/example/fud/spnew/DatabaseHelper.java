package com.example.fud.spnew;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by FUD on 11/12/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MycoSearch.db";

    public static final String IDENTIFIED_TABLE_NAME  = "identified";
    public static final String IDENTIFIED_COLUMN_ID   = "id";
    public static final String IDENTIFIED_COLUMN_PICTURE = "picture";
    public static final String IDENTIFIED_COLUMN_SPECIES = "species";
    public static final String IDENTIFIED_COLUMN_PERCENTAGE = "percentage";
    public static final String IDENTIFIED_COLUMN_DATE = "date";
    public static final String IDENTIFIED_COLUMN_DATA = "data";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS identified");
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table identified " +
                        "(id integer primary key, picture text, species text, percentage text, date text, data text)"
        );
    }

    public boolean insertIdentified (String picture, String species, String percentage, String date, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("picture", picture);
        contentValues.put("species", species);
        contentValues.put("percentage", percentage);
        contentValues.put("date", date);
        contentValues.put("data", data);
        db.insert("identified", null, contentValues);
        return true;
    }

    public ArrayList<String> getAllIdentified() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from identified", null );
        res.moveToFirst();

        //need fixing
        while(res.isAfterLast() == false){}

        return array_list;
    }

    public int deleteContact (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("identified",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

}
