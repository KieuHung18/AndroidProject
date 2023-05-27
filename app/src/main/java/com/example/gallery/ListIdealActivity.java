package com.example.gallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.addapter.SimpleIdealGriddapter;
import com.example.gallery.entities.Artwork;
import com.example.gallery.entities.Ideal;
import com.example.gallery.services.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListIdealActivity extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<Ideal> ideals;
    private SimpleIdealGriddapter adapter;
    private Artwork artwork;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_ideal_activity);
        artwork = (Artwork) getIntent().getSerializableExtra("artwork");
        gridView = findViewById(R.id.idealGridView);
        gridView.setNumColumns(1);

        ideals = new ArrayList<Ideal>();

        adapter = new SimpleIdealGriddapter(this, ideals);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject id = new JSONObject();
                try {
                    id.put("id", artwork.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new AddToIdealTask().execute("/users/ideals/artworks/"+ideals.get(i).getId(),id.toString());
            }
        });
        new GetIdealTask().execute("/users/ideals");
    }
    private class GetIdealTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Request request = new Request(ListIdealActivity.this);
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
                    ideal.setId(id);
                    ideal.setName(name);
                    ideal.setPublish(publish);
                    ideal.setDescription(description);
                    ideal.setUserId(userId);
                    ideals.add(ideal);
                }
                adapter = new SimpleIdealGriddapter(ListIdealActivity.this,ideals);
                gridView.setAdapter(adapter);
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(ListIdealActivity.this ,errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class AddToIdealTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Request request = new Request(ListIdealActivity.this);
                return request.doPost(params[0],params[1]);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONArray response = result.getJSONArray("response");
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(ListIdealActivity.this ,errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
