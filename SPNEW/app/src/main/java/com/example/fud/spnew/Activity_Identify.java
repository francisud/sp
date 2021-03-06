package com.example.fud.spnew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Activity_Identify extends AppCompatActivity implements Fragment_PictureSource.PictureSourceFragmentListener,Fragment_Substrate.SubstrateFragmentListener {
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
    private float[] topScaling = null;
    private float[] bottomScaling = null;
    private String substrate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        //for changing the images of the buttons
        top = (ImageButton)findViewById(R.id.imageButton2);
        bottom = (ImageButton)findViewById(R.id.imageButton4);
    }

    //dialog for selecting source of photo
    public void selectSource(View view){
        Fragment_PictureSource pickSource = new Fragment_PictureSource();
        pickSource.show(getSupportFragmentManager(), "Fragment_PictureSource");

        switch (view.getId()) {
            case R.id.imageButton2:
                source = "top";
                break;
            case R.id.imageButton4:
                source = "bottom";
                break;
        }
    }

    public void onSelectSource(DialogFragment dialog, int which){
        dialog.dismiss();

        Intent intent = new Intent();

        //take picture
        if(which == 0){
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {}

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

    //dialog for selecting substrate
    public void selectSubstrate(View view){
        DialogFragment pickSubstrate = new Fragment_Substrate();
        pickSubstrate.show(getSupportFragmentManager(), "Fragment_Substrate");
    }

    public void onSelectSubstrate(DialogFragment dialog, int which){
        dialog.dismiss();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.substrate_array, R.layout.substrate_listview);

        substrate = Integer.toString(which+1);
        Button substrateButton = (Button)findViewById(R.id.substrateButton);
        substrateButton.setText(adapter.getItem(which).toString());
    }

    //function for creating image file if source is camera
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

        Bitmap bitmap = null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCurrentPhotoPath);
        }catch (Exception e){Log.d("inside-try", e.toString());}

        if(source.equals("top"))
            top.setImageBitmap(bitmap);
        if(source.equals("bottom"))
            bottom.setImageBitmap(bitmap);
    }

    //for starting activity to get bounding box
    private void startCrop(){
        Intent cropIntent = new Intent(Activity_Identify.this, Activity_Crop.class);

        if(source.equals("top")){
            cropIntent.putExtra("photoPath", topPhotoPath.toString());
            startActivityForResult(cropIntent, 3);
        }

        if(source.equals("bottom")){
            cropIntent.putExtra("photoPath", bottomPhotoPath.toString());
            startActivityForResult(cropIntent, 5);
        }
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
            topScaling = (float[]) data.getSerializableExtra("scaling");
        }
        if (requestCode == 5 && resultCode == RESULT_OK) {
            bottomCoords = (ArrayList<Point>) data.getSerializableExtra("coordinates");
            bottomScaling = (float[]) data.getSerializableExtra("scaling");
        }


        if(data != null && requestCode == 6 && resultCode == RESULT_CANCELED && data.getBooleanExtra("errorEncountered",false)){
            Toast toast = Toast.makeText(Activity_Identify.this, "Cannot Process Image", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void startProcessActivity(View view){
        if(topPhotoPath == null && bottomPhotoPath == null){
            Toast toast = Toast.makeText(Activity_Identify.this, "Both Top Photo and Underside Photo can't be empty", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        if(substrate == null){
            Toast toast = Toast.makeText(Activity_Identify.this, "Please pick substrate", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        Intent intent = new Intent(Activity_Identify.this, Activity_Process.class);

        //put top photo data
        if(topPhotoPath != null){
            intent.putExtra("topPhotoPath", topPhotoPath.toString());
            intent.putExtra("topCoords", topCoords);
            intent.putExtra("topScaling", topScaling);
        }

        if(bottomPhotoPath != null){
            intent.putExtra("bottomPhotoPath", bottomPhotoPath.toString());
            intent.putExtra("bottomCoords", bottomCoords);
            intent.putExtra("bottomScaling", bottomScaling);
        }

        intent.putExtra("substrate", substrate);
        startActivityForResult(intent, 6);
    }

}
