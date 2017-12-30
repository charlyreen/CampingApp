package de.hs_ulm.campingapp;

import android.os.Bundle;
import android.text.format.DateFormat;

import java.util.Calendar;


/**
 * Created by Rene on 17.12.2017.
 */

public class SpotComment {
    private String userkey;
    private int rating;
    private String text;
    private long timestamp;

    public SpotComment() {
        //Default constructor required for calls to DataSnapshot.getValue(SpotComment.class)
    }

    public SpotComment(String userkey_, int rating_, String text_)
    {
        userkey = userkey_;
        rating = rating_;
        text = text_;
        timestamp = System.currentTimeMillis();
    }

    public SpotComment(Bundle b) {
        userkey = b.getString("userkey");
        rating = b.getInt("rating");
        text = b.getString("text");
        timestamp = b.getLong("timestamp");
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("userkey", userkey);
        b.putInt("rating", rating);
        b.putString("text", text);
        b.putLong("timestamp", timestamp);
        return b;
    }
    public String getDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd.MM.yyyy HH:mm", cal).toString();
        return date;
    }

    public void setUserkey(String userkey_) {
        userkey = userkey_;
    }
    public void setRating(int rating_) {
        rating = rating_;
    }
    public void setText(String text_) {
        text = text_;
    }

    public String getUserkey() {
        return userkey;
    }
    public int getRating() {
        return rating;
    }
    public String getText() {
        return text;
    }
    public long getTimestamp() { return timestamp; }

}
