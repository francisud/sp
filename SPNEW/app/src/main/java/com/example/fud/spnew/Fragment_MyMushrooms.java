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

import java.util.ArrayList;

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

        Cursor topGetter = db.rawQuery("SELECT id, date, top_picture_scaled FROM identified ORDER BY datetime(date) DESC", null);
        Cursor undersideGetter = db.rawQuery("SELECT id, date, underside_picture_scaled FROM identified ORDER BY datetime(date) DESC", null);

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
        byte[] picture;
        String date;
        int id;

        if(topCursor != null && topCursor.getCount() > 0){
            do {
                picture = topCursor.getBlob(topCursor.getColumnIndex("top_picture_scaled"));
                if(picture != null){
                    bm = BitmapFactory.decodeByteArray(picture, 0 ,picture.length);
                    date = topCursor.getString(topCursor.getColumnIndex("date"));
                    id = topCursor.getInt(topCursor.getColumnIndex("id"));
                    item = new Class_MyMushroomGridItem(bm,date,id);
                    holder.add(item);
                }
                else{
                    picture = undersideCursor.getBlob(undersideCursor.getColumnIndex("underside_picture_scaled"));
                    bm = BitmapFactory.decodeByteArray(picture, 0 ,picture.length);
                    date = undersideCursor.getString(undersideCursor.getColumnIndex("date"));
                    id = undersideCursor.getInt(undersideCursor.getColumnIndex("id"));
                    item = new Class_MyMushroomGridItem(bm,date,id);
                    holder.add(item);
                }

            }while(topCursor.moveToNext() && undersideCursor.moveToNext());

            Adapter_MyMushrooms adapter = new Adapter_MyMushrooms(holder, getContext());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("debug", Integer.toString(position));

                    Intent intent = new Intent(getActivity(), Activity_MyMushroomDetails.class);
                    intent.putExtra("position", Integer.toString(
                            position
                    ));
                    getActivity().startActivity(intent);
                }
            });
        }


        Log.d("debug","inside oncreateview-mm");

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
