package environnement;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tools.math.Vector;

/**
 * Set of objects containing all objects needed to compose a limited terrain
 * with obstacles It has a set of methods to compute classic interractions with
 * these objects
 * 
 * @author Arthur France
 *
 */
public class Terrain implements Serializable {
	private static final long serialVersionUID = -5924414335463343600L;
	private Vector walls;

	// private List<TerrainElement> elements = new ArrayList<>();
	private List<Obstacle> obstacles = new ArrayList<>();
	private List<Point> points = new ArrayList<>();

	/**
	 * Create a Terrain without obstacles and the given size
	 * 
	 * @param size the Vector representation of the size of the Terrain
	 */
	public Terrain(Vector size) {
		this.walls = size;
	}

	/**
	 * Create a Terrain without obstacles and the default size.
	 */
	public Terrain() {
		this(new Vector(Constants.DEFAULT_TERRAIN_SIZE, Constants.DEFAULT_TERRAIN_SIZE));
	}

	public boolean isValid(Vector v) {
		return wallValidTest(v) && obstacleCollisionTest(v);
	}

	public boolean wallValidTest(Vector v) {
		return (v.x() >= 0) && (v.y() >= 0) && walls.under(v);
	}

	public boolean obstacleCollisionTest(Vector v) {
		for (Obstacle o : obstacles) {
			if (o.isInside(v)) {
				return false;
			}
		}
		return true;
	}

	public void showSimpleShower(Graphics g, TerrainVariation tvar, float ratio, boolean noUseless,
			boolean showPointName) {
		show(g, tvar, false, true, ratio, new Vector(), noUseless, showPointName);
	}

	public void showSimulation(Graphics g, TerrainVariation tvar, float ratio, Vector offset, boolean fill,
			boolean affCpt) {
		show(g, tvar, affCpt, fill, ratio, offset, true, false);
	}

	/**
	 * Show the obstacles with the given graphics object
	 * 
	 * @param g              The graphics objet to use
	 * @param affCpt         Affiche les compteurs et autres des composants
	 * @param fill           Fill les composant au lieu de dessiner juste les
	 *                       contours
	 * @param applique       un ratio permettant de de/zoomer
	 * @param noUseless      ne pas afficher les points de type USELESS
	 * @param showPointNames afficher le nom des points
	 */
	public void show(Graphics g, TerrainVariation tvar, boolean affCpt, boolean fill, float ratio, Vector offset,
			boolean noUseless, boolean showPointNames) {
		obstacles.forEach(el -> el.show(g, affCpt, fill, ratio, offset, false));
		if (tvar != null)
			tvar.showPoints(g, affCpt, fill, ratio, offset, showPointNames);
		if (!noUseless)
			points.forEach(p -> {
				if (tvar.isUseless(p))
					p.show(g, affCpt, fill, ratio, offset, showPointNames);
			});
	}

	/**
	 * Call the reset functions in the obstacles
	 */
	public void reset() {
		obstacles.forEach(o -> o.reset());
	}

	/**
	 * Generate an array of readings used by the brains
	 * 
	 * @param tvar      The terrain variation currently used
	 * @param position  The position of the Dot
	 * @param speed     the speed of the Dot
	 * @param bspeed    true if the speed is needed inside the array
	 * @param distance  true if the distance from the goal is needed inside the
	 *                  array
	 * @param direction true if the direction of the goal is needed inside the array
	 * @param walls     true if the raytracing-like sensors are needed inside the
	 *                  array
	 * @param limit     the value limit of every value in the array
	 * @return An array of readings at the position location on the terrain
	 */
	public float[] computeInputs(TerrainVariation tvar, Vector position, Vector speed, boolean bspeed, boolean distance,
			boolean direction, boolean walls, float limit) {
		int nb = 0;
		if (bspeed)
			nb += 2;
		if (distance)
			nb += 1;
		if (direction)
			nb += 2;
		if (walls)
			nb += 8;
		float[] resp = new float[nb];
		nb = 0;
		if (walls) {
			float[] temp = getInputsWalls(position, limit);
			for (int i = 0; i < 8; i++) {
				resp[i] = temp[i];
			}
			nb = 8;
		}
		if (direction || distance) {
			Vector dist = position.sub(tvar.getGoal().getPosition());
			if (direction) {
				resp[nb++] = Math.min((float) dist.x(), limit);
				resp[nb++] = Math.min((float) dist.y(), limit);
			}
			if (distance)
				resp[nb++] = Math.min((float) dist.getAmplitude(), limit);
		}
		if (bspeed) {
			resp[nb++] = Math.min((float) speed.x(), limit);
			resp[nb++] = Math.min((float) speed.y(), limit);
		}

		return resp;
	}

