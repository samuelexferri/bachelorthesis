package p3d4amb.sat.lib;

/**
 * Builds the image as a series of points
 */
public interface PointsProvider {
	Points getPoints(int width, int height);
}