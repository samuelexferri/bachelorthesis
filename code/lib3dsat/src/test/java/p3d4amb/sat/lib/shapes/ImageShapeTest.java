package p3d4amb.sat.lib.shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import org.eclipse.swt.graphics.ImageData;
import org.junit.Test;

import p3d4amb.sat.lib.shapes.ImageShape.ImageSet;

/**
 * The Class ImageShapeTest
 */
public class ImageShapeTest {

	/** The Constant _400 */
	private static final int _400 = 400;

	/**
	 * Test actual shape
	 */
	@Test
	public void testActualShape() {
		// Take one
		List<ImageShape> shapes = ImageShape.getShapes(ImageSet.LANG);
		assertTrue(shapes.size() > 0);
		ImageShape img = shapes.get(0);
		assertNotNull(img);
		// Base - Origin
		assertTrue(img.belongs(0, 0));
		assertEquals(img.getSize(), 300);
	}

	/**
	 * Test full rectangle
	 */
	@Test
	public void testFullRectangle() {
		// Take full rectangle
		ImageShape img = new ImageShape("test/rect_full.png");
		// Base - Origin
		assertTrue(img.belongs(0, 0));
		assertEquals(img.getSize(), 300);
		assertTrue(img.belongs(-150, -150));
		assertTrue(img.belongs(0, -150));
		assertTrue(img.belongs(-150, 0));
		// Zero is considered with positive
		assertTrue(img.belongs(149, 149));
		assertTrue(img.belongs(149, 0));
		assertTrue(img.belongs(0, 149));
		assertTrue(img.belongs(-150, 149));
		assertFalse(img.belongs(-151, 0));
		assertFalse(img.belongs(0, -151));
		assertFalse(img.belongs(150, 0));
		assertFalse(img.belongs(0, 150));
	}

	/**
	 * Test full rectangle resize
	 */
	@Test
	public void testFullRectangleResize() {
		// Takke full rectangle
		ImageShape img = new ImageShape("test/rect_full.png");
		// Double
		img.resize(600);
		// Base - Origin
		assertTrue(img.belongs(0, 0));
		assertEquals(img.getSize(), 600);
		assertTrue(img.belongs(-300, -300));
		// Zero is considered with positive
		assertTrue(img.belongs(299, 299));
	}

	/**
	 * Check image quality
	 */
	@Test
	public void checkImageQuality() {
		String canonicalName = this.getClass().getSimpleName();
		System.out.println(canonicalName);
		String path = this.getClass().getResource(canonicalName + ".class").getPath();
		File dir = new File(path).getParentFile();
		assertTrue(dir.exists() && dir.isDirectory());
		System.out.println(dir);
		for (File f : dir.listFiles()) {
			if (!f.isDirectory())
				continue;
			File[] images = f.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".png");
				}
			});
			String densities = "";
			for (File image : images) {
				densities += " " + checkImage(image);
			}
			System.out.println(f.getName() + " " + densities);
		}
	}

	/**
	 * Check image
	 *
	 * @param image the image
	 * @return the double
	 */
	private double checkImage(File image) {
		// Size
		ImageData i = new ImageData(image.getAbsolutePath());
		if (i.width != _400)
			System.err.println(image + " width " + i.width);
		if (i.height != 400)
			System.err.println(image + " heigth " + i.width);
		int points = 0;
		for (int x = 0; x < i.width; x++) {
			for (int y = 0; y < i.height; y++) {
				if (i.getAlpha(x, y) != 0)
					points++;
			}
		}
		double density = points / ((double) _400 * _400);
		if (density >= .6)
			System.err.println(image + " too many points " + density);
		if (density <= .3)
			System.err.println(image + " too few points " + density);
		return density;
	}
}