package simulator;

import java.io.ObjectInputStream;
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
	
	private void readObject(ObjectInputStream ois) throws Exception{
		//temporary measure to account for old files
		ois.defaultReadObject();
		
		pop.setSimuState(this);
	}
}