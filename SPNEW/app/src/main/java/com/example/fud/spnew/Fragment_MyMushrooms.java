package com.example.fud.spnew;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Fragment_MyMushrooms extends Fragment {
    private Cursor topCursor;
    private Cursor undersideCursor;

    private OnFragmentInteractionListener mListener;

    public Fragment_MyMushrooms() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        topCursor = null;
        undersideCursor = null;
        super.onCreate(savedInstanceState);

        Helper_Database helperDatabase = new Helper_Database(getContext());
        SQLiteDatabase db = helperDatabase.getWritableDatabase();

        Cursor topGetter = db.rawQuery("SELECT id, date, top_picture_scaled, is_uploaded FROM identified ORDER BY datetime(date) DESC", null);
        Cursor undersideGetter = db.rawQuery("SELECT id, date, underside_picture_scaled, is_uploaded FROM identified ORDER BY datetime(date) DESC", null);

        if(topGetter != null && topGetter.getCount() > 0){
            topCursor = topGetter;
            topCursor.moveToFirst();

            undersideCursor = undersideGetter;
            undersideCursor.moveToFirst();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_mushrooms, container, false);
        ListView listView = (ListView) view.findViewById(R.id.gridView);

        final ArrayList<Class_MyMushroomGridItem> holder = new ArrayList<>();
        Class_MyMushroomGridItem item;
        Bitmap bm;
        String picture;
        String date = null;
        int id;
        int is_uploaded;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        if(topCursor != null && topCursor.getCount() > 0){
            do {
                try{
                    date = topCursor.getString(topCursor.getColumnIndex("date"));
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date holder1 = df.parse(date);
                    df.setTimeZone(TimeZone.getDefault());
                    date = df.format(holder1);
                }catch (Exception e){Log.d("debug-catch", e.getMessage(), e);}

                picture = topCursor.getString(topCursor.getColumnIndex("top_picture_scaled"));
                if(picture != null){
                    bm = BitmapFactory.decodeFile(picture);
                    id = topCursor.getInt(topCursor.getColumnIndex("id"));
                    is_uploaded = topCursor.getInt(topCursor.getColumnIndex("is_uploaded"));
                    item = new Class_MyMushroomGridItem(bm,date,id,is_uploaded);
                    holder.add(item);
                }
                else{
                    picture = undersideCursor.getString(undersideCursor.getColumnIndex("underside_picture_scaled"));
                    bm = BitmapFactory.decodeFile(picture);
                    id = undersideCursor.getInt(undersideCursor.getColumnIndex("id"));
                    is_uploaded = undersideCursor.getInt(undersideCursor.getColumnIndex("is_uploaded"));
                    item = new Class_MyMushroomGridItem(bm,date,id,is_uploaded);
                    holder.add(item);
                }

            }while(topCursor.moveToNext() && undersideCursor.moveToNext());

            Adapter_MyMushrooms adapter = new Adapter_MyMushrooms(holder, getContext());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), Activity_MyMushroomDetails.class);
                    intent.putExtra("position", position);
                    getActivity().startActivity(intent);
                }
            });
        }

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
