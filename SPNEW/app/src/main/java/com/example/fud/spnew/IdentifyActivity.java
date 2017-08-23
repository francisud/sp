package com.example.fud.spnew;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.net.Uri;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Bitmap;

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
                        startActivityForResult(intent, 0);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //camera
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if(source.equals("top"))
                top.setImageBitmap(imageBitmap);
            if(source.equals("side"))
                side.setImageBitmap(imageBitmap);
            if(source.equals("bottom"))
                bottom.setImageBitmap(imageBitmap);
        }


        //file browser
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            selectedImagePath = selectedImageUri.getPath();
            //img.setImageURI(selectedImageUri);
        }



    }


}
