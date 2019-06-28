package com.unibg.app3dsat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unibg.app3dsat.util.BitmapBuilder;
import com.unibg.app3dsat.util.BitmapParallelBuilder;
import com.unibg.app3dsat.util.DefaultValues;
import com.unibg.app3dsat.util.SingleBitmapBuilder;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import p3d4amb.sat.lib.MonitorData;
import p3d4amb.sat.lib.Points;
import p3d4amb.sat.lib.SATTest;
import p3d4amb.sat.lib.shapes.ImageShape;
import p3d4amb.sat.lib.shapes.Shape;
import p3d4amb.sat.lib.shapes.ShapeSize;

/**
 * Class: Test
 */
@SuppressWarnings("unused")
public class Test extends AppCompatActivity implements Observer {

    /**
     * Constants
     */
    private static final int DECREASE_COLOR_SHAPE = 50;
    private static final DecimalFormat df = new DecimalFormat("#");
    private static final String TAG = Test.class.getSimpleName();
    private static final boolean USE_MULTIPLE_PORCESSES = false;

    /**
     * Constants loaded from the preferences or set to DefaultValues
     */
    private static int MAXDISPARITY;
    private static int OFFSET;
    private static int NCORR_TO_NEXTLEVEL;
    private static int NERR_TO_STOPTEST;

    /**
     * Color to be used for the anaglyph glasses
     */
    private final int[] leftColor = {255, 0, 0};
    private final int[] rightColor = {0, 0, 255};

    /**
     * Array of Shapes
     */
    private Shape[] shapes;

    /**
     * Display with and height pixels
     */
    private int disWidthPix, disHeigthPix;
    private float disWidthPixPerInch, disHeigthPixPerInch;

    /**
     * SharedPreferences
     */
    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences SPPatient;
    private SharedPreferences SPSettings;

    /**
     * Test variables
     */
    // Session of all the test
    private int colorshape = 100;

    // Set of Images
    private ImageShape.ImageSet imageSet;

    // ImageView containing the anaglyph image
    private ImageView testImgView;

    // The waitBitmap
    private Bitmap waitBitMap;

    // The current SATTest
    private SATTest satTest;

    // The builder of the image
    private BitmapBuilder builder;

