package p3d4amb.sat.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import org.apache.log4j.Logger;

import p3d4amb.sat.lib.background.PointImage;
import p3d4amb.sat.lib.background.RandomDotImage;
import p3d4amb.sat.lib.background.StripesImage;
import p3d4amb.sat.lib.session.DepthCertBase.CertifierStatus;
import p3d4amb.sat.lib.session.TestSession;
import p3d4amb.sat.lib.session.TestSession.SingleAnswer;
import p3d4amb.sat.lib.shapes.ImageShape;
import p3d4amb.sat.lib.shapes.ImageShape.ImageSet;
import p3d4amb.sat.lib.shapes.NullShape;
import p3d4amb.sat.lib.shapes.Shape;
import p3d4amb.sat.lib.shapes.ShapeSize;

/**
 * Main class to build all the data useful for the stereo acuity test; it's
 * observable regarding its state (demo mode for example)
 */
public class SATTest extends Observable implements PointsProvider {

	/** The Constant rnd */
	private static final Random rnd = new Random();

	/** The Constant logger */
	private static final Logger logger = Logger.getLogger(SATTest.class);

	private MonitorData monitorData;

	/** The shapes */
	// The shapes for this experiment
	public List<Shape> shapes;

	/** required shape size */
	private ShapeSize shapeSize;

	/** test session */
	private TestSession session;

	/** The indemomode */
	// It is in demo mode, do not count and do not change shape and pos
	private boolean indemomode;

	/**
	 * Instantiates a new SAT test
	 *
	 * @param initialDepth     the initial depth
	 * @param imageSet         the image set
	 * @param includeNullShape the include null shape
	 */
	public SATTest(int initialDepth, ImageSet imageSet, boolean includeNullShape, MonitorData monitorData,
			ShapeSize ss) {
		this(initialDepth, ImageShape.getShapes(imageSet), includeNullShape, monitorData, ss);
	}

	/**
	 * SATTest
	 * 
	 * @param initialDepth
	 * @param imageSet
	 * @param includeNullShape
	 * @param monitorData
	 * @param ss
	 */
	public SATTest(int initialDepth, Shape[] imageSet, boolean includeNullShape, MonitorData monitorData,
			ShapeSize ss) {
		this(initialDepth, Arrays.asList(imageSet), includeNullShape, monitorData, ss);
	}

	private SATTest(int initialDepth, List<? extends Shape> imageSet, boolean includeNullShape, MonitorData monitorData,
			ShapeSize ss) {
		// Set the set of shapes
		shapes = new ArrayList<>(imageSet);

		// Add the null shape
		if (includeNullShape)
			shapes.add(new NullShape());

		this.monitorData = monitorData;
		this.shapeSize = ss;
		this.session = new TestSession(initialDepth);

		// Demo enabled?
		indemomode = true;
		int mw = monitorData.monitorWidthPixels;
		int mh = monitorData.monitorHeightPixels;

		// Points
		pointsData = usestripes ? new StripesImage(mw, mh) : new RandomDotImage(mw, mh);
	}

	/**
	 * Return the current depth (as established by the certifier), be careful the
	 * certifier may have decided to stop
	 *
	 * @return the current depth
	 */
	public int getCurrentDepth() {
		return session.getCurrentDepth();
	}

	/**
	 * Gets the current status
	 *
	 * @return the current status
	 */
	public CertifierStatus getCurrentStatus() {
		return session.getCurrentStatus();
	}

	/**
	 * The Enum ChoiceResult
	 */
	public enum ChoiceResult {
		/** The skip */
		SKIP,
		/** The right */
		RIGHT,
		/** The wrong */
		WRONG,
		/** The demo */
		DEMO
	}

	/** The current shape shown */
	private Shape currentShape;

	/** The current pos */
	private Position currentPos;

	/** The change position */
	private boolean changePosition;

	/** The shape as pointsData */
	private PointImage pointsData;

	/**
	 * Check the solution and compute the next depth unless in demo: in that case
	 * the solution is not counted
	 *
	 * @param s the s can be null: it corresponds to skip by the user
	 * @return the choice result
	 */
	public ChoiceResult solutionChosen(Shape s) {
		return solutionChosen(s, 0);
	}

