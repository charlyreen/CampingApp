package de.hs_ulm.campingapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Rating;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Rene on 24.01.2018.
 */

public class SpotCommentAdapter extends ArrayAdapter<SpotComment> {
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    Context cont;

    public SpotCommentAdapter(Context context, ArrayList<SpotComment> spotcomments) {
        super(context,0, spotcomments);
        cont = context;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)  {
        final SpotComment spotcom = getItem(position);
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_commentlist, parent, false);
        }
        TextView mCommentTXTVdate = convertView.findViewById(R.id.commentTXTVdate);
        RatingBar mCommentRatingBar = convertView.findViewById(R.id.commentRatingBar);
        TextView mcommentTXTVcomment = convertView.findViewById(R.id.commentTXTVcomment);
        TextView mcommentTXTVAuthor = convertView.findViewById(R.id.commentTXTVAuthor);
        ImageButton mCommDetailDeleteButton = convertView.findViewById(R.id.commDetailDeleteButton);

        mCommentTXTVdate.setText(spotcom.getDate());
        mCommentRatingBar.setRating((float)spotcom.getRating());
        mcommentTXTVcomment.setText(spotcom.getText());

        spotcom.setUserText(mRootRef, mcommentTXTVAuthor);
        if(spotcom.belongsToUser(mAuth.getUid())) {
            mCommDetailDeleteButton.setVisibility(View.VISIBLE);
        }
        else
        {
            mCommDetailDeleteButton.setVisibility(View.GONE);
        }
        mCommDetailDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(cont);
                builder.setTitle(cont.getString(R.string.really_delete_comment));
                builder.setMessage(cont.getString(R.string.really_delete_comment_long));
                builder.setPositiveButton(cont.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        spotcom.delete(mRootRef);
                        Activity thisAct = (Activity) cont;
                        dialogInterface.dismiss();
                        thisAct.recreate();
                    }
                });
                builder.setNegativeButton(cont.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        return convertView;
    }

    @Override
    public SpotComment getItem(int position) {
        //Hack to return spotcomments in reverse order
        return super.getItem(getCount() - 1 - position);
    }

}

