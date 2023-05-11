package com.example.gallery;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.services.Request;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private ImageButton close;
    private Button login;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        close = (ImageButton) findViewById(R.id.close);
        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("email", "kieuhungcm2015@gmail.com");
                    postData.put("password", "123456");
                    new LoginTask().execute("/admin/auth/login", postData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        close.setOnClickListener(
            new View.OnClickListener()
            {
                public void onClick(View view)
                {
                   finish();
                }
            }
        );

    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Request request = new Request(LoginActivity.this);
            return request.doPost(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject response = new JSONObject(result);
                SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor Ed=pref.edit();
                Ed.putString("authentication",response.getString("response") );
                Ed.commit();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            super.onPostExecute(result);

        }
    }

}
