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
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class LandFragment extends Fragment {


    SharedPrefManager sharedPrefManager;

    Button addLandButton;

    LinearLayout landsLayout;

    ArrayList<Long> landsIds;
    ArrayList<String> landsNames;
    ArrayList<Long> landsAreas;
    ArrayList<String> landsLocations;

    private static final String CHANNEL_ID = "دليل العناية الأسبوعي";
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_land, container, false);
        addLandButton = view.findViewById(R.id.addLand);
        landsLayout = view.findViewById(R.id.controlledLandsLayout);
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());
        landsIds = new ArrayList<>();
        landsNames = new ArrayList<>();
        landsAreas = new ArrayList<>();
        landsLocations = new ArrayList<>();
        getLands();
        addLandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddLandDialog();
            }
        });
        checkNotificationState();
        return view;
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
        if (notificationManager != null)
            notificationManager.deleteNotificationChannel(CHANNEL_ID);

        Uri silentUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getActivity().getPackageName() + "/" + R.raw.notification); // Ensure you have a silent audio file in your raw folder

        CharSequence name = "وباء في منطقتك";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setSound(silentUri, null);
        channel.enableVibration(false);
        channel.setDescription("شاهد الأوبئة المنتشرة حالياً في منطقتك");
        notificationManager = getActivity().getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")
    public void createNotification(String title, String body) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        if(sharedPrefManager.readString("NotificationState", "0").equals("0") ||
                sharedPrefManager.readString("NotificationState", "0").equals("4")) {
            sharedPrefManager.writeString("NotificationState", "2");
        }
        else{
            sharedPrefManager.writeString("NotificationState", "6");
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

    private void showAddLandDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_lands, null);
        builder.setView(dialogView);

        EditText editTextLandName = dialogView.findViewById(R.id.editTextLandName);
        EditText editTextLandArea = dialogView.findViewById(R.id.editTextLandArea);
        EditText editTextLocation = dialogView.findViewById(R.id.editTextLocation);
        Button buttonAdd = dialogView.findViewById(R.id.buttonAddLand);

        final AlertDialog dialog = builder.create();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String landName = editTextLandName.getText().toString();
                String landArea = editTextLandArea.getText().toString();
                String location = editTextLocation.getText().toString();
                addLand(landName, landArea, location);
                dialog.dismiss();

            }
        });
        dialog.show();
        reloadLayout();
    }

    @SuppressLint("StaticFieldLeak")
    private void addLand(String landName, String landArea_String, String location) {

        int landArea = Integer.parseInt(landArea_String);
        String email = sharedPrefManager.readString("email", "noValue");
        String addUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/land/add";
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("landName", landName);
            requestData.put("area", landArea);
            requestData.put("location", location);
            requestData.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(addUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);

                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = requestData.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        return "ok";
                    } else {
                        return "Error: " + responseCode;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                reloadLayout();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getLands() {
        String email = sharedPrefManager.readString("email", "noValue");
        String addUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/land/all/" + email;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(addUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        return response.toString();
                    } else {
                        return "Error: " + responseCode;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (!result.startsWith("Error:")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for(int i=0 ; i<jsonArray.length() ; i++){
                            JSONObject land = jsonArray.getJSONObject(i);
                            landsIds.add(land.getLong("landId"));
                            landsNames.add(land.getString("landName"));
                            landsAreas.add(land.getLong("area"));
                            landsLocations.add(land.getString("location"));
                        }
                        fillLayout();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("HTTP Error", result);
                }
            }
        }.execute();
    }

    private int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void fillLayout(){
        Context context = getActivity();

        for (int i = 0; i < landsNames.size(); i++) {
            LinearLayout horizontalLayout = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setLayoutParams(layoutParams);
            horizontalLayout.setBackgroundColor(Color.parseColor("#DCD9D9"));

            LinearLayout innerLayout = new LinearLayout(context);
            LinearLayout.LayoutParams innerLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            innerLayoutParams.setMargins(0, 0, 0, 0);
            innerLayout.setLayoutParams(innerLayoutParams);
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            horizontalLayout.addView(innerLayout);
            int textViewLeftPadding = dpToPx(context, 8);

            TextView landNameTextView = new TextView(context);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textViewParams.setMargins(0, 20, 0, 0);
            landNameTextView.setLayoutParams(textViewParams);
            landNameTextView.setPadding(textViewLeftPadding, 0, 0, 0);
            landNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            landNameTextView.setText(landsNames.get(i));
            landNameTextView.setTypeface(null, Typeface.BOLD);

            TextView landAreaTextView = new TextView(context);
            landAreaTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            landAreaTextView.setPadding(textViewLeftPadding, 0, 0, 0);
            landAreaTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            landAreaTextView.setText(landsAreas.get(i) + " دونم");

            TextView landLocationTextView = new TextView(context);
            landLocationTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            landLocationTextView.setPadding(textViewLeftPadding, 0, 0, 0);
            landLocationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            landLocationTextView.setText(landsLocations.get(i));
            innerLayout.addView(landNameTextView);
            innerLayout.addView(landAreaTextView);
            innerLayout.addView(landLocationTextView);
            ImageButton imageButton = new ImageButton(context);
            LinearLayout.LayoutParams imageButtonParams = new LinearLayout.LayoutParams(
                    dpToPx(context, 173),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            imageButton.setLayoutParams(imageButtonParams);
            imageButton.setImageResource(R.drawable.land);
            imageButton.setBackgroundColor(Color.TRANSPARENT);
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            int finalI = i;
            imageButton.setOnClickListener(v -> {
                sharedPrefManager.writeString("landId", landsIds.get(finalI).toString());
                sharedPrefManager.writeString("landName", landsNames.get(finalI));
                landPage();
            });

            horizontalLayout.addView(imageButton);
            landsLayout.addView(horizontalLayout, landsLayout.getChildCount() - 1);
        }
    }

    private void reloadLayout(){
        LandFragment landFragment = new LandFragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, landFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void landPage(){
        LandCropsFragment newFragment = new LandCropsFragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, newFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void checkNotificationState() {
        if (sharedPrefManager.readString("NotificationState", "0").equals("0")) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    createNotification("وباء في منطقتك", "شاهد الأوبئة المنتشرة حالياً في منطقتك");

                }
            }, 2000);
            sharedPrefManager.writeString("NotificationState", "4");

        } else if (sharedPrefManager.readString("NotificationState", "0").equals("1")) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    createNotification("وباء في منطقتك", "شاهد الأوبئة المنتشرة حالياً في منطقتك");

                }
            }, 2000);
            sharedPrefManager.writeString("NotificationState", "7");
        } else if (sharedPrefManager.readString("NotificationState", "3").equals("3")) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    createNotification("وباء في منطقتك", "شاهد الأوبئة المنتشرة حالياً في منطقتك");

                }
            }, 2000);
            sharedPrefManager.writeString("NotificationState", "7");
        }
    }

}