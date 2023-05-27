package com.example.gallery;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gallery.addapter.GridAdapter;
import com.example.gallery.entities.Artwork;
import com.example.gallery.entities.Ideal;
import com.example.gallery.entities.User;
import com.example.gallery.services.Request;
import com.example.gallery.task.ImageTask;
import com.example.gallery.task.UserInfoTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private View view ;
    private ImageButton setttings;
    private ImageView profileImage;
    private TextView textViewFollowers,textViewFollowing,textViewUserName,textViewEmail;
    private Fragment settingsFragment;
    private GridView gridview;
    private ArrayList<Ideal> ideals;
    private ArrayList<Artwork> artworks;
    private GridAdapter adapter;
    private Button  addIdeal;
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
        addIdeal= view.findViewById(R.id.addIdeal);

        gridview = view.findViewById(R.id.idealGridView);
        gridview.setNumColumns(2);

        ideals = new ArrayList<Ideal>();
        artworks=new ArrayList<Artwork>();

        adapter = new GridAdapter(getContext(), ideals);
        gridview.setAdapter(adapter);

        setttings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).loadFragment(settingsFragment);
            }
        });
        addIdeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AddIdealActivity.class));
            }
        });
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent idealImageList = new Intent(getContext(),IdealImageListActivity.class);
                idealImageList.putExtra("ideal",ideals.get(i));
                startActivity(idealImageList);
            }
        });

        User user = new UserInfoTask(getContext()).getUser();
        textViewUserName.setText(user.getFirstName()+" "+user.getLastName());
        textViewEmail.setText(user.getEmail());
        if(!user.getProfileUrl().equals("null")){
            new ImageTask(profileImage).execute(user.getProfileUrl());
        }
        new GetArtworkTask().execute("/users/artworks");
        return view;
    }

    private class GetIdealTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Request request = new Request(getActivity());
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
                adapter = new GridAdapter(getContext(),ideals);
                gridview.setAdapter(adapter);
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                if(getActivity()!=null){
                    Toast.makeText(getActivity() ,errorMessage,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private class GetArtworkTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Request request = new Request(getActivity());
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
                int like;
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
                allArtWork.setSize(artworks.size());
                if(artworks.size()>0){
                    allArtWork.setThumbnail(artworks.get(0).getUrl());
                }else{
                    allArtWork.setThumbnail("");
                }
                allArtWork.setPublish(true);
                ideals.add(allArtWork);
                new GetIdealTask().execute("/users/ideals");
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                if(getActivity()!=null){
                    Toast.makeText(getActivity() ,errorMessage,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
