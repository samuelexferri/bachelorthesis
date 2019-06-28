package com.unibg.app3dsat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.unibg.app3dsat.util.DefaultValues;

/**
 * Class: MainDoctor
 */
public class MainDoctor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    /**
     * SharedPreferences
     */
    private SharedPreferences SPDoctor, SPPatient;

    /**
     * Method: onCreate
     *
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_doctor);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // SharedPreferences (SPDoctor)
        SPDoctor = getSharedPreferences(DefaultValues.SPDOCTOR, MODE_PRIVATE);
        String doctorMail = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_MAIL, "404");
        String doctorName = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_NAME, "404");
        String doctorSurname = SPDoctor.getString(DefaultValues.ACTUAL_DOCTOR_SURNAME, "404");

        // SharedPreferences (SPPatient)
        SPPatient = getSharedPreferences(DefaultValues.SPPATIENT, MODE_PRIVATE);
        String patientName = SPPatient.getString(DefaultValues.ACTUAL_PATIENT_NAME, "404");
        String patientSurname = SPPatient.getString(DefaultValues.ACTUAL_PATIENT_SURNAME, "404");
        int patientId = SPPatient.getInt(DefaultValues.ACTUAL_PATIENT_ID, -1);

        // Navigation header
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main_doctor);
        TextView headerName = navHeaderView.findViewById(R.id.nav_header_doctor_name);
        TextView headerMail = navHeaderView.findViewById(R.id.nav_header_doctor_mail);
        headerName.setText(doctorName + " " + doctorSurname);
        headerMail.setText(doctorMail);

        // Navigation items
        navigationView.setNavigationItemSelectedListener(this);

        // TextView1
        TextView textView1 = findViewById(R.id.text_view1);
        textView1.setText("DOCTOR: " + doctorName + " " + doctorSurname);

        // TextView2
        TextView textView2 = findViewById(R.id.text_view2);
        textView2.setText("PATIENT: " + patientName + " " + patientSurname + " [" + patientId + "]");

        // Buttons
        Button one = findViewById(R.id.b1);
        one.setOnClickListener(this); // Calling onClick() method
        Button two = findViewById(R.id.b2);
        two.setOnClickListener(this); // Calling onClick() method
        Button three = findViewById(R.id.b3);
        three.setOnClickListener(this); // Calling onClick() method
        Button four = findViewById(R.id.b4);
        four.setOnClickListener(this); // Calling onClick() method
        Button five = findViewById(R.id.b5);
        five.setOnClickListener(this); // Calling onClick() method
    }

    /**
     * Method: onBackPressed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showPopupLogut();
        }
    }

    /**
     * Method: onNavigationItemSelected
     *
     * @param item
     * @return boolean
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here
        int id = item.getItemId();

        if (id == R.id.nav_choose_patient) {
            // Intent
            Intent intent = new Intent(MainDoctor.this, MainPatient.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            // Intent
            Intent intent = new Intent(MainDoctor.this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_site) {
            Uri uri = Uri.parse("http://se4med.unibg.it/home/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method: showPopupLogout
     */
    private void showPopupLogut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainDoctor.this);
        alert.setMessage("Are you sure to logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                }).setNegativeButton("Cancel", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    /**
     * Method: logout
     */
    private void logout() {
        // Delete previous SharedPreferences (SPDoctor)
        SharedPreferences del1 = this.getSharedPreferences("SPDoctor", Context.MODE_PRIVATE);
        del1.edit().clear().commit();

        // Delete previous SharedPreferences (SPPatient)
        SharedPreferences del2 = this.getSharedPreferences("SPPatient", Context.MODE_PRIVATE);
        del2.edit().clear().commit();

        // Intent
        Intent intent = new Intent(MainDoctor.this, LoginActivity.class);
        startActivity(intent);
        // Toast
        Toast toast = Toast.makeText(MainDoctor.this, "Logout succesfull", Toast.LENGTH_LONG);
        toast.show();
        // Finish activity on logout
        finish();
    }

    /**
     * Method: onClick
     * Called by buttons
     *
     * @param view
     */
    @Override
    public void onClick(@NonNull View view) {
        // SharedPreferences (SPPatient)
        SPPatient = getSharedPreferences(DefaultValues.SPPATIENT, MODE_PRIVATE);
        int patientId = SPPatient.getInt(DefaultValues.ACTUAL_PATIENT_ID, -1);

        switch (view.getId()) {
            case R.id.b1:
                // Intent
                Intent intent1 = new Intent(MainDoctor.this, MainPatient.class);
                startActivity(intent1);
                break;

            case R.id.b2:
                if (patientId != -1) {
                    // Intent
                    Intent intent2 = new Intent(MainDoctor.this, Test.class);
                    startActivity(intent2);
                } else {
                    // Toast
                    Toast toast = Toast.makeText(MainDoctor.this, "Select patient before starting the test", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;

            case R.id.b3:
                if (patientId != -1) {
                    // Intent
                    Intent intent3 = new Intent(MainDoctor.this, Results.class);
                    startActivity(intent3);
                } else {
                    // Toast
                    Toast toast = Toast.makeText(MainDoctor.this, "Select patient before see the results", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;

            case R.id.b4:
                // Intent
                Intent intent4 = new Intent(MainDoctor.this, Settings.class);
                startActivity(intent4);
                break;

            case R.id.b5:
                logout();
                break;

            default:
                break;
        }
    }

    /**
     * Method: onResume
     * Used to refresh an activity
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        // Refresh stuff here
        SPPatient = getSharedPreferences(DefaultValues.SPPATIENT, MODE_PRIVATE);
        String patientName = SPPatient.getString(DefaultValues.ACTUAL_PATIENT_NAME, "404");
        String patientSurname = SPPatient.getString(DefaultValues.ACTUAL_PATIENT_SURNAME, "404");
        int patientId = SPPatient.getInt(DefaultValues.ACTUAL_PATIENT_ID, -1);

        TextView textView2 = findViewById(R.id.text_view2);
        textView2.setText("PATIENT: " + patientName + " " + patientSurname + " [" + patientId + "]");
    }
}