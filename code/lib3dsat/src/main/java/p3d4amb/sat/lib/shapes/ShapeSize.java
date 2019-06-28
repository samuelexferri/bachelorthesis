package p3d4amb.sat.lib.shapes;

import java.util.Random;

/**
 * Images dimension
 * 
 * GIGANTE, BIG, MEDIUM, SMALL, MICRO
 * 180, 90, 50, 25, 10 mm
 * 
 * ADATTATIVA: if (angle > 400) return 25; else return 10;
 * CASUALE causale 
 */

/**
 * The Enum ShapeSize
 */
public enum ShapeSize {
	/** The gigante */
	GIGANTE,
	/** The big */
	BIG,
	/** The medium */
	MEDIUM,
	/** The small */
	SMALL,
	/** The micro */
	MICRO,
	/** The casuale */
	CASUALE,
	/** The adattativa */
	ADATTATIVA;

	/** The constant rnd */
	private static final Random rnd = new Random();

	/** The constant sizes */
	private static final int[] sizes = { 180, 90, 50, 25, 10 };

	/**
	 * Instantiates a new shape size
	 */
	ShapeSize() {
	}

	/**
	 * Size in MM it is square (only one dimension)
	 *
	 * @param angle the angle
	 * @return the size in MM
	 */
	public int size(double angle) {
		switch (this) {
		case GIGANTE:
			return sizes[0];
		case BIG:
			return sizes[1];
		case MEDIUM:
			return sizes[2];
		case SMALL:
			return sizes[3];
		case MICRO:
			return sizes[4];
		case CASUALE:
			return sizes[rnd.nextInt(sizes.length)];
		default:
			assert this == ADATTATIVA;
			if (angle > 400)
				return 25;
			else
				return 10;
		}
	}

	/**
	 * Description
	 *
	 * @return the string
	 */
	public String description() {
		switch (this) {
		case CASUALE:
			return name();
		case ADATTATIVA:
			return name() + "(d>400:25mm, d<400:10mm)";
		default:
			return name() + "(" + size(0) + "mm)";
		}
	}
}