package com.example.fud.spnew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class Adapter_Families extends ArrayAdapter<String> {

    private ArrayList<String> data;
    Context mContext;

    private static class ViewHolder {
        TextView textView;
    }

    public Adapter_Families(ArrayList<String> data, Context context) {
        super(context, R.layout.families_row, data);
        this.data = data;
        this.mContext=context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String dataModel = getItem(position);
        Adapter_Families.ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new Adapter_Families.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.families_row, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Adapter_Families.ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(dataModel);

        // Return the completed view to render on screen
        return convertView;
    }
}
