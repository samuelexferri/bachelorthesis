package p3d4amb.sat.lib;

import p3d4amb.sat.lib.SATTest.PointType;

/**
 * Represents the points to be displayed; they are compute by the point provider
 */
public class Points {

	public int colorIntensity = 100; // Between 0 and 100

	public PointType[][] points;

	/**
	 * Instantiates a new points
	 *
	 * @param width              the width
	 * @param height             the height
	 * @param colorhintintensity the colorhintintensity between 0 and 100
	 */
	public Points(int width, int height, int colorhintintensity) {
		points = new PointType[width][height];
		colorIntensity = colorhintintensity;
	}
}