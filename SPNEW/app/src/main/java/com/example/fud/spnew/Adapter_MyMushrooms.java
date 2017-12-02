package com.example.fud.spnew;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class Adapter_MyMushrooms extends ArrayAdapter<Class_MyMushroomGridItem> {

    ProgressDialog progressDialog;
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
//                        upload(data.get(position).getId());
                        new Adapter_MyMushrooms.AsyncClassifyTask().execute(data.get(position).getId());
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


    public void upload(int id){
        if(isConnected()){
            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("http://testing.com");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");

                //get data from database
                Helper_Database helperDatabase = new Helper_Database(getContext());
                SQLiteDatabase db = helperDatabase.getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM identified where id = ?", new String[]{Integer.toString(id)});


                //need to fix getters, cant get too much data at the same time
                Cursor topGetter = db.rawQuery("SELECT id, date, top_picture_scaled FROM identified ORDER BY datetime(date) DESC", null);
                Cursor undersideGetter = db.rawQuery("SELECT id, date, underside_picture_scaled FROM identified ORDER BY datetime(date) DESC", null);

                if(cursor != null && cursor.moveToFirst()){
                    Log.d("debug-cursor",Integer.toString(cursor.getCount()));

                    JSONObject data = new JSONObject();

                    data.put("date", cursor.getString(cursor.getColumnIndex("date")));
                    data.put("substrate", cursor.getString(cursor.getColumnIndex("substrate")));

                    if(cursor.getBlob(cursor.getColumnIndex("top_picture")) != null){
                        String top_picture = new String(Base64.encode(cursor.getBlob(cursor.getColumnIndex("top_picture")), Base64.DEFAULT));
                        data.put("top_picture", top_picture);
                        data.put("top_species", cursor.getString(cursor.getColumnIndex("top_species")));
                        data.put("top_percentage", cursor.getString(cursor.getColumnIndex("top_percentage")));
                        data.put("top_data", cursor.getString(cursor.getColumnIndex("top_data")));
                    }

                    if(cursor.getBlob(cursor.getColumnIndex("underside_picture")) != null){
                        String underside_picture = new String(Base64.encode(cursor.getBlob(cursor.getColumnIndex("underside_picture")), Base64.DEFAULT));
                        data.put("underside_picture", underside_picture);
                        data.put("underside_species", cursor.getString(cursor.getColumnIndex("underside_species")));
                        data.put("underside_percentage", cursor.getString(cursor.getColumnIndex("underside_percentage")));
                        data.put("underside_data", cursor.getString(cursor.getColumnIndex("underside_data")));
                    }

                    String jsonEncoded = URLEncoder.encode(data.toString(),"utf-8");
                    String values = "json=" + jsonEncoded;
                    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
//                    out.write(values.getBytes());
                    out.write(data.toString().getBytes());
                    out.flush();
                    out.close();

                    int responseCode = urlConnection.getResponseCode();
                    Log.d("debug-insideadapter", Integer.toString(responseCode));

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    in.read();

                    Log.d("debug-insideadapter", Integer.toString(in.read()));
                }

            }catch (Exception e){
                Log.d("debug-catch", e.getMessage(), e);
            }finally {
                urlConnection.disconnect();
            }
        }
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class AsyncClassifyTask extends AsyncTask<Integer, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Sending data, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            Log.d("debug-doinbackground","params[0]="+Integer.toString(params[0]));
            upload(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();
        }
    }

}
