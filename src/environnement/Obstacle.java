package environnement;

import java.awt.Graphics;

import tools.math.Vector;

/**
 * An obstacle in a two dimensionnal space
 * 
 * @author Arthur France
 *
 */
public interface Obstacle extends TerrainElement {
	/**
	 * Tell the obstacle that a generation has passed. Used to count the dead bodies
	 * stuck on the obstacle in each generation
	 */
	public void reset();
	
	//for obstacles, mouseIsInside is equals to isInside
	public default boolean mouseIsInside(Vector vect) {
		return isInside(vect);
	}

	/**
	 * Compute the distance traveled by a ray before touching the obstacle.
	 * 
	 * @param position
	 *            The starting point of the ray
	 * @param angle
	 *            The angle the ray goes from the position
	 * @return The distance the ray travel before touching the obstacle or
	 *         Float.MAX_VALUE if the ray don't reach the obstacle
	 */
	public float getDistanceImpact(Vector position, double angle);

	/**
	 * Show a cross or similar on the center of the object. It is used to show the
	 * grab point to move the object.
	 * 
	 * @param g
	 *            The Graphics object to use
	 */
	public void showCenter(Graphics g,float ratio);
	public default void showCenter(Graphics g) {showCenter(g,1);}
	
	public default String getElementCategory() {
		return "Obstacle";
	}
	
	public Obstacle copy();
}
