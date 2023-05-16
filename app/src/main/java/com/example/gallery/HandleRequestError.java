package com.example.gallery;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class HandleRequestError {
    public Error handle(JSONObject result){
        try {
            JSONObject error = result.getJSONObject("error");
            String name = error.getString("name");
            String message = error.getString("message");
            return new Error(name,message);
        } catch (JSONException e) {
            Log.e("HandleRequestError", "handle: "+e.getMessage() );
            return new Error("JSONException",e.getMessage());
        }
    }
    class Error{
        private String name;
        private String message;
        Error(String name, String message){
            this.name = name;
            this.message = message;
        }

        public String getName() {
            return name;
        }

        public String getMessage() {
            return message;
        }
    }
}
