package com.example.gallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    private TextView title, description,userName,countFavorite;
    private ImageView artworkImage,userImage;
    private PopupMenu popupMenu;
    private User user;
    private String TAG ="ArtworkDetailActivity";

    @Override
    protected void onStart() {
        super.onStart();
        String userId = new UserInfoTask(this).getUser().getId();
        if(userId!=null&&userId.equals(artwork.getUserId())){
            new GetArtworkTask().execute("/users/artworks/"+artwork.getId());
        }else{
            new GetArtworkTask().execute("/gallery/artworks/"+artwork.getId());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artwork_detail_activity);
        artwork = (Artwork) getIntent().getSerializableExtra("artwork");
        Ideal ideal = (Ideal) getIntent().getSerializableExtra("ideal");

        countFavorite = findViewById(R.id.textViewFavoriteCount);
        userImage = findViewById(R.id.imageViewUser);
        userName=findViewById(R.id.textViewUserName);
        title = findViewById(R.id.textViewArtworkTitle);
        description = findViewById(R.id.textViewArtworkDescription);
        back  = findViewById(R.id.artworkDetailBack);
        option = findViewById(R.id.artworkDetailOption);
        favorite = findViewById(R.id.imageButtonFavorite);
        artworkImage = findViewById(R.id.imageViewArtworkDetail);
        popupMenu = new PopupMenu(this, option);

        if(!new UserInfoTask(this).logged()){option.setVisibility(View.GONE);}
        popupMenu.getMenuInflater().inflate(R.menu.artwork_detail_menu, popupMenu.getMenu());
        String userId = new UserInfoTask(this).getUser().getId();
        if(artwork.getUserId().equals(userId)){
            popupMenu.getMenu().findItem(R.id.reportArtwork).setVisible(false);
            popupMenu.getMenu().findItem(R.id.hideArtWork).setVisible(artwork.isPublish());
            popupMenu.getMenu().findItem(R.id.publishArtWork).setVisible(!artwork.isPublish());

        }else{
            popupMenu.getMenu().findItem(R.id.editArtWork).setVisible(false);
            popupMenu.getMenu().findItem(R.id.hideArtWork).setVisible(false);
            popupMenu.getMenu().findItem(R.id.publishArtWork).setVisible(false);
            popupMenu.getMenu().findItem(R.id.deleteArtWork).setVisible(false);
        }
        popupMenu.getMenu().findItem(R.id.removeFromIdeal).setVisible(false);

        if(ideal!=null){
            if(ideal.getUserId().equals(userId)){
                popupMenu.getMenu().findItem(R.id.removeFromIdeal).setVisible(true);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete "+artwork.getName()+" ?");
        builder.setCancelable(true);


        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteArtWorkTask().execute("/users/artworks/"+artwork.getId());
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
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
                        new RemoveArtworkTask().execute("/users/artworks/ideals/"+ideal.getId(),id.toString());
                        break;
                    case R.id.deleteArtWork:
                        alert.show();
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
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("artworkId", artwork.getId());
                    new FavoriteTask().execute("/users/likes",postData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        countFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userList = new Intent(ArtworkDetailActivity.this,ListUserActivity.class);
                userList.putExtra("api","/gallery/likes/users/artworks/"+artwork.getId());
                startActivity(userList);
            }
        });
        UserInfoTask userInfoTask = new UserInfoTask(getApplicationContext()) ;

        new GetUserInfo().execute("/gallery/users/"+artwork.getUserId());
        new CountLikeTask().execute("/gallery/likes/count/artworks/"+artwork.getId());
        if(userInfoTask.logged()){
            new CheckLikedTask().execute("/users/likes/artworks/"+artwork.getId());
        }
    }
    public void toUserProfile(View view){
        Intent home = new Intent(ArtworkDetailActivity.this, HomeActivity.class);
        home.putExtra("user",user);
        home.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(home);
    }
    private class CheckLikedTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(ArtworkDetailActivity.this);
            return request.doGet(params[0]);
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                boolean response = result.getBoolean("response");
                if(response){
                    favorite.setImageResource(R.drawable.favorite_fill);
                }else {
                    favorite.setImageResource(R.drawable.favorite);

                }
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
    private class CountLikeTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(ArtworkDetailActivity.this);
            return request.doGet(params[0]);
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                int response = result.getInt("response");
                String count = String.valueOf(response);
                countFavorite.setText(count);
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
                Request request = new Request(ArtworkDetailActivity.this);
                return request.doGet(params[0]);
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject response = result.getJSONObject("response");
                String id,url,publicId,name,description,userId;
                boolean publish;
                    id = response.getString("id");
                    url = response.getString("url");
                    publicId = response.getString("publicId");
                    name = response.getString("name");
                    description = response.getString("description");
                    userId = response.getString("userId");
                    publish = response.getBoolean("publish");

                    artwork.setId(id);
                    artwork.setUrl(url);
                    artwork.setPublicId(publicId);
                    artwork.setName(name);
                    artwork.setPublish(publish);
                    artwork.setDescription(description);
                    artwork.setUserId(userId);

                    new ImageTask(artworkImage).execute(url);
                    title.setText(name);
                    ArtworkDetailActivity.this.description.setText(description);
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
    private class FavoriteTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(ArtworkDetailActivity.this);
            return request.doPost(params[0],params[1]);
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                boolean response = result.getBoolean("response");
                new CountLikeTask().execute("/gallery/likes/count/artworks/"+artwork.getId());
                if(response){
                    favorite.setImageResource(R.drawable.favorite_fill);
                }else {
                    favorite.setImageResource(R.drawable.favorite);

                }
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                String errorName = new HandleRequestError().handle(result).getName();
                Log.d(TAG, "onPostExecute: "+errorMessage);
                if(errorName.equals("NotLogin")){
                    ArtworkDetailActivity.this.startActivity(new Intent(ArtworkDetailActivity.this,LoginActivity.class));
                }
            }
        }
    }
    private class GetUserInfo extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(ArtworkDetailActivity.this);
            return request.doGet(params[0]);
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject response = result.getJSONObject("response");
                user = new User();
                user.setId(response.getString("id"));
                user.setFirstName(response.getString("firstName"));
                user.setLastName(response.getString("lastName"));
                user.setEmail(response.getString("email"));
                user.setProfileUrl(response.getString("profileUrl"));

                userName.setText(response.getString("firstName")+" "+response.getString("lastName"));
                String url = response.getString("profileUrl");
                if(!url.equals("null")){
                    new ImageTask(userImage).execute(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
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
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
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
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
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
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
}
