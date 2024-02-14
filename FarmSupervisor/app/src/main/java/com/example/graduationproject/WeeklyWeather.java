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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class WeeklyWeather extends Fragment {

    private ProgressBar progressBar;

    private String weatherCondition;

    private int index;

    private String imageLink;

    private ArrayList<String> caringMethods;

    private ArrayList<ArrayList<String>> tools;

    LinearLayout main_layout;

    ImageView image;

    TextView main_text;

    public WeeklyWeather() {
    }

    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_weather, container, false);
        main_layout = view.findViewById(R.id.main_layoutWC);
        image = view.findViewById(R.id.imageWC);
        main_text = view.findViewById(R.id.titleWC);
        main_layout.setVisibility(View.GONE);
        String city;
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            city = getCityName(MainActivity.location.getLongitude(), MainActivity.location.getLatitude(), "OK");
        } else {
            city = getCityName(0, 0, "not allowed");
        }


        getWeatherInfo(city);

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

    private void getWeatherInfo(String cityName) {
        if(Objects.equals(cityName, "Not found")){
            return;
        }
        String url = "http://api.weatherapi.com/v1/forecast.json?key=5eef2f8720f24fd2b92213527231811&q=" + cityName + "&days=7&aqi=no&alerts=no";

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"}) JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            JSONObject outcome = findMostSensitiveWeather(response);
            try {
                this.weatherCondition = languageMap(outcome.getString("condition"));
                this.index = outcome.getInt("index");
                this.imageLink = outcome.getString("icon");
                getCaringMethods();

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> Toast.makeText(getContext(), "رجاءً أدخل اسماً صحيحاً للمدينة", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }

    private void getCaringMethods() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8000/api/weatherWarning/" + this.index + "/";
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
                            ArrayList<String> descriptions = new ArrayList<>();
                            ArrayList<ArrayList<String>> toolsList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject caringMethod = jsonArray.getJSONObject(i);
                                descriptions.add(caringMethod.getString("description"));

                                JSONArray toolsArray = caringMethod.getJSONArray("tools");
                                ArrayList<String> toolsNames = new ArrayList<>();
                                for (int j = 0; j < toolsArray.length(); j++) {
                                    JSONObject tool = toolsArray.getJSONObject(j);
                                    toolsNames.add(tool.getString("name"));
                                }
                                toolsList.add(toolsNames);
                            }
                            caringMethods = descriptions;
                            tools = toolsList;
                            progressBar.setVisibility(View.GONE);
                            main_layout.setVisibility(View.VISIBLE);
                            Picasso.get().load("http:".concat(imageLink)).into(image);
                            main_text.setText("أسبوع" + "\n" + weatherCondition );
                            displayMethods();
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



    public JSONObject findMostSensitiveWeather(JSONObject response) {
        try {
            // Ranking of weather conditions
            Map<String, Integer> conditionRanking = new HashMap<>();
            conditionRanking.put("clear", 1);
            conditionRanking.put("sunny", 1); // Will be re-evaluated for temperature > 32°C
            conditionRanking.put("cloudy", 2);
            conditionRanking.put("overcast", 2);
            conditionRanking.put("drizzle", 2);
            conditionRanking.put("heat", 3);
            conditionRanking.put("snow", 4);
            conditionRanking.put("rain", 4);
            conditionRanking.put("sleet", 4);
            conditionRanking.put("fog", 4);
            conditionRanking.put("blizzard", 4);

            JSONArray hoursArray = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour");
            JSONObject mostSensitive = null;
            int maxRank = 0;

            for (int i = 0; i < hoursArray.length(); i++) {
                JSONObject hour = hoursArray.getJSONObject(i);
                String conditionText = hour.getJSONObject("condition").getString("text");
                conditionText = conditionText.toLowerCase();
                double tempC = hour.getDouble("temp_c");
                int rank =0;
                for (Map.Entry<String, Integer> entry : conditionRanking.entrySet()) {
                    String condition = entry.getKey();
                    if(conditionText.contains(condition)){
                        rank = entry.getValue();
                        conditionText = condition;
                        break;
                    }
                }
                int tempCInt = (int) tempC;


                if ("Sunny".equals(conditionText) && tempC > 32) {
                    rank = 3;
                }

                if (rank > maxRank) {
                    maxRank = rank;
                    mostSensitive = new JSONObject();
                    mostSensitive.put("condition", conditionText);
                    mostSensitive.put("index", maxRank);
                    mostSensitive.put("icon", hour.getJSONObject("condition").getString("icon"));
                }
            }

            return mostSensitive;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayMethods(){
        for (int i = 0; i < 4; i++) {
            TextView textView = new TextView(getContext());
            TextView textView2 = new TextView(getContext());
            String tools_text = "";
            if(tools.get(i).size() > 0) {
                tools_text = "Tools: [";
                for (int j = 0; j < tools.get(i).size(); j++) {
                    tools_text = tools_text + tools.get(i).get(j);
                    if (j != tools.get(i).size() - 1)
                        tools_text += ", ";
                }
                tools_text += "]";
            }
            textView.setText((i+1) + "- " + caringMethods.get(i));
            textView2.setText(tools_text);
            textView.setTextSize(19);
            textView.setTypeface(null, Typeface.BOLD);
            textView2.setTextSize(16);
            textView.setPadding(8, 0, 0, 0);
            textView2.setPadding(8, 0, 0, 18);
            textView.setTextColor(Color.parseColor("#00897B"));
            textView2.setTextColor(Color.parseColor("#00897B"));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textView.setLayoutParams(layoutParams);
            textView2.setLayoutParams(layoutParams);
            main_layout.addView(textView);
            main_layout.addView(textView2);
        }
    }

    public static String languageMap(String weatherEnglish){
        switch (weatherEnglish){
            case "clear":
                return "صافي";
            case "sunny":
                return "مشمس";
            case "cloudy":
                return "غائم";
            case "overcast":
                return "غائم";
            case "drizzle":
                return "غائم مع رذاذ";
            case "rain":
                return "ممطر";
            case "sleet":
                return "ممطر";
            case "fog":
                return "ضبابي";
            case "snow":
                return "مثلج";
            case "blizzard":
                return "عواصف ثلجية";
            default:
                return "صافي";
        }

    }


}
