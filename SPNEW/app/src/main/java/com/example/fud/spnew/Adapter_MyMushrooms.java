package com.example.fud.spnew;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class Adapter_MyMushrooms extends ArrayAdapter<Class_MyMushroomGridItem> {

    private ArrayList<Class_MyMushroomGridItem> data;
    Context mContext;

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
        ImageButton buttonUpload;
        ImageButton buttonDelete;
    }

    public Adapter_MyMushrooms(ArrayList<Class_MyMushroomGridItem> data, Context context) {
        super(context, R.layout.mymushrooms_row, data);
        this.data = data;
        this.mContext=context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Class_MyMushroomGridItem dataModel = getItem(position);
        Adapter_MyMushrooms.ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new Adapter_MyMushrooms.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.mymushrooms_row, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.buttonUpload = (ImageButton) convertView.findViewById(R.id.buttonUpload);
            viewHolder.buttonDelete = (ImageButton) convertView.findViewById(R.id.buttonDelete);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Adapter_MyMushrooms.ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageBitmap(dataModel.getImage());
        viewHolder.textView.setText(dataModel.getDate());

        viewHolder.buttonUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Upload data?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Delete data?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int idToRemove = data.get(position).getId();

                        data.remove(position);

                        Helper_Database helperDatabase = new Helper_Database(getContext());
                        SQLiteDatabase db = helperDatabase.getWritableDatabase();
                        db.execSQL("DELETE FROM identified where id = ?", new String[]{Integer.toString(idToRemove)});
                        notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
