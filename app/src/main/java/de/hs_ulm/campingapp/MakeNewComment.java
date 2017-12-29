package de.hs_ulm.campingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MakeNewComment extends AppCompatActivity {

    TextView mNewCommTitle;
    TextView mNewCommDescr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_new_comment);
        //get UI Elements
        mNewCommTitle = (TextView) findViewById(R.id.newCommTitle);
        mNewCommDescr = (TextView) findViewById(R.id.newCommDescr);
        //geplant ist ein Ausschnitt der Karte (möglich?)
        //get Intent data(spotkey)
        Bundle extras = getIntent().getExtras();
        Spot thisSpot = new Spot(extras);
        String spotkey = new String(extras.getString("key"));

        //write UI elements
        mNewCommTitle.setText(thisSpot.getName());
        mNewCommDescr.setText(thisSpot.getDescription());
    }
}
