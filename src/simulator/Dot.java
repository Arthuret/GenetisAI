package simulator;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import brain.BrainData;
import formula.Context;
import formula.Formula;
import tools.math.Vector;

/**
 * A simulation entity, containing a brain
 * 
 * @author Arthur France
 *
 */
public class Dot implements Serializable {
	private static final long serialVersionUID = 1L;
	private BrainData brain;
	private Vector position, speed;
	private boolean isBest = false;// allow different display for the best dot
	private boolean isDead = false;
	private boolean isWin = false;

	private int frameNumber;

	private transient static final Color DEFAULT_COLOR = Color.DARK_GRAY;
	private transient static final Color BEST_COLOR = new Color(0, 128, 0);
	private transient static final Color DEAD_COLOR = new Color(128, 0, 0);
	private transient static final Color WIN_COLOR = new Color(128, 128, 0);

	public Dot(BrainData brain, Vector position) {
		this.brain = brain;
		this.position = position.copy();
		this.speed = new Vector(0, 0);
	}

	public BrainData getBrain() {
		return brain;
	}

	public Vector getPosition() {
		return position;
	}

	public Vector getSpeed() {
		return speed;
	}

	/**
	 * Reset all values to default and teleport to position
	 * 
	 * @param position
	 */
	public void reset(Vector position) {
		this.position = position.copy();
		this.speed = new Vector(0, 0);
		isDead = false;
		isWin = false;
	}

	/**
	 * Show the dot on screen
	 * 
	 * @param g      The Graphics object
	 * @param factor The display scale
	 * @param offset The display offset
	 */
	public void show(Graphics g, float factor, Vector offset) {
		g.setColor((isBest) ? BEST_COLOR : (isDead) ? DEAD_COLOR : (isWin) ? WIN_COLOR : DEFAULT_COLOR);
		int initialRadius = (isBest) ? 20 : 8;
		int radius = (int) (initialRadius * factor);
		Vector spos = position.times(factor).sub(new Vector(radius / 2f)).add(offset);
		g.fillOval((int) spos.x(), (int) spos.y(), radius, radius);
	}

	/**
	 * Call the brain and perform a physic step
	 * 
	 * @param tavar the current terrain environnement
	 */
	public void update(TerrainAndVar tavar) {
		float[] inputs = tavar.t.computeInputs(tavar.tvar, position, speed, brain.cSpeed(), brain.cDistance(),
				brain.cDirection(), brain.cWalls(), brain.sensorLimit());
		float[] outputs = brain.compute(inputs);
		Vector acc = new Vector(outputs[0], outputs[1]);
		speed = speed.add(acc).limit(brain.getSpeedLimit());
		position = position.add(speed);

	}

	private transient static final float MINIMUM_POINTS = 0.001f;

	/**
	 * Compute the fitness score of the Dot
	 * 
	 * @param f    the formula containing the equation to use to compute the fitness
	 *             score
	 * @param tvar the TerrainAndVar of the just passed simulation
	 * @return The fitness score of the Dot
	 */
	public float computeFitness(Formula f, SimuState s) {
		Context c = new Context(this, s);
		float fit = f.getValue(c);
		return Math.max(fit, MINIMUM_POINTS);
	}

	public void setBest(boolean best) {
		this.isBest = best;
	}

	public void setDead(boolean dead, int frameNumber) {
		isDead = dead;
		this.frameNumber = frameNumber;
	}

	public void setWin(boolean win, int frameNumber) {
		isWin = win;
		this.frameNumber = frameNumber;
	}

	/**
	 * The frame number of the win event (given in setWin)
	 * 
	 * @return the number of the winning frame
	 */
	public int getLastFrame() {
		return frameNumber;
	}

	public boolean isBest() {
		return isBest;
	}

	public boolean isDead() {
		return isDead;
	}

	public boolean isWin() {
		return isWin;
	}
}
