package tools.math;
/**
 * A two dimensional vector (double) It consist of just two attributes, x and y
 * There is many convenient functions for manipulation them
 * 
 * @author Arthur France
 *
 */

import java.awt.Dimension;
import java.io.Serializable;

/**
 * A Vector object store two double values (x and y)
 * @author Arthur France
 *
 */
public class Vector implements Serializable {
	private static final long serialVersionUID = 4036973103074361775L;
	private double x;
	private double y;

	public double x() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double y() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Create a new two dimensional vector
	 * 
	 * @param x The x component of the vector
	 * @param y The y component of the vector
	 */
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new two dimensional vector of value 0,0
	 */
	public Vector() {
		this(0,0);
	}

	/**
	 * Create a new two dimensional vector of value f,f
	 * @param f
	 */
	public Vector(float f) {
		this(f,f);
	}

	/**
	 * Generate a new independent vector by adding the components of the two input
	 * vectors
	 * 
	 * @param v1 A vector
	 * @param v2 Another vector
	 * @return The vector resulting from the addition of v1 + v2
	 */
	public static Vector add(Vector v1, Vector v2) {
		return new Vector(v1.x + v2.x, v1.y + v2.y);
	}

	/**
	 * Generate a new independant vector by adding the component of this vector with
	 * the inout vector
	 * 
	 * @param vect the vector to be added
	 * @return The vector resulting from the addition of this vector + vect
	 */
	public Vector add(Vector vect) {
		return Vector.add(this, vect);
	}

	/**
	 * Generate a new independant vector by substracting v2 from v1
	 * 
	 * @param v1 A vector
	 * @param v2 Another vector
	 * @return The result of the substraction v1 - v2
	 */
	public static Vector sub(Vector v1, Vector v2) {
		return new Vector(v1.x - v2.x, v1.y - v2.y);
	}

	/**
	 * Generate a new independent vector by substracting this vector by the input
	 * vector
	 * 
	 * @param vect The vector to substract to this vector
	 * @return the result of the substraction of this vector - vect
	 */
	public Vector sub(Vector vect) {
		return Vector.sub(this, vect);
	}

	/**
	 * Generate a new vector which amplitude is less or equals than max. If the
	 * given vector has an maplitude smaller than max, the output vector is equals
	 * to the given vector, but independants.
	 * 
	 * @param v   The vector to be limited
	 * @param max The amplitude limit
	 * @return A new vector with the same angle but an amplitude less or equals than
	 *         max
	 */
	public static Vector limit(Vector v, double max) {
		Vector resp = v.copy();
		final double dist = Math.hypot(resp.x, resp.y);
		if (dist > max) {
			double angle = Math.atan2(resp.x, resp.y);
			resp.x = Math.sin(angle) * max;
			resp.y = Math.cos(angle) * max;
		}
		return resp;
	}

	/**
	 * Generate a new vector which amplitude is less or equals than max. If the
	 * current vector has an maplitude smaller than max, the output vector is equals
	 * to the given vector, but independants.
	 * 
	 * @param max The amplitude limit
	 * @return A new vector with the same angle but an amplitude less or equals than
	 *         max
	 */
	public Vector limit(double max) {
		return Vector.limit(this, max);
	}

	/**
	 * Generate a new vector from polar coordinates. Radiant based
	 * 
	 * @param angle The ngle of the vector
	 * @param amp   The amplitude of the vector
	 * @return The new vector with the given polar coordinates.
	 */
	public static Vector fromAngle(double angle, double amp) {
		return new Vector(Math.cos(angle) * amp, Math.sin(angle) * amp);
	}

	/**
	 * Generate a vector with a random angle but given amplitude.
	 * 
	 * @param amp The amplitude of the vector
	 * @return The new vector
	 */
	public static Vector random(double amp) {
		return fromAngle(Math.random() * 2 * Math.PI, amp);
	}

	/**
	 * Compute the absolute distance between the two vectors Used to compute the
	 * distance between two positions
	 * 
	 * @param v1 The first point
	 * @param v2 The second point
	 * @return The distance between the two points
	 */
	public static double distance(Vector v1, Vector v2) {
		final double diffx = v1.x - v2.x;
		final double diffy = v1.y - v2.y;
		return Math.sqrt(diffx * diffx + diffy * diffy);
	}

	/**
	 * Generate a independant copy of the vector
	 * 
	 * @return A new Independant vector with the same attributes
	 */
	public Vector copy() {
		return new Vector(x, y);
	}

	/**
	 * Compute the polar angle of the vector
	 * 
	 * @return The angle of the vector
	 */
	public double getAngle() {
		return Math.atan2(y, x);
	}

	/**
	 * Generate a new vector which is the product of the operation v * factor
	 * 
	 * @param v      The vector to be multiplied. It won't be modified.
	 * @param factor The multiplication factor to be applied
	 * @return A new independant vector
	 */
	public static Vector times(Vector v, double factor) {
		return new Vector(v.x * factor, v.y * factor);
	}

	/**
	 * Generate a new vector which is the product of this * factor
	 * 
	 * @param factor The factor to be applied
	 * @return A new independent vector
	 */
	public Vector times(double factor) {
		return Vector.times(this, factor);
	}

	/**
	 * Compute the distance of the vector from 0,0
	 * 
	 * @return The distance computed using sqrt(x*x + y*y)
	 */
	public double getAmplitude() {
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * Generate an awt Dimension object for use in graphical components
	 * 
	 * @return A new Dimension object with width = x & height = y
	 */
	public Dimension getDimension() {
		return new Dimension((int) x, (int) y);
	}

	/**
	 * Compute the middlePoint of the two given points
	 * 
	 * @param a The first point
	 * @param b The second point
	 * @return the middle of a and b
	 */
	public static Vector middle(Vector a, Vector b) {
		return new Vector((a.x() + b.x()) / 2, (a.y() + b.y()) / 2);
	}
	
	/**
	 * Test if the given vector has it's 2 coordinates < this vector
	 * @param v The vector to test
	 * @return true if the given vector is strictly under this vector
	 */
	public boolean under(Vector v) {
		return (v.x <= x && v.y < y);
	}

	/**
	 * @return A String representation of the vector using [x],[y] format
	 */
	public String toString() {
		return x + "," + y;
	}
}