	/**
	 * Compute a lazer-like distance from the position v and the obstacles and walls
	 * @param v the starting position of the rays
	 * @param limit the saturation limit of the sensors
	 * @return An array containing 8 distance in 8 directions
	 */
	private float[] getInputsWalls(Vector v, float limit) {
		float[] resp = new float[8];
		resp[0] = (getDistanceImpact(v, 0));
		resp[1] = (getDistanceImpact(v, Math.PI / 4));
		resp[2] = (getDistanceImpact(v, Math.PI / 2));
		resp[3] = (getDistanceImpact(v, 3 * Math.PI / 4));
		resp[4] = (getDistanceImpact(v, Math.PI));
		resp[5] = (getDistanceImpact(v, -3 * Math.PI / 4));
		resp[6] = (getDistanceImpact(v, -Math.PI / 2));
		resp[7] = (getDistanceImpact(v, -Math.PI / 4));

		return resp;
	}

	/**
	 * Return the distance between the given position in direction give by the angle
	 * to the first obstacle or wall
	 * 
	 * @param position The starting position of the ray
	 * @param angle    The angle of the ray
	 * @return The distance reached by the ray on it's fisrt collision
	 */
	public float getDistanceImpact(Vector position, double angle) {
		// TODO Multithreading
		float min = this.getDistanceImpactWalls(position, angle);
		for (Obstacle o : obstacles) {
			float temp = o.getDistanceImpact(position, angle);
			min = Math.min(temp, min);
		}
		return min;
	}

	/**
	 * Return the distance between the given position and the walls on the given
	 * direction
	 * 
	 * @param position The starting position of the ray
	 * @param angle    The angle of the ray
	 * @return The distance reached by the ray before it's collision with a wall
	 */
	public float getDistanceImpactWalls(Vector position, double angle) {
		// le test est plus simple que celui du rectangle car on est dedans et non
		// dehors
		double min = Double.MAX_VALUE;
		if (Math.abs(angle) < Math.PI / 2) {
			// test mur droit
			double distMur = walls.x() - position.x();
			double res = distMur / Math.cos(angle);
			if (res < min)
				min = res;
		} else if (Math.abs(angle) != (Math.PI / 2)) {
			// test mur gauche
			double distMur = position.x();
			double res = distMur / (-Math.cos(angle));
			if (res < min)
				min = res;
		}
		if (angle < 0 && angle > -Math.PI) {
			// test mur bas
			double distMur = position.y();
			double res = distMur / -Math.sin(angle);
			// double sDifX = Math.cos(angle) * nbY;// on regarde ou on arrive en Y
			// double res = Math.sqrt(distMur * distMur + sDifX * sDifX);
			if (res < min)
				min = res;
		} else if (angle > 0 && angle < Math.PI) {
			// test mur haut
			double distMur = walls.y() - position.y();
			double res = distMur / Math.sin(angle);
			// double sDifX = Math.cos(angle) * nbY;// on regarde ou on arrive en Y
			// double res = Math.sqrt(distMur * distMur + sDifX * sDifX);
			if (res < min)
				min = res;
		}
		return (float) min;
	}

	public Vector getWalls() {
		return walls;
	}

	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setWalls(Vector newSize) {
		walls = newSize;
	}

	public String toString() {
		String resp;
		resp = "Terrain :\nSize = " + this.walls;
		resp += "\nObstacle List :\n";
		resp += obstacles;
		resp += "\nPoint List :\n";
		resp += points;
		return resp;
	}

	public Terrain deepCopy() {
		Terrain resp = new Terrain(this.walls.copy());
		this.obstacles.forEach(o -> resp.obstacles.add(o.copy()));
		this.points.forEach(p -> resp.points.add(p.copy()));
		return resp;
	}

}
