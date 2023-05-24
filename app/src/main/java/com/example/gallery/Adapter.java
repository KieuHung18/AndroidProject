package com.example.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.entities.Artwork;

import java.util.ArrayList;

// Extends the Adapter class to RecyclerView.Adapter
// and implement the unimplemented methods
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<Artwork> artworks;
    private Activity activity;
    // Constructor for initialization
    public Adapter(Activity activity, ArrayList<Artwork> artworks) {
        this.activity = activity;
        this.artworks = artworks;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        // Passing view to ViewHolder
        Adapter.ViewHolder viewHolder = new Adapter.ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
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
                    ArtworkDetailFragment fragment = new ArtworkDetailFragment();
                    fragment.setArtwork( artwork);
                    ((HomeActivity)activity).loadFragment(fragment);
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
}
