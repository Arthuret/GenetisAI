package tools.math;

import java.io.Serializable;

/**
 * A class defining a Segment by it's two Vector ends. This class has
 * mathematical functions to manipulate the segment
 * 
 * @author Arthur France
 *
 */
public class Segment implements Serializable {
	private static final long serialVersionUID = 4414405042838852939L;
	private Vector a = null;
	private Vector b = null;

	/**
	 * Create a segment defined by it's two <b>Vector</b> ends
	 * 
	 * @param a The first end of the segment
	 * @param b The second end of the segment
	 */
	public Segment(Vector a, Vector b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Compute the the direct distance between the given point and the nearest point on the segment
	 * @param point The point
	 * @return The direct distance between the point and the Segment
	 */
	public double distanceFromPoint(Vector point) {
		return distanceFromPoint(a, b, point);
	}

	/**
	 * Compute the direct distance between p and the segment formed by a and b
	 * @param a The first end of the segment
	 * @param b The second end of the segment
	 * @param p the point
	 * @return The distance between p and the segment (a,b)
	 */
	public static double distanceFromPoint(Vector a, Vector b, Vector p) {
		final double xDelta = b.x() - a.x();
		final double yDelta = b.y() - a.y();
		if ((xDelta == 0) && (yDelta == 0)) {
			return Vector.distance(a, p);
		}
		final double u = ((p.x() - a.x()) * xDelta + (p.y() - a.y()) * yDelta)
				/ (xDelta * xDelta + yDelta * yDelta);

		final Vector closestPoint;
		if (u < 0) {
			closestPoint = a;
		} else if (u > 1) {
			closestPoint = b;
		} else {
			closestPoint = new Vector(a.x() + u * xDelta, a.y() + u * yDelta);
		}

		return Vector.distance(p, closestPoint);
	}

	/**
	 * @return The first end of the segment
	 */
	public Vector getA() {
		return a;
	}

	/**
	 * Set the first end of the segment
	 * @param a The new end of the segment
	 */
	public void setA(Vector a) {
		this.a = a;
	}

	/**
	 * @return the second end of the segment
	 */
	public Vector getB() {
		return b;
	}

	/**
	 * Set the second end of the segment
	 * @param b the new en of the segment
	 */
	public void setB(Vector b) {
		this.b = b;
	}
}
