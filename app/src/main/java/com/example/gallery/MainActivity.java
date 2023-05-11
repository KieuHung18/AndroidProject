package com.example.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Button continueLogin, googleLogin;
    private EditText editTextEmailAddress;
    ImageView image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        continueLogin = (Button) findViewById(R.id.continueLogin);
        googleLogin = (Button) findViewById(R.id.googleLogin);

        editTextEmailAddress = (EditText) findViewById(R.id.editTextEmailAddress);

        Intent loginIntent = new Intent(this,LoginActivity.class);
        Intent homeIntent = new Intent(this,HomeActivity.class);

        continueLogin.setOnClickListener(
            new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    String emailAddress = editTextEmailAddress.getText().toString();
                    loginIntent.putExtra("emailAddress",emailAddress);
                    startActivity(loginIntent);
                }
            });
        googleLogin.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        startActivity(homeIntent);
                    }
                });
        image = (ImageView) findViewById(R.id.imageView);
        new MyImgTask().execute("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg");
    }
    private class MyImgTask extends AsyncTask<String, Void, Bitmap> {

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
}
