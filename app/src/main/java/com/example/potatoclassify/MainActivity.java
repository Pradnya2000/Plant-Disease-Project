package com.example.potatoclassify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private int mInputSize = 224;//256;
    private String mModelPath="plantDisease.tflite";
    private String mlabelPath="labels.txt";
    private Classifier classifier;
    private Button cameraButton;
    private Button detectButton;
    private Button galleryButton;
    private ImageView imageView;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try
        {
            initClassifier();
            initViews();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initClassifier() throws IOException {
        classifier=new Classifier(getAssets(),mModelPath, mlabelPath,mInputSize);

    }
    private void initViews()
    {
        cameraButton=findViewById(R.id.mCameraButton);
        imageView=findViewById(R.id.mPhotoImageView);
        detectButton=findViewById(R.id.mDetectButton);
        galleryButton=findViewById(R.id.mGalleryButton);
        cameraButton.setOnClickListener(this);
        detectButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        /*
        findViewById(R.id.healthyid).setOnClickListener(this);
        findViewById(R.id.earlyid).setOnClickListener(this);
        findViewById(R.id.lateid).setOnClickListener(this);
        findViewById(R.id.late2id).setOnClickListener(this);*/
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.mCameraButton)
          dispatchTakePictureIntent();
        else if(view.getId()==R.id.mDetectButton)
        {
            List<Classifier.Recognition> result=classifier.recognizeImage(bitmap);
            Toast.makeText(this, result.get(0).toString(), Toast.LENGTH_SHORT).show();
        }
        else if(view.getId()==R.id.mGalleryButton)
        {
           pickImageFromGallery();
        }

       // Bitmap bitmap= ((BitmapDrawable)((ImageView)view).getDrawable()).getBitmap();
    //    List<Classifier.Recognition> result=classifier.recognizeImage(bitmap);
      //  Toast.makeText(this, result.get(0).toString(), Toast.LENGTH_SHORT).show();
    }


    public static final int IMAGEREQUESTCODE = 2;


    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, IMAGEREQUESTCODE);
    }



    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);

        }
        else if(requestCode == IMAGEREQUESTCODE && resultCode == RESULT_OK)
        {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}