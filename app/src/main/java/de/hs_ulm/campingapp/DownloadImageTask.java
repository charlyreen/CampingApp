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
    /*private Bitmap resizeBitmap(Bitmap img, int toWidth, int toHeight) {
        Bitmap resized = null;
        if(img != null) {
            resized = Bitmap.createScaledBitmap(img, toWidth, toHeight, true);
        }
        return resized;
    }*/

    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if(image != null) {
            if (maxHeight > 0 && maxWidth > 0) {
                int width = image.getWidth();
                int height = image.getHeight();
                float ratioBitmap = (float) width / (float) height;
                float ratioMax = (float) maxWidth / (float) maxHeight;

                int finalWidth = maxWidth;
                int finalHeight = maxHeight;
                if (ratioMax > ratioBitmap) {
                    finalWidth = (int) ((float)maxHeight * ratioBitmap);
                } else {
                    finalHeight = (int) ((float)maxWidth / ratioBitmap);
                }
                image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
                return image;
            } else {
                return image;
            }
        }
        else {
            return null;
        }

    }

    protected void onPostExecute(Bitmap result) {
        if(result != null) {
            /*Bitmap newResult = resizeBitmap(result, 100, 100);
            img.setImageBitmap(newResult);*/
            //Bitmap newResult = resize(result, 500,500);
            img.setImageBitmap(result);
        }
    }
    protected void onCancelled(Bitmap result) {
        Log.e("Error", "Picture couldn't be loaded");
    }
}
