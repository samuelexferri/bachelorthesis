package p3d4amb.sat.lib.shapes;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Represents a rectangle shape to be recognized with fixed proportion of the
 * display [NO LONGER USED]
 */
public class RectangleShape extends GeometricShape {

	/** The w */
	int w;

	/** The h */
	int h;

	@Override
	public boolean belongs(int x, int y) {
		return (-(w / 6) < x && x < (w / 6) && -(h / 6) < y && y < (h / 6));
	}

	@Override
	public Image getIcon(Display display) {
		return null;
	}

	@Override
	public void resize(int width) {
		w = width;
		h = width;
	}

	@Override
	public int getSize() {
		return 0;
	}
}