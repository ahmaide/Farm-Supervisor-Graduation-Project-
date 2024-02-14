package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportProblemFragment extends Fragment {

    Button submit;
    JSONArray pestTypeArray;
    JSONArray pestInfoArray;
    JSONArray symptomsArray;
    LinearLayout pestTypeLayout;

    LinearLayout pestInfoLayout;

    LinearLayout symptomsLayout;

    TextView textView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_problem, container, false);

        submit = view.findViewById(R.id.submitProblem);

        pestTypeLayout = view.findViewById(R.id.pestTypeLayout);
        pestInfoLayout = view.findViewById(R.id.pestInfoLayout);
        symptomsLayout = view.findViewById(R.id.symptomsLayout);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToAPI();
            }
        });


        return view;
    }

    private CheckBox[] extractCheckedCheckboxes(ViewGroup layout) {
        int childCount = layout.getChildCount();
        List<CheckBox> checkedCheckboxes = new ArrayList<>();

        for (int i = 0; i < childCount; i++) {
            View childView = layout.getChildAt(i);
            if (childView instanceof LinearLayout) {
                LinearLayout innerLayout = (LinearLayout) childView;
                CheckBox checkbox = (CheckBox) innerLayout.getChildAt(0);
                if (checkbox.isChecked()) {
                    checkedCheckboxes.add(checkbox);
                }
            }
        }

        return checkedCheckboxes.toArray(new CheckBox[0]);
    }


    private void sendDataToAPI() {
        CheckBox[] checkedPestTypeCheckboxes = extractCheckedCheckboxes(pestTypeLayout);
        CheckBox[] checkedPestInfoCheckboxes = extractCheckedCheckboxes(pestInfoLayout);
        CheckBox[] checkedSymptomsCheckboxes = extractCheckedCheckboxes(symptomsLayout);

        try {
            pestTypeArray = new JSONArray();
            for (CheckBox checkbox : checkedPestTypeCheckboxes) {
                JSONObject pestTypeObject = new JSONObject();
                pestTypeObject.put("type", checkbox.getText().toString());
                pestTypeArray.put(pestTypeObject);
            }

            pestInfoArray = new JSONArray();
            for (CheckBox checkbox : checkedPestInfoCheckboxes) {
                JSONObject pestInfoObject = new JSONObject();
                pestInfoObject.put("pestInfo", checkbox.getText().toString());
                pestInfoArray.put(pestInfoObject);
            }

            symptomsArray = new JSONArray();
            for (CheckBox checkbox : checkedSymptomsCheckboxes) {
                JSONObject symptomObject = new JSONObject();
                symptomObject.put("symptoms", checkbox.getText().toString());
                symptomsArray.put(symptomObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("type", pestTypeArray);
            requestData.put("pestInfo", pestInfoArray);
            requestData.put("symptoms", symptomsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8000/api/pestRecommender1/";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, requestData.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                final String responseBody = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(getContext(), responseBody, Toast.LENGTH_SHORT).show();
                        showResultDialog(responseBody);
                    }
                });
            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
    }
    private void showResultDialog(String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);

         textView = customLayout.findViewById(R.id.resultTextView);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray pestsArray = jsonObject.getJSONArray("pests");

            StringBuilder resultsBuilder = new StringBuilder();

            for (int i = 0; i < pestsArray.length(); i++) {
                String pest = pestsArray.getString(i);
                new PestSearchTask().execute(pest);
            }

            builder.setView(customLayout);

            builder.setNegativeButton("حسناً", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            dialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private class PestSearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... pests) {

            try {
                String pest = pests[0];
                String encodedPestName = URLEncoder.encode(pest, "UTF-8");
                URL url = new URL("http://10.0.2.2:8000/api/pestApiRead/" + pest);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();
                } else {
                    // Handle error
                    return "Error: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);

                StringBuilder displayText = new StringBuilder();

                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject pestObject = jsonArray.getJSONObject(index);

                    String pestName = pestObject.getString("name");
                    displayText.append("\nاسم الحشرة: ").append(pestName).append("\n");

                    JSONArray curesArray = pestObject.getJSONArray("cures");
                    List<String> curesInfoList = new ArrayList<>();
                    for (int i = 0; i < curesArray.length(); i++) {
                        JSONObject cureObject = curesArray.getJSONObject(i);
                        String cureName = cureObject.getString("name");
                        String cureDescription = cureObject.getString("description");
                        int cureTimes = cureObject.getInt("times");
                        String curePeriod = cureObject.getString("period");

                        String cureInfo =
                                " \nاسم العلاج: " + cureName +
                                        "\nوصف العلاج: " + cureDescription +
                                        "\nعدد مرات تطبيق العلاج: " + cureTimes +
                                        "\nفترة تطبيق العلاج: " + curePeriod +
                                        "\n";
                        curesInfoList.add(cureInfo);
                    }

                    JSONArray caringMethodsArray = pestObject.getJSONArray("caringMethods");
                    List<String> caringMethodsInfoList = new ArrayList<>();
                    for (int i = 0; i < caringMethodsArray.length(); i++) {
                        JSONObject caringMethodObject = caringMethodsArray.getJSONObject(i);
                        String caringMethodName = caringMethodObject.getString("name");
                        String caringMethodDescription = caringMethodObject.getString("description");

                        String caringMethodInfo =
                                "\nاسم طريقة العناية: " + caringMethodName +
                                        "\nوصف طريقة الرعاية بالنبتة: " + caringMethodDescription +
                                        "\n";
                        caringMethodsInfoList.add(caringMethodInfo);
                    }

                    displayText.append("\nأنواع العلاجات التي يقترحها النظام: ").append("\n");
                    for (String cureInfo : curesInfoList) {
                        displayText.append(cureInfo).append("\n");
                    }
                    displayText.append("\nطرق الرعاية التي يقترحها النظام: ").append("\n");
                    for (String caringMethodInfo : caringMethodsInfoList) {
                        displayText.append(caringMethodInfo).append("\n");
                    }
                }
                textView.append(displayText.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}