package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    String email;
    final ProfileFragment profileFragment = new ProfileFragment();
    final LandFragment landFragment = new LandFragment();
    final NotificationFragment notificationFragment = new NotificationFragment();
    final MarketFragment marketFragment = new MarketFragment();
    final HomePage homeFragment = new HomePage();

    final PestWarningFragment pestWarningFragment = new PestWarningFragment();
    final WeeklyWeather weeklyWeatherFragment = new WeeklyWeather();

    BottomNavigationView bottomNavigationView;

    SharedPrefManager sharedPrefManager;
    private static final String CHANNEL_ID = "دليل العناية الأسبوعي";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        email = getIntent().getStringExtra("email");
        Bundle bundle = new Bundle();
        bundle.putString("email", email);

        sharedPrefManager = SharedPrefManager.getInstance(HomeActivity.this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.market:
                    marketFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, marketFragment).commit();
                    return true;
                case R.id.notification:
                    notificationFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                    return true;
                case R.id.home:
                    homeFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    return true;
                case R.id.land:
                    landFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, landFragment).commit();
                    return true;
                case R.id.profile:
                    profileFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                    return true;
            }
            return false;
        });
        scheduleRepeatingAlarm();
        checkNotificationState();
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null)
            notificationManager.deleteNotificationChannel(CHANNEL_ID);

        Uri silentUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.notification); // Make sure you have a silent audio file in your raw folder

        CharSequence name = "طرق العناية الأسبوعية" ;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                name, importance);
        channel.setSound(silentUri, null);
        channel.enableVibration(false);
        channel.setDescription("طرق العناية الملائمة بناء على حالة الطقس في منطقتك");
        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }


    @SuppressLint("MissingPermission")
    public void createNotification(String title, String body) {
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        createNotificationChannel();
        playSound(getApplicationContext());
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_small_logo_photoroom_png_photoroom)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSound(soundUri)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_small_logo_photoroom_png_photoroom))
                .setLights(Color.GREEN, 3000, 3000)
                .setVibrate(new long[] { 0, 500, 100, 500 })
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(123, builder.build());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    public void playSound(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.notification);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mediaPlayer.start();
    }

    private void scheduleRepeatingAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        long startTime = System.currentTimeMillis() + AlarmManager.INTERVAL_DAY;
        long interval = AlarmManager.INTERVAL_DAY * 3;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
        }
    }

    private void checkNotificationState(){
        if(sharedPrefManager.readString("NotificationState", "0").equals("1")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, weeklyWeatherFragment).commit();
            sharedPrefManager.writeString("NotificationState", "3");

        }
        else if(sharedPrefManager.readString("NotificationState", "0").equals("2")){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, pestWarningFragment).commit();
            sharedPrefManager.writeString("NotificationState", "4");
        }
        else if(sharedPrefManager.readString("NotificationState", "0").equals("5")){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, weeklyWeatherFragment).commit();
            sharedPrefManager.writeString("NotificationState", "7");
        }
        else if(sharedPrefManager.readString("NotificationState", "0").equals("6")){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, pestWarningFragment).commit();
            sharedPrefManager.writeString("NotificationState", "7");
        }
    }
}
