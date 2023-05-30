package com.example.gallery;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.entities.Artwork;
import com.example.gallery.entities.User;
import com.example.gallery.task.UserInfoTask;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        User user = (User) intent.getSerializableExtra("user");
        if(user!=null){
            ProfileFragment profile = new ProfileFragment();
            profile.setUser(user);
            loadFragment(profile);
        }
    }

    private BottomNavigationView navigationView;
    private BottomNavigationItemView home, add, notification, profile;
    private Intent addArtworkIntent;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_fragment);
        addArtworkIntent = new Intent(this,AddArtworkActivity.class);

        navigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        home = (BottomNavigationItemView) navigationView.findViewById(R.id.home);
        add = (BottomNavigationItemView) navigationView.findViewById(R.id.add);
        notification = (BottomNavigationItemView) navigationView.findViewById(R.id.notification);
        profile = (BottomNavigationItemView) navigationView.findViewById(R.id.profile);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            int fragment = bundle.getInt("fragment");
            switch (fragment){
                case R.layout.profile_fragment:
                    loadFragment(new ProfileFragment());
                    break;
            }

        }else{
            loadFragment(new ImageListFragment());
        }

        home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    loadFragment(new ImageListFragment());
                }
                return false;
            }
        });
        profile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if(new UserInfoTask(getApplicationContext()).logged()){
                        loadFragment(new ProfileFragment());
                    }else {
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    }
                }
                return false;
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new UserInfoTask(getApplicationContext()).logged()){
                    startActivity(addArtworkIntent);
                }else {
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                }
            }
        });

        notification.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if(new UserInfoTask(getApplicationContext()).logged()){
                        loadFragment(new NotificationFragment());
                    }else {
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    }
                }
                return false;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, null);
        fragmentTransaction.commit(); // save the changes
    }
}
