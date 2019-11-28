package menu.training_editor;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import environnement.TerrainVariation;
import environnement.TerrainVariationSet;
import simulator.TerrainAndVar;
import tools.math.Vector;

public class TerrainSimulationSet implements Serializable {
	private static final long serialVersionUID = 3527686517048480224L;

	public List<TerrainVariationSet> terrains;
	
	public transient List<TerrainAndVar> simuTerrains = null;
	
	private static transient final int SCORE_PERCENT_REDUCTION = 5;
	
	public TerrainSimulationSet(List<TerrainVariationSet> varSet) {
		this.terrains = varSet;
	}
	
	public TerrainSimulationSet() {
		this(new ArrayList<>());
	}
	
	public void removeVariation(TerrainVariation var) {
		terrains.forEach(tvs->tvs.removeVariation(var));
	}
	
	/**
	 * Return the size of the largest terrain (largest and longest)
	 * @return
	 */
	public Dimension getMaxSize() {
		Vector max = terrains.get(0).getT().getWalls();
		for(TerrainVariationSet set:terrains) {
			Vector v = set.getT().getWalls();
			max.setX(Math.max(max.x(), v.x()));
			max.setY(Math.max(max.y(), v.y()));
		}
		return max.getDimension();
	}
	
	private transient Iterator<TerrainAndVar> ite = null;
	
	public TerrainAndVar nextVar() {
		if(simuTerrains == null) generateTerrainList();
		if(simuTerrains.size() == 0) throw new UnsupportedOperationException("No variation found");
		
		if(ite == null || !ite.hasNext()) ite = simuTerrains.iterator();
		return ite.next();
	}
	
	public TerrainAndVar nextLowVar() {
		if(simuTerrains == null) generateTerrainList();
		if(simuTerrains.size() == 0) throw new UnsupportedOperationException("No variation found");
		simuTerrains.sort((a,b)->{
			float temp = a.maxFitness-b.maxFitness;
			if(temp > 0) return 1;
			if(temp < 0) return -1;
			return 0;
		});
		System.out.println(simuTerrains);
		for(TerrainAndVar tv:simuTerrains) {
			tv.maxFitness*=((100-SCORE_PERCENT_REDUCTION)/100.);
		}
		return simuTerrains.get(0);
	}
	
	private void generateTerrainList() {
		simuTerrains = new ArrayList<>();
		for(TerrainVariationSet set:terrains) {
			for(TerrainVariation var:set.getVariations()) {
				simuTerrains.add(new TerrainAndVar(set.getT(),var));
			}
		}
	}
	
	public boolean hasVariation() {
		for(TerrainVariationSet set:terrains) {
			if(set.getVariationCount() > 0) return true;
		}
		return false;
	}
}
