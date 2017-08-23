package com.example.fud.spnew;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.net.Uri;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IdentifyActivity extends AppCompatActivity {

    private String source;
    private String selectedImagePath;
    private ImageButton top;
    private ImageButton side;
    private ImageButton bottom;

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

        top = (ImageButton)findViewById(R.id.imageButton2);
        side = (ImageButton)findViewById(R.id.imageButton3);
        bottom = (ImageButton)findViewById(R.id.imageButton4);
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


    String mCurrentPhotoPath;
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
        return image;
    }








    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        //camera
        if (requestCode == 0 && resultCode == RESULT_OK) {
//            Bitmap imageBitmap = null;
//            try{
//                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
//            }catch(IOException e){
//                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
//            }

            // Get the dimensions of the View
            int targetW = top.getWidth();
            int targetH = top.getHeight();

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
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            top.setImageBitmap(bitmap);


//            if(source.equals("top"))
//                top.setImageBitmap(imageBitmap);
//            if(source.equals("side"))
//                side.setImageBitmap(imageBitmap);
//            if(source.equals("bottom"))
//                bottom.setImageBitmap(imageBitmap);
        }


        //file browser
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            selectedImagePath = selectedImageUri.getPath();

            if(source.equals("top"))
                top.setImageURI(selectedImageUri);
            if(source.equals("side"))
                side.setImageURI(selectedImageUri);
            if(source.equals("bottom"))
                bottom.setImageURI(selectedImageUri);
        }

    }


}
