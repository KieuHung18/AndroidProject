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
import com.example.gallery.entities.Ideal;
import com.example.gallery.task.ImageTask;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter<Ideal> {
    public GridAdapter(@NonNull Context context, ArrayList<Ideal> ideals) {
        super(context, 0, ideals);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            view = LayoutInflater.from(getContext()).inflate(R.layout.ideal_item, parent, false);
        }
        Ideal ideal = getItem(position);
        TextView title = view.findViewById(R.id.textViewIdealTitle);
        TextView amount = view.findViewById(R.id.textViewAmountIdeal);
        ImageView image = view.findViewById(R.id.imageViewIdealThumbnail);

        amount.setText(String.valueOf(ideal.getSize()+" Artworks"));
        title.setText(ideal.getName());
        if(!ideal.getThumbnail().equals("")){
            new ImageTask(image).execute(ideal.getThumbnail());
        }
        return view;
    }
}
