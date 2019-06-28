package p3d4amb.sat.lib.shapes;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * Represents an empty shape
 */
public class NullShape extends Shape {

	@Override
	public boolean belongs(int x, int y) {
		// No point in here
		return false;
	}

	@Override
	public void resize(int x) {
		// Nothing
	}

	@Override
	public Image getIcon(Display display) {
		ImageData imageData = new ImageData(getClass().getResourceAsStream("random48x56.png"));
		return new Image(display, imageData);
	}

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public int getSize() {
		return 0;
	}
}