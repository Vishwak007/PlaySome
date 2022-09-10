package com.vvcompany.playsome_1;

import static com.vvcompany.playsome_1.PlayerActivity.favMusicFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FavouriteFragment extends Fragment {
    RecyclerView recyclerViewFav;
    static RecyclerViewAdapter recyclerViewAdapterFav;
    TextView textViewFav;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourite, container, false);
        textViewFav = v.findViewById(R.id.textViewFav);

        recyclerViewFav = v.findViewById(R.id.recyclerview_fav);
        recyclerViewFav.setHasFixedSize(true);

        if (favMusicFiles.size() > 0){
            textViewFav.setVisibility(View.INVISIBLE);
            recyclerViewAdapterFav = new RecyclerViewAdapter(getContext(), favMusicFiles);
            recyclerViewFav.setAdapter(recyclerViewAdapterFav);
            recyclerViewFav.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }else{
            textViewFav.setVisibility(View.VISIBLE);
        }

        return v;
    }
}