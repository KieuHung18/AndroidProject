package com.example.gallery;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
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
    private ImageButton newIdeal,back;
    private Artwork artwork;
    private String TAG = "ListIdealActivity";
    @Override
    protected void onStart() {
        super.onStart();
        ideals = new ArrayList<>();
        new GetIdealTask().execute("/users/ideals");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_ideal_activity);
        artwork = (Artwork) getIntent().getSerializableExtra("artwork");
        gridView = findViewById(R.id.idealGridView);
        back= findViewById(R.id.listIdealBack);
        newIdeal= findViewById(R.id.imageButtonNewIdeal);
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
                new AddToIdealTask().execute("/users/artworks/ideals/"+ideals.get(i).getId(),id.toString());
            }
        });
        newIdeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListIdealActivity.this,AddIdealActivity.class));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                JSONObject response = result.getJSONObject("response");
                ideals = new ArrayList<>();
                new GetIdealTask().execute("/users/ideals");
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
}
