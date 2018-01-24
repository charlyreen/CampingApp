package de.hs_ulm.campingapp;

import android.content.Context;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Rene on 24.01.2018.
 */

public class SpotCommentAdapter extends ArrayAdapter<SpotComment> {
    private DatabaseReference mRootRef;

    public SpotCommentAdapter(Context context, ArrayList<SpotComment> spotcomments) {
        super(context,0, spotcomments);
        mRootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)  {
        SpotComment spotcom = getItem(position);
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_commentlist, parent, false);
        }
        TextView mCommentTXTVdate = convertView.findViewById(R.id.commentTXTVdate);
        RatingBar mCommentRatingBar = convertView.findViewById(R.id.commentRatingBar);
        TextView mcommentTXTVcomment = convertView.findViewById(R.id.commentTXTVcomment);
        TextView mcommentTXTVAuthor = convertView.findViewById(R.id.commentTXTVAuthor);

        //countComments++;
        //sumOfRatings+=(float)spotcom.getRating();
        mCommentTXTVdate.setText(spotcom.getDate());
        mCommentRatingBar.setRating((float)spotcom.getRating());
        mcommentTXTVcomment.setText(spotcom.getText());
        //mcommentTXTVAuthor.setText(spotcom.getUserkey());
        spotcom.setUserText(mRootRef, mcommentTXTVAuthor);
        return convertView;
    }

    @Override
    public SpotComment getItem(int position) {
        //Hack to return spotcomments in reverse order
        return super.getItem(getCount() - 1 - position);
    }

}

