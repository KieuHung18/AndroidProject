package com.example.gallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.services.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddArtworkActivity extends AppCompatActivity {
    private ImageView artwork;
    private EditText name,description;
    private Button add;

    private byte[] imageData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_artwork_activity);

        artwork = (ImageView) findViewById(R.id.imageViewArtwork);
        add= (Button) findViewById(R.id.add);
        name = (EditText) findViewById(R.id.editTextArtworkTitle);
        description = (EditText) findViewById(R.id.editTextArtworkDescription);

        getImageFromAlbum();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadsTask uploadsTask = new UploadsTask();
                uploadsTask.setPostData(imageData);
                uploadsTask.execute();
            }
        });

    }
    private void getImageFromAlbum() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            InputStream iStream = null;
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                artwork.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                iStream = getContentResolver().openInputStream(selectedImageUri);
                imageData = getBytes(iStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            finish();
        }
    }
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private class UploadsTask extends AsyncTask<String, Void, JSONObject> {
        private byte[]postData;

        public void setPostData(byte[] postData) {
            this.postData = postData;
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(AddArtworkActivity.this);
            JSONObject uploadedImage = request.doUpload(postData);
            return uploadedImage;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            try {
                JSONObject uploadedImage = result.getJSONObject("response");
                JSONObject postData = new JSONObject();
                try {
                    postData.put("name", name.getText().toString());
                    postData.put("description", description.getText().toString());
                    postData.put("url", uploadedImage.getString("url"));
                    postData.put("publicId", uploadedImage.getString("publicId"));

                    new AddArtworkTask().execute("/users/artworks", postData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AddArtworkTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(AddArtworkActivity.this);
            JSONObject data = request.doPost(params[0],params[1]);
            return data;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            try {
                String response = result.getString("response");
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        }

    }
}
