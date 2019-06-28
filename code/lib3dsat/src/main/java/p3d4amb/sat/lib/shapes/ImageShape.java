package p3d4amb.sat.lib.shapes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 *
 * Images 400x400
 * 
 * Sets of images:
 * 		LANG("langimages","bird","car","cat","circle","man","star"), 
 *		LEA("leaimages","apple","circle","house","square"), 
 *		LETTERS ("letters","letterA","letterC","letterE","letterK","letterM","letterZ"),
 *		LEA_CONTORNO("leaimages","apple","circle","house","square"), 
 *		PACMAN("pacman","pacmanD","pacmanL","pacmanR","pacmanU"), 
 *		TNO("tno","circle","square","star","triangle");
 */

/**
 * Shapes that represent images
 */
public class ImageShape extends Shape {

	/** The Constant SEPARATOR */
	private static final char SEPARATOR = '/'; // NOT USE File.separator

	/** The Constant logger */
	private static final Logger logger = Logger.getLogger(ImageShape.class);

	/** The Constant IMG_DATA_SIZE */
	private static final int IMG_DATA_SIZE = 300;

	/** The Constant CENTER_X */
	private static final int CENTER_X = IMG_DATA_SIZE / 2;

	/** The Constant CENTER_Y */
	private static final int CENTER_Y = IMG_DATA_SIZE / 2;

	/** The image data */
	private ImageData imageData;

	/** The Constant ICON_WIDTH */
	private static final int ICON_WIDTH = 48;

	/** The Constant ICON_HIGTH */
	private static final int ICON_HIGTH = 56;

	/** The ratio, if ratio = 2 show double */
	private double ratio = 1;

	/** The name */
	private String name;

	/** The size */
	// current size
	private int size = IMG_DATA_SIZE;

	/** The belongs */
	private boolean[][] belongs;

	/**
	 * Instantiates a new image shape
	 *
	 * @param imageName the image name
	 */
	ImageShape(String imageName) {
		logger.debug("loading shape " + imageName);

		// Load the image data
		InputStream inputImageStream = getClass().getResourceAsStream(imageName);

		if (inputImageStream == null) {
			throw new RuntimeException("image " + imageName + " not found");
		}
		imageData = new ImageData(inputImageStream);

		// Setup belongs
		belongs = new boolean[IMG_DATA_SIZE][IMG_DATA_SIZE];
		ImageData scaledImage = imageData.scaledTo(IMG_DATA_SIZE, IMG_DATA_SIZE);
		for (int x = 0; x < IMG_DATA_SIZE; x++) {
			for (int y = 0; y < IMG_DATA_SIZE; y++) {
				belongs[x][y] = (scaledImage.getAlpha(x, y) != 0);
			}
		}

		// Get the name
		name = imageName.substring(0, imageName.indexOf("."));
	}

	@Override
	public boolean belongs(int x, int y) {
		int deltaX = (int) (x / ratio) + CENTER_X;
		int deltaY = (int) (y / ratio) + CENTER_Y;
		if (deltaX >= IMG_DATA_SIZE || deltaY >= IMG_DATA_SIZE || deltaX < 0 || deltaY < 0)
			return false;
		return belongs[deltaX][deltaY];
	}

	@Override
	public void resize(int size) {
		this.ratio = ((double) size) / IMG_DATA_SIZE;
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public Image getIcon(Display display) {
		return new Image(display, imageData.scaledTo(ICON_WIDTH, ICON_HIGTH));
	}

	/**
	 * The Enum ImageSet
	 */
	public enum ImageSet {

		/** The lang */
		LANG("langimages", "bird", "car", "cat", "circle", "man", "star"),

		/** The lea */
		LEA("leaimages", "apple", "circle", "house", "square"),

		/** The letters */
		LETTERS("letters", "letterA", "letterC", "letterE", "letterK", "letterM", "letterZ"),

		/** The lea contorno */
		LEA_CONTORNO("leaimages_contour", "apple", "circle", "house", "square"),

		/** The pacman */
		PACMAN("pacman", "pacmanD", "pacmanL", "pacmanR", "pacmanU"),

		/** The tno */
		TNO("tno", "circle", "square", "star", "triangle");

		/** The subdir */
		String subdir;

		/** The images */
		String[] images;

		/**
		 * Instantiates a new image set
		 *
		 * @param p      the p
		 * @param images the images
		 */
		ImageSet(String p, String... images) {
			subdir = p;
			this.images = images;
		}
	}

	/**
	 * Images similar to LANG TEST *
	 *
	 * @param is the is
	 * @return the shapes
	 */
	static public List<ImageShape> getShapes(ImageSet is) {
		List<ImageShape> shapes = new ArrayList<>();

		for (String i : is.images) {
			shapes.add(new ImageShape(is.subdir + SEPARATOR + i + ".png"));
		}
		return shapes;
	}

	@Override
	public String toString() {
		return name;
	}
}