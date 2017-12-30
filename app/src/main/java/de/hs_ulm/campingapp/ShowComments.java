package de.hs_ulm.campingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.media.Rating;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    FloatingActionButton mCommNewComment;
    TextView mDistance;
    TextView mType;
    ImageView mSpotPic;
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
        mCommNewComment = (FloatingActionButton) findViewById(R.id.commNewComment);
        mDistance = (TextView) findViewById(R.id.commDistance);
        mType = (TextView) findViewById(R.id.commType);
        mSpotPic = (ImageView) findViewById(R.id.spotPic);

        //get Intent: Spot data + spotKey!
        Bundle extras = getIntent().getExtras();
        String spotkey = extras.getString("key");
        Spot thisSpot = new Spot(extras);
        //get Intent: current GPS Location currloc
        Location mCurrentLocation = new Location("currentLocation");
        mCurrentLocation.setLatitude(extras.getFloat("currLocLatitude"));
        mCurrentLocation.setLongitude(extras.getFloat("currLocLongitude"));

        //onClick Floating Button-> Make new Comment Activity
        final Intent makeNewComment;
        makeNewComment = new Intent(this.getApplicationContext(), MakeNewComment.class);
            //extras = getIntent().getExtras() -> no need to be bundled again!
        makeNewComment.putExtras(extras);
        mCommNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(makeNewComment);
            }
        });

        //Get Comments from DB (limited to last 10)
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
                ((TextView) v.findViewById(R.id.commentTXTVdate))
                        .setText(model.getDate());
                spotRating += model.getRating();
            }

        };

        //Attach Adapter to ListView
        mListComments.setAdapter(listAdapter);

        //set Title, Description of Location from Intent Extra Spot Object
        mTitle.setText(thisSpot.getName());
        mDescr.setText(thisSpot.getDescription());
        mDistance.setText(thisSpot.getDistanceToInKM(mCurrentLocation) + " " +getString(R.string.showComments_distance));
        mType.setText(thisSpot.getType());
        //Download Image in Background -> UI doesnt freeze meeeen
        new DownloadImageTask(mSpotPic).execute(thisSpot.getPic());
    }
    @Override
    protected void onStart() {
        super.onStart();
        listAdapter.startListening();
        //Delay wird gebraucht um ListView erstmal zu laden
        //Hier: Gesamtrating  berechnen
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                commCounter = mListComments.getAdapter().getCount();
                spotRating = spotRating/2;
                spotRating = spotRating/commCounter;
                //set gesamtrating in textview
                mCommRating.setRating(spotRating);
            }
        }, 500);


    }
    @Override
    protected void onStop() {
        super.onStop();
        listAdapter.stopListening();
    }
}
