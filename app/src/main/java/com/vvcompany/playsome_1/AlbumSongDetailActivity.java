package com.vvcompany.playsome_1;

import static com.vvcompany.playsome_1.MainActivity.audioFiles;
import static com.vvcompany.playsome_1.RecyclerViewAdapter.getAlbumArt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class AlbumSongDetailActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    ArrayList<AudioData> albumSongs = new ArrayList<>();
    AlbumSongDetailAdapter albumSongDetailAdapter;

//    public static ArrayList<AudioData> sameAlbumDiffSongs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_song_detail);
//        inthisActivity();
//
//        sameAlbumDiffSongs.clear();
//        getSameAlbumSongs();
//        setImageMethod();
//        setRecyclerView();
        albumPhoto = findViewById(R.id.AlbumDetailImage);
        recyclerView = findViewById(R.id.AlbumDetailRecyclerView);
        albumName = getIntent().getStringExtra("albumName");

        int j = 0;
        for (int i = 0 ; i < audioFiles.size() ; i++){
            if (albumName.equals(audioFiles.get(i).getAlbum())){
                albumSongs.add(j, audioFiles.get(i));
                j++;
            }
        }
        byte[] image = getAlbumArt(albumSongs.get(0).getPath());
        if (image != null){
            albumPhoto.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }else{
            albumPhoto.setImageResource(R.drawable.m1);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
//        recyclerView.setHasFixedSize(true);

        albumSongDetailAdapter = new AlbumSongDetailAdapter(this, albumSongs);
        recyclerView.setAdapter(albumSongDetailAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

    }

//    private void setRecyclerView() {
//        recyclerView.setHasFixedSize(true);
//
//        AlbumSongDetailAdapter albumSongDetailAdapter = new AlbumSongDetailAdapter(getApplicationContext(), sameAlbumDiffSongs);
//        recyclerView.setAdapter(albumSongDetailAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
//
//    }

//    private void getSameAlbumSongs() {
//        position = getIntent().getIntExtra("position", -1);
//        int j = 0;
//
//        for (int i = 0; i < audioFiles.size(); i++){
//            if (albums.get(position).getAlbum().equals(audioFiles.get(i).getAlbum())){
//                sameAlbumDiffSongs.add(j, audioFiles.get(i));
//                j++;
//            }
//        }
//    }
//
//    private void setImageMethod() {
//
//        byte[] art = getAlbumArt(sameAlbumDiffSongs.get(0).getPath());
//
//        if (art != null){
//            albumPhoto.setImageBitmap(BitmapFactory.decodeByteArray(art, 0, art.length));
//        }else{
//            albumPhoto.setImageResource(R.drawable.m1);
//        }
//
//    }
//
//    private void inthisActivity() {
//        albumPhoto = findViewById(R.id.AlbumDetailImage);
//        recyclerView = findViewById(R.id.AlbumDetailRecyclerView);
//    }

}