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
import com.example.gallery.task.UserInfoTask;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private Button register;
    private ImageButton close;
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextFirstName, editTextLastName;
    private Intent homeIntent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        register = (Button) findViewById(R.id.register);
        close = (ImageButton) findViewById(R.id.artworkDetailOption);

        editTextEmail = (EditText) findViewById(R.id.editTextEmailAddress);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);

        homeIntent = new Intent(this,HomeActivity.class);

        register.setOnClickListener(
            new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();
                    String confirmPassword = editTextConfirmPassword.getText().toString();
                    String firstName = editTextFirstName.getText().toString();
                    String lastName = editTextLastName.getText().toString();

                    if(password.equals(confirmPassword)) {
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("email", email);
                            postData.put("password", password);
                            postData.put("firstName", firstName);
                            postData.put("lastName", lastName);
                            new RegisterTask().execute("/auth/register", postData.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Confirm password incorrect",Toast.LENGTH_SHORT).show();
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
            });

    }
    private class RegisterTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(RegisterActivity.this);
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

                new UserInfoTask(RegisterActivity.this).execute();
                finish();
                startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
            } catch ( Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}