	/**
	 * Check the solution and compute the next depth unless in demo: in that case
	 * the solution is not counted
	 *
	 * @param s         the s can be null: it corresponds to skip by the user
	 * @param timetaken the timetaken to give this answer (in milliseconds)
	 * @return the choice result
	 */
	public ChoiceResult solutionChosen(Shape s, long timetaken) {
		// Store the current depth (it will be modified when computing the result)
		if (indemomode)
			return ChoiceResult.DEMO;

		// If not demomode
		int savedDepth = session.getCurrentDepth();

		ChoiceResult res = session.solutionChosen(s, currentShape, timetaken, depthAngle(savedDepth));

		logger.info("Immagine: " + s + ", pixels: " + savedDepth + ", angolo: " + depthAngle(savedDepth) + ", result: "
				+ res);

		return res;
	}

	/**
	 * Check solution (position) and set new depth unless in demo: in that case the
	 * solution is not counted
	 *
	 * @param p the p
	 * @return the choice result
	 */
	public ChoiceResult positionChosen(Position p) {
		if (indemomode)
			return ChoiceResult.DEMO;

		// If not demo
		ChoiceResult res = session.positionChosen(p, currentPos);
		logger.info(currentShape + ", " + session.getCurrentDepth() + " " + res + " POSITION ");

		return res;
	}

	/**
	 * Compute the depth from pixel to angle
	 *
	 * @param pixeltransl the pixeltransl
	 * @return the double angle in seconds of grado
	 */
	public double depthAngle(int pixeltransl) {
		// Get the monitor size
		double ms = monitorData.monitorSize10thInc / 10.0;

		if (ms == 0)
			return 0;

		double alphaSec = getAngleSec(pixeltransl, monitorData.monitorWidthPixels, monitorData.monitorWidthMM,
				monitorData.monitorDistance);

		return alphaSec;
	}

	/**
	 * Return true if the test is not finished
	 *
	 * @return true, if successful
	 */
	public boolean hasNextShape() {
		return getCurrentStatus().currentResult == TestSession.Result.CONTINUE;
	}

	/** Set the next shape and position and size if required */
	public void setNextShape() {
		assert getCurrentStatus().currentResult == TestSession.Result.CONTINUE;
		// Position
		if (changePosition) {
			int nextPosInt = rnd.nextInt(Position.values().length);
			currentPos = Position.values()[nextPosInt];
		} else {
			currentPos = Position.CENTER;
		}

		// Shape
		currentShape = shapes.get(rnd.nextInt(shapes.size()));

		// Set the size
		int nextSize = getCurrentPxSize();
		setShapeSize(nextSize);

		// Change the background
		pointsData.reinit();

		// Change the color if in demo mode
		if (indemomode) {
			if (colorShapeIntensity > 60) {
				colorShapeIntensity -= 20;
			} else if (colorShapeIntensity > 0) {
				colorShapeIntensity -= 10;
			} else {
				colorShapeIntensity = 0;
				// Exits demo mode
				exitDemoMode();
			}
		}
	}

	/**
	 * Return the current shape
	 *
	 * @return the next shape
	 */
	public Shape getCurrentShape() {
		return currentShape;
	}

	/**
	 * Gets the angle sec
	 *
	 * @param pixeltransl     number of pixels of translation
	 * @param pixelWidth      the pixel width of the monitor
	 * @param monitorWidthMM  monitor width in mm
	 * @param monitorDistance in cm
	 * @return the angle seconds of grado
	 */
	public static double getAngleSec(int pixeltransl, int pixelWidth, int monitorWidthMM, int monitorDistance) {
		// Get the deltaLayers in mm
		double deltaMM = pixeltransl * monitorWidthMM / (double) pixelWidth;
		// Distance is default 40 cm
		double distanceMM = monitorDistance * 10;
		// Plane and the point given by the coordinates (x, y) on it
		double alpha = Math.atan2(deltaMM, distanceMM);
		// Convert to degree in seconds
		double alphaSec = Math.toDegrees(alpha) * 3600;
		return alphaSec;
	}

	/** Returns the shapes the user must choose from */
	public List<Shape> getShapes() {
		return shapes;
	}

	static public enum PointType {
		OFF, LEFT, RIGHT, BOTH,

		// This is useful if one wants to color somehow a point (Demo mode for example)
		LEFT_COLORED;
	}

	/**
	 * If the shape must be colored (during demo mode for example) it can be between
	 * 0 and 100 - 0 no color, 100 full color
	 */
	static private int colorShapeIntensity = 0;

	/** Traslation between two plans */
	static protected int deltaLayers = 10;

	/** The constant BORDER_DISTANCE */
	private static final int BORDER_DISTANCE = 10;

	/** The center x */
	protected int centerX;

	/** The center y */
	protected int centerY;

