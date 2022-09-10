package com.vvcompany.playsome_1;

import static com.vvcompany.playsome_1.Notification.ACTION_NEXT;
import static com.vvcompany.playsome_1.Notification.ACTION_PLAY;
import static com.vvcompany.playsome_1.Notification.ACTION_PREVIOUS;
import static com.vvcompany.playsome_1.Notification.ACTION_STOP;
import static com.vvcompany.playsome_1.Notification.CHANNEL_ID_2;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class MyService extends Service {
    int position;
    IBinder iBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<AudioData> serviceSongList = new ArrayList<>();
    Uri uri;
    ActionDuringPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;

    @Override
    public void onCreate() {

        serviceSongList = PlayerActivity.musicFiles;

        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");

        super.onCreate();
    }



    public class MyBinder extends Binder{
        MyService getService (){
            return MyService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePos", -1  );

        String actionName = intent.getStringExtra("ActionName");
        if (myPosition != (-1)){
            playMedia(myPosition);
        }



        if (actionName != null){
            switch (actionName){
                case "playPause":
//                    Toast.makeText(this, "playpause", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null){
                        actionPlaying.playPauseMusic();
                    }
                    break;

                case "next":
//                    Toast.makeText(this, "nxt", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null){
                        actionPlaying.moveToNext();
                    }
                    break;

                case "previous":
                    if (actionPlaying != null){
                        actionPlaying.moveToPrev();
                    }
                    break;

                case "stop":
//                    Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        stopForeground(true);
                        stopSelf();

                    }
                    break;


            }
        }
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        position = intent.getIntExtra("position", -1);


        return iBinder;
    }

    private void playMedia(int startPosition){


        serviceSongList = PlayerActivity.musicFiles;

        position = startPosition;
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (!serviceSongList.isEmpty()){
                onCreateMediaPlayer(position);
                mediaPlayer.start();
            }


        }else {
            if (!serviceSongList.isEmpty()){
                onCreateMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        assert mediaPlayer != null;
        mediaPlayer.setOnCompletionListener(onCompletionListener);

//        showNotification(R.drawable.ic_pause);
    }
    public void onCreateMediaPlayer(int positionInner){
        position = positionInner;
        uri = Uri.parse(PlayerActivity.musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    public void start(){
        mediaPlayer.start();
    }

    public void stop(){
        mediaPlayer.stop();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void release(){
        mediaPlayer.release();
    }
    public boolean playing(){
        boolean isPlaying = mediaPlayer.isPlaying();
        return isPlaying;

    }
    public int duration(){
        int dur = mediaPlayer.getDuration();
        return dur;
    }
    public int currentPosition(){
        int curDur = mediaPlayer.getCurrentPosition();
        return curDur;
    }

    public void onCompletion(){
        mediaPlayer.setOnCompletionListener(onCompletionListener);

    }

    public int currentPos(){
        return mediaPlayer.getCurrentPosition();
    }

    public boolean isNull(){
        if (mediaPlayer!= null){
            return false;
        }else{
            return true;
        }
    }

    public void seeking(int i, boolean b){
        if (mediaPlayer != null && b){
//                    mediaPlayer.seekTo(i*1000,MediaPlayer.SEEK_CLOSEST);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                mediaPlayer.seekTo(i*1000,MediaPlayer.SEEK_CLOSEST);
            else
                mediaPlayer.seekTo(i*1000);
        }
    }
    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (actionPlaying != null){
                actionPlaying.moveToNext();
            }

        }
    };

    public void setCallBack(ActionDuringPlaying actionPlay){
        this.actionPlaying = actionPlay;
    }

    public void showNotification(int playPauseBtn){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, intent, 0);

        Intent prevIntent = new Intent(this,NotificationReciever.class);
        prevIntent.setAction(ACTION_PREVIOUS);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this,
                0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this,NotificationReciever.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,
                0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playPauseIntent = new Intent(this,NotificationReciever.class);
        playPauseIntent.setAction(ACTION_PLAY);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this,
                0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this,NotificationReciever.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this,
                0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        byte[] picture = null;
        picture = getAlbumArtI(PlayerActivity.musicFiles.get(PlayerActivity.position).getPath());
        Bitmap bitmapImg = null;
        if (picture != null){
            bitmapImg = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        }else{
            bitmapImg = BitmapFactory.decodeResource(getResources(), R.drawable.m1);
        }

        androidx.media.app.NotificationCompat.MediaStyle mediaStyle = new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionCompat.getSessionToken());

        android.app.Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(bitmapImg)
                .setContentTitle(serviceSongList.get(PlayerActivity.position).getTitle())
                .setContentText(serviceSongList.get(PlayerActivity.position).getArtist())
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "PlayPause", playPausePendingIntent)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPendingIntent)
                .addAction(R.drawable.ic_baseline_stop_24, "Stop", stopPendingIntent)
                .setTicker("Playing "+ serviceSongList.get(PlayerActivity.position).getTitle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(mediaStyle).build();

//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notification);
//        Toast.makeText(getBaseContext(), ""+ PlayerActivity.position, Toast.LENGTH_SHORT).show();

        startForeground(1, notification);



    }

    public byte[] getAlbumArtI(String song_uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(song_uri);

        byte[] art =  retriever.getEmbeddedPicture();

        return art;
    }

    public void playPauseBtnClicked(){
        actionPlaying.playPauseMusic();
    }

    public void nextBtnClicked(){
        actionPlaying.moveToNext();
    }

    public void prevBtnClicked(){
        actionPlaying.moveToPrev();
    }

    
    
    


}
