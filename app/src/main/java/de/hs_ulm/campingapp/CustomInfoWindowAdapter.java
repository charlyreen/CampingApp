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

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);
        ImageButton image = (ImageButton) view.findViewById(R.id.image);
        //ImageView test = (ImageView) view.findViewById(R.id.image);

        Uri bild = new Uri.Builder()
               .path("http://static1.1.sqspcdn.com/static/f/394173/4801463/1258567659777/Twitter.bmp?token=E6epMyYR5dcpG0c7hsyeh2fBcPE%3D")
               .build();
        image.setImageURI(bild);

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



        tvTitle.setText(marker.getTitle());
        tvSubTitle.setText(marker.getSnippet());

        return view;
    }


}
