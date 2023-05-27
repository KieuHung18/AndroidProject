package com.example.gallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.gallery.addapter.StaggeredGridAdapter;
import com.example.gallery.entities.Artwork;
import com.example.gallery.entities.Ideal;
import com.example.gallery.services.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class IdealImageListActivity extends AppCompatActivity {
    private ArrayList<Artwork> artworks;
    private RecyclerView recyclerView;
    private StaggeredGridAdapter staggeredGridAdapter;
    private TextView idealName;
    ImageButton back,option;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideal_image_list_activity);

        Ideal ideal = (Ideal) getIntent().getSerializableExtra("ideal");
        artworks = new ArrayList<Artwork>();

        // Getting reference of recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        idealName= findViewById(R.id.textviewIdeal);
        back= findViewById(R.id.idealDetailBack);
        option= findViewById(R.id.idealDetailOption);

        // Setting the layout as Staggered Grid for vertical orientation
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        // Sending reference and data to Adapter
        staggeredGridAdapter = new StaggeredGridAdapter(this, artworks);
        staggeredGridAdapter.setIdeal(ideal);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(staggeredGridAdapter);
        idealName.setText(ideal.getName());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if(ideal.getId()==null){
            new GetArtworkTask().execute("/users/artworks");
        }
        else {
            new GetArtworkTask().execute("/users/ideals/artworks/"+ideal.getId());
        }
    }
    private class GetArtworkTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(IdealImageListActivity.this);
            return request.doGet(params[0]);
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
                Toast.makeText(IdealImageListActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
