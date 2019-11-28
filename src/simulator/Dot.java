package simulator;

import java.awt.Color;
import java.awt.Graphics;

import brain.BrainData;
import tools.math.Vector;

/**
 * A simulation entity, containing a brain
 * @author Arthur France
 *
 */
public class Dot {
	private BrainData brain;
	private Vector position, speed;
	private boolean isBest = false;// allow different display for the best dot
	private boolean isDead = false;
	private boolean isWin = false;
	
	private int frameNumber;

	private static final Color DEFAULT_COLOR = Color.DARK_GRAY;
	private static final Color BEST_COLOR = new Color(0, 128, 0);
	private static final Color DEAD_COLOR = new Color(128, 0, 0);
	private static final Color WIN_COLOR = new Color(128, 128, 0);

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

	/**
	 * Reset all values to default and teleport to position
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
	 * @param g The Graphics object
	 * @param factor The display scale
	 * @param offset The display offset
	 */
	public void show(Graphics g,float factor,Vector offset) {
		g.setColor((isBest)?BEST_COLOR:(isDead)?DEAD_COLOR:(isWin)?WIN_COLOR:DEFAULT_COLOR);
		int initialRadius = (isBest)?20:8;
		int radius = (int) (initialRadius*factor);
		Vector spos = position.times(factor).sub(new Vector(radius/2f)).add(offset);
		g.fillOval((int) spos.x(), (int) spos.y(), radius, radius);
	}

	/**
	 * Call the brain and perform a physic step
	 * @param tavar the current terrain environnement
	 */
	public void update(TerrainAndVar tavar) {
		float[] inputs = tavar.t.computeInputs(tavar.tvar, position, speed, brain.cSpeed(), brain.cDistance(), brain.cDirection(), brain.cWalls(),
				brain.sensorLimit());
		float[] outputs = brain.compute(inputs);
		Vector acc = new Vector(outputs[0],outputs[1]);
		speed = speed.add(acc).limit(brain.getSpeedLimit());
		position = position.add(speed);
		
	}
	
	//scores used to compute fitness scores //TODO Put that in a file or interface
	private static final float WIN_POINTS = 50_000;
	private static final float DEATH_POINTS = 10;
	private static final float DISTANCE_POINTS = 10_000;
	
	/**
	 * Compute the fitness score of the Dot
	 * @param goal the goal to reach
	 * @return The fitness score of the Dot
	 */
	public float computeFitness(Vector goal) {
		float fit = 0;
		if(isWin) fit+=(WIN_POINTS/frameNumber);
		if(isDead) fit-=DEATH_POINTS;
		fit+=DISTANCE_POINTS/Vector.distance(goal, position);
		return Math.max(fit,0.1f);
	}

	public void setBest(boolean best) {
		this.isBest = best;
	}

	public void setDead(boolean dead) {
		isDead = dead;
	}

	public void setWin(boolean win,int frameNumber) {
		isWin = win;
		this.frameNumber = frameNumber;
	}
	
	/**
	 * The frame number of the win event (given in setWin)
	 * @return the number of the winning frame
	 */
	public int getWinFrame() {
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
