package com.unibg.app3dsat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.unibg.app3dsat.util.DefaultValues;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * Class: CreatePatient
 */
public class CreatePatient extends AppCompatActivity {

    /**
     * SharedPreferences
     */
    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences SPDoctor;
    private SharedPreferences SPPatient;

    /**
     * Variables
     */
    private String name, surname;

    /**
     * Method: onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_patient);

        // SharedPreferences
        /**
         * SharedPreferences
         */
        SPDoctor = getSharedPreferences(DefaultValues.SPDOCTOR, MODE_PRIVATE);
        SPPatient = getSharedPreferences(DefaultValues.SPPATIENT, MODE_PRIVATE);

        final String doctorMail = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_MAIL, "404");
        final String doctorPassword = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_PASSWORD, "404");

        Button button = findViewById(R.id.button);

        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        name = Objects.requireNonNull(((TextInputLayout) findViewById(R.id.name)).getEditText()).getText().toString();
                        surname = Objects.requireNonNull(((TextInputLayout) findViewById(R.id.surname)).getEditText()).getText().toString();

                        if (name.length() != 0 && surname.length() != 0) {
                            createPatient(doctorMail, doctorPassword, name, surname);
                        } else {
                            Toast t = Toast.makeText(getApplicationContext(), "Compile data fields", Toast.LENGTH_LONG);
                            t.show();
                        }
                    }
                });
    }

    /**
     * Method: createPatient
     *
     * @param doctorMail
     * @param doctorPassword
     * @param name
     * @param surname
     */
    @SuppressLint("StaticFieldLeak")
    private void createPatient(final String doctorMail, final String doctorPassword, final String name, final String surname) {

        new AsyncTask<Void, Void, Void>() {

            /**
             * Method: doInBackground
             *
             * @param params
             * @return
             */
            @Nullable
            public Void doInBackground(Void... params) {
                // URI BUILDER
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .encodedAuthority(DefaultValues.AUTHORITY)
                        .appendPath("se4medservice")
                        .appendPath("")
                        .appendQueryParameter(DefaultValues.ACTION_PARAM_NAME, DefaultValues.CREATEPATIENTDOC_ACTION)
                        .appendQueryParameter(DefaultValues.EMAIL_PARAM, doctorMail)
                        .appendQueryParameter(DefaultValues.PASSWORD_PARAM, doctorPassword)
                        .appendQueryParameter(DefaultValues.NAMEPAT_PARAM, name)
                        .appendQueryParameter(DefaultValues.SURNAMEPAT_PARAM, surname)
                        .appendQueryParameter(DefaultValues.IDAPP_PARAM, DefaultValues.STEREOTEST); // Insert semicolon

                String myUri = builder.build().toString();
                System.out.println("URI: " + myUri);

                // URL CONNECTION
                HttpURLConnection urlConnection = null;
                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(myUri);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000 /* milliseconds */);
                    urlConnection.setConnectTimeout(15000 /* milliseconds */);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    int responseCode = urlConnection.getResponseCode();
                    System.out.println("RESPONSE CODE: " + responseCode);

                    if ((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_CREATED)) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                    } else {
                        // Toast (Handler)
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast t = Toast.makeText(getApplicationContext(), "Network error (HTTP)", Toast.LENGTH_LONG);
                                t.show();
                            }
                        });
                    }
                } catch (Exception e) {
                    // Toast (Handler)
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast t = Toast.makeText(getApplicationContext(), "Network error (E)", Toast.LENGTH_LONG);
                            t.show();
                        }
                    });
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

                try {
                    String response = "{" + result.toString().substring(result.toString().indexOf("{") + 1, result.toString().lastIndexOf("}")) + "}";
                    System.out.println("RESPONSE: " + response);

                    JSONObject jsonobject = new JSONObject(response);
                    final String status = jsonobject.getString("status");
                    System.out.println("STATUS: " + status);

                    if (status.equals(DefaultValues.STATUS_OK)) {
                        SharedPreferences.Editor e = SPPatient.edit();
                        e.putString(DefaultValues.ACTUAL_PATIENT_NAME, name);
                        e.putString(DefaultValues.ACTUAL_PATIENT_SURNAME, surname);
                        e.putInt(DefaultValues.ACTUAL_PATIENT_ID, jsonobject.getInt("idpatient"));
                        e.apply();

                        // Toast (Handler)
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast t = Toast.makeText(getApplicationContext(), "Patient created", Toast.LENGTH_LONG);
                                t.show();
                            }
                        });

                        // Intent
                        Intent intent = new Intent(CreatePatient.this, MainDoctor.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Toast (Handler)
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast t = Toast.makeText(getApplicationContext(), "Status: " + status, Toast.LENGTH_LONG);
                                t.show();
                            }
                        });

                        // Intent
                        Intent intent = new Intent(CreatePatient.this, MainPatient.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}