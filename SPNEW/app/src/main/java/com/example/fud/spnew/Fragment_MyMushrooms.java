package com.example.fud.spnew;

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
    private Cursor cursor;

    private OnFragmentInteractionListener mListener;

    public Fragment_MyMushrooms() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        cursor = null;
        super.onCreate(savedInstanceState);

        Helper_Database helperDatabase = new Helper_Database(getContext());
        SQLiteDatabase db = helperDatabase.getWritableDatabase();

        Cursor getter = db.rawQuery("SELECT * FROM identified ORDER BY datetime(date) DESC", null);
        if(getter.getCount() > 0){
            cursor = getter;
            cursor.moveToFirst();
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
        if(cursor != null){
            do {
                picture = cursor.getBlob(cursor.getColumnIndex("top_picture"));
                if(picture != null){
                    bm = BitmapFactory.decodeByteArray(picture, 0 ,picture.length);
                    date = cursor.getString(cursor.getColumnIndex("date"));
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    item = new Class_MyMushroomGridItem(bm,date,id);
                    holder.add(item);
                }
                else{
                    picture = cursor.getBlob(cursor.getColumnIndex("underside_picture"));
                    bm = BitmapFactory.decodeByteArray(picture, 0 ,picture.length);
                    date = cursor.getString(cursor.getColumnIndex("date"));
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    item = new Class_MyMushroomGridItem(bm,date,id);
                    holder.add(item);
                }

            }while(cursor.moveToNext());

            Adapter_MyMushrooms adapter = new Adapter_MyMushrooms(holder, getContext());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("debug", Integer.toString(position));

                    cursor.moveToFirst();
                    for(int i = 0; i < position; i++)
                        cursor.moveToNext();


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
