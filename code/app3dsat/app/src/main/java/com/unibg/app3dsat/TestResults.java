package com.unibg.app3dsat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.unibg.app3dsat.util.DefaultValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class: TestResults
 */
public class TestResults extends AppCompatActivity {

    /**
     * Constants
     */
    private static final DecimalFormat angleformat = new DecimalFormat("#");
    private static final DecimalFormat timeformat = new DecimalFormat("#");

    /**
     * SharedPreferences
     */
    private SharedPreferences SPDoctor, SPPatient, SPSettings;

    /**
     * Variables
     */
    private int APID;
    @Nullable
    private String doctorMail;
    @Nullable
    private String doctorPassword;

    /**
     * Method: isExternalStorageReadOnly
     *
     * @return boolean
     */
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    /**
     * Method: isExternalStorageAvailable
     *
     * @return boolean
     */
    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    /**
     * Method onCreate
     *
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);

        // SharedPreferences (SPDoctor)
        SPDoctor = getSharedPreferences(DefaultValues.SPDOCTOR, MODE_PRIVATE);
        doctorMail = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_MAIL, "404");
        doctorPassword = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_PASSWORD, "404");

        // SharedPreferences (SPPatient)
        SPPatient = getSharedPreferences(DefaultValues.SPPATIENT, MODE_PRIVATE);
        String APName = SPPatient.getString(DefaultValues.ACTUAL_PATIENT_NAME, DefaultValues.ACTUAL_PATIENT_NAME);
        String APSurname = SPPatient.getString(DefaultValues.ACTUAL_PATIENT_SURNAME, DefaultValues.ACTUAL_PATIENT_SURNAME);
        APID = SPPatient.getInt(DefaultValues.ACTUAL_PATIENT_ID, -1);

        // SharedPreferences (SPSettings)
        SPSettings = getSharedPreferences(DefaultValues.SPSETTINGS, MODE_PRIVATE);
        String distance = String.valueOf(SPSettings.getInt(DefaultValues.PREF_DISTANCE, DefaultValues.DEFAULT_DISTANCE));

        TextView tv = findViewById(R.id.txtv_actualuser);
        tv.setText(APName + " " + APSurname + " [" + APID + "]");

        // Get the data
        Bundle dataFromMain = getIntent().getExtras();
        String imageSet = Objects.requireNonNull(dataFromMain).getString(DefaultValues.CURRENT_IMAGE_SET);
        int widthPix = dataFromMain.getInt(DefaultValues.WIDTH_PIX);
        int heighPix = dataFromMain.getInt(DefaultValues.HEIGHT_PIX);
        float widthPixPerInch = dataFromMain.getFloat(DefaultValues.WIDTH_PIX_PER_INCH);
        float heighPixPerInch = dataFromMain.getFloat(DefaultValues.HEIGHT_PIX_PER_INCH);
        if (imageSet == null)
            return;

        ((TextView) findViewById(R.id.txtv_sessioninfo)).setText(imageSet);

        try {
            // JSON Session data
            JSONArray jsonArray = new JSONArray(getIntent().getStringExtra(DefaultValues.SESSION_DATA));
            System.out.println("Array: " + jsonArray.toString());

            // Get short version for list
            ArrayList<String> sessionsStringShort = new ArrayList<>();
            final ListView sessionsListView = findViewById(R.id.listview_sessions);

            int finalangleresult = computeAngleResult(jsonArray);
            ((TextView) findViewById(R.id.txtv_sessioninfo)).setText(imageSet + ", Final angle: " + finalangleresult + "\"");

            for (int i = 0; i < jsonArray.length(); i++) {
                String singleanswer = jsonArray.get(i).toString();
                String[] data = singleanswer.split(",");
                String chosenS = data[0].substring(data[0].indexOf('/') + 1);
                String actualS = data[1].substring(data[1].indexOf('/') + 1);
                String disparity = data[2];
                double angle = Double.parseDouble(data[3]);
                String time = data[4];
                String line;
                if (chosenS.equals(actualS)) {
                    // Add one point because the disparity values is decreased after a corrected answer
                    line = "Disparity: " + Integer.valueOf(disparity) + " OK " + "(" + actualS + ")";
                } else {
                    line = "Disparity: " + Integer.valueOf(disparity) + " WRONG (" + actualS + " -> " + chosenS + ")";
                }
                line += ", angle " + angleformat.format(angle) + "\" in " + timeformat.format(Long.parseLong(time) / 1000.0) + " sec";
                sessionsStringShort.add(line);

                // Set ListView
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sessionsStringShort);
                sessionsListView.setAdapter(adapter);

                sessionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    /**
                     * Method: onItemClick
                     *
                     * @param adapter
                     * @param component
                     * @param pos
                     * @param id
                     */
                    @Override
                    public void onItemClick(AdapterView<?> adapter, final View component, int pos, long id) {
                        // Nothing
                    }
                });
            }

            // JSON Session data (Complete)
            JSONObject sessiondatacomplete = new JSONObject();
            sessiondatacomplete.put(DefaultValues.ACTUAL_PATIENT_NAME, APName);
            sessiondatacomplete.put(DefaultValues.ACTUAL_PATIENT_SURNAME, APSurname);
            sessiondatacomplete.put(DefaultValues.ACTUAL_PATIENT_ID, APID);
            sessiondatacomplete.put(DefaultValues.PREF_DISTANCE, distance);
            sessiondatacomplete.put(DefaultValues.WIDTH_PIX, widthPix);
            sessiondatacomplete.put(DefaultValues.HEIGHT_PIX, heighPix);
            sessiondatacomplete.put(DefaultValues.WIDTH_PIX_PER_INCH, widthPixPerInch);
            sessiondatacomplete.put(DefaultValues.HEIGHT_PIX_PER_INCH, heighPixPerInch);
            sessiondatacomplete.put(DefaultValues.CURRENT_IMAGE_SET, imageSet);
            sessiondatacomplete.put(DefaultValues.SESSION_DATA, jsonArray);
            sessiondatacomplete.put(DefaultValues.FINALANGLERESULT, finalangleresult);

            // Save session data (Complete) to file
            saveResultsOnInternalFile(sessiondatacomplete);
            saveResultsOnServlet(sessiondatacomplete);

            final JSONObject sessiondatacompletefinal = sessiondatacomplete;

            // Buttons
            Button retry = findViewById(R.id.retry);
            Button results = findViewById(R.id.results);

            retry.setVisibility(View.INVISIBLE);
            retry.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    saveResultsOnServlet(sessiondatacompletefinal);
                }
            });

            results.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Intent
                    Intent intent = new Intent(TestResults.this, Results.class);
                    startActivity(intent);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method: computeAngleResult
     *
     * @param listresults
     * @return int
     */
    private int computeAngleResult(@NonNull JSONArray listresults) {
        double[] angles = new double[1];

        try {
            for (int i = 0; i < listresults.length(); i++) {
                String singleanswer = listresults.get(i).toString();
                String[] data = singleanswer.split(",");
                String chosenS = data[0].substring(data[0].indexOf('/') + 1);
                String actualS = data[1].substring(data[1].indexOf('/') + 1);

                if (chosenS.equals(actualS)) {
                    // Overwrite until the last correct angle
                    angles[0] = Double.parseDouble(data[3]);
                }
            }
            return ((int) (angles[0] + 1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Method: saveResultsOnInternalFile
     *
     * @param sessiondatacomplete
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveResultsOnInternalFile(@NonNull JSONObject sessiondatacomplete) {
        File myfile = null;

        if (isExternalStorageAvailable() && !isExternalStorageReadOnly())
            myfile = new File(getExternalFilesDir(DefaultValues.FOLDER_RESULTS), DefaultValues.FILE_RESULTS_NAME + "_" + APID + ".txt");

        FileOutputStream outputStream;

        try {
            if (!Objects.requireNonNull(myfile).exists()) {
                myfile.createNewFile();
            }
            outputStream = new FileOutputStream(myfile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(outputStream);
            myOutWriter.append(sessiondatacomplete.toString()).append("\n");
            myOutWriter.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method: saveResultsOnServlet
     *
     * @param sessiondatacomplete
     */
    @SuppressLint("StaticFieldLeak")
    private void saveResultsOnServlet(@NonNull final JSONObject sessiondatacomplete) {

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
                        .appendQueryParameter(DefaultValues.ACTION_PARAM_NAME, DefaultValues.STORERESULTS_NOT_REGISTERED_ACTION)
                        .appendQueryParameter(DefaultValues.EMAIL_PARAM, doctorMail)
                        .appendQueryParameter(DefaultValues.PASSWORD_PARAM, doctorPassword)
                        .appendQueryParameter(DefaultValues.IDPATIENT_PARAM, String.valueOf(APID))
                        .appendQueryParameter(DefaultValues.IDAPP_PARAM, DefaultValues.STEREOTEST)
                        .appendQueryParameter(DefaultValues.RESULT_PARAM, sessiondatacomplete.toString()); // Insert semicolon

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
                        // Button
                        Button retry = findViewById(R.id.retry);
                        retry.setVisibility(View.VISIBLE);

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
                    // Button
                    Button retry = findViewById(R.id.retry);
                    retry.setVisibility(View.VISIBLE);

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
                        // Button
                        Button retry = findViewById(R.id.retry);
                        retry.setVisibility(View.INVISIBLE);

                        // Toast (Handler)
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast t = Toast.makeText(getApplicationContext(), "Results saved", Toast.LENGTH_LONG);
                                t.show();
                            }
                        });
                    } else {
                        // Button
                        Button retry = findViewById(R.id.retry);
                        retry.setVisibility(View.VISIBLE);

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
        }.execute();
    }
}