package de.hs_ulm.campingapp;

/**
 * Created by Rene on 17.12.2017.
 */

public class SpotComment {
    private String userkey;
    private int rating;
    private String text;

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
