package com.example.gallery;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.gallery.addapter.NotificationGridAdapter;
import com.example.gallery.entities.Notification;
import com.example.gallery.services.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    private GridView gridView;
    private ArrayList<Notification> notifications;
    private NotificationGridAdapter adapter;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.notification_fragment, container, false);

        gridView = view.findViewById(R.id.notificationGridView);
        gridView.setNumColumns(1);

        notifications = new ArrayList<Notification>();

        adapter = new NotificationGridAdapter(getContext(), notifications);
        gridView.setAdapter(adapter);
        new GetNotificationTask().execute("/users/notifications");
        return view;
    }

    private class GetNotificationTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(getContext());
            return request.doGet(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONArray response = result.getJSONArray("response");
                for(int i=0;i<response.length();i++){
                    Notification notification = new Notification();
                    notification.setTitle(((JSONObject)response.get(i)).getString("title"));
                    notification.setContent(((JSONObject)response.get(i)).getString("content"));
                    notifications.add(notification);
                }
                adapter = new NotificationGridAdapter(view.getContext(),notifications);
                gridView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d("NotificationFragment", "onPostExecute: "+errorMessage);
            }
        }
    }
}
