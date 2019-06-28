package p3d4amb.sat.lib.background;

import java.util.Random;

/**
 * Random dots
 */
public class RandomDotImage extends PointImage {

	/** The constant rnd */
	private static final Random rnd = new Random();

	/**
	 * Instantiates a new random dot image
	 *
	 * @param MAX_WIDTH  the max width
	 * @param MAX_HEIGHT the max height
	 */
	public RandomDotImage(int MAX_WIDTH, int MAX_HEIGHT) {
		super(MAX_WIDTH, MAX_HEIGHT);
		reinit();
	}

	/**
	 * Generate a new set of points
	 */
	@Override
	public void reinit() {
		rnd.nextBytes(pointsData);
	}
}