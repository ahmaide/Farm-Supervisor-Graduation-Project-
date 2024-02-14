package com.example.graduationproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.graduationproject.Domains.WeatherAdapter;
import com.example.graduationproject.Domains.WeatherModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WeatherFragment extends Fragment {
    private WeatherAdapter weatherAdapter;
    private RecyclerView recyclerView;
    private ArrayList<WeatherModel> items;
    private TextView cityName, weatherStatus, temperature, timeDate, rainPer, windSpeed, humidity;
    private ImageView wStatus, wRecommendations;
    View view;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather, container, false);

//        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        String city;
        cityName = view.findViewById(R.id.tv_cityName);
        weatherStatus = view.findViewById(R.id.tv_weatherStatus);
        temperature = view.findViewById(R.id.temperature);
        timeDate = view.findViewById(R.id.tv_timeDate);
        rainPer = view.findViewById(R.id.tv_rain);
        windSpeed = view.findViewById(R.id.tv_windSpeed);
        humidity = view.findViewById(R.id.tv_humidity);
        recyclerView = view.findViewById(R.id.nextDays);
        wStatus = view.findViewById(R.id.iv_wStatus);
        wRecommendations = view.findViewById(R.id.iv_weatherRecommendations);
        items = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(items, this.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(weatherAdapter);
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           city = getCityName(MainActivity.location.getLongitude(), MainActivity.location.getLatitude(), "OK");

        } else {
//            givenPerm = "not allowed";
            city = getCityName(0, 0, "not allowed");
        }
        cityName.setText(city);
        getWeatherInfo(city);

        wRecommendations.setOnClickListener(v -> {
            WeeklyWeather weeklyWeather = new WeeklyWeather();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, weeklyWeather);
            fragmentTransaction.addToBackStack(null); // Optional: adds this transaction to the back stack
            fragmentTransaction.commit();}
        );

        return view;
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
//                    else {
//                        //Log.d("TAG", "المدينة غير موجودة");
//                        //Toast.makeText(getContext(), "المدينة غير موجودة..", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {
        if(Objects.equals(cityName, "Not found")){
            return;
        }
        String url = "http://api.weatherapi.com/v1/forecast.json?key=5eef2f8720f24fd2b92213527231811&q=" + cityName + "&days=1&aqi=no&alerts=no";
//        String url = "http://api.weatherapi.com/v1/forecast.json?key=5eef2f8720f24fd2b92213527231811&q=London&days=1&aqi=no&alerts=no";

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"}) JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            items.clear();
            try {
                String date = response.getJSONObject("location").getString("localtime");
                String temp = response.getJSONObject("current").getString("temp_c");
                temperature.setText(temp + " °س");
//                    int isDay = response.getJSONObject("current").getInt("is_day");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("dd");
//                String day, month, dayName;
                Date date1 = input.parse(date);
                @SuppressLint("SimpleDateFormat") DateFormat d = new SimpleDateFormat("EEEE");
                @SuppressLint("SimpleDateFormat") DateFormat m = new SimpleDateFormat("MMMM");
                assert date1 != null;
                String timeDate1 = d.format(date1) + " | " + output.format(date1) + " " + m.format(date1);
                timeDate.setText(timeDate1);

                String cond = response.getJSONObject("current").getJSONObject("condition").getString("text");
                System.out.println(cond);
                String icon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                weatherStatus.setText(cond);
                Picasso.get().load("http:".concat(icon)).into(wStatus);
                String windS = response.getJSONObject("current").getString("wind_kph");
                windSpeed.setText(windS + " كم/س");

                String humid = response.getJSONObject("current").getString("humidity");
                humidity.setText(humid+ "%");

                JSONObject forecastObj = response.getJSONObject("forecast");
                JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                JSONObject dayArray = forecastO.getJSONObject("day");
                String rainP = dayArray.getString("daily_chance_of_rain");
                rainPer.setText(rainP + "%");
                JSONArray hourArray = forecastO.getJSONArray("hour");

                for(int i=0; i<hourArray.length(); i++) {
                    JSONObject hourObj = hourArray.getJSONObject(i);
                    String time = hourObj.getString("time");
                    String temp2 = hourObj.getString("temp_c");
                    String img = hourObj.getJSONObject("condition").getString("icon");
                    String wind = hourObj.getString("wind_kph");
                    items.add(new WeatherModel(time, temp2, img, wind));
                }
                weatherAdapter.notifyDataSetChanged();

            } catch (JSONException | ParseException e) {
                throw new RuntimeException(e);
            }
        }, error -> Toast.makeText(getContext(), "رجاءً أدخل اسماً صحيحاً للمدينة", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }
}