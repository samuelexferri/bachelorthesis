package com.unibg.app3dsat.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.nio.ByteBuffer;

import p3d4amb.sat.lib.Points;

/**
 * Class: SingleBitmapBuilder
 */
public class SingleBitmapBuilder extends BitmapBuilder {

    /**
     * Constructor: SingleBitmapBuilder
     *
     * @param testImgView
     * @param points       Matrix of PointType
     * @param bitmapWidth
     * @param bitmapHeigth
     * @param bottons
     * @param leftColor
     * @param rightColor
     * @param colorshape
     */
    public SingleBitmapBuilder(ImageView testImgView, Points points, int bitmapWidth, int bitmapHeigth, Button[] bottons, @NonNull int[] leftColor, @NonNull int[] rightColor, int colorshape) {
        super(testImgView, points, bitmapWidth, bitmapHeigth, bottons, leftColor, rightColor, colorshape);
    }

    /**
     * Method: doInBackground
     * Decode image in background
     *
     * @param v
     * @return Bitmap
     */
    @Override
    protected Bitmap doInBackground(Void... v) {

        ByteBuffer buffer = ByteBuffer.allocate(bitmapWidth * bitmapHeigth * 2);

        // Build the bitmap with points obtained
        for (int y = 0; y < bitmapHeigth; y++) {
            for (int x = 0; x < bitmapWidth; x++) {
                // Using 4 bytes
                short color = pointTypeToColorRGB_565[points.points[x][y].ordinal()];
                buffer.putShort(color);
            }
        }
        buffer.rewind();
        Bitmap b = Bitmap.createBitmap(bitmapWidth, bitmapHeigth, Bitmap.Config.RGB_565);
        b.copyPixelsFromBuffer(buffer);

        Log.d("TAG", "Created bitmap size " + b.getWidth() + "X" + b.getHeight());

        return b;
    }
}