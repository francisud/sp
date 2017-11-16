package com.example.fud.spnew;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends DialogFragment{

    public static DetailsFragment newInstance(String species){
        DetailsFragment frag = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("species", species);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String species = getArguments().getString("species").trim();

        //database
        String query = "SELECT * FROM species WHERE species='" + species + "'";
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();

        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.show_details, null);
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout);

        TextView speciesTV = (TextView)layout.findViewById(R.id.species);
        TextView colorsTV = (TextView)layout.findViewById(R.id.colors);
        TextView textureTV = (TextView)layout.findViewById(R.id.texture);
        TextView substrateTV = (TextView)layout.findViewById(R.id.substrate);
        ListView pictures = (ListView)layout.findViewById(R.id.pictures);

        speciesTV.setText(cursor.getString(cursor.getColumnIndex("species")));
        colorsTV.setText(cursor.getString(cursor.getColumnIndex("colors")));
        textureTV.setText(cursor.getString(cursor.getColumnIndex("texture")));
        substrateTV.setText(cursor.getString(cursor.getColumnIndex("substrate")));

        AssetManager am = getActivity().getAssets();
        InputStream in;
        ArrayList<Bitmap> images = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            try{
                in = am.open(cursor.getString(cursor.getColumnIndex("picture"+Integer.toString(i))));
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                images.add(bitmap);
            }catch (IOException e){}
        }

        ArrayAdapter<Bitmap> adapter = new ArrayAdapter<Bitmap>(view.getContext(), android.R.layout.simple_gallery_item, images);
        pictures.setAdapter(adapter);

        //species textview
//        tv = new TextView(getActivity());
//        tv.setText(cursor.getString(cursor.getColumnIndex("colors")));
//        tv.setTextSize(18);
//        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        tv.setPadding(10,10,10,10);
//
//        v = new View(getActivity());
//        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
//        v.setBackgroundColor(Color.DKGRAY);

        //colors textview

        //shape textview

        //texture textview

        //substrate textview

        //pictures listview(grid)

        //button close


        //for dispaying images
//        for(int i = 0; i < adapter.getCount(); i++){
//            v = new View(getActivity());
//            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
//            v.setBackgroundColor(Color.DKGRAY);
//
//            final int id = i;
//            tv = new TextView(getActivity());
//            tv.setId(i);
//            tv.setText(adapter.getItem(i));
//            tv.setTextSize(18);
//            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            tv.setPadding(10,10,10,10);
//            tv.setGravity(1);
//
//            tv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onSelectSubstrate(SubstrateFragment.this, id);
//                }
//            });
//
//            layout.addView(v);
//            layout.addView(tv);
//        }

        builder.setView(view);
        return builder.create();
    }
}
