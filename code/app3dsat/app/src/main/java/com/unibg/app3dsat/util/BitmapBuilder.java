package com.unibg.app3dsat.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import p3d4amb.sat.lib.Points;
import p3d4amb.sat.lib.SATTest;

/**
 * Class: BitmapBuilder
 * This class build the bitmap for the image test
 */
@SuppressWarnings("SameParameterValue")
@SuppressLint("StaticFieldLeak")
public abstract class BitmapBuilder extends AsyncTask<Void, Void, Bitmap> {

    private static final int[] bothColor = {255, 0, 255};
    private static final int[] coloredColor = {255, 255, 255};
    final Points points;
    final int bitmapWidth;
    final int bitmapHeigth;
    final short[] pointTypeToColorRGB_565 = new short[SATTest.PointType.values().length];
    private final ImageView testImgView;
    private final Button[] bottons;
    private final short leftBit;
    private final short rightBit;
    private final short bothBit;
    private final short coloredBit;
    private long tStart;

    /**
     * Constructor: BitmapBuilder
     * Returns a Bitmap given the matrix of PointType
     *
     * @param testImgView
     * @param points
     * @param colorshape
     */
    BitmapBuilder(ImageView testImgView, Points points, int bitmapWidth, int bitmapHeigth, Button[] bottons, int[] leftColor, int[] rightColor, int colorshape) {
        this.points = points;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeigth = bitmapHeigth;
        this.testImgView = testImgView;
        this.bottons = bottons;

        // Set left and right colors

        // Colors in bits
        leftBit = fromRGB888toRGB565(leftColor[0], leftColor[1], leftColor[2]);
        rightBit = fromRGB888toRGB565(rightColor[0], rightColor[1], rightColor[2]);
        bothBit = fromRGB888toRGB565((leftColor[0] > rightColor[0] ? leftColor[0] : rightColor[0]), (leftColor[1] > rightColor[1] ? leftColor[1] : rightColor[1]), (leftColor[2] > rightColor[2] ? leftColor[2] : rightColor[2]));

        // Find the color for the Green channel when coloring the points
        short coloredColorG = (short) (2 * colorshape);
        coloredBit = fromRGB888toRGB565(coloredColor[0], coloredColorG, coloredColor[2]);

        for (SATTest.PointType p : SATTest.PointType.values())
            pointTypeToColorRGB_565[p.ordinal()] = pointTypeToColorRGB565(p);
    }

    /**
     * Method: PointTypeToColor
     * Converts a PointType to a color
     *
     * @param point
     * @param leftColor
     * @param rightColor
     * @return int
     */
    static int PointTypeToColor(SATTest.PointType point, int[] leftColor, int[] rightColor) {
        switch (point) {
            case OFF:
                return Color.BLACK;
            case BOTH:
                return Color.rgb(bothColor[0], bothColor[1], bothColor[2]);
            case LEFT:
                return Color.rgb(leftColor[0], leftColor[1], leftColor[2]);
            case RIGHT:
                return Color.rgb(rightColor[0], rightColor[1], rightColor[2]);
            case LEFT_COLORED:
                return Color.rgb(coloredColor[0], coloredColor[1], coloredColor[2]);
            default:
                return Color.YELLOW;
        }
    }

    /**
     * Method: fromRGB888toRGB565
     * Converts RGB888 to RGB565
     *
     * @param r
     * @param g
     * @param b
     * @return short
     */
    private static short fromRGB888toRGB565(int r, int g, int b) {
        StringBuilder rBit;
        StringBuilder gBit;
        StringBuilder bBit;

        rBit = new StringBuilder(Integer.toBinaryString(r));
        gBit = new StringBuilder(Integer.toBinaryString(g));
        bBit = new StringBuilder(Integer.toBinaryString(b));

        System.out.println("Bit R " + rBit + " " + gBit + " " + bBit);

        for (int i = rBit.length(); i < 8; i++)
            rBit.insert(0, "0");

        for (int i = gBit.length(); i < 8; i++)
            gBit.insert(0, "0");

        for (int i = bBit.length(); i < 8; i++)
            bBit.insert(0, "0");

        System.out.println("Bit R after conversion " + rBit + " " + gBit + " " + bBit);

        String finalconverter = gBit.substring(3, 6) + bBit.substring(0, 5) + rBit.substring(0, 5) + gBit.substring(0, 3);

        System.out.println("Bit R after last conversion " + gBit.substring(3, 6) + bBit.substring(0, 5) + rBit.substring(0, 5) + gBit.substring(0, 3));

        short finalconversion = 0;

        for (int i = 0; i < 16; i++)
            finalconversion += Short.parseShort(finalconverter.substring(i, i + 1)) * Math.pow(2, 15 - i);

        System.out.println("Final conversion " + finalconversion);

        return finalconversion;
    }

    /**
     * Method: doInBackground
     * Decode image in background
     *
     * @param v
     * @return abstract
     */
    @Override
    abstract protected Bitmap doInBackground(Void... v);

    /**
     * Method: onPostExecute
     * Once complete, see if ImageView is still around and set bitmap
     *
     * @param bitmap
     */
    @Override
    protected void onPostExecute(@Nullable Bitmap bitmap) {
        Log.d("TAG", "setImageBitmap");

        if (testImgView != null && bitmap != null) {
            testImgView.setImageBitmap(bitmap);
            tStart = System.currentTimeMillis();
        }

        for (Button b : bottons) b.setEnabled(true);
    }

    /**
     * Method: getElapsedTime
     * Elapsed time from when the activity has finisehd to now
     *
     * @return long
     */
    public long getElapsedTime() {
        long tEnd = System.currentTimeMillis();
        return tEnd - tStart;
    }

    /**
     * Method: pointTypeToColorRGB565
     * Each pixel is stored on 2 bytes and only the RGB channels are encoded: red is stored with 5 bits of precision (32 possible values), green is stored with 6 bits of precision (64 possible values) and blue is stored with 5 bits of precision
     * Attention: bit is inverted
     * http://www.rinkydinkelectronics.com/calc_rgb565.php
     * http://www.binaryconvert.com/convert_signed_short.html
     *
     * @param point
     * @return short
     */
    private short pointTypeToColorRGB565(SATTest.PointType point) {
        switch (point) {
            case OFF:
                return 0;
            case LEFT:
                return leftBit;
            case BOTH:
                return bothBit;
            case RIGHT:
                return rightBit;
            case LEFT_COLORED:
                return coloredBit;
            default:
                throw new RuntimeException();
        }
    }
}