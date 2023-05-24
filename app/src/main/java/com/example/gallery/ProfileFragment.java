package com.example.gallery;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gallery.services.Request;

import org.json.JSONObject;


public class ProfileFragment extends Fragment {
    private View view ;
    private ImageButton setttings;
    private ImageView profileImage;
    private TextView textViewFollowers,textViewFollowing,textViewUserName,textViewEmail;
    private Fragment settingsFragment;
    ImageView image;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        settingsFragment = new SettingsFragment();

        textViewFollowers = view.findViewById(R.id.textViewFollowers);
        textViewFollowing = view.findViewById(R.id.textViewFollowing);
        textViewUserName = view.findViewById(R.id.textViewUserName);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        setttings = view.findViewById(R.id.settings);
        profileImage = (ImageView)view.findViewById(R.id.imageViewProfile);

        setttings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).loadFragment(settingsFragment);
            }
        });

        new GetUserInfoTask().execute("/auth/");

        return view;
    }

    private class GetUserInfoTask extends AsyncTask<String, Void, JSONObject> {
        private boolean refresh;

        public void setRefresh(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(getActivity());
            if(refresh){
                return request.doPost(params[0],"");
            }else {
                return request.doGet(params[0]);
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject response = result.getJSONObject("response");
                textViewUserName.setText(response.getString("firstName")+" "+response.getString("lastName"));
                textViewEmail.setText(response.getString("email"));
                String profileUrl=response.getString("profileUrl");
                if(!profileUrl.equals("null")){
                    new ImageTask(profileImage).execute(profileUrl);
                }
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getActivity() ,errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
