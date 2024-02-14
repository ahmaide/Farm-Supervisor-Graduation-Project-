package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TillageFarmersFragment extends Fragment {
    LinearLayout farmersLayout;
    SharedPrefManager sharedPrefManager;
    ArrayList<String> farmersEmails, farmersPhones, farmersFirstNames, farmersLastNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tillage_farmers, container, false);
        farmersLayout = view.findViewById(R.id.tillageFarmersLayout);
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());
        farmersEmails = new ArrayList<>();
        farmersPhones = new ArrayList<>();
        farmersFirstNames = new ArrayList<>();
        farmersLastNames = new ArrayList<>();

        addTillageFarmers();

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void addTillageFarmers() {
        String email = sharedPrefManager.readString("email", "noValue");
        String addUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/specialization/typeFarmers/tillage";

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
                        if (farmersEmails != null && !farmersEmails.isEmpty()) {
                            farmersPhones.clear();
                            farmersEmails.clear();
                            farmersFirstNames.clear();
                            farmersLastNames.clear();
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject spec = jsonArray.getJSONObject(i);
                            farmersPhones.add(spec.getString("mobileNumber"));
                            farmersEmails.add(spec.getString("email"));
                            farmersFirstNames.add(spec.getString("firstName"));
                            farmersLastNames.add(spec.getString("lastName"));
                        }
                        if (farmersEmails != null && !farmersEmails.isEmpty()) {
                            fillLayout();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("HTTP Error", result);
                    // Handle API call failure appropriately (e.g., show an error message)
                }
            }
        }.execute();
    }

    private int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @SuppressLint("SetTextI18n")
    private void fillLayout() {
        Context context = requireContext();

        farmersLayout.removeAllViews();

        for (int i = 0; i < farmersEmails.size(); i++) {
            LinearLayout horizontalLayout = new LinearLayout(context);
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setBackgroundColor(Color.parseColor("#DCD9D9"));
            horizontalLayout.setGravity(Gravity.CENTER_HORIZONTAL);
//            horizontalLayout.setPadding(8, 0, 0, 0);

            LinearLayout innerLayout = new LinearLayout(context);
            LinearLayout.LayoutParams innerLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
//            innerLayoutParams.setMargins(5, 5, 5, 5);

            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            horizontalLayout.addView(innerLayout);
            int textViewLeftPadding = dpToPx(context, 8);

            TextView farmerEmailTextView = new TextView(context);
            farmerEmailTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            farmerEmailTextView.setText(farmersFirstNames.get(i) + " " + farmersLastNames.get(i));
            farmerEmailTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            farmerEmailTextView.setTypeface(null, Typeface.BOLD);
//            serviceTextView.setPadding(16, 8, 16, 8);
//            serviceTextView.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams params = farmerEmailTextView.getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
            }
            farmerEmailTextView.setPadding(textViewLeftPadding, 0, 0, 0);
//            ((ViewGroup.MarginLayoutParams) params).setMargins(
//                    50, // in pixels
//                    20,
//                    50,
//                    20
//            );
            farmerEmailTextView.setLayoutParams(params);
//            innerLayoutParams.setMargins(15, 0, 15, 0);
            innerLayout.addView(farmerEmailTextView);

            TextView farmerPhoneTextView = new TextView(context);
            farmerPhoneTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            farmerPhoneTextView.setPadding(textViewLeftPadding, 0, 0, 0);
            farmerPhoneTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            farmerPhoneTextView.setText(farmersPhones.get(i));
            innerLayout.addView(farmerPhoneTextView);
            ImageButton imageButton = new ImageButton(context);
            LinearLayout.LayoutParams imageButtonParams = new LinearLayout.LayoutParams(
                    dpToPx(context, 173),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            imageButton.setLayoutParams(imageButtonParams);
            imageButton.setImageResource(R.drawable.tractor2);
            imageButton.setBackgroundColor(Color.TRANSPARENT);
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            horizontalLayout.addView(imageButton);

            farmersLayout.addView(horizontalLayout, farmersLayout.getChildCount() - 1); // Add to the main layout
        }
    }


}