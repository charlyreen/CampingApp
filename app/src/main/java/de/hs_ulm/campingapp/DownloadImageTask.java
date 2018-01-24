package de.hs_ulm.campingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

/**
 * Created by Rene on 30.12.2017.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView img;

    public DownloadImageTask(ImageView img) {
        this.img = img;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);

        }
        catch(Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }
    private Bitmap resizeBitmap(Bitmap img, int toWidth, int toHeight) {
        Bitmap resized = null;
        if(img != null) {
            resized = Bitmap.createScaledBitmap(img, toWidth, toHeight, true);
        }
        return resized;
    }

    protected void onPostExecute(Bitmap result) {
        if(result != null) {
            /*Bitmap newResult = resizeBitmap(result, 100, 100);
            img.setImageBitmap(newResult);*/
            img.setImageBitmap(result);
        }
    }
    protected void onCancelled(Bitmap result) {
        Log.e("Error", "Picture couldn't be loaded");
    }
}
