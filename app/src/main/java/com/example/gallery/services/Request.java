package com.example.gallery.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Request {

    private static String localhost = "http://172.24.179.73:3001";
    private static String host = "https://kieuhung18-mobile-backend.onrender.com";
    public static final String  BACKEND_URL=host;
    private String authentication;
    public Request(Context context){
        if(context!=null){
            SharedPreferences pref = context.getSharedPreferences("Authentication", 0);
            authentication = pref.getString("authentication", null);
        }
    }
    public JSONObject doPost(String url, String postData){
        url=BACKEND_URL+url;
        String data = "";
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("authentication", authentication);
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                data += current;
                inputStreamData = inputStreamReader.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        JSONObject response = null;
        try {
            response = new JSONObject(data);
        } catch (JSONException e) {
            Log.e("Request", "doPost: "+e.getMessage() );
        }
        return response;

    };
    public JSONObject doDelete(String url, String postData){
        url=BACKEND_URL+url;
        String data = "";
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("authentication", authentication);
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                data += current;
                inputStreamData = inputStreamReader.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        JSONObject response = null;
        try {
            response = new JSONObject(data);
        } catch (JSONException e) {
            Log.e("Request", "doPost: "+e.getMessage() );
        }
        return response;

    };
    public JSONObject doGet(String url){
        url=BACKEND_URL+url;
        String data = "";
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("authentication", authentication);

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                data += current;
                inputStreamData = inputStreamReader.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        JSONObject response = null;
        try {
            response = new JSONObject(data);
        } catch (JSONException e) {
            Log.e("Request", "doGet: "+e.getMessage() );
        }
        return response;
    };
    public JSONObject doDelete(String url){
        url=BACKEND_URL+url;
        String data = "";
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setRequestProperty("authentication", authentication);

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                data += current;
                inputStreamData = inputStreamReader.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        JSONObject response = null;
        try {
            response = new JSONObject(data);
        } catch (JSONException e) {
            Log.e("Request", "doPost: "+e.getMessage() );
        }
        return response;
    };

    public JSONObject doUpload(byte[] postData){
        //implements file name, file description
        String url=BACKEND_URL+"/upload";
        String data = "";
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("POST");

            String boundary = UUID.randomUUID().toString();
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpURLConnection.setRequestProperty("authentication", authentication);
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes("--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"description\"\r\n\r\n");
            wr.writeBytes("fileDescription" + "\r\n");

            wr.writeBytes("--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + "image.jpg" + "\"\r\n\r\n");
            wr.write(postData);
            wr.writeBytes("\r\n");
            wr.writeBytes("--" + boundary + "--\r\n");
            wr.flush();
            wr.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                data += current;
                inputStreamData = inputStreamReader.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        JSONObject response = null;
        try {
            response = new JSONObject(data);
        } catch (JSONException e) {
            Log.e("Request", "doUpload: "+e.getMessage() );
        }
        return response;
    };

}
