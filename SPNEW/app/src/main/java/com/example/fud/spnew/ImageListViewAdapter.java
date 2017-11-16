package com.example.fud.spnew;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageListViewAdapter extends ArrayAdapter<Bitmap> {
    private ArrayList<Bitmap> data;
    Context mContext;

    private static class ViewHolder {
        ImageView imageView;
    }

    public ImageListViewAdapter(ArrayList<Bitmap> data, Context context) {
        super(context, R.layout.show_details, data);
        this.data = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitmap dataModel = getItem(position);
        ImageListViewAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ImageListViewAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.image_row, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageListViewAdapter.ViewHolder) convertView.getTag();
        }

//        viewHolder.imageView.setImageBitmap(Bitmap.createScaledBitmap(dataModel,200,200,false));
        viewHolder.imageView.setImageBitmap(dataModel);

        // Return the completed view to render on screen
        return convertView;
    }
}
