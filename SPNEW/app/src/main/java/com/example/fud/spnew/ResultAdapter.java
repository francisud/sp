package com.example.fud.spnew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultAdapter extends ArrayAdapter<ResultRowClass> {
    private ArrayList<ResultRowClass> data;
    Context mContext;

    private static class ViewHolder {
        TextView speciesTV;
        TextView percentageTV;
    }

    public ResultAdapter(ArrayList<ResultRowClass> data, Context context) {
        super(context, R.layout.result_row, data);
        this.data = data;
        this.mContext=context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ResultRowClass dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.result_row, parent, false);
            viewHolder.speciesTV = (TextView) convertView.findViewById(R.id.species);
            viewHolder.percentageTV = (TextView) convertView.findViewById(R.id.percentage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.speciesTV.setText(dataModel.getSpecies());
        viewHolder.percentageTV.setText(dataModel.getPercentage());

        // Return the completed view to render on screen
        return convertView;
    }

}
