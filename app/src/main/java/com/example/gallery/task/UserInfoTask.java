package com.example.gallery.task;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
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
    private String TAG ="UserInfoTask";
    @Override
    protected JSONObject doInBackground(String... params) {
        Request request = new Request(context);
        return request.doGet("/auth");
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            JSONObject response = result.getJSONObject("response");

            SharedPreferences pref = context.getSharedPreferences("User", MODE_PRIVATE);
            SharedPreferences.Editor Ed = pref.edit();

            Ed.putString("id",response.getString("id") );
            Ed.putString("firstName",response.getString("firstName") );
            Ed.putString("lastName",response.getString("lastName") );
            Ed.putString("role",response.getString("role") );
            Ed.putString("email",response.getString("email") );
            Ed.putString("hashPassword",response.getString("hashPassword") );
            Ed.putString("profileUrl",response.getString("profileUrl") );
            Ed.commit();

        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = new HandleRequestError().handle(result).getMessage();
            Log.d(TAG, "onPostExecute: "+errorMessage);
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

    public boolean logged(){
        SharedPreferences pref = context.getSharedPreferences("Authentication", 0);
        String authentication = pref.getString("authentication", null);
        if(authentication!=null){return true;}else {return false;}
    }
    public void logout (){
        SharedPreferences pref = context.getSharedPreferences("Authentication", 0);
        SharedPreferences.Editor Ed = pref.edit();
        Ed.clear();
        Ed.commit();
        pref = context.getSharedPreferences("User", 0);
        Ed = pref.edit();
        Ed.clear();
        Ed.commit();
    }
}