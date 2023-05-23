package com.example.gallery;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SettingsFragment extends Fragment {
    private View view;
    private ImageButton back;
    private TextView logout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment, container, false);

        back = (ImageButton) view.findViewById(R.id.back);
        logout = (TextView) view.findViewById(R.id.textViewLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSharedPreferences("Login", 0).edit().remove("authentication");
                getActivity().finish();
                new Intent(getActivity(),MainActivity.class);
            }
        });


        return view;
    }
}
