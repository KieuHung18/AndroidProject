package com.example.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.services.Request;
import com.example.gallery.task.ImageTask;
import com.example.gallery.task.UserInfoTask;


public class MainActivity extends AppCompatActivity {
    private Button continueLogin, continueWithoutAccount;
    private EditText editTextEmailAddress;
    private TextView textViewRegister;
    ImageView image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if(new UserInfoTask(this).logged()){
            finish();
            startActivity(new Intent(this,HomeActivity.class));
        }
        continueLogin = (Button) findViewById(R.id.continueLogin);
        continueWithoutAccount = (Button) findViewById(R.id.continueWithoutAccount);
        editTextEmailAddress = (EditText) findViewById(R.id.editTextEmailAddress);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);

        Intent loginIntent = new Intent(this,LoginActivity.class);
        Intent homeIntent = new Intent(this,HomeActivity.class);
        Intent registerIntent = new Intent(this,RegisterActivity.class);

        image = (ImageView) findViewById(R.id.imageView);
//        new ImageTask(image).execute("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg");
        continueLogin.setOnClickListener(
            new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    String emailAddress = editTextEmailAddress.getText().toString();
                    if(!emailAddress.equals("")){
                        loginIntent.putExtra("emailAddress",emailAddress);
                        startActivity(loginIntent);
                    }else{
                        Toast.makeText(getApplicationContext(),"Please enter your email",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        continueWithoutAccount.setOnClickListener(
            new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    startActivity(homeIntent);
                }
            });
        textViewRegister.setOnClickListener(
            new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    startActivity(registerIntent);
                }

            });

    }
}
