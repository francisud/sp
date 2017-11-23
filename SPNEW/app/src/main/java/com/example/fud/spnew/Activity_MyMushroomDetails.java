package com.example.fud.spnew;

import android.app.FragmentManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import static org.opencv.core.CvType.CV_8UC3;

public class Activity_MyMushroomDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__my_mushroom_details);

        Bundle extras = getIntent().getExtras();
        int position = extras.getInt("position");

        Helper_Database helperDatabase = new Helper_Database(this);
        SQLiteDatabase db = helperDatabase.getWritableDatabase();

        Cursor topGetter = db.rawQuery("SELECT substrate, top_picture, top_species, top_percentage FROM identified ORDER BY datetime(date) DESC", null);
        Cursor undersideGetter = db.rawQuery("SELECT underside_picture, underside_species, underside_percentage FROM identified ORDER BY datetime(date) DESC", null);

        topGetter.moveToFirst();
        undersideGetter.moveToFirst();

        Log.d("debug-inside details", Integer.toString(position));
        for(int i = 0; i < position; i++){
            topGetter.moveToNext();
            undersideGetter.moveToNext();
        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

        String[] substrates = getResources().getStringArray(R.array.substrate_array);
        String substrate = topGetter.getString(topGetter.getColumnIndex("substrate"));

        TextView textView = new TextView(Activity_MyMushroomDetails.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText("Substrate: " + substrates[Integer.parseInt(substrate)-1]);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0,15,0,0);
        layout.addView(textView);

        byte[] top_blob = topGetter.getBlob(topGetter.getColumnIndex("top_picture"));
        if(top_blob != null){
            Bitmap top_picture = BitmapFactory.decodeByteArray(top_blob, 0 ,top_blob.length);
            String top_species = topGetter.getString(topGetter.getColumnIndex("top_species"));
            String top_percentage = topGetter.getString(topGetter.getColumnIndex("top_percentage"));
            displayResults(top_picture, top_species, top_percentage);
        }

        byte[] underside_blob = undersideGetter.getBlob(undersideGetter.getColumnIndex("underside_picture"));
        if(underside_blob != null){
            Bitmap underside_picture = BitmapFactory.decodeByteArray(underside_blob, 0 ,underside_blob.length);
            String underside_species = undersideGetter.getString(undersideGetter.getColumnIndex("underside_species"));
            String underside_percentage = undersideGetter.getString(undersideGetter.getColumnIndex("underside_percentage"));
            displayResults(underside_picture, underside_species, underside_percentage);
        }

    }

    private void displayResults(Bitmap bm, String species, String percentage) {
        //display image
        ImageView iv = new ImageView(this);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.content_process_width), (int) getResources().getDimension(R.dimen.content_process_height));
        lp1.gravity = Gravity.CENTER_HORIZONTAL;
        iv.setLayoutParams(lp1);
        iv.setImageBitmap(bm);

        final ArrayList<Class_ResultRow> rrc;
        Adapter_Result adapter;

        String[] speciesArray = species.split(",");
        String[] percentageArray = percentage.split(",");

        ListView listView = new ListView(this);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.zerodp),
                1.0f);
        listView.setLayoutParams(lp2);

        //add all species and percentage
        rrc = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            rrc.add(new Class_ResultRow(speciesArray[i], percentageArray[i]));
        }

        adapter = new Adapter_Result(rrc, Activity_MyMushroomDetails.this);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class_ResultRow picked = rrc.get(position);
                showDetails(picked.getSpecies());
            }
        });
        listView.setScrollbarFadingEnabled(false);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.addView(iv);
        layout.addView(listView);
    }

    private void showDetails(String species){
        FragmentManager fm = Activity_MyMushroomDetails.this.getFragmentManager();
        Fragment_Details details = Fragment_Details.newInstance(species);
        details.show(getSupportFragmentManager(), "dialog");
    }
}
