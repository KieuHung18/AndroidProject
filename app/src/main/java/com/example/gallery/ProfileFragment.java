package com.example.gallery;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gallery.addapter.IdealGridAdapter;
import com.example.gallery.entities.Artwork;
import com.example.gallery.entities.Ideal;
import com.example.gallery.entities.User;
import com.example.gallery.services.Request;
import com.example.gallery.task.ImageTask;
import com.example.gallery.task.UserInfoTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private View view ;
    private ImageButton settings;
    private ImageView profileImage;
    private TextView textViewFollowers,textViewFollowing,textViewUserName,textViewEmail;
    private Fragment settingsFragment;
    private GridView gridview;
    private ArrayList<Ideal> ideals;
    private ArrayList<Artwork> artworks;
    private IdealGridAdapter adapter;
    private Button  profileAction;
    private  User user;
    private String TAG = "ProfileFragment";

    @Override
    public void onStart() {
        super.onStart();
        ideals = new ArrayList<>();
        artworks = new ArrayList<>();
        String userId = new UserInfoTask(view.getContext()).getUser().getId();
        if(userId!=null&&userId.equals(user.getId())){
            new GetArtworkTask().execute("/users/artworks");
        }else{
            new GetArtworkTask().execute("/gallery/artworks/users/"+user.getId());
        }

    }

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
        settings = view.findViewById(R.id.settings);
        profileImage = (ImageView)view.findViewById(R.id.imageViewProfile);
        profileAction= view.findViewById(R.id.profileAction);

        gridview = view.findViewById(R.id.idealGridView);
        gridview.setNumColumns(2);

        ideals = new ArrayList<Ideal>();
        artworks=new ArrayList<Artwork>();

        adapter = new IdealGridAdapter(getContext(), ideals);
        gridview.setAdapter(adapter);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).loadFragment(settingsFragment);
            }
        });

        if(user==null){
            profileAction.setText("More ideal");
            profileAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), AddIdealActivity.class));
                }
            });
        }else {
            profileAction.setText("Follow");
            settings.setVisibility(View.GONE);
            profileAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("followingId", user.getId());
                        new FollowTask().execute("/users/follows",postData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Ideal toIdeal;
                try{
                    Intent idealImageList = new Intent(getContext(),IdealImageListActivity.class);
                    idealImageList.putExtra("ideal",ideals.get(i));
                    startActivity(idealImageList);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });
        UserInfoTask userInfoTask = new UserInfoTask(getContext());
        if(user==null){
            user = userInfoTask.getUser();
        }else{
            if(userInfoTask.logged()){
                new  CheckFollowTask().execute("/users/follows/users/"+user.getId());
            }
        }
        textViewFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userList = new Intent(getActivity(),ListUserActivity.class);
                userList.putExtra("api","/gallery/follows/follower/users/"+user.getId());
                startActivity(userList);
            }
        });
        textViewFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userList = new Intent(getActivity(),ListUserActivity.class);
                userList.putExtra("api","/gallery/follows/following/users/"+user.getId());
                startActivity(userList);
            }
        });
        if(!user.getProfileUrl().equals("null")){
            new ImageTask(profileImage).execute(user.getProfileUrl());
        }

        textViewUserName.setText(user.getFirstName()+" "+user.getLastName());
        textViewEmail.setText(user.getEmail());
        new CountFollowTask().execute("/gallery/follows/count/users/"+user.getId());

        return view;
    }
    public void setUser(User user) {
        this.user = user;
    }

    private class GetIdealTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Request request = new Request(view.getContext());
                return request.doGet(params[0]);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONArray response = result.getJSONArray("response");
                String id,name,description,userId;
                boolean publish;
                for(int i=0;i<response.length();i++){
                    Ideal ideal = new Ideal();
                    id = ((JSONObject)response.get(i)).getString("id");
                    name = ((JSONObject)response.get(i)).getString("name");
                    description = ((JSONObject)response.get(i)).getString("description");
                    publish = ((JSONObject)response.get(i)).getBoolean("publish");
                    userId = ((JSONObject)response.get(i)).getString("userId");

                    ideal.setThumbnail(((JSONObject)response.get(i)).getString("thumbnail"));
                    ideal.setSize(((JSONObject)response.get(i)).getInt("size"));
                    ideal.setId(id);
                    ideal.setName(name);
                    ideal.setPublish(publish);
                    ideal.setDescription(description);
                    ideal.setUserId(userId);
                    ideals.add(ideal);
                }
                adapter = new IdealGridAdapter(view.getContext(),ideals);
                gridview.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }

    private class CheckFollowTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(view.getContext());
            return request.doGet(params[0]);
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                boolean response = result.getBoolean("response");
                if(response){
                    profileAction.setText("Followed");
                }else{
                    profileAction.setText("Follow");
                }
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
    private class FollowTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(view.getContext());
            return request.doPost(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                boolean response = result.getBoolean("response");
                if(response){
                    profileAction.setText("Followed");
                }else{
                    profileAction.setText("Follow");
                }
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
                String errorName = new HandleRequestError().handle(result).getName();
                if(errorName.equals("NotLogin")){
                    getActivity().startActivity(new Intent(getActivity(),LoginActivity.class));
                }
            }
        }
    }
    private class CountFollowTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(view.getContext());
            return request.doGet(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject response = result.getJSONObject("response");
                textViewFollowers.setText(response.getInt("follower")+" follower");
                textViewFollowing.setText(response.getInt("following")+" following");
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
    private class GetArtworkTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Request request = new Request(view.getContext());
                return request.doGet(params[0]);
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONArray response = result.getJSONArray("response");
                String id,url,publicId,name,description,userId;
                boolean publish;
                for(int i=0;i<response.length();i++){
                    Artwork artwork = new Artwork();
                    id = ((JSONObject)response.get(i)).getString("id");
                    url = ((JSONObject)response.get(i)).getString("url");
                    publicId = ((JSONObject)response.get(i)).getString("publicId");
                    name = ((JSONObject)response.get(i)).getString("name");
                    description = ((JSONObject)response.get(i)).getString("description");
                    userId = ((JSONObject)response.get(i)).getString("userId");
                    publish = ((JSONObject)response.get(i)).getBoolean("publish");

                    artwork.setId(id);
                    artwork.setUrl(url);
                    artwork.setPublicId(publicId);
                    artwork.setName(name);
                    artwork.setPublish(publish);
                    artwork.setDescription(description);
                    artwork.setUserId(userId);
                    artworks.add(artwork);
                }
                Ideal allArtWork  = new Ideal();
                allArtWork.setName("All artworks");
                allArtWork.setUserId(user.getId());
                allArtWork.setSize(artworks.size());
                if(artworks.size()>0){
                    allArtWork.setThumbnail(artworks.get(0).getUrl());
                }else{
                    allArtWork.setThumbnail("");
                }
                allArtWork.setPublish(true);
                ideals.add(allArtWork);
                if(user!=null){
                    new GetIdealTask().execute("/gallery/ideals/users/"+user.getId());
                }else{
                    new GetIdealTask().execute("/users/ideals");
                }

            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
}