    /**
     * Method: onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // SharedPreferences (SPPatient, SPSettings)
        SPPatient = getSharedPreferences(DefaultValues.SPPATIENT, MODE_PRIVATE);
        SPSettings = getSharedPreferences(DefaultValues.SPSETTINGS, MODE_PRIVATE);
        loadPreferences();

        // Measure display
        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        {
            disWidthPix = dm.widthPixels;
            disHeigthPix = dm.heightPixels;
            disWidthPixPerInch = dm.xdpi;
            disHeigthPixPerInch = dm.ydpi;

            // Includes window decorations (Statusbar bar, Menu bar)
            try {
                // Used when SDK_INT >= 17; includes window decorations (Statusbar bar, Menu bar)
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(dm, realSize);
                disWidthPix = realSize.x;
                disHeigthPix = realSize.y;
            } catch (Exception ignored) {
            }
        }

        double x = Math.pow(disWidthPix / dm.xdpi, 2);
        double y = Math.pow(disHeigthPix / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);

        // Set monitorData
        double monitorSize10thInc = screenInches * 10;
        int monitorWidthMM = (int) (disWidthPix / dm.xdpi * 25.4);
        int monitorDistance = SPSettings.getInt(DefaultValues.PREF_DISTANCE, DefaultValues.DEFAULT_DISTANCE);

        // Save display measures into monitorData
        // MonitorData used
        MonitorData monitorData = new MonitorData(monitorSize10thInc, disWidthPix, monitorWidthMM, dm.heightPixels, monitorDistance);

        Log.d(TAG, "MonitorData " + monitorSize10thInc + " " + disWidthPix + " " + monitorWidthMM + " " + monitorDistance);

        // Identify layouts
        testImgView = findViewById(R.id.testImgView);
        Button btn_1 = findViewById(R.id.btn_1);
        Button btn_2 = findViewById(R.id.btn_2);
        Button btn_3 = findViewById(R.id.btn_3);
        Button btn_4 = findViewById(R.id.btn_4);

        // Set buttons based on current ImageSet, loaded through R.drawable
        switch (imageSet) {
            case LANG: {
                btn_1.setBackgroundResource(R.drawable.btn_lang_bird);
                btn_2.setBackgroundResource(R.drawable.btn_lang_car);
                btn_3.setBackgroundResource(R.drawable.btn_lang_cat);
                btn_4.setBackgroundResource(R.drawable.btn_lang_circle);
                break;
            }
            case LEA: {
                btn_1.setBackgroundResource(R.drawable.btn_lea_apple);
                btn_2.setBackgroundResource(R.drawable.btn_lea_circle);
                btn_3.setBackgroundResource(R.drawable.btn_lea_house);
                btn_4.setBackgroundResource(R.drawable.btn_lea_square);
                break;
            }
            case LEA_CONTORNO: {
                btn_1.setBackgroundResource(R.drawable.btn_contour_apple);
                btn_2.setBackgroundResource(R.drawable.btn_contour_circle);
                btn_3.setBackgroundResource(R.drawable.btn_contour_house);
                btn_4.setBackgroundResource(R.drawable.btn_contour_square);
                break;
            }
            case LETTERS: {
                btn_1.setBackgroundResource(R.drawable.btn_letter_a);
                btn_2.setBackgroundResource(R.drawable.btn_letter_c);
                btn_3.setBackgroundResource(R.drawable.btn_letter_e);
                btn_4.setBackgroundResource(R.drawable.btn_letter_k);
                break;
            }
            case PACMAN: {
                btn_1.setBackgroundResource(R.drawable.btn_pacman_d);
                btn_2.setBackgroundResource(R.drawable.btn_pacman_l);
                btn_3.setBackgroundResource(R.drawable.btn_pacman_r);
                btn_4.setBackgroundResource(R.drawable.btn_pacman_u);
                break;
            }
            case TNO: {
                btn_1.setBackgroundResource(R.drawable.btn_tno_circle);
                btn_2.setBackgroundResource(R.drawable.btn_tno_square);
                btn_3.setBackgroundResource(R.drawable.btn_tno_star);
                btn_4.setBackgroundResource(R.drawable.btn_tno_triangle);
                break;
            }
        }

        loadPreferences();

        // Enum size of the Shape (SMALL, MEDIUM, BIG)
        ShapeSize shapeSize = DefaultValues.getSize(monitorSize10thInc);

        // Prepare Shapes
        shapes = new Shape[DefaultValues.POSSIBLECHOICES];
        List<ImageShape> ii = ImageShape.getShapes(imageSet);

        for (int i = 0; i < DefaultValues.POSSIBLECHOICES; i++) {
            shapes[i] = ii.get(i);

            Log.d(TAG, "Shapes[" + i + "] is " + shapes[i].toString());
        }
        // Build bitmap for wait message
        waitBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.wait_image);

        Log.d(TAG, "Wait bitmap " + waitBitMap.getHeight());

        // Build test
        satTest = new SATTest(MAXDISPARITY, shapes, false, monitorData, shapeSize);

        Log.v(TAG, "Demo mode " + satTest.isIndemomode() + " intensity " + colorshape);

        SATTest.setColorShape(colorshape);

        // Register this
        satTest.addObserver(this);

        // Start test
        nextShape();
    }

    /**
     * Method: nextShape
     */
    private void nextShape() {
        // Test finished?
        if (!satTest.hasNextShape()) {
            finishTest();
        } else {
            testImgView.setImageBitmap(waitBitMap);

            // Make smallest bitmap
            float heigthReduction = 0.75f;

            // Change shape and position
            satTest.setNextShape();

            // Get the new depth
            int nextDepth = satTest.getCurrentDepth();

            // Show the depth
            double angle = satTest.depthAngle(nextDepth);

            Button[] bottons = {findViewById(R.id.btn_1), findViewById(R.id.btn_2), findViewById(R.id.btn_3), findViewById(R.id.btn_4)};

            Points points = satTest.getPoints(disWidthPix, (int) (disHeigthPix * heigthReduction));

            System.out.println("LEFT COLOR: " + leftColor[0] + " " + leftColor[1] + " " + leftColor[2]);
            System.out.println("RIGHT COLOR: " + rightColor[0] + " " + rightColor[1] + " " + rightColor[2]);

            // Get points
            if (colorshape > 0) {
                colorshape -= DECREASE_COLOR_SHAPE;
            } else if (colorshape == 0) {
                Button button = findViewById(R.id.btn_starttest);
                button.setEnabled(false);
                startTest();
            }

            if (USE_MULTIPLE_PORCESSES)
                builder = new BitmapParallelBuilder(testImgView, points, disWidthPix, (int) (disHeigthPix * heigthReduction), bottons, leftColor, rightColor, colorshape);    //fai costruire la bitmap
            else
                builder = new SingleBitmapBuilder(testImgView, points, disWidthPix, (int) (disHeigthPix * heigthReduction), bottons, leftColor, rightColor, colorshape);    //fai costruire la bitmap

            builder.execute();

            Log.d(TAG, satTest.getCurrentStatus().toString() + " " + satTest.getCurrentShape() + " " + satTest.getCurrentDepth() + " demo?" + satTest.isIndemomode());

            // Disable buttons
            findViewById(R.id.btn_1).setEnabled(false);
            findViewById(R.id.btn_2).setEnabled(false);
            findViewById(R.id.btn_3).setEnabled(false);
            findViewById(R.id.btn_4).setEnabled(false);

            TextView statusView = findViewById(R.id.statusView);
            statusView.setText(satTest.isIndemomode() ? "Demo (" + colorshape + ")" : (df.format(angle) + "\" (" + nextDepth + ")"));

            // Change also the color for the demo mode (Visibility)
            if (satTest.isIndemomode())
                SATTest.setColorShape(colorshape);
        }
    }

