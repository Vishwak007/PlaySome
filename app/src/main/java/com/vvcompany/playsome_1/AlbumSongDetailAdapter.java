package com.vvcompany.playsome_1;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class AlbumSongDetailAdapter extends RecyclerView.Adapter<AlbumSongDetailAdapter.MyViewHolder> {
    Context mContext;
    public static ArrayList<AudioData> albumFiles;

    public AlbumSongDetailAdapter(Context mContext, ArrayList<AudioData> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.itemviewsongs, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songName.setText(albumFiles.get(position).getTitle());

        byte[] art = getAlbumArtH(albumFiles.get(position).getPath());

        if (art != null){
            holder.songImage.setImageBitmap(BitmapFactory.decodeByteArray(art, 0, art.length));
        }else{
            holder.songImage.setImageResource(R.drawable.m1);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("sender", "album");
                intent.putExtra("song_position", holder.getAdapterPosition());

                mContext.startActivity(intent);

            }
        });
        holder.moreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(mContext, view); //view or name of button
                popupMenu.getMenuInflater().inflate(R.menu.popmenu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.deleteFile){
                            deleteFile(holder.getAdapterPosition(), view);
                        }

                        return true;


                    }

                });


            }
        });

    }
    private void deleteFile(int adapterPosition, View view) {
        Uri contentUris = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(albumFiles.get(adapterPosition).getId()));

        File file = new File(albumFiles.get(adapterPosition).getPath());

        if (file.exists()){
            boolean fileDeleted = file.delete();
            if (fileDeleted){
                mContext.getContentResolver().delete(contentUris, null, null);
                albumFiles.remove(adapterPosition);

                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, albumFiles.size());

                Snackbar.make(view,"File Deleted", Snackbar.LENGTH_LONG).show();
            }
            else{
                Snackbar.make(view,"File Can't be Deleted", Snackbar.LENGTH_LONG).show();
            }
        }else{
            Snackbar.make(view,"File does not exist", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView songImage, moreImage;
        TextView songName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.itemImageView);
            songName = itemView.findViewById(R.id.itemTextView);
            moreImage = itemView.findViewById(R.id.deleteMenu);
        }
    }
    public byte[] getAlbumArtH(String song_uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(song_uri);

        byte[] art =  retriever.getEmbeddedPicture();

        return art;
    }
}
