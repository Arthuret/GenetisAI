package menu.training_editor;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ParseException;

import brain.BrainTemplate;
import formula.Formula;

public class BrainSimulationSet implements Serializable {
	private static final long serialVersionUID = -8263389888642229445L;
	public BrainTemplate brainTemplate = null;
	public int populationSize = 200;
	public int nbRelMut = 50;
	public ChangementLaws changeLaw = ChangementLaws.NORMAL_LAW;
	public float sigma = 0.1f;
	public int nbAbsMut = 0;
	public int keepedProportion = 50;
	public ChildOrigin childOrigin = ChildOrigin.OLD_GENERATION;
	public transient static String DEFAULT_FIT = "10/$DISTANCE";
	public Formula fitness;

	public BrainSimulationSet() {
		try {
			fitness = Formula.parse(DEFAULT_FIT,FormulaTypes.FITNESS);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		String resp = brainTemplate.toString() + "\n";
		resp += "popSize=" + populationSize + ";";
		resp += "nbRelMut=" + nbRelMut + ";";
		resp += "loiChang=" + changeLaw + ";";
		resp += "sigma=" + sigma + ";";
		resp += "nbAbsMut=" + nbAbsMut + ";";
		resp += "childProp=" + keepedProportion + ";";
		resp += "chilsOrg=" + childOrigin + ";";
		resp += "fitness=" + fitness + ";";
		return resp;
	}
	
	private void readObject(ObjectInputStream ois) throws Exception{
		//temporary measure to account for old files
		ois.defaultReadObject();
		
		if(nbRelMut == 0)
			nbRelMut = 50;
	}
}
