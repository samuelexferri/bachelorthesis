package com.unibg.app3dsat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.unibg.app3dsat.util.DefaultValues;

import java.util.Objects;

import p3d4amb.sat.lib.shapes.ImageShape;

/**
 * Class: Settings
 */
public class Settings extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    /**
     * SharedPreferences
     */
    private SharedPreferences SPSettings;

    /**
     * SetColors
     */
    private SeekBar redL;
    private SeekBar greenL;
    private SeekBar blueL;
    private SeekBar redR;
    private SeekBar greenR;
    private SeekBar blueR;
    private Button btnR;
    private Button btnL;

    /**
     * Views of the layout
     */
    private EditText edit_maxdisparity;
    private EditText edit_distance;
    private RadioGroup radioGroupImageSet;

    /**
     * Loaded values
     */
    private int loaded_maxdisparity;
    private int loaded_distance;
    private ImageShape.ImageSet loaded_imageset;

    /**
     * Method: onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // SharedPreferences
        SPSettings = getSharedPreferences(DefaultValues.SPSETTINGS, MODE_PRIVATE);

        // View of the layout
        edit_distance = findViewById(R.id.edit_distance);
        edit_maxdisparity = findViewById(R.id.edit_maxdisparity);
        radioGroupImageSet = findViewById(R.id.radio_ImageSet);
        loadPreferences();

        // Loaded values
        edit_distance.setText(String.valueOf(loaded_distance));
        edit_maxdisparity.setText(String.valueOf(loaded_maxdisparity));
        RadioButton r = (findViewById(getRIDOF(loaded_imageset)));
        r.setChecked(true);

        // SetColors
        btnL = findViewById(R.id.button_lefteye);
        btnR = findViewById(R.id.button_righteye);

        redL = findViewById(R.id.seekBarRL);
        greenL = findViewById(R.id.seekBarGL);
        blueL = findViewById(R.id.seekBarBL);
        redR = findViewById(R.id.seekBarRR);
        greenR = findViewById(R.id.seekBarGR);
        blueR = findViewById(R.id.seekBarBR);

        redL.setOnSeekBarChangeListener(this);
        greenL.setOnSeekBarChangeListener(this);
        blueL.setOnSeekBarChangeListener(this);
        redR.setOnSeekBarChangeListener(this);
        greenR.setOnSeekBarChangeListener(this);
        blueR.setOnSeekBarChangeListener(this);

        // Update values with preferences
        redL.setProgress(SPSettings.getInt(DefaultValues.RED_L, DefaultValues.CURRENT_RED_L));
        greenL.setProgress(SPSettings.getInt(DefaultValues.GREEN_L, DefaultValues.CURRENT_GREEN_L));
        blueL.setProgress(SPSettings.getInt(DefaultValues.BLUE_L, DefaultValues.CURRENT_BLUE_L));
        redR.setProgress(SPSettings.getInt(DefaultValues.RED_R, DefaultValues.CURRENT_RED_R));
        greenR.setProgress(SPSettings.getInt(DefaultValues.GREEN_R, DefaultValues.CURRENT_GREEN_R));
        blueR.setProgress(SPSettings.getInt(DefaultValues.BLUE_R, DefaultValues.CURRENT_BLUE_R));
    }

    /**
     * Method: getImageSet
     * Get the ImageSet with the Id of the radio button checked
     *
     * @param radioID
     * @return ImageSet
     */
    @NonNull
    private ImageShape.ImageSet getImageSet(int radioID) {
        switch (radioID) {
            case R.id.radio_Lang:
                return ImageShape.ImageSet.LANG;
            case R.id.radio_Lea:
                return ImageShape.ImageSet.LEA;
            case R.id.radio_LeaContour:
                return ImageShape.ImageSet.LEA_CONTORNO;
            case R.id.radio_Letters:
                return ImageShape.ImageSet.LETTERS;
            case R.id.radio_Pacman:
                return ImageShape.ImageSet.PACMAN;
            case R.id.radio_Tno:
                return ImageShape.ImageSet.TNO;
            default:
                return ImageShape.ImageSet.LANG;
        }
    }

    /**
     * Get the ID of the Radio Button corresponding the given ImageSet
     *
     * @param imageSet
     * @return int
     */
    private int getRIDOF(ImageShape.ImageSet imageSet) {
        switch (imageSet) {
            case LANG:
                return R.id.radio_Lang;
            case LEA:
                return R.id.radio_Lea;
            case LEA_CONTORNO:
                return R.id.radio_LeaContour;
            case LETTERS:
                return R.id.radio_Letters;
            case PACMAN:
                return R.id.radio_Pacman;
            case TNO:
                return R.id.radio_Tno;
            default:
                return R.id.radio_Lang;
        }
    }

    /**
     * Method: loadPreferences
     * Method used to load preferences and save them to the variable
     */
    private void loadPreferences() {
        loaded_maxdisparity = SPSettings.getInt(DefaultValues.PREF_MAXDISPARITY, DefaultValues.DEFAULT_MAXDISPARITY);
        loaded_distance = SPSettings.getInt(DefaultValues.PREF_DISTANCE, DefaultValues.DEFAULT_DISTANCE);
        loaded_imageset = DefaultValues.stringToImageSet(Objects.requireNonNull(SPSettings.getString(DefaultValues.PREF_IMAGESET, "NOT FOUND")));
    }

    /**
     * Method: back
     * Exit without saving modifies, loads the previous loaded values before any changes
     *
     * @param view
     */
    public void back(View view) {
        SharedPreferences.Editor e = SPSettings.edit();
        e.putInt(DefaultValues.PREF_MAXDISPARITY, loaded_maxdisparity);
        e.putInt(DefaultValues.PREF_DISTANCE, loaded_distance);
        e.putString(DefaultValues.PREF_IMAGESET, loaded_imageset.name());
        e.apply();

        Intent intent = new Intent(this, MainDoctor.class);
        startActivity(intent);
    }

    /**
     * Method: reset
     * Reset the settings to DefaultValues
     *
     * @param view
     */
    public void reset(View view) {
        SharedPreferences.Editor e = SPSettings.edit();
        e.putInt(DefaultValues.PREF_MAXDISPARITY, DefaultValues.DEFAULT_MAXDISPARITY);
        e.putInt(DefaultValues.PREF_DISTANCE, DefaultValues.DEFAULT_DISTANCE);
        e.putString(DefaultValues.PREF_IMAGESET, DefaultValues.DEFAULT_IMAGESET.name());
        e.apply();

        resetColors();

        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish(); // Avoid multiple Settings activity
    }

    /**
     * Method: confirm
     * Save the modified values
     *
     * @param view
     */
    public void confirm(View view) {
        SharedPreferences.Editor e = SPSettings.edit();
        e.putInt(DefaultValues.PREF_MAXDISPARITY, Integer.parseInt(edit_maxdisparity.getText().toString()));
        e.putInt(DefaultValues.PREF_DISTANCE, Integer.parseInt(edit_distance.getText().toString()));
        e.putString(DefaultValues.PREF_IMAGESET, getImageSet(radioGroupImageSet.getCheckedRadioButtonId()).name());
        e.apply();

        confirmColors();

        Intent intent = new Intent(this, MainDoctor.class);
        startActivity(intent);
    }

    /**
     * Method: onProgressChanged
     * SetColors seekBar
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        btnL.setBackgroundColor(Color.rgb(redL.getProgress(), greenL.getProgress(), blueL.getProgress()));
        btnR.setBackgroundColor(Color.rgb(redR.getProgress(), greenR.getProgress(), blueR.getProgress()));
    }

    /**
     * Method: onStartTrackingTouch
     * SetColors seekBar
     *
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /**
     * Method: onStopTrackingTouch
     * SetColors seekBar
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    /**
     * Method: confirmColors
     * Confirm colors
     */
    private void confirmColors() {
        SharedPreferences.Editor e = SPSettings.edit();
        e.putInt(DefaultValues.RED_L, redL.getProgress());
        e.putInt(DefaultValues.BLUE_L, blueL.getProgress());
        e.putInt(DefaultValues.GREEN_L, greenL.getProgress());
        e.putInt(DefaultValues.RED_R, redR.getProgress());
        e.putInt(DefaultValues.BLUE_R, blueR.getProgress());
        e.putInt(DefaultValues.GREEN_R, greenR.getProgress());
        e.apply();
    }

    /**
     * Method: resetColors
     * Reset colors
     */
    private void resetColors() {
        SharedPreferences.Editor e = SPSettings.edit();
        e.putInt(DefaultValues.RED_L, DefaultValues.CURRENT_RED_L);
        e.putInt(DefaultValues.BLUE_L, DefaultValues.CURRENT_BLUE_L);
        e.putInt(DefaultValues.GREEN_L, DefaultValues.CURRENT_GREEN_L);
        e.putInt(DefaultValues.RED_R, DefaultValues.CURRENT_RED_R);
        e.putInt(DefaultValues.BLUE_R, DefaultValues.CURRENT_BLUE_R);
        e.putInt(DefaultValues.GREEN_R, DefaultValues.CURRENT_GREEN_R);
        e.apply();
    }
}