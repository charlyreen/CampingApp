package de.hs_ulm.campingapp;

import android.media.Rating;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class ShowComments extends AppCompatActivity {
    DatabaseReference mRootRef;
    private FirebaseListAdapter<SpotComment> listAdapter;
    ListView mListComments;
    TextView mTitle;
    TextView mDescr;
    RatingBar mCommRating;
    float spotRating = 0;
    private int commCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        commCounter = 0;
        spotRating = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comments);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        //get UI elements
        mListComments = (ListView) findViewById(R.id.commListView);
        mTitle = (TextView) findViewById(R.id.commTitle);
        mDescr = (TextView) findViewById(R.id.commDescr);
        mCommRating = (RatingBar) findViewById(R.id.commRating);

        //get Intent
        Bundle extras = getIntent().getExtras();
        String spotkey = extras.getString("key");

        Query queryFiltered = mRootRef.child("comments").child(spotkey).limitToLast(10);
        FirebaseListOptions<SpotComment> options = new FirebaseListOptions.Builder<SpotComment>()
                .setQuery(queryFiltered, SpotComment.class)
                .setLayout(R.layout.layout_commentlist)
                .build();
        listAdapter = new FirebaseListAdapter<SpotComment>(options) {
            @Override
            protected void populateView(View v, SpotComment model, int position) {
                //Populate Listview
                ((TextView) v.findViewById(R.id.commentTXTVAuthor))
                        .setText("AuthorID: " + model.getUserkey());
                ((RatingBar) v.findViewById(R.id.commentRatingBar))
                        .setRating((float) model.getRating());
                ((TextView) v.findViewById(R.id.commentTXTVcomment))
                        .setText(model.getText());
                spotRating += model.getRating();
            }

        };

        //Attach Adapter to ListView
        mListComments.setAdapter(listAdapter);

        //get Location, set Title
        mRootRef.child("spots").child(spotkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Spot location;
                location = dataSnapshot.getValue(Spot.class);
                mTitle.setText(location.getName());
                mDescr.setText(location.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        listAdapter.startListening();
        //Delay wird gebraucht um ListView erstmal zu laden
        //Hier: Gesamtrating
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                commCounter = mListComments.getAdapter().getCount();
                spotRating = spotRating/2;
                spotRating = spotRating/commCounter;
                mCommRating.setRating(spotRating);
                Toast.makeText(getApplicationContext(), Float.toString(spotRating), Toast.LENGTH_LONG).show();
            }
        }, 500);

        //Set Gesamtrating


    }
    @Override
    protected void onStop() {
        super.onStop();
        listAdapter.stopListening();
    }

}
