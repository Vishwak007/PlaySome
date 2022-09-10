package com.vvcompany.playsome_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements ActionDuringPlaying{
    ImageView backArrow, shuffleBtn, prevBtn, play_pauseBtn, nextBtn, repeatBtn, songImage, gradImage;
    TextView songName, artistName, startTime, endTime;
    SeekBar seekBar;
    RelativeLayout mContainer;
    static int position;
    int random;
    String path;
    String sender;
    public static String id;
    int currentPosition;

    Uri uri;
//    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    boolean isShuffleOn = false;
    boolean isRepeatOn = false;
    static ArrayList<AudioData> musicFiles = new ArrayList<>();
    public static ArrayList<AudioData> favMusicFiles = new ArrayList<>();

    GestureLibrary mLibrary;
    Thread playpauseThread, nextBtnThread, prevBtnThread;

    MyService myService;
    boolean isStillBound = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen(); // iske bina b chal ra hai
        setContentView(R.layout.activity_player);

        Objects.requireNonNull(getSupportActionBar()).hide();

        inthisActivity();



        getIntentMethod();

        setValue(position);
        getSongImgColor();
        setGestureMethod();
//        playMusic(path);

//        Notification noti = new Notification(this);
//        noti.createNotification();
//        createNotification();



        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        PlayerActivity.this.runOnUiThread(runnable);

        btnClick();
//
//        mediaPlayer.setOnCompletionListener(onCompletionListener);
//        myService.onCompletion();




    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // window ke andar action bar tool bar aur poori activity hti hai
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void getServiceStart() {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("position", position);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder miBinder = (MyService.MyBinder) iBinder;
            myService = miBinder.getService();
            isStillBound = true;

            myService.setCallBack(PlayerActivity.this);

            playMusic(path);

            myService.showNotification(R.drawable.ic_pause);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isStillBound = false;

        }
    };


    private void imRotAnimation(Context context, ImageView image1, int image2){
        Animation animRot = AnimationUtils.loadAnimation(context, R.anim.rotate_animation);
        image1.startAnimation(animRot);
        if (image2 != 0){
            image1.setImageResource(image2);
        }

    }

    @Override
    protected void onResume() {
        getServiceStart();
        playpauseThread();
        nextBtnThread();
        prevBtnThread();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(serviceConnection);
    }




    private void setGestureMethod() {
        mLibrary = GestureLibraries.fromRawResource(getApplicationContext(), R.raw.gestures);
        if (!mLibrary.load()){
            finish();
        }

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestureLayout);
        gestures.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {
                ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
                if (!predictions.isEmpty() && predictions.get(0).score > 1.0){
                    String result = predictions.get(0).name;

//                    if ("favourite".equalsIgnoreCase(result)){
//                        Toast.makeText(getApplicationContext(), "Opening favourite", Toast.LENGTH_SHORT).show();
//                        Intent intent  =  new Intent(getApplicationContext(), FavouriteFragment.class);
//                        startActivity(intent);
//                    }
                    if("favsave".equalsIgnoreCase(result)){


                        if (!favMusicFiles.contains(musicFiles.get(position))){
                            Toast.makeText(getApplicationContext(), "this song added to favourite", Toast.LENGTH_SHORT).show();
                            favMusicFiles.add(musicFiles.get(position));
                        }else{
                            Toast.makeText(getApplicationContext(), "this song is removed form favourite", Toast.LENGTH_SHORT).show();
                            favMusicFiles.remove(musicFiles.get(position));
                        }


                    }
                    else{
                        Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
                        intent.putExtra("sender", "gesture");
                        intent.putExtra("searchChar", result);
                        startActivity(intent);
                    }
                }

            }
        });


    }

    private void getSongImgColor() {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);
        byte[] art = metadataRetriever.getEmbeddedPicture();

        Bitmap bitmap;

        if (art != null){
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            Palette palette = Palette.from(bitmap).generate();
            Palette.Swatch vibrant = palette.getDarkVibrantSwatch();


            if (vibrant != null){
                int bodyColor = vibrant.getRgb();
                int textColor = vibrant.getTitleTextColor();
                int textBodyColor = vibrant.getBodyTextColor();

                gradImage.setBackgroundResource(R.drawable.grad_playeractivitycover);
                gradImage.setVisibility(View.VISIBLE);
                mContainer.setBackgroundResource(R.color.black);

                int[] colorArray = new int[]{bodyColor, 0x00000000};
                int[] colorArrayBg = new int[]{bodyColor, bodyColor};

                GradientDrawable gradientDrawable = new  GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,colorArray);
                GradientDrawable gradForBg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colorArrayBg);

                gradImage.setBackground(gradientDrawable);
                mContainer.setBackground(gradForBg);

                songName.setTextColor(textColor);
                artistName.setTextColor(textBodyColor);

            }else{
                gradImage.setBackgroundResource(R.drawable.grad_playeractivitycover);
                gradImage.setVisibility(View.VISIBLE);
                mContainer.setBackgroundResource(R.color.black);

                int[] colorArray = new int[]{0xff000000, 0x00000000}; // f means full transparency (aarrggbb) aa means aplha means transparency
                int[] colorArrayBg = new int[]{0xff000000, 0xff000000};

                GradientDrawable gradientDrawable = new  GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,colorArray);
                GradientDrawable gradForBg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colorArrayBg);

                gradImage.setBackground(gradientDrawable);
                mContainer.setBackground(gradForBg);

                songName.setTextColor(Color.WHITE);
                artistName.setTextColor(Color.DKGRAY);

            }

        }else{
            gradImage.setBackgroundResource(R.drawable.grad_playeractivitycover);
            gradImage.setVisibility(View.INVISIBLE);
            mContainer.setBackgroundResource(R.color.black);

            songName.setTextColor(Color.WHITE);
            artistName.setTextColor(Color.DKGRAY);
        }
    }

    private void getIntentMethod() {
        Intent intent = getIntent();
        position = intent.getIntExtra("song_position", -1);

        sender = intent.getStringExtra("sender");

        if (sender != null && sender.equals("album")){
            musicFiles = AlbumSongDetailAdapter.albumFiles;
        }else{
            musicFiles = RecyclerViewAdapter.musicFiles;
        }

        if (!musicFiles.isEmpty()){
            path = musicFiles.get(position).getPath();
        }
    }

    public void moveToNext() {
        if (myService.playing()){
            myService.stop();
            myService.release();


            if (!isRepeatOn){       // same as isRepeatOn == false
                if (isShuffleOn){
                    random = getRandom();
                    position = random;
                }else{
                    if (position == (musicFiles.size()-1)){
                        position = 0;
                    }else{
                        position = position + 1;
                    }
                }

            }

            path = musicFiles.get(position).getPath();

            uri = Uri.parse(path);
//
//            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            myService.onCreateMediaPlayer(position);

            myService.start();

            play_pauseBtn.setImageResource(R.drawable.ic_pause);


            seekBar.setMax((myService.duration())/1000);

            setValue(position);

            SharedPreferences.Editor sharedPref = getSharedPreferences("miniSharePos", MODE_PRIVATE).edit();
            sharedPref.putInt("miniPos", position);
            sharedPref.apply();


            getSongImgColor();

            seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

            PlayerActivity.this.runOnUiThread(runnable);

            btnClick();

//            mediaPlayer.setOnCompletionListener(onCompletionListener);
            myService.showNotification(R.drawable.ic_pause);
            myService.onCompletion();

        }else{
            if (!isRepeatOn){       // same as isRepeatOn == false
                if (isShuffleOn){
                    random = getRandom();
                    position = random;
                }else{
                    if (position == (musicFiles.size()-1)){
                        position = 0;
                    }else{
                        position = position + 1;
                    }
                }

            }

            path = musicFiles.get(position).getPath();

            uri = Uri.parse(path);

//            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            myService.onCreateMediaPlayer(position);
            myService.start();
            play_pauseBtn.setImageResource(R.drawable.ic_pause);


            seekBar.setMax((myService.duration())/1000);

            setValue(position);
            getSongImgColor();

            seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

            PlayerActivity.this.runOnUiThread(runnable);

            btnClick();

//            mediaPlayer.setOnCompletionListener(onCompletionListener);
            myService.showNotification(R.drawable.ic_pause);
            myService.onCompletion();

        }
    }

    public void moveToPrev() {

        if (myService.playing()){
            myService.stop();
            myService.release();

//            if (isRepeatOn == true) {
//                position = position;
//            }

            if (!isRepeatOn){       // same as isRepeatOn == false
                if (isShuffleOn){
                    random = getRandom();
                    position = random;
                }else{
                    if (position == 0){
                        position = (musicFiles.size() - 1);
                    }else{
                        position = position - 1;
                    }
                }

            }

            path = musicFiles.get(position).getPath();

            uri = Uri.parse(path);

//            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            myService.onCreateMediaPlayer(position);
            myService.start();

            play_pauseBtn.setImageResource(R.drawable.ic_pause);
            seekBar.setMax((myService.duration())/1000);

            setValue(position);
            getSongImgColor();

            seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

            PlayerActivity.this.runOnUiThread(runnable);
            myService.showNotification(R.drawable.ic_pause);

            btnClick();

//            mediaPlayer.setOnCompletionListener(onCompletionListener);
            myService.onCompletion();


        }else{

            if (!isRepeatOn){       // same as isRepeatOn == false
                if (isShuffleOn){
                    random = getRandom();
                    position = random;
                }else{
                    if (position == 0){
                        position = (musicFiles.size() - 1);
                    }else{
                        position = position - 1;
                    }
                }

            }

            path = musicFiles.get(position).getPath();

            uri = Uri.parse(path);

//            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            myService.onCreateMediaPlayer(position);
            myService.start();

            play_pauseBtn.setImageResource(R.drawable.ic_pause);
            seekBar.setMax((myService.duration())/1000);

            setValue(position);
            getSongImgColor();

            seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

            PlayerActivity.this.runOnUiThread(runnable);

            btnClick();

//            mediaPlayer.setOnCompletionListener(onCompletionListener);
            myService.showNotification(R.drawable.ic_pause);
            myService.onCompletion();
        }

    }

    private void btnClick() {

        playpauseThread();
        nextBtnThread();
        prevBtnThread();

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShuffleOn ){     // similar as isShuffleOn == false

                    if (isRepeatOn){
                        isRepeatOn = false;
                        imRotAnimation(getApplicationContext(), repeatBtn, R.drawable.ic_repeat);
                    }
                    isShuffleOn = true;

                    imRotAnimation(getApplicationContext(),shuffleBtn, R.drawable.ic_blue_shuffle);



                }else{
                    isShuffleOn = false;
                    imRotAnimation(getApplicationContext(), shuffleBtn, R.drawable.ic_shuffle);

                }


            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRepeatOn){
                    isRepeatOn = true;

                    if (isShuffleOn){
                        isShuffleOn = false;

                        imRotAnimation(getApplicationContext(), shuffleBtn, R.drawable.ic_shuffle);

                    }

                    imRotAnimation(getApplicationContext(), repeatBtn, R.drawable.ic_blue_repeat);

                }else{
                    isRepeatOn = false;
                    imRotAnimation(getApplicationContext(), repeatBtn, R.drawable.ic_repeat);

                }

            }
        });


    }

    private void prevBtnThread() {
        prevBtnThread = new Thread(){
            @Override
            public void run() {
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moveToPrev();
                    }
                });
            }
        };
        prevBtnThread.start();
    }

    private void nextBtnThread() {
        nextBtnThread = new Thread(){
            @Override
            public void run() {
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moveToNext();
                    }
                });
            }
        };
        nextBtnThread.start();
    }

    private int getRandom() {
        Random rand = new Random();
        random = rand.nextInt(musicFiles.size());
        return random;
    }

