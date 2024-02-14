package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFarmer extends AppCompatActivity {

    Farmer farmer = new Farmer();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_farmer);

        EditText emailEditText = (EditText) SignUpFarmer.this.findViewById(R.id.email);
        EditText firstNameEditText = (EditText) SignUpFarmer.this.findViewById(R.id.fName);
        EditText lastNameEditText = (EditText) SignUpFarmer.this.findViewById(R.id.lName);
        EditText passwordEditText = (EditText) SignUpFarmer.this.findViewById(R.id.password);
        EditText confirmPasswordEditText = (EditText) SignUpFarmer.this.findViewById(R.id.pass_confirm);
        EditText mobileEditText = (EditText)SignUpFarmer.this.findViewById(R.id.phone);

        Button signUpButton = (Button) SignUpFarmer.this.findViewById(R.id.signUpButton);


        signUpButton.setOnClickListener(view -> {

            final boolean[] somethingWrong = {false};
            final String[] errorMessage = new String[1];
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(5, Color.RED);
            drawable.setCornerRadius(10f);
            GradientDrawable d2 = new GradientDrawable();
            d2.setShape(GradientDrawable.RECTANGLE);
            d2.setStroke(3, Color.blue(5));
            d2.setCornerRadius(10f);

            if (firstNameEditText.getText().toString().trim().length() < 3 || firstNameEditText.getText().toString().trim().length() > 20) {
                firstNameEditText.setBackground(drawable);

            }else {
                firstNameEditText.setBackground(d2);
            }

            if (lastNameEditText.getText().toString().trim().length() < 3 || lastNameEditText.getText().toString().trim().length() >20) {
                lastNameEditText.setBackground(drawable);
            } else {
                lastNameEditText.setBackground(d2);
            }

            if (!isEmailValid(emailEditText.getText().toString().trim())) {
                emailEditText.setBackground(drawable);
            } else {
                emailEditText.setBackground(d2);
            }

            if (!isPasswordValid(passwordEditText.getText().toString())) {
                passwordEditText.setBackground(drawable);
            } else {
                passwordEditText.setBackground(d2);
            }

            if (!isPasswordValid(confirmPasswordEditText.getText().toString()) || !confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {
                confirmPasswordEditText.setBackground(drawable);
            } else {
                confirmPasswordEditText.setBackground(d2);
            }

            if ((mobileEditText.getText().toString()).equals("")) {
                mobileEditText.setBackground(drawable);
            } else {
                mobileEditText.setBackground(d2);
            }

            if (!isEmailValid(emailEditText.getText().toString().trim())) {
                somethingWrong[0] = true;
                errorMessage[0] = "يجب إدخال البريد الالكتروني بصورة صحيحة";
            } else {
                farmer.setEmail(emailEditText.getText().toString().trim());
            }

            if (!somethingWrong[0]) {
                if (firstNameEditText.getText().toString().trim().length() < 2) {
                    somethingWrong[0] = true;
                    errorMessage[0] = "يجب أن يتكون الاسم الاول على الأقل من حرفين";
                } else {
                    farmer.setFirstName(firstNameEditText.getText().toString().trim());
                }

                if (!somethingWrong[0]) {
                    if (lastNameEditText.getText().toString().trim().length() < 2) {
                        somethingWrong[0] = true;
                        errorMessage[0] = "يجب أن يتكون الاسم الاخير على الأقل من حرفين";
                    } else {
                        farmer.setLastName(lastNameEditText.getText().toString().trim());
                    }

                    if (!somethingWrong[0]) {
                        if (!isPasswordValid(passwordEditText.getText().toString().trim())) {
                            somethingWrong[0] = true;
                            errorMessage[0] = "يجب ان تحتوي كلة السر على الاقل على حرف صغير, حرف كبير ورقم واحد";
                        } else {
                            farmer.setPassword(passwordEditText.getText().toString().trim());
                        }

                        if (!somethingWrong[0]) {
                            if (!confirmPasswordEditText.getText().toString().trim().equals(passwordEditText.getText().toString().trim())) {
                                somethingWrong[0] = true;
                                errorMessage[0] = "لم تتوافق كلمة السر مع ما تم ادخاله";
                            } else {
                                farmer.setPassword(confirmPasswordEditText.getText().toString().trim());
                            }
                            if (!somethingWrong[0]) {
                                if ((mobileEditText.getText().toString().trim()).equals("")) {
                                    somethingWrong[0] = true;
                                    errorMessage[0] = "ادخل رقم الهاتف ";
                                } else {
                                    farmer.setMobileNumber(mobileEditText.getText().toString().trim());

                                    String jsonInputString = convertFarmerObjectToJson(farmer);

                                    new AsyncTask<Void, Void, String>() {
                                        @SuppressLint("StaticFieldLeak")
                                        @Override
                                        protected String doInBackground(Void... voids) {
                                            try {
                                                URL url = new URL("http://" + IpAddress.VALUE + ":8080/api/v1/farmer/add");
                                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                                urlConnection.setRequestMethod("POST");
                                                urlConnection.setRequestProperty("Content-Type", "application/json");
                                                urlConnection.setDoOutput(true);

                                                try (OutputStream os = urlConnection.getOutputStream()) {
                                                    byte[] input = jsonInputString.getBytes("utf-8");
                                                    os.write(input, 0, input.length);
                                                }
                                                int responseCode = urlConnection.getResponseCode();
                                                return "Success";

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                return "Error: " + e.getMessage();
                                            }

                                        }
                                        @SuppressLint("StaticFieldLeak")
                                        @Override
                                        protected void onPostExecute(String result) {

                                            if ("Success".equals(result)) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpFarmer.this);
                                                builder.setMessage("تمت عملية التسجيل بنجاح! هل ترغب في تسجيل الدخول؟")
                                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {

                                                                Intent intent = new Intent(SignUpFarmer.this, MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        })
                                                        .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                    dialog.dismiss();
                                                            }
                                                        });
                                                AlertDialog alertDialog = builder.create();
                                                alertDialog.show();
                                            } else {
                                                Toast.makeText(SignUpFarmer.this, result, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }.execute();
                                }
                            }
                        }
                    }
                }
            }

            if (somethingWrong[0]) {
                Toast toast =Toast.makeText(SignUpFarmer.this,"هناك خطأ في ادخال المعلومات",Toast.LENGTH_SHORT);
                toast.show();
            }

        });

    }

    static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    static boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z]).{8,15}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }
    private String convertFarmerObjectToJson(Farmer farmer) {
        Gson gson = new Gson();
        return gson.toJson(farmer);
    }

}