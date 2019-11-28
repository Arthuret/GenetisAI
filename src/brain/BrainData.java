package brain;

import java.io.Serializable;

import menu.training_editor.BrainSimulationSet;

public interface BrainData extends Serializable{
	public float[] compute(float[] inputs);
	
	public void mutate(BrainSimulationSet set);
	
	public boolean cSpeed();
	public boolean cDistance();
	public boolean cDirection();
	public boolean cWalls();
	public float sensorLimit();
	public float getSpeedLimit();
	public BrainData copy();
}
