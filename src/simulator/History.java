package simulator;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import tools.math.Vector;

/**
 * Store a location history of a dot
 * @author Arthur France
 *
 */
public class History {
	private List<Vector> chemin = new ArrayList<>();
	
	private boolean old = false;//change the display color
	
	private static final Color NEW_C = new Color(0, 64, 0);
	private static final Color OLD_C = new Color(64, 0, 0);
	
	public void setOld() {
		old = true;
	}
	
	/**
	 * Add an entry to the history
	 * @param v The position to add. The stored position will be independent
	 */
	public void appPos(Vector v) {
		Vector vi = v.copy();
		synchronized(this) {
			chemin.add(vi);
		}
	}
	
	/**
	 * Show the positions on screen
	 * @param g the Graphics object
	 * @param factor The display scale
	 * @param offset the display offset
	 */
	public void show(Graphics g, float factor, Vector offset) {
		g.setColor((old)?OLD_C:NEW_C);
		int radius = (int) (8*factor);
		Vector radi = new Vector((radius)/2f);
		synchronized(this) {
			chemin.forEach(v->{
				Vector spos = v.times(factor).sub(radi).add(offset);
				g.fillOval((int) spos.x(), (int) spos.y(), radius, radius);
			});
		}
	}
}
