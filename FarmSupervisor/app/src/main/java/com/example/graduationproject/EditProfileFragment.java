package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class EditProfileFragment extends Fragment {
    TextView farmerEmail;
    EditText firstname, lastname, password, mobileNumber, farmer_email;
    Button save;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Bundle bundle = getArguments();

            if(bundle!=null){
                email = bundle.getString("email");
                System.out.println("Email: " + email);
            }
        System.out.println("Email: " + email);

        farmerEmail = root.findViewById(R.id.farmerEmail);
        farmer_email = root.findViewById(R.id.email_prof);
        firstname = root.findViewById(R.id.fname_prof);
        lastname = root.findViewById(R.id.lname_prof);
        password = root.findViewById(R.id.password_prof);
        mobileNumber = root.findViewById(R.id.phone_prof);
        save = root.findViewById(R.id.save);

        String firstNameUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/firstname?email=" + email;
        String lastNameUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/lastname?email=" + email;
//        String passwordUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/password?email=" + email;
        String mobileNumberUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/mobilenumber?email=" + email;


        putFarmerData(firstNameUrl , firstname);
        putFarmerData(lastNameUrl , lastname);
//        putFarmerData(passwordUrl , password);
        putFarmerData(mobileNumberUrl , mobileNumber);
        farmer_email.setText(email);
        farmerEmail.setText(email);
        password.setText("********");


        save.setOnClickListener(view -> {
            if(validateData()) {
                String updateFarmerInfoUrl = "http://" + IpAddress.VALUE + ":8080/api/v1/farmer/update?email=" + email
                        +"&firstName=" + firstname.getText().toString() + "&lastName=" + lastname.getText().toString() +
                        "&password=" + password.getText().toString() + "&mobileNumber=" + mobileNumber.getText().toString();
                System.out.println("number: " + mobileNumber.getText().toString());
                updateFarmerData(updateFarmerInfoUrl);
                Toast.makeText(getContext(), "تم تحديث معلوماتك بنجاح :)", Toast.LENGTH_SHORT).show();

            }
        });

        return root;
    }

    @SuppressLint("StaticFieldLeak")
    private void putFarmerData(String url, TextView textView) {
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

    @SuppressLint("StaticFieldLeak")
    private void updateFarmerData(String url) {
        new AsyncTask<Void, Void, String>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL apiUrl = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) apiUrl.openConnection();
                    urlConnection.setRequestMethod("PUT");
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
                } else {
                    Toast.makeText(getContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public boolean validateData() {
        if (firstname.getText().toString().length() == 0) {
            firstname.setError("يجب إدخال الاسم");
            firstname.requestFocus();
            return false;
        } else if (firstname.getText().toString().length() < 3) {
            firstname.setError("يجب ان يتكون الاسم من ثلاث حروف على الأقل");
            firstname.requestFocus();
            return false;
        }
        if (lastname.getText().toString().isEmpty()) {
            lastname.setError(" يجب إدخال الاسم الأخير ");
            lastname.requestFocus();
            return false;
        } else if (lastname.getText().toString().length() < 3) {
            lastname.setError("يجب ان يتكون الاسم من ثلاث حروف على الأقل");
            lastname.requestFocus();
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            password.setError("يجب إدخال كلمة السرّ");
            password.requestFocus();
            return false;
        } else if (password.getText().toString().length() < 8) {
            password.setError("يجب ان تحتوي كلمة المرور على 8 احرف و ارقام ");
            password.requestFocus();
            return false;
        } else if (!isValidPassword(password.getText().toString())) {
            password.setError("كلمة المرور يجب ان تحتوي على حرف واحد كبير ورقم واحد على الاقل");
            password.requestFocus();
            return false;
        }
        if (mobileNumber.getText().toString().isEmpty()) {
            mobileNumber.setError("يجب ادخال رقم الهاتف");
            mobileNumber.requestFocus();
            return false;
        } else if (mobileNumber.getText().toString().length() < 10) {
            mobileNumber.setError("رقم الهاتف يجب ان يحتوي على الاقل من 10 خانات");
            mobileNumber.requestFocus();
            return false;
        }

        return true;
    }

    static boolean isValidPassword (String str){
        if (!str.matches(".*\\d.*")) {
            return false;
        }
        if (!str.matches(".*[a-z].*")) {
            return false;
        }
        return str.matches(".*[A-Z].*");
    }
}