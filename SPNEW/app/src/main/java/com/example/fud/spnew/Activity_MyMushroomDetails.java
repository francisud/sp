package com.example.fud.spnew;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Activity_MyMushroomDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__my_mushroom_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        int position = extras.getInt("position");

        Helper_Database helperDatabase = new Helper_Database(this);
        SQLiteDatabase db = helperDatabase.getWritableDatabase();

        Cursor topGetter = db.rawQuery("SELECT top_picture_scaled FROM identified ORDER BY datetime(date) DESC", null);
        Cursor undersideGetter = db.rawQuery("SELECT underside_picture_scaled FROM identified ORDER BY datetime(date) DESC", null);

        topGetter.moveToFirst();
        undersideGetter.moveToFirst();

        for(int i = 0; i < position; i++){
            topGetter.moveToNext();
            undersideGetter.moveToNext();
        }

    }
}
