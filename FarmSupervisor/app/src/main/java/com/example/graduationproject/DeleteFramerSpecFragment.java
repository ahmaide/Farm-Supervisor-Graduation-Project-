package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DeleteFramerSpecFragment extends Fragment {
    Button deleteSpec;
    SharedPrefManager sharedPrefManager;
    LinearLayout farmerSpecLayout;
    ArrayList<String> farmerSpec;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete_framer_spec, container, false);

        deleteSpec = view.findViewById(R.id.deleteSpec);
        farmerSpecLayout = view.findViewById(R.id.farmerSpecLay2);
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());
        farmerSpec = new ArrayList<>();

        addFramerSpec();
        deleteSpec.setOnClickListener(v -> Toast.makeText(getContext(), "The button is not ready yet!", Toast.LENGTH_SHORT).show());

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void addFramerSpec() {
        String email = sharedPrefManager.readString("email", "noValue");
        String addUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/specialization/farmer/" + email;

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
                        if (farmerSpec != null && !farmerSpec.isEmpty()) {
                            farmerSpec.clear();
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject spec = jsonArray.getJSONObject(i);
                            farmerSpec.add(spec.getString("type"));
                        }
                        if (farmerSpec != null && !farmerSpec.isEmpty()) {
                            Log.d("farmerSpec", Integer.toString(farmerSpec.size()));
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

//    private int dpToPx(Context context) {
//        float density = context.getResources().getDisplayMetrics().density;
//        return Math.round((float) 8 * density);
//    }

    @SuppressLint("SetTextI18n")
    private void fillLayout() {
        Context context = requireContext(); // Use requireContext() for safer access

        farmerSpecLayout.removeAllViews(); // Clear existing views before repopulating

        for (String service : farmerSpec) {
            LinearLayout horizontalLayout = new LinearLayout(context);
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    700,
                    125
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

            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            horizontalLayout.addView(innerLayout);

            TextView serviceTextView = new TextView(context);
            serviceTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            serviceTextView.setText(service);
            serviceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // Set text size
            serviceTextView.setTypeface(null, Typeface.BOLD);
//            serviceTextView.setPadding(16, 8, 16, 8);
//            serviceTextView.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams params = serviceTextView.getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }
            ((ViewGroup.MarginLayoutParams) params).setMargins(
                    50, // in pixels
                    20,
                    50,
                    20
            );
            serviceTextView.setLayoutParams(params);
//            innerLayoutParams.setMargins(15, 0, 15, 0);
            innerLayout.addView(serviceTextView);

            TextView serviceNameTextView = new TextView(context);
            serviceNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            serviceNameTextView.setText("اسم الخدمة:");
            serviceNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//            serviceNameTextView.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams params2 = serviceNameTextView.getLayoutParams();
            if (params2 == null) {
                params2 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }
            ((ViewGroup.MarginLayoutParams) params2).setMargins(
                    50, // in pixels
                    20,
                    50,
                    20
            );
            serviceNameTextView.setLayoutParams(params2);
//            serviceNameTextView.setTypeface(null, Typeface.BOLD);
            innerLayout.addView(serviceNameTextView);

            farmerSpecLayout.addView(horizontalLayout); // Add to the main layout
        }
    }

}