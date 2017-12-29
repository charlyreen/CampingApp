package de.hs_ulm.campingapp;

import android.os.Bundle;

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
        return b;
    }

}