//    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
//        @Override
//        public void onCompletion(MediaPlayer mediaPlayer) {
//            play_pauseBtn.setImageResource(R.drawable.ic_play);
//            moveToNext();
////            nextBtnThread();
//        }
//    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (myService != null){
                int mCurrentTimePos = myService.currentPos();
                seekBar.setProgress(mCurrentTimePos/ 1000);
                startTime.setText(getTimeInFormat(mCurrentTimePos));
            }
            handler.postDelayed(this, 1000);
        }
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//            if (mediaPlayer != null && b){
////                    mediaPlayer.seekTo(i*1000,MediaPlayer.SEEK_CLOSEST);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                    mediaPlayer.seekTo(i*1000,MediaPlayer.SEEK_CLOSEST);
//                else
//                    mediaPlayer.seekTo(i*1000);
//            }
            myService.seeking(i,b);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            play_pauseBtn.setImageResource(R.drawable.ic_play);

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            if (myService.playing()){
                play_pauseBtn.setImageResource(R.drawable.ic_pause);
            }else{
                play_pauseBtn.setImageResource(R.drawable.ic_play);
            }


        }
    };

    private void playpauseThread(){
        playpauseThread = new Thread(){
            @Override
            public void run() {
                play_pauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseMusic();
                    }
                });
            }
        };
        playpauseThread.start();
    }


    public void playPauseMusic() {

        if (myService.playing()){
            myService.pause();
            Animation playpauseAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_animation);

//            playpauseAnim.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
////                    play_pauseBtn.setImageResource(R.drawable.ic_play);
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
            play_pauseBtn.startAnimation(playpauseAnim);
            play_pauseBtn.setImageResource(R.drawable.ic_play);
            myService.showNotification(R.drawable.ic_play);


        }else{
            myService.start();
            Animation playpauseAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_animation);

            play_pauseBtn.startAnimation(playpauseAnim);
            play_pauseBtn.setImageResource(R.drawable.ic_pause);
            myService.showNotification(R.drawable.ic_pause);

        }

    }
