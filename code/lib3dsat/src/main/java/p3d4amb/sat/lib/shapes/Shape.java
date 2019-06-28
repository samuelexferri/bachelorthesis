package p3d4amb.sat.lib.shapes;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Represents a shape to be recognized
 */
public abstract class Shape {

	public abstract boolean belongs(int x, int y);

	public abstract void resize(int size);

	public abstract Image getIcon(Display display);

	public abstract int getSize();
}