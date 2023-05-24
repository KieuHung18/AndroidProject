package com.example.gallery;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.gallery.entities.Artwork;
import com.example.gallery.services.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageListFragment extends Fragment {
    private View view;
    private ArrayList<Artwork> artworks;
    private RecyclerView recyclerView;
    private Adapter adapter;
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
        adapter = new Adapter(this.getActivity(), artworks);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);
        new GetArtworkTask().execute("/artworks");
        return view;
    }
    private class GetArtworkTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(getActivity());
            return request.doGet(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONArray response = result.getJSONArray("response");
                String id,url,publicId,name,description;
                int like;
                boolean publish;
                Artwork artwork = new Artwork();
                for(int i=0;i<response.length();i++){
                    id = ((JSONObject)response.get(0)).getString("id");
                    url = ((JSONObject)response.get(0)).getString("url");
                    publicId = ((JSONObject)response.get(0)).getString("publicId");
                    like = ((JSONObject)response.get(0)).getInt("like");
                    name = ((JSONObject)response.get(0)).getString("name");
                    description = ((JSONObject)response.get(0)).getString("description");

                    artwork.setId(id);
                    artwork.setUrl(url);
                    artwork.setPublicId(publicId);
                    artwork.setLike(like);
                    artwork.setName(name);
                    artwork.setDescription(description);

                    artworks.add(artwork);
                }
                adapter.setArtworks(artworks);
                recyclerView.setAdapter(adapter);
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getActivity() ,errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
