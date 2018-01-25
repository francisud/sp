package com.example.fud.spnew;

import android.app.Dialog;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Fragment_Details extends DialogFragment{

    public static Fragment_Details newInstance(String species){
        Fragment_Details frag = new Fragment_Details();
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
        Helper_Database helperDatabase = new Helper_Database(getContext());
        SQLiteDatabase db = helperDatabase.getReadableDatabase();
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
        GridView pictures = (GridView)layout.findViewById(R.id.pictures);

        speciesTV.setText(cursor.getString(cursor.getColumnIndex("species")));
        colorsTV.setText(cursor.getString(cursor.getColumnIndex("colors")));
        textureTV.setText(cursor.getString(cursor.getColumnIndex("texture")));
        substrateTV.setText(cursor.getString(cursor.getColumnIndex("substrate")));

        speciesTV.setTextSize(16);
        colorsTV.setTextSize(16);
        textureTV.setTextSize(16);
        substrateTV.setTextSize(16);

        AssetManager am = getActivity().getAssets();
        InputStream in;
        ArrayList<Bitmap> images = new ArrayList<>();

        for(int i = 1; i < 10; i++){
            try{
                in = am.open(cursor.getString(cursor.getColumnIndex("picture"+Integer.toString(i))) + ".jpg" );
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                images.add(bitmap);
            }catch (IOException e){
                Log.d("debug", "in catch" + e.toString());
            }
        }

        Adapter_ImageListView adapter = new Adapter_ImageListView(images, view.getContext());
        pictures.setAdapter(adapter);

        builder.setView(view);
        return builder.create();
    }
}
