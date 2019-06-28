package p3d4amb.sat.lib;

/**
 * Information about the screen where the images will be displayed; necessary to
 * compute some other info, like points and so on
 */
public class MonitorData {

	/**
	 * Instantiates a new monitor data
	 *
	 * @param monitorSize10thInc the monitor size10th inc
	 * @param monitorWidthPixels the monitor width pixels
	 * @param monitorWidthMM     the monitor width mm
	 * @param monitorHeightPixel
	 * @param monitorDistance    the monitor distance in centimeters
	 */
	public MonitorData(double monitorSize10thInc, int monitorWidthPixels, int monitorWidthMM, int monitorHeightPixels,
			int monitorDistance) {
		assert monitorSize10thInc > 0;
		assert monitorWidthPixels > 0;
		assert monitorWidthMM > 0;
		assert monitorHeightPixels > 0;
		assert monitorDistance > 0;
		this.monitorSize10thInc = monitorSize10thInc;
		this.monitorWidthPixels = monitorWidthPixels;
		this.monitorWidthMM = monitorWidthMM;
		this.monitorDistance = monitorDistance;
		this.monitorHeightPixels = monitorHeightPixels;
	}

	/** The monitor size10th inc */
	double monitorSize10thInc;

	/** The monitor width pixels */
	int monitorWidthPixels;

	/** The monitor width pixels */
	int monitorHeightPixels;

	/** The monitor width mm */
	int monitorWidthMM;

	/** The monitor distance */
	int monitorDistance;
}