//    private void playSongThread(String path){
//        playSongThread = new Thread(){
//            @Override
//            public void run() {
//                playMusic(path);
//            }
//        };
//        playSongThread.start();
//    }

    private void playMusic(String path) {

        uri = Uri.parse(path);

//        if(myService.isNull()){
//
////            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//            myService.onCreateMediaPlayer(position);
//            myService.start();
//            play_pauseBtn.setImageResource(R.drawable.ic_pause);
//            seekBar.setMax((myService.duration())/1000);
//
//
////            Toast.makeText(getApplicationContext(), "Media player was not playing", Toast.LENGTH_SHORT).show();
//        }else{
//            myService.stop();
//            myService.release();
////            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//            myService.onCreateMediaPlayer(position);
//            myService.start();
//            play_pauseBtn.setImageResource(R.drawable.ic_pause);
//            seekBar.setMax((myService.duration())/1000);
//
//            Toast.makeText(getApplicationContext(), "playing", Toast.LENGTH_SHORT).show();
//
//        }

        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("servicePos", position);
        intent.putExtra("serviceSender", sender);
        startService(intent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//        } else {
//            startService(intent);
//        }

        play_pauseBtn.setImageResource(R.drawable.ic_pause);
//        seekBar.setMax((myService.duration())/1000);
//        myService.onCompletion(onCompletionListener);







    }

    private void setValue(int position) {
        songName.setText(musicFiles.get(position).getTitle());
        artistName.setText(musicFiles.get(position).getArtist());
        byte[] img = getAlbumArtH(musicFiles.get(position).getPath());
        Animation imageAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeanimation);
        imageAnim.setDuration(500);
        imageAnim.setStartOffset(100);

        if (img != null){

            songImage.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));

            songImage.startAnimation(imageAnim);
        }else{

            songImage.setImageResource(R.drawable.mscimg4);

            songImage.startAnimation(imageAnim);
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!myService.isNull() && myService.playing()){
                    Intent backIntent = new Intent(PlayerActivity.this, MainActivity.class);
                    startActivity(backIntent);


                }else{
                    Intent backIntent = new Intent(PlayerActivity.this, MainActivity.class);
                    startActivity(backIntent);
                }

            }
        });

        String duration = musicFiles.get(position).getDuration();
        int dur = Integer.parseInt(duration);
        String time = getTimeInFormat(dur);
