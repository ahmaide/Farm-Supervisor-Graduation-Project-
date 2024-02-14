package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ProfileFragment extends Fragment {

    TextView farmerFullName, farmerEmail, firstname, lastname, password, mobileNumber;
    Button editProfile, signOut;
    String email;

    SharedPrefManager sharedPrefManager;

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());
        email = sharedPrefManager.readString("email", "noValue");
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        assert getArguments() != null;
        farmerFullName = root.findViewById(R.id.farmerName);
        farmerEmail = root.findViewById(R.id.farmerEmail);
        firstname = root.findViewById(R.id.fname_prof);
        lastname = root.findViewById(R.id.lname_prof);
//        password = root.findViewById(R.id.password_prof);
        mobileNumber = root.findViewById(R.id.phone_prof);
        editProfile = root.findViewById(R.id.save);
        signOut = root.findViewById(R.id.signOut);

        String firstNameUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/firstname?email=" + email;
        String lastNameUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/lastname?email=" + email;
//        String passwordUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/password?email=" + email;
        String mobileNumberUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/mobilenumber?email=" + email;
        String fullNameUrl =  "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/fullName?email=" + email;

        fetchData(firstNameUrl , firstname);
        fetchData(lastNameUrl , lastname);
//        fetchData(passwordUrl , password);
        fetchData(mobileNumberUrl , mobileNumber);
        farmerEmail.setText(email);
        fetchData(fullNameUrl , farmerFullName);

        signOut.setOnClickListener(view -> startActivity(new Intent(getContext(), MainActivity.class)));

        editProfile.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("email", email);

            EditProfileFragment fragment = new EditProfileFragment();
            fragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        });

        return root;
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchData(String url, TextView textView) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL apiUrl = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) apiUrl.openConnection();
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
            protected void onPostExecute(String data) {
                if (!data.startsWith("Error")) {
                    textView.setText(data);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


}