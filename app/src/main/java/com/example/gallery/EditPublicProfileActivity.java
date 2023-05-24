package com.example.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.services.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditPublicProfileActivity extends AppCompatActivity {
    private Button done;
    private ImageButton back;
    private EditText editTextFirstName,editTextLastName;
    private ImageView profileImage;
    private byte[] imageData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_public_profile_activity);
        done = findViewById(R.id.publicProfileDone);
        back = findViewById(R.id.publicProfileBack);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        profileImage = findViewById(R.id.imageViewProfile);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromAlbum();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageData!=null){
                    UploadsTask uploadsTask = new UploadsTask();
                    uploadsTask.setPostData(imageData);
                    uploadsTask.execute();
                }else{
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("firstName", editTextFirstName.getText().toString());
                        postData.put("lastName", editTextLastName.getText().toString());
                        new PostUserInfoTask().execute("/users/", postData.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        new GetUserInfoTask().execute("/auth/");
    }
    public void getImageFromAlbum() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            profileImage.setImageBitmap(bitmap);
            InputStream iStream = null;
            try {
                iStream = getContentResolver().openInputStream(selectedImageUri);
                imageData = getBytes(iStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    public class UploadsTask extends AsyncTask<String, Void, JSONObject> {
        private byte[]postData;

        public void setPostData(byte[] postData) {
            this.postData = postData;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(EditPublicProfileActivity.this);
            JSONObject uploadedImage = request.doUpload(postData);
            return uploadedImage;
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            try {
                JSONObject uploadedImage = result.getJSONObject("response");
                String url = uploadedImage.getString("url");
                JSONObject postData = new JSONObject();
                try {
                    postData.put("profileUrl", url);
                    postData.put("firstName", editTextFirstName.getText().toString());
                    postData.put("lastName", editTextLastName.getText().toString());
                    new PostUserInfoTask().execute("/users/", postData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(EditPublicProfileActivity.this ,errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class GetUserInfoTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(EditPublicProfileActivity.this);
            return request.doGet(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject response = result.getJSONObject("response");
                editTextFirstName.setText(response.getString("firstName"));
                editTextLastName.setText(response.getString("lastName"));
                String profileUrl=response.getString("profileUrl");
                if(!profileUrl.equals("null")){
                    new ImageTask(profileImage).execute(profileUrl);
                }
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(EditPublicProfileActivity.this ,errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class PostUserInfoTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(EditPublicProfileActivity.this);
            return request.doPost(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            try {
                String response = result.getString("response");
                Toast.makeText(EditPublicProfileActivity.this ,response,Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(EditPublicProfileActivity.this ,errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