//        seekBar.setMax((myService.duration())/1000);
        seekBar.setMax((dur)/1000);
        endTime.setText(time);



    }

    public static String getTimeInFormat(int duration){
        // duration is in milli sec
        int tSec = (duration/1000);

        int min = (tSec/60);
        int sec = (tSec%60); // or tSec - (min*60)

        String strMin = String.valueOf(min);
        String strSec = String.valueOf(sec);
        String newMin;
        String newSec;

        if (strMin.length() < 2 ){
            newMin = "0"+strMin;

        }else{
            newMin = strMin;
        }
        if (strSec.length() < 2 ){
            newSec = "0"+strSec;

        }else{
            newSec = strSec;
        }

        String time;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            time = String.join(":",newMin, newSec);
        }
        else{
            time = "00:00";
        }
        return time;

    }








    private void inthisActivity() {
        backArrow = findViewById(R.id.backButton);

        shuffleBtn = findViewById(R.id.shuffle_off_player_activity);
        prevBtn = findViewById(R.id.previous_player);
        play_pauseBtn = findViewById(R.id.play_song_player);
        nextBtn = findViewById(R.id.next_song_player);
        repeatBtn = findViewById(R.id.repeat_off_player);
        gradImage = findViewById(R.id.imageView_cover);
        songImage = findViewById(R.id.songImage);
        songName = findViewById(R.id.songName_Player);
        artistName = findViewById(R.id.artistName_Player);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);
        seekBar = findViewById(R.id.seekBar_player);
        mContainer = findViewById(R.id.mContainer);

    }
    public static byte[] getAlbumArtH(String song_uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(song_uri);

        byte[] art =  retriever.getEmbeddedPicture();

        return art;
    }



}