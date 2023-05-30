package com.example.gallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.services.Request;
import com.example.gallery.task.UserInfoTask;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private ImageButton close;
    private Button login;
    private EditText email,password;
    private TextView register;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        close = (ImageButton) findViewById(R.id.artworkDetailOption);
        login = (Button) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.editTextEmailAddress);
        password = (EditText) findViewById(R.id.editTextPassword);
        register= (TextView) findViewById(R.id.textViewRegister);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            email.setText(extras.getString("emailAddress"));
        }

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                if(userEmail.equals("")){
                    Toast.makeText(getApplicationContext(),"Email is empty",Toast.LENGTH_SHORT).show();
                }else {
                    if(userPassword.equals("")){
                        Toast.makeText(getApplicationContext(),"Password is empty",Toast.LENGTH_SHORT).show();
                    }else{
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("email", userEmail);
                            postData.put("password", userPassword);
                            new LoginTask().execute("/auth/login", postData.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
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
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

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
                SharedPreferences pref = getApplicationContext().getSharedPreferences("Authentication", MODE_PRIVATE);
                SharedPreferences.Editor Ed = pref.edit();
                Ed.putString("authentication",response );
                Ed.commit();
                new UserInfoTask(LoginActivity.this).execute();
                finish();
                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d("LoginActivity", "onPostExecute: "+errorMessage);
                Toast.makeText(getApplicationContext(),"Wrong user name or password",Toast.LENGTH_SHORT).show();
            }
        }
    }



}
