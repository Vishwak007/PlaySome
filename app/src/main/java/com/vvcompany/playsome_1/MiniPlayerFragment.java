package com.vvcompany.playsome_1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class MiniPlayerFragment extends Fragment {
    private  MyService musicService;
    ImageView mp_play_pause, mp_next, mp_prev;
    FrameLayout mpFrameLayout;
    int posMini;

    public MiniPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mini_player, container, false);


        mp_play_pause = v.findViewById(R.id.mini_player_play_pause);
        mp_next = v.findViewById(R.id.mp_next);
        mp_prev = v.findViewById(R.id.mini_btn_pre);
        mpFrameLayout = v.findViewById(R.id.frame_layout_mini);
        mpFrameLayout.setVisibility(View.INVISIBLE);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setServiceIntent();

    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unbindService(mini_service);
    }

    private void setServiceIntent() {
        Intent intent = new Intent(getContext(), MyService.class);
        getContext().bindService(intent, mini_service, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection mini_service = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder mBinder = (MyService.MyBinder) iBinder;
            musicService = mBinder.getService();
            setSongBtn();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void setSongBtn() {
        if (!musicService.isNull()){
            mpFrameLayout.setVisibility(View.VISIBLE);
            if (musicService.playing()){
                mp_play_pause.setImageResource(R.drawable.ic_pause);
            }else{
                mp_play_pause.setImageResource(R.drawable.ic_play);
            }
        }
        mp_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!musicService.isNull()){
                    if (musicService.playing()){
                        rotateAnim(mp_play_pause);
                        mp_play_pause.setImageResource(R.drawable.ic_play);

                    }else{
                        rotateAnim(mp_play_pause);
                        mp_play_pause.setImageResource(R.drawable.ic_pause);
                    }
                    musicService.playPauseBtnClicked();
                }


            }
        });

        mp_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!musicService.isNull()){
                    rotateAnim(mp_play_pause);
                    mp_play_pause.setImageResource(R.drawable.ic_pause);
                    Animation animation_left = AnimationUtils.loadAnimation(getContext(), R.anim.move_animation_left);
                    mp_prev.startAnimation(animation_left);
                    musicService.prevBtnClicked();
                }

            }
        });

        mp_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!musicService.isNull()){
                    rotateAnim(mp_play_pause);
                    mp_play_pause.setImageResource(R.drawable.ic_pause);
                    Animation animationMoveRight = AnimationUtils.loadAnimation(getContext(), R.anim.move_animation_right);
                    mp_next.startAnimation(animationMoveRight);
                    musicService.nextBtnClicked();
                }

            }
        });

    }

    private void rotateAnim(ImageView imageView){
        Animation animRotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
        imageView.startAnimation(animRotate);
    }

}