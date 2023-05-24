package com.example.gallery;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.gallery.entities.Artwork;

public class ArtworkDetailFragment extends Fragment{
    private Artwork artwork;
    private ImageButton back,option,favorite;
    private TextView title, description;
    private ImageView artworkImage;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artwork_detail_fragment, container, false);

        title = view.findViewById(R.id.textViewArtworkTitle);
        description = view.findViewById(R.id.textViewArtworkDescription);
        back  = view.findViewById(R.id.artworkDetailBack);
        option = view.findViewById(R.id.artworkDetailOption);
        favorite = view.findViewById(R.id.imageButtonFavorite);
        artworkImage = view.findViewById(R.id.imageViewArtworkDetail);

        new ImageTask(artworkImage).execute(artwork.getUrl());
        title.setText(artwork.getName());
        description.setText(artwork.getDescription());

        PopupMenu pm = new PopupMenu(view.getContext(), option);

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pm.getMenuInflater().inflate(R.menu.artwork_detail_option_menu, pm.getMenu());
                pm.show();
            }
        });
        return view;
    }

    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }
}
