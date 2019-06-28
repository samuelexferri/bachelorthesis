package p3d4amb.sat.lib.background;

/**
 * Represents the image done by a set of points
 */
public abstract class PointImage {

	/** The Constant MAX_WIDTH */
	protected final int MAX_WIDTH;

	/** The Constant MAX_HEIGHT */
	protected final int MAX_HEIGHT;

	/** The threshold */
	private static int threshold = 20;

	/**
	 * The points data. each point can have a byte value the greater is the value,
	 * the greter is that change that it will be illuminated
	 */
	byte[] pointsData;

	/**
	 * Instantiates a new point image
	 *
	 * @param MAX_WIDTH  the max width
	 * @param MAX_HEIGHT the max height
	 */
	protected PointImage(int MAX_WIDTH, int MAX_HEIGHT) {
		assert MAX_HEIGHT > 0 && MAX_WIDTH > 0;
		this.MAX_WIDTH = MAX_WIDTH;
		this.MAX_HEIGHT = MAX_HEIGHT;
		final int MAX_NUM_POINTS = MAX_WIDTH * MAX_HEIGHT;
		pointsData = new byte[MAX_NUM_POINTS];
	}

	/**
	 * Positive
	 *
	 * @param x the x
	 * @param y the y
	 * @return true id the point must be shown
	 */
	public boolean positive(int x, int y) {
		return pointsData[y * MAX_WIDTH + x] > threshold;
	}

	/**
	 * Generate a new set of points
	 */
	public abstract void reinit();
}