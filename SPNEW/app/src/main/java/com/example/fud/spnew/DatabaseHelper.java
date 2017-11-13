package com.example.fud.spnew;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by FUD on 11/12/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MycoSearch.db";

    public static final String IDENTIFIED_TABLE_NAME  = "identified";

    public static final String IDENTIFIED_COLUMN_ID   = "id";
    public static final String IDENTIFIED_COLUMN_DATE = "date";

    public static final String IDENTIFIED_COLUMN_PICTURE_TOP = "top_picture";
    public static final String IDENTIFIED_COLUMN_SPECIES_TOP = "top_species";
    public static final String IDENTIFIED_COLUMN_PERCENTAGE_TOP = "top_percentage";
    public static final String IDENTIFIED_COLUMN_DATA_TOP = "top_data";

    public static final String IDENTIFIED_COLUMN_PICTURE_UNDERSIDE = "underside_picture";
    public static final String IDENTIFIED_COLUMN_SPECIES_UNDERSIDE = "underside_species";
    public static final String IDENTIFIED_COLUMN_PERCENTAGE_UNDERSIDE = "underside_percentage";
    public static final String IDENTIFIED_COLUMN_DATA_UNDERSIDE = "underside_data";

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
        try {
            db.execSQL(
                    "create table identified " +
                            "(id integer primary key, date text " +
                            "top_picture text, top_species text, top_percentage text,  top_data text " +
                            "underside_picture text, underside_species text, underside_percentage text, underside_data text)"
            );
        } catch (SQLException e) {

        }
    }

    public void insertIdentified (String date,
                         String top_picture, String top_species, String top_percentage, String top_data,
                         String underside_picture, String underside_species, String underside_percentage, String underside_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);

        contentValues.put("top_picture", top_picture);
        contentValues.put("top_species", top_species);
        contentValues.put("top_percentage", top_percentage);
        contentValues.put("top_data", top_data);

        contentValues.put("underside_picture", underside_picture);
        contentValues.put("underside_species", underside_species);
        contentValues.put("underside_percentage", underside_percentage);
        contentValues.put("underside_data", underside_data);

        db.insert("identified", null, contentValues);
    }

    public int deleteIdentified (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("identified",
                "id = ? ",
                new String[] { Integer.toString(id) });
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

}
