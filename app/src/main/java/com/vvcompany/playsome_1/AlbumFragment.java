package com.vvcompany.playsome_1;

import static com.vvcompany.playsome_1.MainActivity.albums;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlbumFragment extends Fragment {

    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;



    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = v.findViewById(R.id.albumRecyclerView);
        recyclerView.setHasFixedSize(true);

        if (!(albums.size() < 1)){
            albumAdapter = new AlbumAdapter(getContext(), albums);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
//        albumRecyViewAdapter = new AlbumRecyViewAdapter(getContext(), albums);
//        recyclerView.setAdapter(albumRecyViewAdapter);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        return v;
    }
}