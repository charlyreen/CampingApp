package de.hs_ulm.campingapp;

import android.os.Bundle;

/**
 * Created by Rene on 17.12.2017.
 */

public class SpotComment {
    private String userkey;
    private int rating;
    private String text;

    public SpotComment() {}

    public SpotComment(String userkey_, int rating_, String text_)
    {
        userkey = userkey_;
        rating = rating_;
        text = text_;
    }

    public SpotComment(Bundle b) {
        userkey = b.getString("userkey");
        rating = b.getInt("rating");
        text = b.getString("text");
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("userkey", userkey);
        b.putInt("rating", rating);
        b.putString("text", text);
        return b;
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
}
