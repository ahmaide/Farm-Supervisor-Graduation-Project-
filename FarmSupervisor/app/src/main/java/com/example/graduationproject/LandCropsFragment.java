package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class LandCropsFragment extends Fragment {


    SharedPrefManager sharedPrefManager;

    Button addCrops;

    ArrayList<Long> cropsIds;
    ArrayList<String> cropsNames;
    ArrayList<Integer> cropsNums;
    ArrayList<String> cropsDates;

    LinearLayout cropsLayout;

    TextView landName;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_land_crops, container, false);
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());
        cropsLayout = view.findViewById(R.id.cropsLayout);
        addCrops = view.findViewById(R.id.addCrop);
        landName = view.findViewById(R.id.landName);
        cropsIds = new ArrayList<>();
        cropsNames = new ArrayList<>();
        cropsNums = new ArrayList<>();
        cropsDates = new ArrayList<>();
        landName.setText(sharedPrefManager.readString("landName", "No Name"));
        getCrops();
        addCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCropDialog();
            }
        });
        return view;
    }

    @SuppressLint("MissingInflatedId")
    private void showAddCropDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_crops, null);
        builder.setView(dialogView);

        EditText editTextCropName = dialogView.findViewById(R.id.editTextCropName);
        EditText editTextCropNum = dialogView.findViewById(R.id.editTextCropNum);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        Button buttonAdd = dialogView.findViewById(R.id.buttonCropAddition);
        final String[] date_String = new String[1];
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                editTextDate.setText(selectedDate);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                try {
                                    Date date = dateFormat.parse(selectedDate);
                                    date_String[0] = date.toString();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        final AlertDialog dialog = builder.create();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cropName = editTextCropName.getText().toString();
                String cropNums = editTextCropNum.getText().toString();
                String part1 = date_String[0].substring(0, 20);
                String part2 =date_String[0].substring(30, 34);
                String finalDateString = part1 + part2;
                addCrop(cropName, cropNums, finalDateString);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @SuppressLint("StaticFieldLeak")
    private void addCrop(String cropName, String cropNum_String, String date) {

        int cropNum = Integer.parseInt(cropNum_String);
        int landId = Integer.parseInt(sharedPrefManager.readString("landId", "noValue"));
        String addUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/crop/add";
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("landId", landId);
            requestData.put("cropName", cropName);
            requestData.put("plantsNum", cropNum);
            requestData.put("plantedDate", date);
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
    private void getCrops() {
        int landId = Integer.parseInt(sharedPrefManager.readString("landId", "noValue"));
        String addUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/crop/all/" + landId;

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
                            cropsIds.add(land.getLong("cropId"));
                            cropsNames.add(land.getString("cropName"));
                            cropsNums.add(land.getInt("plantsNum"));
                            String fullDate = land.getString("plantedDate");
                            String neededDate = monthToNumber(fullDate.substring(4, 7)) + "/" + fullDate.substring(20, 24);
                            cropsDates.add(neededDate);
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

        for (int i = 0; i < cropsNames.size(); i++) {
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

            TextView cropNameTextView = new TextView(context);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textViewParams.setMargins(0, 20, 0, 0);
            cropNameTextView.setLayoutParams(textViewParams);
            cropNameTextView.setPadding(textViewLeftPadding, 0, 0, 0);
            cropNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            cropNameTextView.setText(cropsNames.get(i));
            cropNameTextView.setTypeface(null, Typeface.BOLD);

            TextView cropNumTextView = new TextView(context);
            cropNumTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            cropNumTextView.setPadding(textViewLeftPadding, 0, 0, 0);
            cropNumTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            cropNumTextView.setText("عدد النبتات: " + cropsNums.get(i));


            TextView dateTextView = new TextView(context);
            dateTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            dateTextView.setPadding(textViewLeftPadding, 0, 0, 0);
            dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            dateTextView.setText("تاريخ الزراعة: " + cropsDates.get(i));
            innerLayout.addView(cropNameTextView);
            innerLayout.addView(cropNumTextView);
            innerLayout.addView(dateTextView);
            ImageButton imageButton = new ImageButton(context);
            LinearLayout.LayoutParams imageButtonParams = new LinearLayout.LayoutParams(
                    dpToPx(context, 173),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            imageButton.setLayoutParams(imageButtonParams);
            imageButton.setImageResource(R.drawable.avocado);
            imageButton.setBackgroundColor(Color.TRANSPARENT);
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            int finalI = i;
            imageButton.setOnClickListener(v -> {
                sharedPrefManager.writeString("CropPest", cropsIds.get(finalI).toString());
                recordPage();
            });

            horizontalLayout.addView(imageButton);
            cropsLayout.addView(horizontalLayout, cropsLayout.getChildCount() - 1);
        }
    }

    private void reloadLayout(){
        LandCropsFragment fragment = new LandCropsFragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void recordPage(){
        CropRecordFragment fragment = new CropRecordFragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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