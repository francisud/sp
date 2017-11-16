package com.example.fud.spnew;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by FUD on 11/12/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static boolean ifIntializedSpecies = false;

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

    Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS identified");
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("debug", "inside on create");
        try {
            db.execSQL(
                    "create table identified " +
                            "(id integer primary key autoincrement, date text " +
                            "top_picture text, top_species text, top_percentage text,  top_data text " +
                            "underside_picture text, underside_species text, underside_percentage text, underside_data text)"
            );


            db.execSQL("create table species " +
                    "(id integer primary key autoincrement, " +
                    "species text, colors text, texture text, substrate text, picture0 text, picture1 text, " +
                    "picture2 text)");
        } catch (SQLException e) {
            Log.d("debug", e.toString());
        }

        initializeSpecies(db);
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

    private void initializeSpecies(SQLiteDatabase db){
        /*<string-array name="species_array">
        <item>Agaricaceae</item>
        <item>Agaricales</item>
        <item>Auriculariaceae</item>
        <item>Boletaceae</item>
        <item>Boletinellaceae</item>
        <item>Cantharellaceae</item>
        <item>Ceratomyxa</item>
        <item>Clavariaceae</item>
        <item>Corticiaceae</item>
        <item>Crepidotaceae</item>
        <item>Cudoniaceae</item>
        <item>Fomitopsidaceae</item>
        <item>Ganodermataceae</item>
        <item>Geastraceae</item>
        <item>Hydnaceae</item>
        <item>Hydnangiaceae</item>
        <item>Hygrophoraceae</item>
        <item>Hymenochaetaceae</item>
        <item>Hysteriaceae</item>
        <item>Lycogala</item>
        <item>Marasmiaceae</item>
        <item>Meruliaceae</item>
        <item>Mycenaceae</item>
        <item>Phaeolaceae</item>
        <item>Pleurotaceae</item>
        <item>Pluteaceae</item>
        <item>Podoscyphaceae</item>
        <item>Polyporaceae</item>
        <item>Polyporales</item>
        <item>Psathyrellaceae</item>
        <item>Sarcoscyphaceae</item>
        <item>Schizophyllaceae</item>
        <item>Stereaceae</item>
        <item>Strophariaceae</item>
        <item>Theleporaceae</item>
        <item>Tremellaceae</item>
        <item>Tricholomataceae</item>*/

        ContentValues contentValues = new ContentValues();
        String path = "picture-species/";

//        "species text, colors text, texture text, substrate text, picture1 text, picture2 text, picture3 text)"

        contentValues.put("species", "Agaricaceae");
        contentValues.put("colors", "Gray, Brown, White, Yellow-Brown, Black");
        contentValues.put("texture", "Smooth, Plicated, Crumbly, Scale-like, Web-like");
        contentValues.put("substrate", "Soil, Decaying Wood, Dead Wood, Animal Manure");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "agaricaceae" + Integer.toString(i));
        }

        long checker = db.insert("species", null, contentValues);
        if(checker == -1)
            Log.d("debug", "error in inserting");
        else
            Log.d("debug", "success in inserting");


        ifIntializedSpecies = true;
    }

}
