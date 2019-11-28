package environnement;

import java.awt.Graphics;
import java.io.Serializable;

import tools.math.Vector;

/**
 * Describe a usage of a terrain by storing a starting and ending points
 * @author Arthur France
 */
public class TerrainVariation implements Serializable{
	private static final long serialVersionUID = -4208897886009452095L;
	private Point origin;
	private Point goal;
	private String name;
	private static transient int nb = 0;
	
	/**
	 * Full constructor
	 * @param origin the spawn point of the population
	 * @param goal the goal of the dots
	 * @param name the name of the variation
	 */
	public TerrainVariation(Point origin, Point goal, String name) {
		super();
		this.origin = origin;
		this.goal = goal;
		this.name = name;
	}
	
	/**
	 * The nameless constructor. The name of the variation will be a generated one.
	 * @param origin the spawn point of the population
	 * @param goal the goal of the dots
	 */
	public TerrainVariation(Point origin, Point goal) {
		this(origin, goal, "["+(nb++)+"]");
	}
	
	/**
	 * The edition constructor. It's name will be void ("")
	 * @param def The default point to put on origin and goal
	 */
	public TerrainVariation(Point def) {
		this(def,def,"");
	}
	
	public Point getOrigin() {
		return origin;
	}
	public void setOrigin(Point origin) {
		this.origin = origin;
	}
	public Point getGoal() {
		return goal;
	}
	public void setGoal(Point goal) {
		this.goal = goal;
	}
	
	public String toString() {
		return getName();
	}
	public String getName() {
		if(name == null) name = "["+(nb++)+"]";
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * generate a reference copy of this variation
	 * @return A new TerrainVariation referencing the same points
	 */
	public TerrainVariation refcopy() {
		return new TerrainVariation(origin,goal,name);
	}
	/**
	 * copy the references of the given tvar into itself
	 * @param tvar the TerrainVariation whom references will be copied
	 */
	public void absorb(TerrainVariation tvar) {
		this.origin = tvar.origin;
		this.goal = tvar.goal;
		this.name = tvar.name;
	}
	public void showPoints(Graphics g,boolean affCpt,boolean fill,float ratio,Vector offset,boolean showName) {
		goal.show(g, affCpt, fill, ratio, offset, showName, PointType.GOAL);
		origin.show(g, affCpt, fill, ratio, offset, showName, PointType.ORIGIN);
	}
	public boolean isUseless(Point p) {
		return (p != goal && p != origin);
	}
}
