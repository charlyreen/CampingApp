package de.hs_ulm.campingapp;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MakeNewComment extends AppCompatActivity {
    DatabaseReference mRootRef;
    TextView mNewCommTitle;
    TextView mNewCommDescr;
    FloatingActionButton mNewCommConfirm;
    EditText mNewCommComment;
    RatingBar mNewCommRatingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_new_comment);
        //get DB REF
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        //get UI Elements
        mNewCommTitle = (TextView) findViewById(R.id.newCommTitle);
        mNewCommDescr = (TextView) findViewById(R.id.newCommDescr);
        mNewCommConfirm = (FloatingActionButton) findViewById(R.id.newCommConfirm);
        mNewCommComment = (EditText) findViewById(R.id.newCommComment);
        mNewCommRatingBar = (RatingBar) findViewById(R.id.newCommRatingBar);
        //geplant ist ein Ausschnitt der Karte (möglich?)
        //get Intent data(spotkey)
        Bundle extras = getIntent().getExtras();
        Spot thisSpot = new Spot(extras);
        final String spotkey = extras.getString("key");

        //write UI elements
        mNewCommTitle.setText(thisSpot.getName());
        mNewCommDescr.setText(thisSpot.getDescription());

        //On Click mNewCommConfirm:
        mNewCommConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpotComment spotcom;
                String newComment;
                String userkey;
                int rating;
                newComment = mNewCommComment.getText().toString();
                rating = (int) mNewCommRatingBar.getRating();
                //TODO: userkey von firebase auth
                userkey = "admin";
                spotcom = new SpotComment(userkey, rating, newComment);
                //Abfrage ob der Kommentar eine Mindestanzahl an Zeichen hat:
                if(newComment.length() < 4)
                {
                    Toast.makeText(getApplicationContext(), R.string.makeNewComments_errorCommentLength, Toast.LENGTH_LONG).show();
                }
                else
                {
                    writeCommentToDataBase(spotcom, spotkey);
                    Toast.makeText(getApplicationContext(), "Done.", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            //Toast.makeText(getApplicationContext(), mAuth.getUid(), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    public void finish() {
        super.finish();
    }

    private void writeCommentToDataBase(SpotComment spotcom, String spotkey) {
        mRootRef.child("comments").child(spotkey).push().setValue(spotcom);
    }
}