package com.example.gallery.services;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Request {
    public static final String  BACKEND_URL="http://172.24.179.73:3001";
    private String authentication;
    public Request(Context context){
        SharedPreferences pref = context.getSharedPreferences("Login", 0);
        authentication = pref.getString("authentication", null);
    }
    public String doPost(String url, String postData){
        url=BACKEND_URL+url;
        String data = "";
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
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
        return data;
    };

    public JSONObject doGet(String url){
        return null;
    };

    public String doUpload(String url, byte[] postData){
        //implements file name, file description
        url=BACKEND_URL+url;
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
            wr.writeBytes("Content-Disposition: form-data; name=\"files\"; filename=\"" + "image.jpg" + "\"\r\n\r\n");
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
        return data;
    };

}
