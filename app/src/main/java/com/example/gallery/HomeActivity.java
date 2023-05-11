package com.example.gallery;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.services.Request;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


public class HomeActivity extends AppCompatActivity {
    private Fragment imageListFragment, profileFragment;
    private BottomNavigationView navigationView;
    private BottomNavigationItemView home, add, message, profile, search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_fragment);

        imageListFragment = new ImageListFragment();
        profileFragment = new ProfileFragment();

        navigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        home = (BottomNavigationItemView) navigationView.findViewById(R.id.home);
        add = (BottomNavigationItemView) navigationView.findViewById(R.id.add);
        search = (BottomNavigationItemView) navigationView.findViewById(R.id.search);
        message = (BottomNavigationItemView) navigationView.findViewById(R.id.message);
        profile = (BottomNavigationItemView) navigationView.findViewById(R.id.profile);
        loadFragment(imageListFragment);
        home.setOnClickListener(
            new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                public void onClick(View view) {
                    uncheckedItem();
                    home.setChecked(true);
                }
            }
        );
        profile.setOnClickListener(
            new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                public void onClick(View view) {
                    uncheckedItem();
                    profile.setChecked(true);
                }
            }
        );
        add.setOnClickListener(
            new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                public void onClick(View view) {
                    uncheckedItem();
                    add.setChecked(true);
                    getImageFromAlbum();
                }
            }
        );
        search.setOnClickListener(
            new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                public void onClick(View view) {
                    uncheckedItem();
                }
            }
        );
        message.setOnClickListener(
            new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                public void onClick(View view) {
                    uncheckedItem();
                    message.setChecked(true);
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                    String auth = pref.getString("authentication", null);
                    Log.e("TAG", auth);
                }
            }
        );
    }

    @SuppressLint("RestrictedApi")
    private void uncheckedItem() {
        home.setChecked(false);
        add.setChecked(false);
        search.setChecked(false);
        message.setChecked(false);
        profile.setChecked(false);
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
            String picturePath = getPath(this.getApplicationContext(), selectedImageUri);

            InputStream iStream = null;
            try {
                iStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] inputData = getBytes(iStream);
                UploadsTask uploadsTask = new UploadsTask();
                uploadsTask.setPostData(inputData);
                uploadsTask.execute("/admin/uploads");
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
    private static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, null);
        fragmentTransaction.commit(); // save the changes
    }

    private class UploadsTask extends AsyncTask<String, Void, String> {
        private byte[]postData;

        public void setPostData(byte[] postData) {
            this.postData = postData;
        }
        @Override
        protected String doInBackground(String... params) {
            Request request = new Request(HomeActivity.this);
            String data = request.doUpload(params[0],postData);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
        }

    }
}