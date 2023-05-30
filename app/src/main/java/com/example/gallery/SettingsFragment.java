package com.example.gallery;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.gallery.task.UserInfoTask;

public class SettingsFragment extends Fragment {
    private View view;
    private ImageButton back;
    private TextView logout,publicProfile;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment, container, false);

        back = (ImageButton) view.findViewById(R.id.back);
        logout = (TextView) view.findViewById(R.id.textViewLogout);
        publicProfile = (TextView) view.findViewById(R.id.textViewPublicProfile);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UserInfoTask(getContext()).logout();
                getActivity().finish();
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        });
        publicProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent EditPublicProfileIntent = new Intent(getActivity(),EditPublicProfileActivity.class);
                        startActivity(EditPublicProfileIntent);
                    }
                }
        );
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).loadFragment(new ProfileFragment());
            }
        });

        return view;
    }
}
