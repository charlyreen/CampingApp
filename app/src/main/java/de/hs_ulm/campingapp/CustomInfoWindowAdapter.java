package de.hs_ulm.campingapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * Created by mad on 16.12.17.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    private Activity context;
    private DatabaseReference mRootRef;
    TextView tvTitle, tvSubTitle;


    public CustomInfoWindowAdapter(Activity context)
    {
        this.context = context;

    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        View view = context.getLayoutInflater().inflate(R.layout.custominfowindow, null);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);
        ImageView mImage= (ImageView) view.findViewById(R.id.image);
        Spot location = (Spot) marker.getTag();
        tvTitle.setText(location.getName());
        tvSubTitle.setText((CharSequence) location.getType());


        //ImageView test = (ImageView) view.findViewById(R.id.image);
        String uri_ = "http://static1.1.sqspcdn.com/static/f/394173/4801463/1258567659777/Twitter.bmp?token=E6epMyYR5dcpG0c7hsyeh2fBcPE%3D";
        new DownloadImageTask(mImage)
                .execute(uri_);
        /*Uri bild = new Uri.Builder()
               .path(uri_)
               .build();
        image.setImageURI(bild);*/

//        try
//        {
//            Bitmap bitmap =
//                    BitmapFactory.decodeStream((InputStream)new URL("https://upload.wikimedia.org/wikipedia/commons/1/11/Giengen_an_der_Brenz_001.jpg")
//                            .getContent());
//            image.setImageBitmap(bitmap);
//        }
//        catch (MalformedURLException e)
//        {
//            e.printStackTrace();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }





        return view;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

