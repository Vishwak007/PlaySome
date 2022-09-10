package com.vvcompany.playsome_1;

import android.annotation.SuppressLint;
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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    Context context;
    static ArrayList<AudioData> musicFiles;

    public RecyclerViewAdapter(Context context, ArrayList<AudioData> musicFiles) {
        this.context = context;
        this.musicFiles = musicFiles;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.itemviewsongs, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemName.setText(musicFiles.get(position).getTitle());
        byte[] img = getAlbumArt(musicFiles.get(position).getPath());

        if (img != null){
            holder.itemImage.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
        }else{
            holder.itemImage.setImageResource(R.drawable.m1);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("song_position", holder.getAdapterPosition());
                intent.putExtra("sender", "song");
                context.startActivity(intent);
            }
        });

        holder.moreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, view); //view or name of button
                popupMenu.getMenuInflater().inflate(R.menu.popmenu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.deleteFile){
                            deleteFile(position, view);
                        }

                        return true;


                    }

                });


            }
        });

    }

    private void deleteFile(int adapterPosition, View view) {
        Uri contentUris = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(musicFiles.get(adapterPosition).getId()));

        File file = new File(musicFiles.get(adapterPosition).getPath());

        if (file.exists()){
            boolean fileDeleted = file.delete();
            if (fileDeleted){
                context.getContentResolver().delete(contentUris, null, null);
                musicFiles.remove(adapterPosition);

                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, musicFiles.size());

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
        return musicFiles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView itemImage, moreImage;
        TextView itemName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.itemImageView);
            itemName = itemView.findViewById(R.id.itemTextView);
            moreImage = itemView.findViewById(R.id.deleteMenu);

        }
    }
    public static byte[] getAlbumArt(String song_uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(song_uri);

        byte[] art =  retriever.getEmbeddedPicture();

        return art;
    }
    public void updateList(ArrayList<AudioData> mFiles){
        musicFiles = new ArrayList<>();
        musicFiles.addAll(mFiles);

//        musicFiles = mFiles; // ye b sahi hai



        notifyDataSetChanged();

    }

}
