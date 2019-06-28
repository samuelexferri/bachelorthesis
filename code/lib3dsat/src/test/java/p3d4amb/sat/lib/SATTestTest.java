package p3d4amb.sat.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static p3d4amb.sat.lib.session.TestSession.Result.CONTINUE;

import java.util.List;

import org.junit.Test;

import p3d4amb.sat.lib.SATTest.PointType;
import p3d4amb.sat.lib.shapes.ImageShape.ImageSet;
import p3d4amb.sat.lib.shapes.Shape;
import p3d4amb.sat.lib.shapes.ShapeSize;

public class SATTestTest {
	// FUL HD
	MonitorData md = new MonitorData(10, 1920, 200, 1080, 40);

	// Create new test
	SATTest test = new SATTest(12, ImageSet.LANG, false, md, ShapeSize.CASUALE);

	@Test
	public void testSATFirstStep() {
		System.out.println("testSATFirstStep()");
		// Get the possible shapes
		List<Shape> shapes = test.getShapes();
		assertEquals(6, shapes.size());

		assertSame(CONTINUE, test.getCurrentStatus().currentResult);
		test.setNextShape();
		// Get the points to visualize
		test.getPoints(1000, 500);
	}

	@Test
	public void testSATFirstStepTestAllRight() {
		System.out.println("testSATFirstStepTestAllRight()");
		while (test.getCurrentStatus().currentResult == CONTINUE) {
			// Set the next solution
			test.setNextShape();
			System.out.println(test.getCurrentDepth());

			Shape cs = test.getCurrentShape();
			Points points = test.getPoints(1000, 200);
			assertNotNull(points.points);
			checkNotNullPoints(points.points, 1000, 200);
			test.solutionChosen(cs);
		}
	}

	@Test
	public void testSATFirstStepSmallScreen() {
		System.out.println("testSATFirstStepSmallScreen()");
		// Get the possible shapes
		List<Shape> shapes = test.getShapes();
		assertEquals(6, shapes.size());

		assertSame(CONTINUE, test.getCurrentStatus().currentResult);
		test.setNextShape();
		// Get the points to visualize
		test.getPoints(100, 200);
	}

	@Test
	public void testSATFullHD() {
		System.out.println("testSATFullHD()");
		test.setNextShape();
		long start = System.currentTimeMillis();
		// Get the points to visualize
		test.getPoints(1920, 1080);
		long elapsedTimeMillis = System.currentTimeMillis() - start;
		System.out.println("time " + elapsedTimeMillis);
	}

	private void checkNotNullPoints(PointType[][] points, int width, int height) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				assertNotNull("null in " + x + " " + y, points[x][y]);
			}
		}
	}

	@Test
	public void testSkip() {
		System.out.println("testSkip()");
		test.setNextShape();
		// Like skip
		test.solutionChosen(null);
		test.getCurrentStatus();
	}
}