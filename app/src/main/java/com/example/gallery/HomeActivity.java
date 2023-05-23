package com.example.gallery;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity {
    private Fragment imageListFragment, profileFragment,settingsFragment;
    private BottomNavigationView navigationView;
    private BottomNavigationItemView home, add, message, profile, search;
    private Intent addArtworkIntent;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_fragment);

        imageListFragment = new ImageListFragment();
        profileFragment = new ProfileFragment();
        settingsFragment = new SettingsFragment();

        addArtworkIntent = new Intent(this,AddArtworkActivity.class);

        navigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        home = (BottomNavigationItemView) navigationView.findViewById(R.id.home);
        add = (BottomNavigationItemView) navigationView.findViewById(R.id.add);
        search = (BottomNavigationItemView) navigationView.findViewById(R.id.search);
        message = (BottomNavigationItemView) navigationView.findViewById(R.id.message);
        profile = (BottomNavigationItemView) navigationView.findViewById(R.id.profile);
        loadFragment(imageListFragment);

        home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                loadFragment(imageListFragment);
                return false;
            }
        });
        profile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                loadFragment(profileFragment);
                return false;
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(addArtworkIntent);

            }
        });

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        message.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
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
