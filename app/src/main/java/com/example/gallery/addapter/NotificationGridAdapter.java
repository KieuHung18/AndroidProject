package com.example.gallery.addapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gallery.R;
import com.example.gallery.entities.Ideal;
import com.example.gallery.entities.Notification;
import com.example.gallery.task.ImageTask;

import java.util.ArrayList;

public class NotificationGridAdapter extends ArrayAdapter<Notification> {
    public NotificationGridAdapter(@NonNull Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            view = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }
        Notification notification = getItem(position);
        TextView title = view.findViewById(R.id.textViewNotificationTitle);
        TextView content = view.findViewById(R.id.textViewNotificationContent);
        title.setText(notification.getTitle());
        content.setText(notification.getContent());
        return view;
    }
}
