package de.hs_ulm.campingapp;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.media.Rating;
import android.os.Handler;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
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
    ImageButton mcommDeleteButton;
    private float spotRating;
    private int commCounter;
    private ArrayList<SpotComment> commentArrayList;
    private SpotCommentAdapter spotcomAdapter;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    class viewWorkAround {
        View v;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        commCounter = 0;
        spotRating = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comments);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
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
        final String spotkey = extras.getString("key");
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

        //Get Comments from DB with normal listview without firebaseUI
        mListComments = findViewById(R.id.commListView);
        commentArrayList = new ArrayList<SpotComment>();
        spotcomAdapter = new SpotCommentAdapter(this, commentArrayList);
        mListComments.setAdapter(spotcomAdapter);
        mRootRef.child("comments").child(spotkey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SpotComment comm = dataSnapshot.getValue(SpotComment.class);
                comm.setCommentKey(dataSnapshot.getKey());
                comm.setSpotKey(spotkey);
                commentArrayList.add(comm);
                spotcomAdapter.notifyDataSetChanged();
                countRatingUp(comm.getRating(), mCommRating);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                SpotComment comm = dataSnapshot.getValue(SpotComment.class);
                commentArrayList.remove(comm);
                spotcomAdapter.notifyDataSetChanged();
                countRatingDown(comm.getRating(), mCommRating);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set Title, Description of Location from Intent Extra Spot Object
        mTitle.setText(thisSpot.getName());
        mDescr.setText(thisSpot.getDescription());
        mDistance.setText(thisSpot.getDistanceToInKM(mCurrentLocation) + " " + getString(R.string.showComments_distance));
        mType.setText(thisSpot.getType());
        //Download Image in Background -> UI doesnt freeze meeeen
        new DownloadImageTask(mSpotPic).execute(thisSpot.getPic());
        mcommDeleteButton = findViewById(R.id.commDeleteButton);
        //Set onClickListener to Delete + Dialog INterface
        mcommDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(ShowComments.this);
                builder.setTitle(getString(R.string.really_delete_spot));
                builder.setMessage(getString(R.string.really_delete_spot_long));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteSpot(spotkey);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


    }

    private void deleteSpot(String spotkey) {
        mRootRef.child("spots").child(spotkey).setValue(null);
        mRootRef.child("comments").child(spotkey).setValue(null);
        mRootRef.child("imagePaths").child(spotkey).setValue(null);
        //deleting folders is not possible without keeping track by yourself
        //so: delete the index file:
        StorageReference refToDelete= storageRef.child(spotkey + "/index.png");
        refToDelete.delete();
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //listAdapter.startListening();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            mRootRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        User dbUser = dataSnapshot.getValue(User.class);
                        updateUI(dbUser);
                    }
                    else {
                        updateUI(null);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    private void countRatingUp(int currentRating, RatingBar rtb) {
        this.commCounter++;
        this.spotRating = this.spotRating + (float) currentRating;
        rtb.setRating(this.spotRating/(float)this.commCounter);
    }
    private void countRatingDown(int currentRating, RatingBar rtb) {
        this.commCounter--;
        this.spotRating = this.spotRating - (float) currentRating;
        rtb.setRating(this.spotRating/(float)this.commCounter);
    }
    private void updateUI(User user) {
        if(user != null) {
            if(user.isAdmin()) {
                mcommDeleteButton.setVisibility(View.VISIBLE);
            }
            else {
                mcommDeleteButton.setVisibility(View.GONE);
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    public void finish() {
        super.finish();
    }
}
