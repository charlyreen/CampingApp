package de.hs_ulm.campingapp;

/**
 * Created by Rene on 14.12.2017.
 */

public class Spot {
    private int authorID;
    private double latitude;
    private double longitude;
    private String name;
    private String pic;
    private long timestamp;
    private String type;
    private boolean visible;

    public Spot() {
        //Default constructor required for calls to DataSnapshot.getValue(Spot.class)
    }
    public Spot(int authorID_, double latitude_, double longitude_, String name_, String pic_,
                long timestamp_, String type_, boolean visible_)
    {
        this.authorID = authorID_;
        this.latitude = latitude_;
        this.longitude = longitude_;
        this.name = name_;
        this.pic = pic_;
        this.timestamp = timestamp_;
        this.type = type_;
        this.visible = visible_;
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
    public String getType()
    {
        return type;
    }

}
