package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CropRecordFragment extends Fragment {

    SharedPrefManager sharedPrefManager;

    Button recordButton;

    LinearLayout pestsLayout;

    ArrayList<String> pestsNames;

    ArrayList<String> infectedDates;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop_record, container, false);
        recordButton = view.findViewById(R.id.cropRecord);
        pestsLayout = view.findViewById(R.id.pestsLayout);
        pestsNames = new ArrayList<>();
        infectedDates = new ArrayList<>();
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());

        recordButton.setOnClickListener(v -> {
            SearchDiseaseFragment fragment = new SearchDiseaseFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null); // Optional: adds this transaction to the back stack
            fragmentTransaction.commit();
        });

        getPests();
        return view;
    }


    @SuppressLint("StaticFieldLeak")
    private void getPests() {
        int pestId = Integer.parseInt(sharedPrefManager.readString("CropPest", "noValue"));
        String addUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/pest/all/" + pestId;

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
                            JSONObject object = jsonArray.getJSONObject(i);
                            pestsNames.add(object.getString("typeName"));
                            String fullDate = object.getString("infectedDate");
                            String neededDate = fullDate.substring(8, 10) + "/" + monthToNumber(fullDate.substring(4, 7)) +
                                    "/" + fullDate.substring(20, 24);
                            infectedDates.add(neededDate);
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

    @SuppressLint("DiscouragedApi")
    private void fillLayout(){
        Context context = getActivity();

        for (int i = 0; i < pestsNames.size(); i++) {
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
            innerLayout.setLayoutParams(innerLayoutParams);
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            horizontalLayout.addView(innerLayout);
            int textViewLeftPadding = dpToPx(context, 8);

            TextView pestNameTextView = new TextView(context);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textViewParams.setMargins(0, 20, 0, 0);
            pestNameTextView.setLayoutParams(textViewParams);
            pestNameTextView.setPadding(textViewLeftPadding, 0, 0, 0);
            pestNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            pestNameTextView.setText(pestsNames.get(i));
            pestNameTextView.setTypeface(null, Typeface.BOLD);

            TextView infectedDateTextView = new TextView(context);
            infectedDateTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            infectedDateTextView.setPadding(textViewLeftPadding, 0, 0, 0);
            infectedDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            infectedDateTextView.setText("Recorded in: " + infectedDates.get(i));
            innerLayout.addView(pestNameTextView);
            innerLayout.addView(infectedDateTextView);

            ImageButton imageButton = new ImageButton(context);
            int imageSize = dpToPx(context, 80);
            LinearLayout.LayoutParams imageButtonParams = new LinearLayout.LayoutParams(
                    imageSize,
                    imageSize
            );
            imageButton.setLayoutParams(imageButtonParams);
            String pestPhotoName = pestsNames.get(i).toLowerCase().replaceAll(" ", "_");
            int drawableId = getResources().getIdentifier(pestPhotoName, "drawable", getContext().getPackageName());
            if (drawableId == 0) {
                Toast.makeText(getContext(), "Pest photo doesn't exist", Toast.LENGTH_SHORT).show();
            } else {
                imageButton.setImageResource(drawableId);
            }
            imageButton.setBackgroundColor(Color.TRANSPARENT);
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

            horizontalLayout.addView(imageButton);
            pestsLayout.addView(horizontalLayout, pestsLayout.getChildCount() - 1);
        }
    }


    public static String monthToNumber(String month) {
        switch (month) {
            case "Jan":
                return "01";
            case "Feb":
                return "02";
            case "Mar":
                return "03";
            case "Apr":
                return "04";
            case "May":
                return "05";
            case "Jun":
                return "06";
            case "Jul":
                return "07";
            case "Aug":
                return "08";
            case "Sep":
                return "09";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
            default:
                return "Invalid month";
        }
    }
}