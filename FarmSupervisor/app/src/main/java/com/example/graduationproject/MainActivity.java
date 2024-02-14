package com.example.graduationproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    CheckBox rememberMe;
    SharedPrefManager sharedPrefManager;

    String city;

    boolean locationReady = false;

    boolean authenticationReady = false;
    public static LocationManager locationManager;
    public static Location location;
    private final int PERMISSION_CODE = 1;

    ProgressBar progressBar;

    @SuppressLint({"StaticFieldLeak", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
            Log.d("PermissionDebug", "Requesting Permissions");
        }

        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.homeBar);
        progressBar.setVisibility(View.GONE);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        rememberMe = findViewById(R.id.rememberMe);
        Button signIn = findViewById(R.id.btn_sign_in);
        Button signUP = findViewById(R.id.btn_sign_up);

        sharedPrefManager = SharedPrefManager.getInstance(MainActivity.this);
        if (!Objects.equals(sharedPrefManager.readString("email", "noValue"), "noValue")) {
            email.setText(sharedPrefManager.readString("email", "noValue"));
        }

        signIn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            getLocation();
            String enteredEmail = email.getText().toString().trim();

            String apiUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/emailcheck?email=" + enteredEmail;

            new AsyncTask<Void, Void, String>() {
                @SuppressLint("StaticFieldLeak")
                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        URL url = new URL(apiUrl);
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
                    if ("available".equals(result)) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "الإيميل غير مسجّل من قبل", Toast.LENGTH_SHORT).show();

                    } else if ("taken".equals(result)) {
                        checkPassword();
                    } else {
                        Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();

                    }
                }
            }.execute();

        });

        signUP.setOnClickListener(view -> {
            getLocation();
            startActivity(new Intent(MainActivity.this, SignUpFarmer.class));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Objects.equals(sharedPrefManager.readString("email", "noValue"), "noValue")) {
            email.setText(sharedPrefManager.readString("email", ""));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("PermissionDebug", "Request Code: " + requestCode);

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "تم منح الإذن..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "الرّجاء منح الإذن..", Toast.LENGTH_SHORT).show();
//                finish();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location updatedLocation) {
                        location = updatedLocation;
                        locationReady = true;
                        if(authenticationReady) {
                            try {
                                navigateToHomePageFragment();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }


                        Log.d("LocationDebug", "Updated Location: " + location);

                        locationManager.removeUpdates(this);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        // Handle status changes if needed
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // Handle provider enabled if needed
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        // Handle provider disabled if needed
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                Log.d("LocationDebug", "Network provider is not enabled.");
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void checkPassword() {

        String enterPassword = password.getText().toString().trim();
        String enteredEmail = email.getText().toString().trim();

        String passwordCheckUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/logincheck?email=" + enteredEmail + "&password=" + enterPassword;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(passwordCheckUrl);
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
            protected void onPostExecute(String passwordResult) {
                if ("correct password".equals(passwordResult)) {
                    if (rememberMe.isChecked()) {
                        sharedPrefManager.writeString("email", email.getText().toString().trim());
                    }
                    sharedPrefManager.writeString("NotificationState", "0");
                    authenticationReady = true;
                    if (locationReady) {
                       try {
                            navigateToHomePageFragment();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else if ("wrong password".equals(passwordResult)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "كلمة المرور غير صحيحة", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, passwordResult, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getToken() {

        String enterPassword = password.getText().toString().trim();
        String enteredEmail = email.getText().toString().trim();

        String tokenUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/checker/authenticate";
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("email", enteredEmail);
            requestData.put("password", enterPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(tokenUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);

                    try(OutputStream os = urlConnection.getOutputStream()) {
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

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        String token = jsonResponse.getString("token");

                        return token;
                    } else {
                        return "Error: " + responseCode;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Error: " + e.getMessage();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            protected void onPostExecute(String token) {
                sharedPrefManager.writeString("token", token);
                try {
                    updateDjango();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }

    private void updateDjango() throws JSONException {
        OkHttpClient client = new OkHttpClient();
        city = getCityName(location.getLongitude(), location.getLatitude(), "OK");
        sharedPrefManager.writeString("city", city);
        String url = "http://10.0.2.2:8000/api/user/";
        JSONObject json = new JSONObject();
        json.put("email", email.getText());
        json.put("location", city);
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

    private void navigateToHomePageFragment() throws JSONException {
        Intent i = new Intent(MainActivity.this, HomeActivity.class);
        i.putExtra("email", email.getText().toString());
        progressBar.setVisibility(View.GONE);
        startActivity(i);
        getToken();
    }

    private String getCityName(double longitude, double latitude, String status) {
        String cityName = "Not found";
        if (Objects.equals(status, "not allowed"))
            return cityName;

        Geocoder gcd = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for (Address add : addresses) {
                if (add != null) {
                    String city = add.getLocality();
                    if (city != null && !city.isEmpty()) {
                        cityName = city;
                    } else {
                        Log.d("TAG", "المدينة غير موجودة");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }

}