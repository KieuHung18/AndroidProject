package com.example.gallery.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class ImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView image;
    public ImageTask(ImageView image){
        this.image=image;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap b=null;
        try {
            InputStream is = (InputStream) new URL(strings[0]).openStream();
            b = BitmapFactory.decodeStream(is);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return b;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if(result!=null){
            image.setImageBitmap(result);
        }
    }
}

