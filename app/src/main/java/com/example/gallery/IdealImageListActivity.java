package com.example.gallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.gallery.addapter.StaggeredGridAdapter;
import com.example.gallery.entities.Artwork;
import com.example.gallery.entities.Ideal;
import com.example.gallery.entities.User;
import com.example.gallery.services.Request;
import com.example.gallery.task.UserInfoTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class IdealImageListActivity extends AppCompatActivity {
    private ArrayList<Artwork> artworks;
    private RecyclerView recyclerView;
    private StaggeredGridAdapter staggeredGridAdapter;
    private TextView idealName;
    ImageButton back,option;
    private String TAG="IdealImageListActivity";
    private Ideal ideal;
    private User user;
    @Override
    protected void onStart() {
        super.onStart();
        artworks = new ArrayList<>();
        if(ideal.getId()==null){
            if(ideal.getUserId().equals(user.getId())){
                new GetArtworkTask().execute("/users/artworks");
            }else{
                new GetArtworkTask().execute("/gallery/artworks/users/"+ideal.getUserId());
            }
        }
        else {
            if(ideal.getUserId().equals(user.getId())){
                new GetArtworkTask().execute("/users/artworks/ideals/"+ideal.getId());
            }
            else{
                new GetArtworkTask().execute("/gallery/artworks/ideals/"+ideal.getId());
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideal_image_list_activity);

        ideal = (Ideal) getIntent().getSerializableExtra("ideal");
        artworks = new ArrayList<Artwork>();

        // Getting reference of recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        idealName= findViewById(R.id.textviewIdeal);
        back= findViewById(R.id.idealDetailBack);
        option= findViewById(R.id.idealDetailOption);
        option = findViewById(R.id.idealDetailOption);

        // Setting the layout as Staggered Grid for vertical orientation
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        PopupMenu popupMenu = new PopupMenu(this, option);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        // Sending reference and data to Adapter
        staggeredGridAdapter = new StaggeredGridAdapter(this, artworks);
        staggeredGridAdapter.setIdeal(ideal);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(staggeredGridAdapter);
        idealName.setText(ideal.getName());


        user = new UserInfoTask(this).getUser();
        popupMenu.getMenuInflater().inflate(R.menu.ideal_detail_menu, popupMenu.getMenu());

        if(ideal.getId()==null || !ideal.getUserId().equals(user.getId())){
            option.setVisibility(View.GONE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(IdealImageListActivity.this);
        builder.setMessage("Write your message here.");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteIdealTask().execute("/users/ideals/"+ideal.getId());
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.editIdeal:
                        Intent addIdealIntent = new Intent(IdealImageListActivity.this, AddIdealActivity.class);
                        addIdealIntent.putExtra("ideal",ideal);
                        startActivity(addIdealIntent);
                        break;
                    case R.id.deleteIdeal:
                        alert.show();
                        break;
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();            }
        });

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
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
    private class DeleteIdealTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(IdealImageListActivity.this);
            return request.doDelete(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject response = result.getJSONObject("response");
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
}
