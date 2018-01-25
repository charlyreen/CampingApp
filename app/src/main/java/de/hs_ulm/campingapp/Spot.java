package de.hs_ulm.campingapp;

import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;

/**
 * Created by Rene on 14.12.2017.
 */

public class Spot {
    private String authorID;
    private double latitude;
    private double longitude;
    private String name;
    private String description;
    private String pic;
    private long timestamp;
    private String type;
    private boolean visible;
    private String spotKey;

    public Spot()
    {
        //Default constructor required for calls to DataSnapshot.getValue(Spot.class)
    }
    public Spot(String authorID_, double latitude_, double longitude_, String name_,
                String description_, String pic_, long timestamp_, String type_, boolean visible_)
    {
        this.authorID = authorID_;
        this.latitude = latitude_;
        this.longitude = longitude_;
        this.name = name_;
        this.description = description_;
        this.pic = pic_;
        this.timestamp = timestamp_;
        this.type = type_;
        this.visible = visible_;
    }
    public Spot(Bundle b) {
        authorID = b.getString("authorID");
        latitude = b.getDouble("latitude");
        longitude = b.getDouble("longitude");
        name = b.getString("name");
        description = b.getString("description");
        pic = b.getString("pic");
        timestamp = b.getLong("timestamp");
        type = b.getString("type");
        visible = b.getBoolean("visible");
        spotKey = b.getString("key");
    }
    public double getLatitude()
    {
        return latitude;
    }
    public double getLongitude()
    {
        return longitude;
    }
    public String getName()
    {
        return name;
    }
    public String getDescription()
    {
        return description;
    }
    public String getType()
    {
        return type;
    }
    public String getPic()
    {
        return pic;
    }
    public boolean isVisible() { return visible; }
    public long getTimestamp() { return timestamp; }
    public String getAuthorID() { return authorID; }
    @Exclude
    public String getSpotKey() { return spotKey; }
    @Exclude
    public void setSpotKey(String spotkey_) { spotKey = spotkey_; }
    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("authorID", authorID);
        b.putDouble("latitude", latitude);
        b.putDouble("longitude",longitude);
        b.putString("name", name);
        b.putString("description", description);
        b.putString("pic", pic);
        b.putLong("timestamp", timestamp);
        b.putString("type", type);
        b.putBoolean("visible", visible);
        b.putString("key", spotKey);
        return b;
    }
    private float getDistanceTo(Location loc) {
        //get Distance from this spot to another location object in meters!
        Location thisLoc = new Location(name);
        thisLoc.setLatitude(latitude);
        thisLoc.setLongitude(longitude);
        float distance = thisLoc.distanceTo(loc);
        return distance;
    }
    public String getDistanceToInKM(Location loc)
    {
        float distanceInM = this.getDistanceTo(loc)/1000;
        BigDecimal bd = new BigDecimal(Float.toString(distanceInM));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.toString() + " km";
    }
    public void setUserText(DatabaseReference mRootRef, final TextView userTxtxV)
    {
        mRootRef.child("users").child(this.getAuthorID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    User dbUser = dataSnapshot.getValue(User.class);
                    userTxtxV.setText(("by " + dbUser.getName()));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
