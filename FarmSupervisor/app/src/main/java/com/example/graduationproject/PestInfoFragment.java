package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PestInfoFragment extends Fragment {

    String pestName,  encodedPestName;

    SharedPrefManager sharedPrefManager;

    ImageView pestPhoto;

    int pestId;

    Button addPest;

    ScrollView scrollView1;

    ScrollView scrollView2;

    ProgressBar progressBar;

    TextView tv_pestName, tv_pestInfo, tv_stMonth, tv_endMonth, tv_pestType, tv_pestCure, tv_pestCaringMethods;

    @SuppressLint({"DiscouragedApi", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pest_info, container, false);
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());
        Bundle arguments = getArguments();
        pestName = arguments.getString("pest name");
        try {
            encodedPestName = URLEncoder.encode(pestName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        scrollView1 = view.findViewById(R.id.pestInfoScroll);
        scrollView2 = view.findViewById(R.id.progressView);
        progressBar = view.findViewById(R.id.pestInfoBar);
        addPest = view.findViewById(R.id.addPestInfo);
        pestPhoto = view.findViewById(R.id.iv_pest);
        tv_pestName = view.findViewById(R.id.tv_pestName);
        tv_pestInfo = view.findViewById(R.id.tv_pestInfo);
        tv_stMonth = view.findViewById(R.id.tv_stMonth);
        tv_endMonth = view.findViewById(R.id.tv_endMonth);
        tv_pestType = view.findViewById(R.id.tv_pestType);
        tv_pestCure = view.findViewById(R.id.tv_pestCure);
        tv_pestCaringMethods = view.findViewById(R.id.tv_pestCaringMethods);
        hide();

        if(sharedPrefManager.readString("CropPest", "None").equals("None"))
            addPest.setVisibility(View.GONE);
        else
            addPest.setVisibility(View.VISIBLE);

        String pestPhotoName = pestName.toLowerCase().replaceAll(" ", "_");
//        pestPhotoName += ".jpg";

        @SuppressLint("DiscouragedApi") int drawableId = getResources().getIdentifier(pestPhotoName, "drawable", getContext().getPackageName());
        if (drawableId == 0) {
//            pestPhotoName = pestPhotoName.replace("jpg", "png");
            Toast.makeText(getContext(), "Pest photo doesn't exist", Toast.LENGTH_SHORT).show();
        }else {
            pestPhoto.setImageResource(getResources().getIdentifier(pestPhotoName, "drawable", getContext().getPackageName()));
            Log.d("pestPhotoName", pestPhotoName);
        }

        addPest.setOnClickListener(v -> {
            addPestToCrop();
//            try {
//                addPestToFarmer();
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
            addPest.setVisibility(View.GONE);
        });

        fillPestInfo();

        return view;
    }

    private void fillPestInfo(){
        String url = "http://10.0.2.2:8000/api/pests/" + pestName;
        Log.d("url", url);
        StringBuilder cure = new StringBuilder();
        StringBuilder caringMethods = new StringBuilder();

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"}) JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {

//                JSONArray responseArray = response.getJSONArray("");
                JSONObject pestData = response.getJSONObject("pest_info");
                pestId = pestData.getInt("id");
                String name = pestData.getString("name");
                String info = pestData.getString("pestInfo");
                String sMonth = pestData.getString("startMonth");
                String eMonth = pestData.getString("endMonth");
                String type = pestData.getString("pestType");
                JSONArray curesArray = pestData.optJSONArray("cures");
                JSONArray caringMethodsArray = pestData.optJSONArray("caringMethods");

                Log.d("pest name", name);
                showAll();
                tv_pestName.setText(name);
                tv_pestInfo.setText(info);
                tv_stMonth.setText(sMonth);
                tv_endMonth.setText(eMonth);
                tv_pestType.setText(type);
                for (int i = 0; i < curesArray.length(); i++) {

                    JSONObject cureObject = curesArray.getJSONObject(i);
                    cure.append(cureObject.getString("name"));

                    if (i < curesArray.length() - 1) {
                        cure.append("\n");
                    }
                }
                tv_pestCure.setText(cure);

                for (int i = 0; i < caringMethodsArray.length(); i++) {

                    JSONObject cureObject = caringMethodsArray.getJSONObject(i);
                    caringMethods.append(cureObject.getString("name"));

                    if (i < curesArray.length() - 1) {
                        caringMethods.append("\n");
                    }
                }
                tv_pestCaringMethods.setText(cure);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> Toast.makeText(getContext(), "Can't get this pest info", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }

    @SuppressLint("StaticFieldLeak")
    private void addPestToCrop(){
        int cropId = Integer.parseInt(sharedPrefManager.readString("CropPest", "noValue"));
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
        String formattedDate = formatter.format(currentDate);
        String addUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/pest/add";
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("cropId", cropId);
            requestData.put("infectedDate", formattedDate);
            requestData.put("pestType", "pest");
            requestData.put("typeId", pestId);
            requestData.put("typeName", pestName);
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

            }
        }.execute();
    }


    private void addPestToFarmer() throws JSONException {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8000/api/pest_warning/";
        JSONObject json = new JSONObject();
        json.put("user", sharedPrefManager.readString("email", "noValue"));
        json.put("pest", pestId);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json.toString(), mediaType);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Log.e("HTTP Request Failure", "Request failed to send", e);
            }
        });

    }

    private void hide(){
        scrollView2.setVisibility(View.VISIBLE);
        scrollView1.setVisibility(View.GONE);
    }

    private void showAll(){
        scrollView1.setVisibility(View.VISIBLE);
        scrollView2.setVisibility(View.GONE);
    }
}