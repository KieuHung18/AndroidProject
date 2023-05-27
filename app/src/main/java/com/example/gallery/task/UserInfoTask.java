package com.example.gallery.task;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.gallery.HandleRequestError;
import com.example.gallery.entities.User;
import com.example.gallery.services.Request;

import org.json.JSONObject;

public class UserInfoTask extends AsyncTask<String, Void, JSONObject> {
    private Context context;
    public UserInfoTask(Context context){
        this.context=context;
    }
    @Override
    protected JSONObject doInBackground(String... params) {
        Request request = new Request(context);
        return request.doGet("/auth");
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            JSONObject response = result.getJSONObject("response");
            JSONObject User = response.getJSONObject("user");
            String sessionId = response.getString("sessionId");

            SharedPreferences pref = context.getSharedPreferences("Authentication", MODE_PRIVATE);
            SharedPreferences.Editor Ed = pref.edit();
            Ed.putString("authentication",sessionId );
            Ed.commit();

            pref = context.getSharedPreferences("User", MODE_PRIVATE);
            Ed = pref.edit();

            Ed.putString("id",User.getString("id") );
            Ed.putString("firstName",User.getString("firstName") );
            Ed.putString("lastName",User.getString("lastName") );
            Ed.putString("role",User.getString("role") );
            Ed.putString("email",User.getString("email") );
            Ed.putString("hashPassword",User.getString("hashPassword") );
            Ed.putString("profileUrl",User.getString("profileUrl") );
            Ed.commit();

        } catch (Exception e) {
            String errorMessage = new HandleRequestError().handle(result).getMessage();
            Toast.makeText(context,errorMessage,Toast.LENGTH_SHORT).show();
        }
    }
    public User getUser(){
        User user = new User();
        SharedPreferences pref = context.getSharedPreferences("User", 0);
        user.setId(pref.getString("id", null));
        user.setFirstName(pref.getString("firstName", null));
        user.setLastName(pref.getString("lastName", null));
        user.setRole(pref.getString("role", null));
        user.setEmail(pref.getString("email", null));
        user.setHashPassword(pref.getString("hashPassword", null));
        user.setProfileUrl(pref.getString("profileUrl", null));
        return user;
    }
}