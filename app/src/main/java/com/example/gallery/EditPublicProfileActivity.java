package com.example.gallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.entities.User;
import com.example.gallery.services.Request;
import com.example.gallery.task.ImageTask;
import com.example.gallery.task.UserInfoTask;

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
    private String TAG = "EditPublicProfileActivity";
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
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                if(firstName.equals("")){
                    Toast.makeText(getApplicationContext(),"First name is empty",Toast.LENGTH_SHORT).show();
                }else{
                    if(lastName.equals("")){
                        Toast.makeText(getApplicationContext(),"Last name is empty",Toast.LENGTH_SHORT).show();
                    }else {
                        if(imageData!=null){
                            UploadsTask uploadsTask = new UploadsTask();
                            uploadsTask.setPostData(imageData);
                            uploadsTask.execute();
                        }else{
                            JSONObject postData = new JSONObject();
                            try {
                                postData.put("firstName", firstName);
                                postData.put("lastName", lastName);
                                new PostUserInfoTask().execute("/users/", postData.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        User user = new UserInfoTask(getApplicationContext()).getUser();
        editTextFirstName.setText(user.getFirstName());
        editTextLastName.setText(user.getLastName());
        if(!user.getProfileUrl().equals("null")){
            new ImageTask(profileImage).execute(user.getProfileUrl());
        }
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
                String publicId = uploadedImage.getString("publicId");
                JSONObject postData = new JSONObject();
                try {
                    postData.put("publicId", publicId);
                    postData.put("profileUrl", url);
                    postData.put("firstName", editTextFirstName.getText().toString());
                    postData.put("lastName", editTextLastName.getText().toString());
                    new PostUserInfoTask().execute("/users/", postData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
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
                new RefreshUserData().execute("/auth");
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
    private class RefreshUserData extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(EditPublicProfileActivity.this);
            return request.doPost(params[0],"");
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                String response = result.getString("response");
                SharedPreferences pref = getApplicationContext().getSharedPreferences("Authentication", MODE_PRIVATE);
                SharedPreferences.Editor Ed = pref.edit();
                Ed.putString("authentication",response );
                Ed.commit();
                new UserInfoTask(EditPublicProfileActivity.this).execute();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
}