	/**
	 * Set the position
	 *
	 * @param p the new position
	 */
	private final void centerShape(int width, int height) {
		assert currentPos != null;

		// First set the X
		switch (currentPos) {
		case NORTH_EAST:
		case SOUTH_EAST:
			centerX = shape_width / 2 + BORDER_DISTANCE - session.getCurrentDepth();
			break;
		case CENTER:
			centerX = width / 2;
			break;
		default:
			centerX = width - (shape_width / 2) - BORDER_DISTANCE - session.getCurrentDepth();
		}

		// Set the y
		switch (currentPos) {
		case NORTH_EAST:
		case NORTH_WEST:
			centerY = shape_height / 2 + BORDER_DISTANCE;
			break;
		case CENTER:
			centerY = height / 2;
			break;
		default:
			centerY = height - (shape_height / 2) - BORDER_DISTANCE;
		}
		logger.debug("Set position " + currentPos + " centerX=" + centerX + " centerY=" + centerY);
	}

	/** The shape width, desired area for the shape */
	private static int shape_width;

	/** The shape height */
	private static int shape_height;

	/**
	 * Use strips (useful for testing) instead of dots; not in the constructor to
	 * leave it simple
	 */
	static private boolean usestripes = false;

	/**
	 * Sets the shape size
	 *
	 * @param ss shape size in pixels
	 */
	private void setShapeSize(int ss) {
		currentShape.resize(ss);
		shape_width = ss;
		shape_height = ss;
		logger.debug("Set shape size in pixels [" + shape_width + " x " + shape_height + "]");
	}

	/**
	 * Sets the use stripes
	 *
	 * @param usestripes the new use stripes
	 */
	static public void setUseStripes(boolean us) {
		usestripes = us;
	}

	/**
	 * Checks if is indemomode
	 *
	 * @return true, if is indemomode
	 */
	public boolean isIndemomode() {
		return indemomode;
	}

	/**
	 * Exit demo mode
	 */
	public void exitDemoMode() {
		assert indemomode == true;
		logger.info("Existing demo mode, starting real test");
		this.indemomode = false;
		// Notify the observers!
		setChanged();
		notifyObservers();
	}

	/**
	 * Build the point to visualize
	 * 
	 * @param width  of the image
	 * @param height of the image
	 * @return
	 */
	@Override
	public Points getPoints(int width, int height) {
		Points result = new Points(width, height, colorShapeIntensity);

		// Set the position (it can change)
		centerShape(width, height);

		// Dive the translation between the two
		int depthTranslationX = session.getCurrentDepth() / 2;
		int depthTranslationY = session.getCurrentDepth() - depthTranslationX;

		// Init points
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++)
				result.points[x][y] = PointType.OFF;
		}
		// In only one pass (every point is first decided if left and then eventually
		// right)
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				boolean toMove = currentShape.belongs(x - centerX, y - centerY);
				boolean movedAreaX = currentShape.belongs(x - centerX - depthTranslationX, y - centerY);
				boolean movedAreaY = currentShape.belongs(x - centerX + depthTranslationY, y - centerY);
				if (pointsData.positive(x, y)) {
					if (toMove) {
						if (x + deltaLayers + depthTranslationX < width)
							result.points[x + deltaLayers + depthTranslationX][y] = colorShapeIntensity > 0
									? PointType.LEFT_COLORED
									: PointType.LEFT;
						if (x - depthTranslationY > 0)
							result.points[x
									- depthTranslationY][y] = result.points[x - depthTranslationY][y] == PointType.OFF
											? PointType.RIGHT
											: PointType.BOTH;
					}
					if (!movedAreaX && x + deltaLayers < width) {
						result.points[x + deltaLayers][y] = PointType.LEFT;
					}
					if (!movedAreaY) {
						result.points[x][y] = result.points[x][y] == PointType.OFF ? PointType.RIGHT : PointType.BOTH;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Gets the next size
	 *
	 * @return the size in pixels
	 */
	public int getCurrentPxSize() {
		// Get the dimension in millimeters
		int dimMM = shapeSize.size(depthAngle(getCurrentDepth()));

		// In pixels
		return (monitorData.monitorWidthPixels * dimMM) / monitorData.monitorWidthMM;
	}

	/**
	 * Sets the color shape
	 *
	 * @param b the new color shape: between 0 and 100
	 */
	public static void setColorShape(int b) {
		colorShapeIntensity = b;
	}

	/**
	 * Get session results
	 * 
	 * @return the results of the tests
	 */
	public List<String> getSessionResults() {
		return session.getSessionResults();
	}

	/**
	 * Get session answers
	 * 
	 * @return the results of the tests
	 */
	public List<SingleAnswer> getSessionAnswers() {
		return session.getSessionAnswers();
	}
}