package com.vvcompany.playsome_1;

import static com.vvcompany.playsome_1.Notification.ACTION_NEXT;
import static com.vvcompany.playsome_1.Notification.ACTION_PLAY;
import static com.vvcompany.playsome_1.Notification.ACTION_PREVIOUS;
import static com.vvcompany.playsome_1.Notification.ACTION_STOP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, MyService.class);


        if (actionName != null){
            switch (actionName){
                case ACTION_STOP:
//                    Toast.makeText(context, "going to stop", Toast.LENGTH_SHORT).show();
                    serviceIntent.putExtra("ActionName", "stop");
                    context.startService(serviceIntent);
                    break;

                case ACTION_PLAY:
                    serviceIntent.putExtra("ActionName", "playPause");
                    context.startService(serviceIntent);
                    break;

                case ACTION_NEXT:
                    serviceIntent.putExtra("ActionName", "next");
                    context.startService(serviceIntent);
                    break;

                case ACTION_PREVIOUS:
//                    Toast.makeText(context, "going to prev", Toast.LENGTH_SHORT).show();
                    serviceIntent.putExtra("ActionName", "previous");
                    context.startService(serviceIntent);
                    break;



            }
        }

    }
}
