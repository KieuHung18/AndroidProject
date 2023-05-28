package com.example.gallery.addapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.ArtworkDetailActivity;
import com.example.gallery.HomeActivity;
import com.example.gallery.R;
import com.example.gallery.entities.Artwork;
import com.example.gallery.entities.Ideal;
import com.example.gallery.task.ImageTask;

import java.util.ArrayList;

// Extends the Adapter class to RecyclerView.Adapter
// and implement the unimplemented methods
public class StaggeredGridAdapter extends RecyclerView.Adapter<StaggeredGridAdapter.ViewHolder> {
    private ArrayList<Artwork> artworks;
    private Activity activity;
    private Ideal ideal;
    // Constructor for initialization
    public StaggeredGridAdapter(Activity activity, ArrayList<Artwork> artworks) {
        this.activity = activity;
        this.artworks = artworks;
    }

    @NonNull
    @Override
    public StaggeredGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        // Passing view to ViewHolder
        StaggeredGridAdapter.ViewHolder viewHolder = new StaggeredGridAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull StaggeredGridAdapter.ViewHolder holder, int position) {
        // TypeCast Object to int type
        holder.setArtwork(artworks.get(position));
    }
    @Override
    public int getItemCount() {
        // Returns number of items currently available in Adapter
        return artworks.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView images;
        private Artwork artwork;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent artworkDetail = new Intent(activity,ArtworkDetailActivity.class);
                    artworkDetail.putExtra("artwork",artwork);
                    if(ideal!=null&&ideal.getId()!=null){
                        artworkDetail.putExtra("ideal",ideal);
                    }
                    activity.startActivity(artworkDetail);
                }
            });
            images = (ImageView) view.findViewById(R.id.imageView);
        }
        public void setArtwork(Artwork artwork) {
            this.artwork = artwork;
            new ImageTask(images).execute(artwork.getUrl());
        }
    }
    public void setArtworks(ArrayList<Artwork> artworks) {
        this.artworks = artworks;
    }

    public void setIdeal(Ideal ideal) {
        this.ideal = ideal;
    }
}
