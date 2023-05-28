package com.example.gallery;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.entities.Artwork;
import com.example.gallery.entities.Ideal;
import com.example.gallery.entities.User;
import com.example.gallery.services.Request;
import com.example.gallery.task.ImageTask;
import com.example.gallery.task.UserInfoTask;

import org.json.JSONException;
import org.json.JSONObject;

public class ArtworkDetailActivity extends AppCompatActivity {
    private Artwork artwork;

    private ImageButton back,option,favorite;
    private TextView title, description;
    private ImageView artworkImage;
    private PopupMenu popupMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artwork_detail_activity);
        artwork = (Artwork) getIntent().getSerializableExtra("artwork");
        Ideal ideal = (Ideal) getIntent().getSerializableExtra("ideal");

        title = findViewById(R.id.textViewArtworkTitle);
        description = findViewById(R.id.textViewArtworkDescription);
        back  = findViewById(R.id.artworkDetailBack);
        option = findViewById(R.id.artworkDetailOption);
        favorite = findViewById(R.id.imageButtonFavorite);
        artworkImage = findViewById(R.id.imageViewArtworkDetail);

        new ImageTask(artworkImage).execute(artwork.getUrl());
        title.setText(artwork.getName());
        description.setText(artwork.getDescription());

        popupMenu = new PopupMenu(this, option);
        User user = new UserInfoTask(this).getUser();
        popupMenu.getMenuInflater().inflate(R.menu.artwork_detail_menu, popupMenu.getMenu());

        if(artwork.getUserId().equals(user.getId())){
            popupMenu.getMenu().findItem(R.id.reportArtwork).setVisible(false);
            popupMenu.getMenu().findItem(R.id.hideArtWork).setVisible(artwork.isPublish());
            popupMenu.getMenu().findItem(R.id.publishArtWork).setVisible(!artwork.isPublish());
            if(ideal==null){
                popupMenu.getMenu().findItem(R.id.addToIdeal).setVisible(true);
                popupMenu.getMenu().findItem(R.id.removeFromIdeal).setVisible(false);
            }else{
                popupMenu.getMenu().findItem(R.id.addToIdeal).setVisible(false);
                popupMenu.getMenu().findItem(R.id.removeFromIdeal).setVisible(true);
            }
        }else{
            popupMenu.getMenu().findItem(R.id.editArtWork).setVisible(false);
            popupMenu.getMenu().findItem(R.id.hideArtWork).setVisible(false);
            popupMenu.getMenu().findItem(R.id.publishArtWork).setVisible(false);
            popupMenu.getMenu().findItem(R.id.deleteArtWork).setVisible(false);
            popupMenu.getMenu().findItem(R.id.removeFromIdeal).setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.editArtWork:
                        Intent addArtWorkIntent = new Intent(ArtworkDetailActivity.this,AddArtworkActivity.class);
                        addArtWorkIntent.putExtra("artwork",artwork);
                        startActivity(addArtWorkIntent);
                        break;
                    case R.id.hideArtWork:
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("publish", false);
                            new HideArtWorkTask().execute("/users/artworks/"+artwork.getId(),postData.toString());
                            popupMenu.getMenu().findItem(R.id.hideArtWork).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.publishArtWork).setVisible(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.publishArtWork:
                        postData = new JSONObject();
                        try {
                            postData.put("publish", true);
                            new HideArtWorkTask().execute("/users/artworks/"+artwork.getId(),postData.toString());
                            popupMenu.getMenu().findItem(R.id.hideArtWork).setVisible(true);
                            popupMenu.getMenu().findItem(R.id.publishArtWork).setVisible(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.addToIdeal:
                        Intent listIdeal = new Intent(ArtworkDetailActivity.this,ListIdealActivity.class);
                        listIdeal.putExtra("artwork",artwork);
                        startActivity(listIdeal);
                        break;
                    case R.id.removeFromIdeal:
                        JSONObject id = new JSONObject();
                        try {
                            id.put("id", artwork.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new RemoveArtworkTask().execute("/users/ideals/artworks/"+ideal.getId(),id.toString());
                        break;
                    case R.id.deleteArtWork:
                        new DeleteArtWorkTask().execute("/users/artworks/"+artwork.getId());
                        break;
                    case R.id.reportArtwork:
                        Intent report = new Intent(ArtworkDetailActivity.this,ReportActivity.class);
                        report.putExtra("artwork",artwork);
                        startActivity(report);
                        break;
                }
                return false;
            }
        });
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private class DeleteArtWorkTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(ArtworkDetailActivity.this);
            return request.doDelete(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject response = result.getJSONObject("response");
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class RemoveArtworkTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(ArtworkDetailActivity.this);
            return request.doDelete(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject response = result.getJSONObject("response");
                finish();
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class HideArtWorkTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(getApplicationContext());
            return request.doPost(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject response = result.getJSONObject("response");
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
