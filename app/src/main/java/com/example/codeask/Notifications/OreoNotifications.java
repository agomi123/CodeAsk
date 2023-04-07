package com.example.codeask.Notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

public class OreoNotifications extends ContextWrapper {
    private static final String CHANNEL_ID="com.koddev.chatapp";
    private static final String CHANNEL_NAME="chatapp";
    private NotificationManager notificationManager;

    public OreoNotifications(Context context){
        super(context);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createchannel();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createchannel(){
        NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }
    public NotificationManager getManager(){
        if(notificationManager==null)
            notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    return notificationManager;
    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title, String body, PendingIntent pendingIntent, Uri sounduri,String icon){
        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(sounduri)
                .setAutoCancel(true);
    }

}
