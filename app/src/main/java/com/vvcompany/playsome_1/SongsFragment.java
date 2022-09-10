package com.vvcompany.playsome_1;

import static com.vvcompany.playsome_1.MainActivity.audioFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SongsFragment extends Fragment {

    RecyclerView recyclerView;
    static RecyclerViewAdapter recyclerViewAdapter;
//    ArrayList<AudioData> audioFile = MainActivity.audioFile;


    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_layout);
        recyclerView.setHasFixedSize(true);


        if(audioFiles != null){
            recyclerViewAdapter = new RecyclerViewAdapter(getContext(), audioFiles);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            recyclerView.setAdapter(recyclerViewAdapter);

        }

//        assert audioFile != null;
//        if(audioFile.size() > 0){
//            // do somethig
//        }




        return view;
    }
}