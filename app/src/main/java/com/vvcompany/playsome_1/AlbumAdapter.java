package com.vvcompany.playsome_1;

import static com.vvcompany.playsome_1.RecyclerViewAdapter.getAlbumArt;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<AudioData> albumFiles;

    public AlbumAdapter(Context mContext, ArrayList<AudioData> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.itemviewalbum, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.albumName.setText(albumFiles.get(position).getTitle());
        byte[] art = getAlbumArt(albumFiles.get(position).getPath());
        if (art != null){
            holder.albumImage.setImageBitmap(BitmapFactory.decodeByteArray(art, 0, art.length));
        }else{
            holder.albumImage.setImageResource(R.drawable.m1);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlbumSongDetailActivity.class);
//                intent.putExtra("position", holder.getAdapterPosition());
                intent.putExtra("albumName", albumFiles.get(holder.getAdapterPosition()).getAlbum());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView albumImage;
        TextView albumName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            albumImage = itemView.findViewById(R.id.albumImage);
            albumName = itemView.findViewById(R.id.albumName);
        }
    }
}
