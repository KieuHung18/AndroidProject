package com.example.gallery;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.gallery.services.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageListFragment extends Fragment {
    private View view;
    private ArrayList<String> imageUrls;
    private RecyclerView recyclerView;
    private Adapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_list_fragment, container, false);
        imageUrls = new ArrayList<String>();

//        for (int i =0 ;i< 10 ; i ++){
//            if(i%2==0){
//                imageUrls.add("https://ik.imagekit.io/ikmedia/backlit.jpg");
//            }
//            if(i%3==0){
//                imageUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Lady_Liberty_under_a_blue_sky_%28cropped%29.jpg/1200px-Lady_Liberty_under_a_blue_sky_%28cropped%29.jpg");
//            }else{
//                imageUrls.add("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg");
//            }
//        }
        // Getting reference of recyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Setting the layout as Staggered Grid for vertical orientation
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        // Sending reference and data to Adapter
        adapter = new Adapter(view.getContext(), imageUrls);

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
                String url ="";
                for(int i=0;i<response.length();i++){
                    url = ((JSONObject)response.get(0)).getString("url");
                    imageUrls.add(url);
                }
                adapter = new Adapter(view.getContext(), imageUrls);
                recyclerView.setAdapter(adapter);
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getActivity() ,errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
