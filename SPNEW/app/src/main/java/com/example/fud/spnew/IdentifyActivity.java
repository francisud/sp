package com.example.fud.spnew;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IdentifyActivity extends AppCompatActivity {

    //source of button click
    private String source;

    //for updating buttons in UI
    private String mCurrentPhotoPath;

    //for saving the paths
    private String topPhotoPath;
    private String sidePhotoPath;
    private String bottomPhotoPath;

    private ImageButton top;
    private ImageButton side;
    private ImageButton bottom;

    private Button spinner;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //for changing the images of the buttons
        top = (ImageButton)findViewById(R.id.imageButton2);
        side = (ImageButton)findViewById(R.id.imageButton3);
        bottom = (ImageButton)findViewById(R.id.imageButton4);

        //for picking substrate
        spinner = (Button) findViewById(R.id.button);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.substrate_array, android.R.layout.simple_spinner_item);

    }

    public void createDialog(View view) {

        switch (view.getId()) {
            case R.id.imageButton2:
                source = "top";
                break;
            case R.id.imageButton3:
                source = "side";
                break;
            case R.id.imageButton4:
                source = "bottom";
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(IdentifyActivity.this);
        builder.setTitle(R.string.pick_action);
        builder.setItems(R.array.actions_array, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                Intent intent = new Intent();

                if(which == 0){
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(getApplication().getApplicationContext(),
                                    "com.example.fud.spnew.fileprovider",
                                    photoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(intent, 0);
                        }
                    }
                }

                if(which == 1){
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        if(source.equals("top")){
            topPhotoPath = image.getAbsolutePath();
        }
        if(source.equals("side")){
            sidePhotoPath = image.getAbsolutePath();
        }
        if(source.equals("bottom")){
            bottomPhotoPath = image.getAbsolutePath();
        }
        return image;
    }

    private void setPic() {
        int targetW = -1;
        int targetH = -1;

        // Get the dimensions of the View
        if(source.equals("top")){
            targetW = top.getWidth();
            targetH = top.getHeight();
        }
        if(source.equals("side")){
            targetW = side.getWidth();
            targetH = side.getHeight();
        }
        if(source.equals("bottom")){
            targetW = bottom.getWidth();
            targetH = bottom.getHeight();
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        if(source.equals("top"))
            top.setImageBitmap(bitmap);
        if(source.equals("side"))
            side.setImageBitmap(bitmap);
        if(source.equals("bottom"))
            bottom.setImageBitmap(bitmap);    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //camera
        if (requestCode == 0 && resultCode == RESULT_OK) {
            setPic();
        }

        //file browser
        if (requestCode == 1 && resultCode == RESULT_OK) {

            //http://hmkcode.com/android-display-selected-image-and-its-real-path/
            //https://github.com/hmkcode/Android/blob/master/android-show-image-and-path/src/com/hmkcode/android/image/RealPathUtil.java
            //for getting the real / absolute path

            if (Build.VERSION.SDK_INT < 11)
                mCurrentPhotoPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

            // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                mCurrentPhotoPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

            // SDK > 19 (Android 4.4)
            else
                mCurrentPhotoPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());


            if(source.equals("top")){
                topPhotoPath = mCurrentPhotoPath;
            }
            if(source.equals("side")){
                sidePhotoPath = mCurrentPhotoPath;
            }
            if(source.equals("bottom")){
                bottomPhotoPath = mCurrentPhotoPath;
            }

            setPic();
        }
    }

    public void selectSubstrate(View view){
        new AlertDialog.Builder(this)
            .setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    spinner.setText(adapter.getItem(which));
                    dialog.dismiss();
                }
            }).create().show();
    }

}
