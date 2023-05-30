package com.example.gallery.addapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gallery.R;
import com.example.gallery.entities.User;
import com.example.gallery.task.ImageTask;

import java.util.ArrayList;

public class SimpleUserGridAdapter extends ArrayAdapter<User> {
    public SimpleUserGridAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            view = LayoutInflater.from(getContext()).inflate(R.layout.simple_user_item, parent, false);
        }
        User user = getItem(position);
        TextView title = view.findViewById(R.id.textViewUserName);
        ImageView image = view.findViewById(R.id.imageViewUserProfile);
        title.setText(user.getFirstName()+" "+user.getLastName());
        if(!user.getProfileUrl().equals("null")){
            new ImageTask(image).execute(user.getProfileUrl());
        }
        return view;
    }
}
