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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class Adapter_MyMushrooms extends ArrayAdapter<Class_MyMushroomGridItem> {

    private ProgressDialog progressDialog;
    private ArrayList<Class_MyMushroomGridItem> data;
    private Context mContext;
    private int responseCode;
    private String resultCode;


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


    private void upload(int id){
        responseCode = -1;
        resultCode = "";

        if(isConnected()){
            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("test.com");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");

                //get data from database
                Helper_Database helperDatabase = new Helper_Database(getContext());
                SQLiteDatabase db = helperDatabase.getWritableDatabase();

                //cursor cant handle too much data, need to split to 2
                Cursor topGetter = db.rawQuery("SELECT date, substrate, top_picture, top_species, top_percentage, top_data FROM identified where id = ?", new String[]{Integer.toString(id)});

                Cursor undersideGetter = db.rawQuery("SELECT underside_picture, underside_species, underside_percentage,  underside_data FROM identified where id = ?", new String[]{Integer.toString(id)});


                if(topGetter != null && topGetter.moveToFirst() && undersideGetter.moveToFirst()){
                    JSONObject data = new JSONObject();

                    data.put("date", topGetter.getString(topGetter.getColumnIndex("date")));
                    data.put("substrate", topGetter.getString(topGetter.getColumnIndex("substrate")));

                    if(topGetter.getBlob(topGetter.getColumnIndex("top_picture")) != null){
                        String top_picture = new String(Base64.encode(topGetter.getBlob(topGetter.getColumnIndex("top_picture")), Base64.DEFAULT));
                        data.put("top_picture", top_picture);
                        data.put("top_species", topGetter.getString(topGetter.getColumnIndex("top_species")));
                        data.put("top_percentage", topGetter.getString(topGetter.getColumnIndex("top_percentage")));
                        data.put("top_data", topGetter.getString(topGetter.getColumnIndex("top_data")));
                    }

                    if(undersideGetter.getBlob(undersideGetter.getColumnIndex("underside_picture")) != null){
                        String underside_picture = new String(Base64.encode(undersideGetter.getBlob(undersideGetter.getColumnIndex("underside_picture")), Base64.DEFAULT));
                        data.put("underside_picture", underside_picture);
                        data.put("underside_species", undersideGetter.getString(undersideGetter.getColumnIndex("underside_species")));
                        data.put("underside_percentage", undersideGetter.getString(undersideGetter.getColumnIndex("underside_percentage")));
                        data.put("underside_data", undersideGetter.getString(undersideGetter.getColumnIndex("underside_data")));
                    }

                    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                    out.write(data.toString().getBytes());
                    out.flush();
                    out.close();

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line + "\n");
                    }

                    responseCode = urlConnection.getResponseCode();
                    resultCode = result.toString().trim();
                }

            }catch (Exception e){
                Log.d("debug-catch", e.getMessage(), e);
            }finally {
                urlConnection.disconnect();
            }
        }
    }

    private boolean isConnected(){
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
            upload(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();

            if(responseCode != 200 || !resultCode.equals("1")){
                Toast toast = Toast.makeText(mContext, "Unable to upload data", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            if(responseCode == 200 && resultCode.equals("1")){
                Toast toast = Toast.makeText(mContext, "Data upload success", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

}
