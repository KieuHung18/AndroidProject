package com.example.gallery;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gallery.addapter.SimpleUserGridAdapter;
import com.example.gallery.entities.Artwork;
import com.example.gallery.entities.User;
import com.example.gallery.services.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListUserActivity extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<User> users;
    private SimpleUserGridAdapter adapter;
    private ImageButton back;
    private String TAG = "ListUserActivity";
    private String api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = getIntent().getExtras().getString("api");
        setContentView(R.layout.list_user_activity);
        gridView = findViewById(R.id.userGridView);
        back= findViewById(R.id.listUserBack);
        gridView.setNumColumns(1);

        users = new ArrayList<User>();

        adapter = new SimpleUserGridAdapter(this, users);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                finish();
                Intent home = new Intent(ListUserActivity.this, HomeActivity.class);
                home.putExtra("user",users.get(i));
                home.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(home);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new GetUserTask().execute(api);
    }
    private class GetUserTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Request request = new Request(ListUserActivity.this);
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
                for(int i=0;i<response.length();i++){
                    JSONObject fetchUser = response.getJSONObject(i);
                    User user = new User();
                    user.setProfileUrl(fetchUser.getString("profileUrl"));
                    user.setId(fetchUser.getString("id"));
                    user.setFirstName(fetchUser.getString("firstName"));
                    user.setLastName(fetchUser.getString("lastName"));
                    user.setRole(fetchUser.getString("role"));
                    user.setEmail(fetchUser.getString("email"));
                    users.add(user);

                }
                adapter = new SimpleUserGridAdapter(ListUserActivity.this,users);
                gridView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d(TAG, "onPostExecute: "+errorMessage);
            }
        }
    }
}
