package simulator;

import java.io.Serializable;

import environnement.Terrain;
import environnement.TerrainVariation;

/**
 * Store a Terrain and a TerrainVariation
 * Also store a history and a max fitness
 * Used in simulation to cycle through all available terrainVariations
 * @author Arthur France
 *
 */
public class TerrainAndVar implements Serializable{
	private static final long serialVersionUID = 1L;
	public Terrain t;
	public TerrainVariation tvar;
	public History hist = null;
	public float maxFitness = 0;
	
	public TerrainAndVar(Terrain t,TerrainVariation tvar) {
		this.t = t;
		this.tvar = tvar;
	}
	
	public String toString() {
		return tvar.toString()+";"+maxFitness;
	}
}
