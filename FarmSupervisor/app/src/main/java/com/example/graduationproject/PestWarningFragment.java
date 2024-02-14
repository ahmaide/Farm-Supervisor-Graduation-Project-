package com.example.graduationproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class PestWarningFragment extends Fragment {

    private ProgressBar progressBar;


    LinearLayout main_layout;

    ArrayList<String> pests;

    ArrayList<Integer> cases;

    ArrayList<Integer> ids;

    public PestWarningFragment() {

    }


    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pest_warning, container, false);
        main_layout = view.findViewById(R.id.main_layoutPC);
        progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        main_layout.setVisibility(View.GONE);
        String city;
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            city = getCityName(MainActivity.location.getLongitude(), MainActivity.location.getLatitude(), "OK");
        } else {
            city = getCityName(0, 0, "not allowed");
        }
        getAreaPests(city);
        return view;
    }

    private void getAreaPests(String city) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8000/api/location_pests/" + city + "/";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                final String responseBody = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);
                            pests = new ArrayList<>();
                            cases = new ArrayList<>();
                            ids = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject elements = jsonArray.getJSONObject(i);
                                pests.add(elements.getString("pest_name"));
                                cases.add(elements.getInt("cases"));
                                ids.add(elements.getInt("pest_id"));
                            }
                            progressBar.setVisibility(View.GONE);
                            main_layout.setVisibility(View.VISIBLE);
                            addPests();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void addPests(){
        for (int i = 0; i < pests.size(); i++) {
            LinearLayout horizontalLayout = new LinearLayout(getContext());
            horizontalLayout.setBackgroundColor(Color.WHITE);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 20;
            horizontalLayout.setLayoutParams(layoutParams);
            horizontalLayout.setPadding(10, 10, 10, 10);

            ImageView imageView = new ImageView(getContext());
            String fileName = pests.get(i).toLowerCase().replaceAll(" ", "_");
            int resourceId = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());
            imageView.setImageResource(resourceId);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(300, 300);
            imageParams.rightMargin = 20;
            imageView.setLayoutParams(imageParams);
            horizontalLayout.addView(imageView);
            LinearLayout textLayout = new LinearLayout(getContext());
            textLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textLayout.setLayoutParams(textLayoutParams);

            TextView pestNameView = new TextView(getContext());
            pestNameView.setText(pests.get(i));
            pestNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            pestNameView.setTextColor(Color.parseColor("#00897B"));
            pestNameView.setTypeface(null, Typeface.BOLD);
            textLayout.addView(pestNameView);

            TextView casesView = new TextView(getContext());
            casesView.setText(cases.get(i) + " :" + "الحالات");
            casesView.setGravity(Gravity.LEFT);
            casesView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            casesView.setTextColor(Color.parseColor("#00897B"));
            casesView.setTypeface(null, Typeface.BOLD);
            textLayout.addView(casesView);
            horizontalLayout.addView(textLayout);
            main_layout.addView(horizontalLayout);
        }
    }



    private String getCityName(double longitude, double latitude, String status) {
        String cityName = "Not found";
        if(Objects.equals(status, "not allowed"))
            return cityName;
        Geocoder gcd = new Geocoder(requireActivity().getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for(Address add: addresses){
                if(add != null){
                    String city = add.getLocality();
                    if(city != null && !city.equals("")){
                        cityName = city;
                    }
                    else {
                        Log.d("TAG", "المدينة غير موجودة");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cityName;
    }
}