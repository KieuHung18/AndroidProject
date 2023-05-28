package com.example.gallery;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.gallery.addapter.StaggeredGridAdapter;
import com.example.gallery.entities.Artwork;
import com.example.gallery.services.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageListFragment extends Fragment {
    private View view;
    private ArrayList<Artwork> artworks;
    private RecyclerView recyclerView;
    private StaggeredGridAdapter staggeredGridAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_list_fragment, container, false);
        artworks = new ArrayList<Artwork>();

        // Getting reference of recyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Setting the layout as Staggered Grid for vertical orientation
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        // Sending reference and data to Adapter
        staggeredGridAdapter = new StaggeredGridAdapter(this.getActivity(), artworks);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(staggeredGridAdapter);
        new GetArtworkTask().execute("/artworks");
        return view;
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
                staggeredGridAdapter.setArtworks(artworks);
                recyclerView.setAdapter(staggeredGridAdapter);
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                if(getActivity()!=null){
                    Toast.makeText(getActivity() ,errorMessage,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
