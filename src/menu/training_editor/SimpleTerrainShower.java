package menu.training_editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import environnement.*;

@SuppressWarnings("serial")
public class SimpleTerrainShower extends JPanel implements Scrollable{

	private Terrain t;
	private TerrainVariation tvar;
	
	private boolean uselessPoints,pointNames;
	
	/**
	 * Create the panel.
	 */
	public SimpleTerrainShower(Dimension tailleMax,Terrain t,TerrainVariation tvar,boolean showUselessPoints,boolean showPointNames) {
		super();
		this.t = t;
		this.tvar = tvar;
		this.uselessPoints = showUselessPoints;
		this.pointNames = showPointNames;
		Dimension taille = (tailleMax != null)?tailleMax:t.getWalls().getDimension();
		this.setMaximumSize(taille);
		this.setPreferredSize(taille);
		this.setMinimumSize(taille);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(t != null && tvar != null) {
			Dimension max = this.getMaximumSize();
			float ratio = (float) Math.min(Math.min(max.height/t.getWalls().y(), max.width/t.getWalls().x()), 1.);
			Color temp = g.getColor();
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, (int)(t.getWalls().x()*ratio),(int) (t.getWalls().y()*ratio));
			g.setColor(temp);
			t.showSimpleShower(g, tvar, ratio, !uselessPoints,pointNames);
		}
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return t.getWalls().getDimension();
	}

	@Override
	public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	public void setTerrain(Terrain t,TerrainVariation tvar) {
		this.t = t;
		this.tvar = tvar;
	}
}
