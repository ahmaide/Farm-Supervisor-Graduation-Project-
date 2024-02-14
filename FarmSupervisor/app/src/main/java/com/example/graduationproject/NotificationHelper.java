package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "دليل العناية الأسبوعي";

    @SuppressLint("MissingPermission")
    public static void showNotification(Context context, SharedPrefManager sharedPrefManager) {
        sharedPrefManager.writeString("firstTime", "1");
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel(context);

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_small_logo_photoroom_png_photoroom)
                .setContentTitle("طرق العناية الأسبوعية")
                .setContentText("أفضل طرق العناية بناء على حالة الطقس في منطقتك")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("أفضل طرق العناية بناء على حالة الطقس في منطقتك"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSound(soundUri)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_small_logo_photoroom_png_photoroom))
                .setLights(Color.GREEN, 3000, 3000)
                .setVibrate(new long[]{0, 500, 100, 500})
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(123, builder.build());
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "طرق العناية الأسبوعية";
            String description = "أفضل طرق العناية بناء على حالة الطقس في منطقتك";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 100, 500});
            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}