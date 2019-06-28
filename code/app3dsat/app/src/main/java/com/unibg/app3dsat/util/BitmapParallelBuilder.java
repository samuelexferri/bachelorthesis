package com.unibg.app3dsat.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import p3d4amb.sat.lib.Points;
import p3d4amb.sat.lib.SATTest;

/**
 * Class: BitmapParallelBuilder
 * This class build the bitmap for the image test in parallel
 */
@SuppressWarnings("ALL")
public class BitmapParallelBuilder extends BitmapBuilder {

    private static final int N_IMAGES_IN_PARALLEL = 8;

    /**
     * Constructor: BitmapParallelBuilder
     * Returns a Bitmap given the matrix of PointType
     *
     * @param testImgView
     * @param points
     */
    public BitmapParallelBuilder(ImageView testImgView, Points points, int bitmapWidth, int bitmapHeigth, Button[] bottons, @NonNull int[] leftColor, @NonNull int[] rightColor, int colorshape) {
        super(testImgView, points, bitmapWidth, bitmapHeigth, bottons, leftColor, rightColor, colorshape);
    }

    /**
     * Method: doInBackground
     * Decode image in background
     *
     * @param v
     * @return Bitmap
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Bitmap doInBackground(Void... v) {
        // Compute step
        final int stepHeigh = bitmapHeigth / N_IMAGES_IN_PARALLEL;

        // Run the processes
        ExecutorService executor = Executors.newFixedThreadPool(N_IMAGES_IN_PARALLEL);

        // Run threads
        FutureTask<Bitmap>[] futureTask;
        futureTask = new FutureTask[N_IMAGES_IN_PARALLEL];

        for (int pr = 0; pr < N_IMAGES_IN_PARALLEL; pr++) {
            // Compute this heigh for the last one could be smaller
            int thisHeigh = Math.min(stepHeigh, bitmapHeigth - stepHeigh * pr);
            PartilalImageBuilder bp = new PartilalImageBuilder(bitmapWidth, thisHeigh, stepHeigh * pr, points.points);
            futureTask[pr] = new FutureTask<Bitmap>(bp);
            executor.execute(futureTask[pr]);

            Log.d("TAG", "Creating image bitmap number " + pr + " with heigh " + thisHeigh);
        }

        Bitmap result = Bitmap.createBitmap(bitmapWidth, super.bitmapHeigth, Bitmap.Config.RGB_565);//ARGB_8888);
        Canvas canvas = new Canvas(result);

        // Get results and build canvas
        for (int pr = 0; pr < N_IMAGES_IN_PARALLEL; pr++) {
            try {
                canvas.drawBitmap(futureTask[pr].get(), 0f, stepHeigh * pr, null);

                Log.d("TAG", "Getting image " + pr + " at heigh " + stepHeigh * pr);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}

/**
 * Class: PartialImageBuilder
 */
class PartilalImageBuilder implements Callable<Bitmap> {

    private static final int[] leftColor = {255, 0, 0};
    private static final int[] rightColor = {0, 0, 255};
    private final SATTest.PointType[][] points;
    private final int starty;
    private final int bitmapWidth;
    private final int stepHeigh;

    /**
     * Constructor: PartilalImageBuilder
     *
     * @param bw     Whidth of the image
     * @param bh     Heigth of the image
     * @param starty Starting point to draw
     * @param points
     */
    PartilalImageBuilder(int bw, int bh, int starty, SATTest.PointType[][] points) {
        bitmapWidth = bw;
        stepHeigh = bh;
        this.points = points;
        this.starty = starty;
    }

    /**
     * Method: call
     *
     * @return Bitmap
     * @throws Exception
     */
    @Override
    public Bitmap call() {
        Bitmap result = Bitmap.createBitmap(bitmapWidth, stepHeigh, Bitmap.Config.RGB_565);

        for (int y = 0; y < stepHeigh; y++) {
            for (int x = 0; x < bitmapWidth; x++) {
                result.setPixel(x, y, BitmapBuilder.PointTypeToColor(points[x][y + starty], leftColor, rightColor));
            }
        }

        return result;
    }
}