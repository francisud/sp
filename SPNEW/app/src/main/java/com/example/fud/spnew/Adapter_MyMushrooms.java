package com.example.fud.spnew;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class Adapter_MyMushrooms extends ArrayAdapter<Class_MyMushroomGridItem> {

    private ArrayList<Class_MyMushroomGridItem> data;
    Context mContext;

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    public Adapter_MyMushrooms(ArrayList<Class_MyMushroomGridItem> data, Context context) {
        super(context, R.layout.mymushrooms_imageview, data);
        this.data = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Class_MyMushroomGridItem dataModel = getItem(position);
        Adapter_MyMushrooms.ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new Adapter_MyMushrooms.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.mymushrooms_imageview, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Adapter_MyMushrooms.ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageBitmap(dataModel.getImage());
        viewHolder.textView.setText(dataModel.getTitle());

        // Return the completed view to render on screen
        return convertView;
    }
}


//public class Adapter_MyMushrooms extends ArrayAdapter<Class_MyMushroomGridItem> {
//    private Context context;
//    private Class_MyMushroomGridItem data;
//
//    public Adapter_MyMushrooms(Context context, Class_MyMushroomGridItem data) {
//        super(context, R.layout.mymushrooms_imageview, data);
//        this.context = context;
//        this.data = data;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View row = convertView;
//        ViewHolder holder = null;
//
//        if (row == null) {
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            row = inflater.inflate(R.layout.mymushrooms_imageview, parent, false);
//            holder = new ViewHolder();
//            holder.imageTitle = (TextView) row.findViewById(R.id.text);
//            holder.image = (ImageView) row.findViewById(R.id.image);
//            row.setTag(holder);
//        } else {
//            holder = (ViewHolder) row.getTag();
//        }
//
//        Class_MyMushroomGridItem item = data;
//        holder.imageTitle.setText(item.getTitle());
//        holder.image.setImageBitmap(item.getImage());
//        return row;
//    }
//
//    static class ViewHolder {
//        TextView imageTitle;
//        ImageView image;
//    }
//}