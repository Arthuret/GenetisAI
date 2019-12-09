package simulator;

import java.io.Serializable;

import environnement.Point;
import tools.math.Vector;

/**
 * Host functions to update all Dots
 * @author Arthur France
 *
 */
public class DotUpdater implements Serializable{
	private static final long serialVersionUID = 1L;
	private TerrainAndVar tavar;

	public DotUpdater(TerrainAndVar tavar) {
		this.tavar = tavar;
	}

	/**
	 * Update the dot and perform death and win tests
	 * @param d the dot to update
	 * @param frameNumber the actual frame number
	 */
	public void updateDot(Dot d, int frameNumber) {
		if (!d.isDead() && !d.isWin()) {
			d.update(tavar);

			if (!tavar.t.isValid(d.getPosition()))
				d.setDead(true, frameNumber);
			Point goal = tavar.tvar.getGoal();
			if (Vector.distance(goal.getPosition(), d.getPosition()) < goal.getCollisionRadius())
				d.setWin(true, frameNumber);
		}
	}
}
