package de.hs_ulm.campingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mad on 06.01.18.
 */


public class AddSpot extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);
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

    public void oukay(View view)
    {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        Bundle extras = getIntent().getExtras();
        LatLng markerPosition = extras.getParcelable("position");
        String author = extras.getString("author");

        //show intent values in text fields
        EditText name = (EditText) findViewById(R.id.addSpotName);
        EditText description = (EditText) findViewById(R.id.addSpotDescription);
        EditText picture = (EditText) findViewById(R.id.addSpotPicture);
        Spinner type = (Spinner) findViewById(R.id.addSpotSpinnerType);

        Long tsLong = System.currentTimeMillis()/1000;

        Spot newSpot;
        newSpot = new Spot(author, markerPosition.latitude,
                markerPosition.longitude, name.getText().toString(),
                description.getText().toString(), picture.getText().toString(), tsLong,
                type.getSelectedItem().toString(), true);


        Toast.makeText(getApplicationContext(), newSpot.toString() , Toast.LENGTH_LONG)
                .show();
        mRootRef.child("spots").push().setValue(newSpot);

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
