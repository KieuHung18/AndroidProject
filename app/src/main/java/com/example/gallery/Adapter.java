package com.example.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Extends the Adapter class to RecyclerView.Adapter
// and implement the unimplemented methods
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    ArrayList<String> imageUrls;
    Context context;

    // Constructor for initialization
    public Adapter(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
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
        String url = imageUrls.get(position);
        holder.setUrl(url);
    }
    @Override
    public int getItemCount() {
        // Returns number of items currently available in Adapter
        return imageUrls.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView images;
        public ViewHolder(View view) {
            super(view);
            images = (ImageView) view.findViewById(R.id.imageView);
        }

        public void setUrl(String url) {
            new ImageTask(images).execute(url);
        }
    }
}
