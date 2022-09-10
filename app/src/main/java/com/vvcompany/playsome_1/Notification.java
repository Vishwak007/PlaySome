package com.vvcompany.playsome_1;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notification extends Application {

    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_ID_2 = "channel2";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_STOP = "actionstop";
    @Override
    public void onCreate() {
        createNotification();
        super.onCreate();
    }

    public void createNotification(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1,
                   "Channel(1)", NotificationManager.IMPORTANCE_HIGH );

           channel1.setDescription("Channel 1 Desc...");

           NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2,
                   "Channel(2)", NotificationManager.IMPORTANCE_HIGH);
           channel2.setDescription("Channel 2 Desc..");

           NotificationManager notificationManager =
                   getSystemService(NotificationManager.class);

           notificationManager.createNotificationChannel(channel1);
           notificationManager.createNotificationChannel(channel2);

        }

//        Intent intent = new Intent(this, PlayerActivity.class);
//        intent.putExtra("Title", strTitle);
//        intent.putExtra("Text", strText);
//
//
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "100");
//        builder.setContentTitle(strText);
//        builder.setContentText(strText);
//        builder.setTicker("Notification started");
//        builder.addAction(R.drawable.ic_launcher_background, "Action Button", pendingIntent);
////        builder.setContent(builder.createContentView());
//        builder.setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(requestCode, builder.build());


    }






}
