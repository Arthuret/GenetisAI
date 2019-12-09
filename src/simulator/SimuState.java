package simulator;

import java.io.Serializable;

import menu.training_editor.SimulationDataSet;

public class SimuState implements Serializable {
	public static final long serialVersionUID = 1L;
	public int genNumber = 0;// the number of the current generation
	public int frameNumber = 0;// the number of the actual frame
	public SimulationDataSet set;
	public TerrainAndVar current;
	public History newHist;

	public Population pop;
	public DotUpdater dup;
}