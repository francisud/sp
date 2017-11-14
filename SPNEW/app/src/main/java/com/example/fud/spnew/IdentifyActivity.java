package com.example.fud.spnew;

import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class IdentifyActivity extends AppCompatActivity implements SubstrateFragment.SubstrateFragmentListener {
    private ImageButton top;
    private ImageButton bottom;

    //source of button click
    private String source;

    //for updating buttons in UI
    private Uri mCurrentPhotoPath;

    //data
    private Uri topPhotoPath = null;
    private Uri bottomPhotoPath = null;

    private ArrayList<Point> topCoords = new ArrayList<>();
    private ArrayList<Point> bottomCoords = new ArrayList<>();

    private String substrate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //for changing the images of the buttons
        top = (ImageButton)findViewById(R.id.imageButton2);
        bottom = (ImageButton)findViewById(R.id.imageButton4);
    }

    public void createDialog(View view) {

        switch (view.getId()) {
            case R.id.imageButton2:
                source = "top";
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

                //take picture
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

                //select from files
                if(which == 1){
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
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
        mCurrentPhotoPath = Uri.fromFile(image);

        if(source.equals("top")){
            topPhotoPath = Uri.fromFile(image);
        }
        if(source.equals("bottom")){
            bottomPhotoPath = Uri.fromFile(image);
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
        if(source.equals("bottom")){
            targetW = bottom.getWidth();
            targetH = bottom.getHeight();
        }

//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCurrentPhotoPath);
        }catch (Exception e){Log.d("inside-try", e.toString());}

        if(source.equals("top"))
            top.setImageBitmap(bitmap);
        if(source.equals("bottom"))
            bottom.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //camera
        if (requestCode == 0 && resultCode == RESULT_OK) {
            setPic();
            startCrop();
        }

        //file browser
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mCurrentPhotoPath = data.getData();

            if(source.equals("top")){
                topPhotoPath = mCurrentPhotoPath;
            }
            if(source.equals("bottom")){
                bottomPhotoPath = mCurrentPhotoPath;
            }

            setPic();
            startCrop();
        }

        //get top mushroom coordinates
        if (requestCode == 3 && resultCode == RESULT_OK) {
            topCoords = (ArrayList<Point>) data.getSerializableExtra("coordinates");
        }
        if (requestCode == 5 && resultCode == RESULT_OK) {
            bottomCoords = (ArrayList<Point>) data.getSerializableExtra("coordinates");
        }
    }


    private void startCrop(){
        Intent cropIntent = new Intent(IdentifyActivity.this, CropActivity.class);

        if(source.equals("top")){
            cropIntent.putExtra("photoPath", topPhotoPath.toString());
            startActivityForResult(cropIntent, 3);
        }

        if(source.equals("bottom")){
            cropIntent.putExtra("photoPath", bottomPhotoPath.toString());
            startActivityForResult(cropIntent, 5);
        }
    }


    public void selectSubstrate(View view){
        DialogFragment pickSubstrate = new SubstrateFragment();
        pickSubstrate.show(getSupportFragmentManager(), "SubstrateFragment");
    }

    public void onSelect(DialogFragment dialog, int which){
        dialog.dismiss();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.substrate_array, R.layout.substrate_listview);

        substrate = adapter.getItem(which).toString();
        Button substrateButton = (Button)findViewById(R.id.substrateButton);
        substrateButton.setText(substrate);
    }

    public void startProcessActivity(View view){
        if(topPhotoPath == null && bottomPhotoPath == null){
            Toast.makeText(IdentifyActivity.this, "Both Top Picture and Underside Picture can't be empty",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if(substrate == null){
            Toast.makeText(IdentifyActivity.this, "Please pick substrate",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(IdentifyActivity.this, ProcessActivity.class);

        //put top photo data
        if(topPhotoPath != null){
            intent.putExtra("topPhotoPath", topPhotoPath.toString());
            intent.putExtra("topCoords", topCoords);
        }

        if(bottomPhotoPath != null){
            intent.putExtra("bottomPhotoPath", bottomPhotoPath.toString());
            intent.putExtra("bottomCoords", bottomCoords);
        }

        intent.putExtra("substrate", substrate);
        startActivity(intent);
    }

}
