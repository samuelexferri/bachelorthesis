package com.unibg.app3dsat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.unibg.app3dsat.util.DefaultValues;
import com.unibg.app3dsat.util.ListViewAdapter;
import com.unibg.app3dsat.util.Patient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class: MainPatient
 */
public class MainPatient extends AppCompatActivity {

    /**
     * ArrayList
     */
    private static ArrayList<Patient> patientArrayList; // ArrayList of patients
    private static ArrayList<Patient> patientArraySort; // ArrayList of filtered patients
    private final ArrayList<Patient> patientList = new ArrayList<>();

    /**
     * SharedPreferences
     */
    private SharedPreferences SPDoctor, SPPatient;

    /**
     * Variables
     */
    private int textlength = 0;
    private EditText search;
    private ListView list;
    private ListViewAdapter adapter;
    @Nullable
    private String doctorMail;
    @Nullable
    private String doctorPassword;

    /**
     * Method: onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_patient);

        // SharedPreferences (SPDoctor)
        SPDoctor = getSharedPreferences(DefaultValues.SPDOCTOR, MODE_PRIVATE);
        doctorMail = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_MAIL, "404");
        doctorPassword = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_PASSWORD, "404");

        // SharedPreferences (SPPatient)
        SPPatient = getSharedPreferences(DefaultValues.SPPATIENT, MODE_PRIVATE);

        getPatientList(doctorMail, doctorPassword);

        // Floating button
        FloatingActionButton fab = findViewById(R.id.add_patient);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent
                Intent intent = new Intent(MainPatient.this, CreatePatient.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Method: doActivity()
     * Called by onPostExecute in getPatientList
     */
    private void doActivity() {
        list = findViewById(R.id.listView);

        // Rearrange patient list
        Collections.sort(patientArrayList);

        adapter = new ListViewAdapter(MainPatient.this, patientArrayList);
        list.setAdapter(adapter);

        // Short click
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor e = SPPatient.edit();
                e.putString(DefaultValues.ACTUAL_PATIENT_NAME, patientArraySort.get(position).getName());
                e.putString(DefaultValues.ACTUAL_PATIENT_SURNAME, patientArraySort.get(position).getSurname());
                e.putInt(DefaultValues.ACTUAL_PATIENT_ID, patientArraySort.get(position).getId());
                e.apply();

                // Toast
                Toast.makeText(MainPatient.this, patientArraySort.get(position).toString(), Toast.LENGTH_LONG).show();

                finish();
            }
        });

        list.setLongClickable(true);

        // Long click
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainPatient.this);
                alertDialog.setTitle("Delete patient");
                alertDialog.setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deletePatient(doctorMail, doctorPassword, patientArraySort.get(position).getId());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Nothing
                            }
                        });
                alertDialog.show();
                return true;
            }
        });

        search = findViewById(R.id.editText);

        search.addTextChangedListener(new TextWatcher() {

            /**
             * Method: afterTextChanged
             *
             * @param s
             */
            public void afterTextChanged(Editable s) {
            }

            /**
             * Method: beforeTextChanged
             *
             * @param s
             * @param start
             * @param count
             * @param after
             */
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * Method: onTextChanged
             *
             * @param s
             * @param start
             * @param before
             * @param count
             */
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textlength = search.getText().length();
                patientArraySort.clear();

                for (int i = 0; i < patientArrayList.size(); i++) {
                    int length = patientArrayList.get(i).toString().length();

                    if (textlength <= length) {
                        String string = patientArrayList.get(i).toString();

                        if (string.toLowerCase().trim().contains(search.getText().toString().toLowerCase().trim())) {
                            patientArraySort.add(patientArrayList.get(i));
                        }
                    }
                }

                // Rearrange the list
                Collections.sort(patientArraySort);

                adapter = new ListViewAdapter(MainPatient.this, patientArraySort);
                list.setAdapter(adapter);
            }
        });
    }

    /**
     * Method: getPatientList
     *
     * @param doctorMail
     * @param doctorPassword
     */
    @SuppressLint("StaticFieldLeak")
    private void getPatientList(final String doctorMail, final String doctorPassword) {

        new AsyncTask<Void, Void, Void>() {

            /**
             * Method doInBackground
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
                        .appendQueryParameter(DefaultValues.ACTION_PARAM_NAME, DefaultValues.GETPATIENTDOCLIST_ACTION)
                        .appendQueryParameter(DefaultValues.EMAIL_PARAM, doctorMail)
                        .appendQueryParameter(DefaultValues.PASSWORD_PARAM, doctorPassword)
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
                        JSONArray jsonarray = jsonobject.getJSONArray("patients");
                        System.out.println("JSON: " + jsonarray.toString());

                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject objects = jsonarray.getJSONObject(i);
                            patientList.add(new Patient(objects.getString("name"), objects.getString("surname"), objects.getInt("id")));
                        }
                    } else {
                        // Toast (Handler)
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast t = Toast.makeText(getApplicationContext(), "Status: " + status, Toast.LENGTH_LONG);
                                t.show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            /**
             * Method: onPostExecute
             *
             * @param result
             */
            public void onPostExecute(Void result) {
                // Download patient list (Without reference)
                patientArrayList = new ArrayList<>(patientList);
                patientArraySort = new ArrayList<>(patientList);

                doActivity();
            }
        }.execute();
    }

    /**
     * Method: deletePatient
     *
     * @param doctorMail
     * @param doctorPassword
     */
    @SuppressLint("StaticFieldLeak")
    private void deletePatient(final String doctorMail, final String doctorPassword, final int idPatient) {

        new AsyncTask<Void, Void, Void>() {

            /**
             * Method doInBackground
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
                        .appendQueryParameter(DefaultValues.ACTION_PARAM_NAME, DefaultValues.DELETEPATIENTDOC_ACTION)
                        .appendQueryParameter(DefaultValues.EMAIL_PARAM, doctorMail)
                        .appendQueryParameter(DefaultValues.PASSWORD_PARAM, doctorPassword)
                        .appendQueryParameter(DefaultValues.IDPATIENT_PARAM, String.valueOf(idPatient))
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
                        e.putString(DefaultValues.ACTUAL_PATIENT_NAME, "404");
                        e.putString(DefaultValues.ACTUAL_PATIENT_SURNAME, "404");
                        e.putInt(DefaultValues.ACTUAL_PATIENT_ID, -1);
                        e.apply();

                        // Toast (Handler)
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast t = Toast.makeText(getApplicationContext(), "Patient deleted", Toast.LENGTH_LONG);
                                t.show();
                            }
                        });
                    } else {
                        // Toast (Handler)
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast t = Toast.makeText(getApplicationContext(), "Status: " + status, Toast.LENGTH_LONG);
                                t.show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            /**
             * Method: onPostExecute
             *
             * @param result
             */
            public void onPostExecute(Void result) {
                // Refresh activity
                finish();
                startActivity(getIntent());
            }
        }.execute();
    }
}