    /**
     * Method: loadPreferences
     * Method used to load preferences and save them to the variable
     */
    private void loadPreferences() {
        MAXDISPARITY = SPSettings.getInt(DefaultValues.PREF_MAXDISPARITY, DefaultValues.DEFAULT_MAXDISPARITY);
        OFFSET = SPSettings.getInt(DefaultValues.PREF_OFFSET, DefaultValues.DEFAULT_OFFSET);
        NCORR_TO_NEXTLEVEL = SPSettings.getInt(DefaultValues.PREF_NCORR_TO_NEXTLEVEL, DefaultValues.DEFAULT_NCORR_TONEXTLEVE);
        NERR_TO_STOPTEST = SPSettings.getInt(DefaultValues.PREF_NERR_TOSTOPTEST, DefaultValues.DEFAULT_NERR_TOSTOPTEST);
        imageSet = DefaultValues.stringToImageSet(Objects.requireNonNull(SPSettings.getString(DefaultValues.PREF_IMAGESET, "NOT FOUND")));

        leftColor[0] = SPSettings.getInt(DefaultValues.RED_L, DefaultValues.CURRENT_RED_L);
        leftColor[1] = SPSettings.getInt(DefaultValues.GREEN_L, DefaultValues.CURRENT_GREEN_L);
        leftColor[2] = SPSettings.getInt(DefaultValues.BLUE_L, DefaultValues.CURRENT_BLUE_L);
        rightColor[0] = SPSettings.getInt(DefaultValues.RED_R, DefaultValues.CURRENT_RED_R);
        rightColor[1] = SPSettings.getInt(DefaultValues.GREEN_R, DefaultValues.CURRENT_GREEN_R);
        rightColor[2] = SPSettings.getInt(DefaultValues.BLUE_R, DefaultValues.CURRENT_BLUE_R);

        savePreferences();
    }

    /**
     * Method: savePreferences
     * Method used to save the actual variables into the SharedPreferences
     */
    private void savePreferences() {
        SharedPreferences.Editor e = SPSettings.edit();
        e.putInt(DefaultValues.PREF_MAXDISPARITY, MAXDISPARITY);
        e.putInt(DefaultValues.PREF_OFFSET, OFFSET);
        e.putInt(DefaultValues.PREF_NERR_TOSTOPTEST, NERR_TO_STOPTEST);
        e.putInt(DefaultValues.PREF_NCORR_TO_NEXTLEVEL, NCORR_TO_NEXTLEVEL);
        e.putString(DefaultValues.PREF_IMAGESET, imageSet.name());
        e.apply();
    }

    /**
     * Method: OnClickSkip
     * This button handles the Skip-Button
     * Generates a new Test with the same disparity level not increasing the "n" variable
     *
     * @param view
     */
    public void OnClickSkip(View view) {
        SATTest.ChoiceResult res = satTest.solutionChosen(null);
        showResultGoNext();

        Log.d(TAG, "Pressed skip button");
    }

