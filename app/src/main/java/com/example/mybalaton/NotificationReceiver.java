package com.example.mybalaton;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String attractionName = intent.getStringExtra("attraction_name");
        String channelId = "alarm_channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Ne felejtsd el!")
                .setContentText("Nézd meg a(z) " + attractionName + " látnivalót!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
} 