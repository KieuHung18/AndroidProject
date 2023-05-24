package com.example.gallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.services.Request;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private ImageButton close;
    private Button login;
    private EditText email,password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        close = (ImageButton) findViewById(R.id.artworkDetailOption);
        login = (Button) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.editTextEmailAddress);
        password = (EditText) findViewById(R.id.editTextPassword);

        Bundle extras = getIntent().getExtras();
        email.setText(extras.getString("emailAddress"));
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("email", email.getText().toString());
                    postData.put("password", password.getText().toString());
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

    private class LoginTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(LoginActivity.this);
            return request.doPost(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                String response = result.getString("response");
                SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor Ed=pref.edit();
                Ed.putString("authentication",response );
                Ed.commit();
                finish();
                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }

}