    /**
     * Method: OnClickTest
     * This button handles the Start-Button
     * Start the real test
     *
     * @param view
     */
    public void OnClickTest(View view) {
        startTest();
    }

    /**
     * Method: startTest
     */
    private void startTest() {
        if (satTest.isIndemomode()) {
            satTest.exitDemoMode();
            SATTest.setColorShape(0);
            nextShape();

            // Disable button, test started
            Button button = findViewById(R.id.btn_starttest);
            button.setEnabled(false);

            Log.d(TAG, "Starting actual test");
        }
    }

    /**
     * Method: OnClickFirst
     * This button handles the First-Button
     * Method called when the user click the first button
     *
     * @param view
     */
    public void OnClickFirst(View view) {
        OnClickButton(0);
    }

    /**
     * Method: OnClickSecond
     * This button handles the Second-Button
     * Method called when the user click the second button
     *
     * @param view
     */
    public void OnClickSecond(View view) {
        OnClickButton(1);
    }

    /**
     * Method: OnClickThird
     * This button handles the Third-Button
     * Method called when the user click the third button
     *
     * @param view
     */
    public void OnClickThird(View view) {
        OnClickButton(2);
    }

    /**
     * Method: OnClickFourth
     * This button handles the Fourth-Button
     * Method called when the user click the fourth button
     *
     * @param view
     */
    public void OnClickFourth(View view) {
        OnClickButton(3);
    }

    /**
     * Method: OnClickButton
     *
     * @param buttonNumber
     */
    @SuppressWarnings("unused")
    private void OnClickButton(int buttonNumber) {
        // Get the chose shape
        Shape cs = shapes[buttonNumber];

        SATTest.ChoiceResult res = satTest.solutionChosen(cs, builder.getElapsedTime());
        showResultGoNext();

        Log.d(TAG, "Pressed button " + (buttonNumber + 1) + " with " + cs + " after " + builder.getElapsedTime());
    }

    /**
     * Method: showResultGoNext
     * Show the feedback and set the new image
     */
    private void showResultGoNext() {
        // Proceed to the next shape
        nextShape();
    }

    /**
     * Method: finishTest
     */
    private void finishTest() {
        Log.d(TAG, "Test finished");

        // Intent
        Intent i = new Intent(Test.this, TestResults.class);

        // Pass the data
        i.putExtra(DefaultValues.CURRENT_IMAGE_SET, imageSet.toString() + " - " + satTest.getCurrentStatus().currentResult.toString());
        i.putExtra(DefaultValues.WIDTH_PIX, disWidthPix);
        i.putExtra(DefaultValues.HEIGHT_PIX, disHeigthPix);
        i.putExtra(DefaultValues.WIDTH_PIX_PER_INCH, disWidthPixPerInch);
        i.putExtra(DefaultValues.HEIGHT_PIX_PER_INCH, disHeigthPixPerInch);

        // Store the session data
        List<String> sessionResults = satTest.getSessionResults();

        // JSON Session data
        JSONArray jasonresults = new JSONArray();

        for (String s : sessionResults) {
            jasonresults.put(s);
        }

        i.putExtra(DefaultValues.SESSION_DATA, jasonresults.toString());

        finish();
        startActivity(i);
        this.finish();
    }

    /**
     * Method: OnClickQuit
     * This button handles the Quit-Button
     * End the test
     *
     * @param view
     */
    public void OnClickQuit(View view) {
        Log.d(TAG, "Pressed quit button");

        if (!satTest.isIndemomode()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Test.this);
            alertDialog.setTitle("Saving results");
            alertDialog.setMessage("Do you want to save the results?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishTest();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            goToMain();
                        }
                    });
            alertDialog.show();
        } else {
            goToMain();
        }
    }

    /**
     * Method: goToMain
     */
    private void goToMain() {
        // Intent
        Intent intent = new Intent(this, MainDoctor.class);
        startActivity(intent);
    }

    /**
     * Method: update
     *
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable == satTest) {
            SATTest test = (SATTest) observable;

            // For now, only the finished event (Demo mode)
            if (!test.isIndemomode())
                startTest();
        }
    }
}