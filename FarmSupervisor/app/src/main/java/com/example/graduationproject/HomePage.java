package com.example.graduationproject;


import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomePage extends Fragment {

    LinearLayout addCrop;

    ImageView weather, cropCaring, communication;
//    addCropIcon, compareAreas, wishlist;

    SharedPrefManager sharedPrefManager;

    private static final String CHANNEL_ID = "دليل العناية الأسبوعي";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());
        addCrop = view.findViewById(R.id.add_crop_layout);
        weather = view.findViewById(R.id.weather);
        cropCaring = view.findViewById(R.id.crops_caring);
        communication = view.findViewById(R.id.communication);

        weather.setOnClickListener(v -> {
            WeatherFragment weatherFragment = new WeatherFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, weatherFragment);
            fragmentTransaction.addToBackStack(null); // Optional: adds this transaction to the back stack
            fragmentTransaction.commit();
        });

        cropCaring.setOnClickListener(v -> {
            CropCaringFragment cropCaringFragment = new CropCaringFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, cropCaringFragment);
            fragmentTransaction.addToBackStack(null); // Optional: adds this transaction to the back stack
            fragmentTransaction.commit();
        });

        communication.setOnClickListener(v -> {
            CommunicationFragment communicationFragment = new CommunicationFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, communicationFragment);
            fragmentTransaction.addToBackStack(null); // Optional: adds this transaction to the back stack
            fragmentTransaction.commit();
        });

        checkNotificationState();
        sharedPrefManager.writeString("CropPest", "None");
        return view;
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
        if (notificationManager != null)
            notificationManager.deleteNotificationChannel(CHANNEL_ID);

        Uri silentUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getActivity().getPackageName() + "/" + R.raw.notification); // Ensure you have a silent audio file in your raw folder

        CharSequence name = "طرق العناية الأسبوعية";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setSound(silentUri, null);
        channel.enableVibration(false);
        channel.setDescription("أفضل طرق العناية بناء على حالة الطقس في منطقتك");
        notificationManager = getActivity().getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")
    public void createNotification(String title, String body) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        if(sharedPrefManager.readString("NotificationState", "0").equals("0") ||
                sharedPrefManager.readString("NotificationState", "0").equals("3")) {
            sharedPrefManager.writeString("NotificationState", "1");
        }
        else{
            sharedPrefManager.writeString("NotificationState", "5");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_MUTABLE);
        createNotificationChannel();
        playSound(getActivity());
        Uri soundUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                .setSmallIcon(R.drawable.app_small_logo_photoroom_png_photoroom)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSound(soundUri)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_small_logo_photoroom_png_photoroom))
                .setLights(Color.GREEN, 3000, 3000)
                .setVibrate(new long[]{0, 500, 100, 500})
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        notificationManager.notify(123, builder.build());
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

    private void checkNotificationState(){
        if(sharedPrefManager.readString("NotificationState", "0").equals("0")) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    createNotification("طرق العناية الأسبوعية","طرق العناية الملائمة بناء على حالة الطقس في منطقتك");;

                }
            }, 2000);
            sharedPrefManager.writeString("NotificationState", "3");

        }
        else if(sharedPrefManager.readString("NotificationState", "0").equals("2")){
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    createNotification("طرق العناية الأسبوعية","طرق العناية الملائمة بناء على حالة الطقس في منطقتك");;

                }
            }, 2000);
            sharedPrefManager.writeString("NotificationState", "7");
        }
        else if(sharedPrefManager.readString("NotificationState", "0").equals("4")){
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    createNotification("طرق العناية الأسبوعية","طرق العناية الملائمة بناء على حالة الطقس في منطقتك");;

                }
            }, 2000);
            sharedPrefManager.writeString("NotificationState", "7");
        }
    }

}