package com.unibg.app3dsat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.unibg.app3dsat.util.DefaultValues;
import com.unibg.app3dsat.util.Group;
import com.unibg.app3dsat.util.MyExpandableListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class: Results
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class Results extends AppCompatActivity {

    /**
     * Constants
     */
    private static final DecimalFormat angleformat = new DecimalFormat("#");
    private static final DecimalFormat timeformat = new DecimalFormat("#");
    private final ArrayList<String> resultList = new ArrayList<>();

    /**
     * SharedPreferences
     */
    private SharedPreferences SPDoctor, SPPatient;

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
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
     * Method: onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // SharedPreferences (SPDoctor)
        SPDoctor = getSharedPreferences(DefaultValues.SPDOCTOR, MODE_PRIVATE);
        doctorMail = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_MAIL, "404");
        doctorPassword = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_PASSWORD, "404");

        // SharedPreferences (SPPatient)
        SPPatient = getSharedPreferences(DefaultValues.SPPATIENT, MODE_PRIVATE);
        APID = SPPatient.getInt(DefaultValues.ACTUAL_PATIENT_ID, -1);

        // Function
        getResultsList(doctorMail, doctorPassword);
    }

    /**
     * Method: getResultsList
     *
     * @param doctorMail
     * @param doctorPassword
     */
    @SuppressLint("StaticFieldLeak")
    private void getResultsList(final String doctorMail, final String doctorPassword) {

        resultList.clear();

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
                        .appendQueryParameter(DefaultValues.ACTION_PARAM_NAME, DefaultValues.GETRESULTS_NOT_REGISTERED_ACTION)
                        .appendQueryParameter(DefaultValues.EMAIL_PARAM, doctorMail)
                        .appendQueryParameter(DefaultValues.PASSWORD_PARAM, doctorPassword)
                        .appendQueryParameter(DefaultValues.IDPATIENT_PARAM, String.valueOf(APID))
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
                        JSONArray jsonarray = jsonobject.getJSONArray("results");
                        System.out.println("JSON: " + jsonarray.toString());

                        for (int i = 0; i < jsonarray.length(); i++) {
                            resultList.add(jsonarray.getJSONObject(i).toString());
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
                // Write results into file
                writeFile(resultList);

                doActivity();
            }
        }.execute();
    }

    /**
     * Method: writeFile
     *
     * @param patientList
     */
    private void writeFile(@NonNull ArrayList<String> patientList) {
        try {
            File file = null;

            // Delete previous file
            deleteFile();

            if (isExternalStorageAvailable() && !isExternalStorageReadOnly())
                file = new File(getExternalFilesDir(DefaultValues.FOLDER_RESULTS), DefaultValues.FILE_RESULTS_NAME + "_" + APID + ".txt");

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for (String single : patientList) {
                writer.write(single + "\n");
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method: doActivity()
     * Called by onPostExecute in getResultsList
     */
    private void doActivity() {
        File readfile = null;

        if (isExternalStorageAvailable() && !isExternalStorageReadOnly())
            readfile = new File(getExternalFilesDir(DefaultValues.FOLDER_RESULTS), DefaultValues.FILE_RESULTS_NAME + "_" + APID + ".txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(readfile));
            String line;
            int count = 0;
            Group group;
            SparseArray<Group> groups = new SparseArray<>();

            // JSON
            JSONObject jsonObject;

            while ((line = br.readLine()) != null) {
                jsonObject = new JSONObject(line);
                System.out.println("Line: " + line);

                String APName = (String) jsonObject.get(DefaultValues.ACTUAL_PATIENT_NAME);
                String APSurname = (String) jsonObject.get(DefaultValues.ACTUAL_PATIENT_SURNAME);
                int APID = (int) jsonObject.get(DefaultValues.ACTUAL_PATIENT_ID);
                String imageset = (String) jsonObject.get(DefaultValues.CURRENT_IMAGE_SET);
                String distance = (String) jsonObject.get(DefaultValues.PREF_DISTANCE);
                int finalangleresult = (int) jsonObject.get(DefaultValues.FINALANGLERESULT);
                Timestamp dateandtime = (Timestamp.valueOf((String) jsonObject.get(DefaultValues.DATEANDTIME_PARAM)));

                JSONArray sessiondata = (JSONArray) jsonObject.get(DefaultValues.SESSION_DATA);

                // Create group for visualization
                group = new Group(APName + " " + APSurname + " [" + APID + "] - Final angle: " + finalangleresult + "\" \nTime: " + dateandtime.toString());
                group.children.add("" + imageset);
                group.children.add("Distance: " + distance + " cm");
                group.children.add("Angle: " + finalangleresult + "\"");

                for (int i = 0; i < sessiondata.length(); i++) {
                    String[] data = sessiondata.get(i).toString().split(",");
                    String chosenS = data[0].substring(data[0].indexOf('/') + 1);
                    String actualS = data[1].substring(data[1].indexOf('/') + 1);
                    String disparity = data[2];
                    String angle = data[3];
                    String time = data[4];
                    String lineList;
                    if (chosenS.equals(actualS)) {
                        // Add one point because the disparity values is decreased after a corrected answer
                        lineList = "Disparity: " + Integer.valueOf(disparity) + " OK " + "(" + actualS + ")";
                    } else {
                        lineList = "Disparity: " + Integer.valueOf(disparity) + " WRONG (" + actualS + " -> " + chosenS + ")";
                    }
                    lineList += ", angle " + angleformat.format(Double.parseDouble(angle)) + "\" in " + timeformat.format(Long.parseLong(time) / 1000.0) + " sec";
                    group.children.add(lineList);
                }

                groups.append(count, group);
                count++;
            }

            // List visualization
            ExpandableListView listView = findViewById(R.id.expandableList);
            MyExpandableListAdapter adapternew = new MyExpandableListAdapter(this, groups);
            listView.setAdapter(adapternew);


            listView.setLongClickable(true);

            // Long click
            listView.setOnItemLongClickListener(new ExpandableListView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Results.this);
                    alertDialog.setTitle("Delete single test result");
                    alertDialog.setMessage("Are you sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteSingleResults(position);
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
        } catch (FileNotFoundException f) {
            Toast.makeText(Results.this, "File not found", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method: deleteFile
     */
    private void deleteFile() {
        File myfile = null;

        if (isExternalStorageAvailable() && !isExternalStorageReadOnly())
            myfile = new File(getExternalFilesDir(DefaultValues.FOLDER_RESULTS), DefaultValues.FILE_RESULTS_NAME + "_" + APID + ".txt");

        Objects.requireNonNull(myfile).delete();
    }

    /**
     * Method: deleteFileResults
     * Delete all test of a patient (Called by button in XML)
     *
     * @param v
     */
    public void deleteAllResults(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Results.this);
        alertDialog.setTitle("Delete all test results");
        alertDialog.setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @SuppressLint("StaticFieldLeak")
                    public void onClick(DialogInterface dialog, int id) {

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
                                        .appendQueryParameter(DefaultValues.ACTION_PARAM_NAME, DefaultValues.DELETERESULTS_ALL_ACTION)
                                        .appendQueryParameter(DefaultValues.EMAIL_PARAM, doctorMail)
                                        .appendQueryParameter(DefaultValues.PASSWORD_PARAM, doctorPassword)
                                        .appendQueryParameter(DefaultValues.IDPATIENT_PARAM, String.valueOf(APID))
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
                                        // Toast (Handler)
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast t = Toast.makeText(getApplicationContext(), "Results deleted", Toast.LENGTH_LONG);
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
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nothing
                    }
                });
        alertDialog.show();
    }

    /**
     * Method: deleteRowFromFile
     * Delete single test of a patient
     *
     * @param position
     */
    @SuppressLint("StaticFieldLeak")
    private void deleteSingleResults(int position) {
        System.out.println("Group position: " + position);

        String linea = "";

        try {
            File file = null;

            if (isExternalStorageAvailable() && !isExternalStorageReadOnly())
                file = new File(getExternalFilesDir(DefaultValues.FOLDER_RESULTS), DefaultValues.FILE_RESULTS_NAME + "_" + APID + ".txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));

            int counter = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                if (counter == position)
                    linea = line;
                counter++;
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String result = linea;

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
                        .appendQueryParameter(DefaultValues.ACTION_PARAM_NAME, DefaultValues.DELETERESULT_SINGLE_ACTION)
                        .appendQueryParameter(DefaultValues.EMAIL_PARAM, doctorMail)
                        .appendQueryParameter(DefaultValues.PASSWORD_PARAM, doctorPassword)
                        .appendQueryParameter(DefaultValues.IDPATIENT_PARAM, String.valueOf(APID))
                        .appendQueryParameter(DefaultValues.IDAPP_PARAM, DefaultValues.STEREOTEST)
                        .appendQueryParameter(DefaultValues.RESULT_PARAM, result); // Insert semicolon

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
                        // Toast (Handler)
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast t = Toast.makeText(getApplicationContext(), "Result deleted", Toast.LENGTH_LONG);
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