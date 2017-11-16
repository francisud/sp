package com.example.fud.spnew;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageListViewAdapter extends ArrayAdapter<Bitmap> {
    private ArrayList<Bitmap> data;
    Context mContext;

    private static class ViewHolder {
        TextView speciesTV;
        TextView colorsTV;
        TextView textureTV;
        TextView substrateTV;
        ListView pictures;
    }

    public ImageListViewAdapter(ArrayList<Bitmap> data, Context context) {
        super(context, R.layout.show_details, data);
        this.data = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        return null;
    }
}
