package menu.training_editor;

import java.io.Serializable;

/**
 * An Object that contains all the data necessary to launch a simulation
 * @author Arthur France
 *
 */
public class SimulationDataSet implements Serializable{
	private static final long serialVersionUID = -45205180091715572L;

	public TerrainSimulationSet terrainSets;
	public BrainSimulationSet brainSimuSet;
	
	public SimulationDataSet() {
		terrainSets = new TerrainSimulationSet();
		brainSimuSet = new BrainSimulationSet();
	}

	public SimulationDataSet(TerrainSimulationSet terrainSets, BrainSimulationSet brainSimuSet) {
		this.terrainSets = terrainSets;
		this.brainSimuSet = brainSimuSet;
	}
	
	public String toString() {
		return terrainSets.toString()+"\n"+brainSimuSet.toString();
	}
}
