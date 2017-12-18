package de.hs_ulm.campingapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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
    private Spot location;

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
        location = (Spot) marker.getTag();
        View view = context.getLayoutInflater().inflate(R.layout.custominfowindow, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);
        //ImageButton image = (ImageButton) view.findViewById(R.id.image);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        tvTitle.setText(location.getName());
        tvSubTitle.setText(location.getDescription());


        Uri bild = new Uri.Builder()
               .path(location.getPic())
               .build();
        image.setImageURI(bild);

       //try
       //{
       //    Bitmap bitmap =
       //            BitmapFactory.decodeStream((InputStream)new URL(location.getPic())
       //                    .getContent());
       //    image.setImageBitmap(bitmap);
       //    image.isShown();
       //
       //}
       //catch (MalformedURLException e)
       //{
       //    e.printStackTrace();
       //}
       //catch (IOException e)
       //{
       //    e.printStackTrace();
       //}

        return view;
    }


}
