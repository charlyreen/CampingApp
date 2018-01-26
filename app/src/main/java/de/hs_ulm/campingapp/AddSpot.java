package de.hs_ulm.campingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Martin on 06.01.18.
 */


public class AddSpot extends AppCompatActivity
{
    /*Firebase Storage Reference*/
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    ImageButton launchPhoto;
    EditText name;
    EditText description;
    Spinner type;
    ImageView mAddSpotImgPreview;
    int REQUEST_IMAGE_CAPTURE = 103;
    DatabaseReference mRootRef;
    String spotkey;
    ProgressDialog mProgress;
    String imgPath;
    private int countPics;
    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        countPics = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);
        mCustomPagerAdapter = new CustomPagerAdapter(this);
        mViewPager = findViewById(R.id.addSpotviewPager);
        mViewPager.setAdapter(mCustomPagerAdapter);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        spotkey = mRootRef.child("spots").push().getKey();
        launchPhoto = findViewById(R.id.addSpotLaunchCam);
        name = (EditText) findViewById(R.id.addSpotName);
        description = (EditText) findViewById(R.id.addSpotDescription);
        //EditText picture = (EditText) findViewById(R.id.addSpotPicture);
        type = (Spinner) findViewById(R.id.addSpotSpinnerType);
        //mAddSpotImgPreview = findViewById(R.id.addSpotImgPreview);
        //mAddSpotImgPreview.setVisibility(View.GONE);
        mProgress = new ProgressDialog(this);
        //Set onClickListener for launching Camera
        launchPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera();
            }
        });

        // read the intent values
        Bundle extras = getIntent().getExtras();
        if (extras == null)
        {
            return;
        }
        LatLng markerPosition = extras.getParcelable("position");
        if (markerPosition != null)
        {
            Toast.makeText(getApplicationContext(), "Fill out form to add marker" ,
                    Toast.LENGTH_LONG).show();
        }
        else
            return;

    }
    public void onLaunchCamera()
    {
        if(countPics < 3) {
            Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePic, REQUEST_IMAGE_CAPTURE);
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.tooManyPics), Toast.LENGTH_SHORT).show();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(countPics < 3) {
                mProgress.setMessage("Uploading image...");
                mProgress.show();
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                resizeBitmap(imageBitmap, 200);
                //mAddSpotImgPreview.setVisibility(View.VISIBLE);
                //mAddSpotImgPreview.setImageBitmap(imageBitmap);
                String storagePath = spotkey + "/" + countPics + ".png";
                StorageReference indexPic = storageRef.child(storagePath);
                encodeBitmapAndSaveToFireBase(imageBitmap, indexPic);
                countPics++;

            }
        }
    }
    public void encodeBitmapAndSaveToFireBase(Bitmap bitmap, StorageReference stRef)
    {
        imgPath = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = stRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                imageToDB(downloadUrl, mRootRef, spotkey);
                imgPath = downloadUrl.toString();
                Log.d("Success", taskSnapshot.getDownloadUrl().toString());
                mCustomPagerAdapter.add(imgPath);
                mProgress.dismiss();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_SHORT);
            mAddSpotImgPreview.setVisibility(View.GONE);
            }
        });
    }
    public void imageToDB(Uri pathToImg, DatabaseReference mmRootRef, String spotkey) {
        String pathToImgStr;
        pathToImgStr = pathToImg.toString();
        mmRootRef.child("imagePaths").child(spotkey).child("index").push().setValue(pathToImgStr);
    }

    private Bitmap resizeBitmap(Bitmap img, int maxSize) {
        Bitmap resized = null;

        int outWidth;
        int outHeight;
        int inWidth = img.getWidth();
        int inHeight = img.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        if(img != null) {
            resized = Bitmap.createScaledBitmap(img, outWidth, outHeight, true);
        }
        return resized;
    }
    /*public void onStart()
    {
        super.onStart();
        //Spot dummySpot;
        //dummySpot = new Spot("",  0.0, 0.0, "", "", "", 0, "", false);
        spotkey = mRootRef.child("spots").push().getKey();

    }*/

    public void oukay(View view)
    {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        Bundle extras = getIntent().getExtras();
        LatLng markerPosition = extras.getParcelable("position");
        String author = extras.getString("author");

        //show intent values in text fields
        EditText name = (EditText) findViewById(R.id.addSpotName);
        EditText description = (EditText) findViewById(R.id.addSpotDescription);
        //EditText picture = (EditText) findViewById(R.id.addSpotPicture);
        Spinner type = (Spinner) findViewById(R.id.addSpotSpinnerType);


        Long tsLong = System.currentTimeMillis()/1000;

        Spot newSpot;
        String imgPath_;
        if(imgPath != null) {
            imgPath_ = imgPath;
        }
        else {
            imgPath_ = "";
        }
        newSpot = new Spot(author, markerPosition.latitude,
                markerPosition.longitude, name.getText().toString(),
                description.getText().toString(), imgPath_, tsLong,
                type.getSelectedItem().toString(), true);


        //Toast.makeText(getApplicationContext(), newSpot.toString() , Toast.LENGTH_LONG)
        //        .show();
        mRootRef.child("spots").child(spotkey).setValue(newSpot);

        finish();
    }

    public void cancel(View view)
    {
        Toast.makeText(getApplicationContext(), "Cancelled spot creation" , Toast.LENGTH_LONG)
                .show();
        finish();
    }

    @Override
    public void onBackPressed()
    {
        cancel(getCurrentFocus());
        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        //Intent data = new Intent();
        //data.putExtra("returnKey2", "testeroni");
        //setResult(RESULT_OK, data);
        super.finish();
    }


}
