package menu.training_editor;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ParseException;

import brain.BrainTemplate;
import formula.Context;
import formula.Formula;
import formula.FormulaTypes;
import simulator.SimuState;

public class BrainSimulationSet implements Serializable {
	private static final long serialVersionUID = -8263389888642229445L;
	public BrainTemplate brainTemplate = null;
	public int populationSize = 200;
	public Formula mutation;
	public ChangementLaws changeLaw = ChangementLaws.NORMAL_LAW;
	public float sigma = 0.1f;
	public int nbAbsMut = 0;
	public int keepedProportion = 50;
	public ChildOrigin childOrigin = ChildOrigin.OLD_GENERATION;
	public transient static String DEFAULT_FIT = "10/$DISTANCE";
	public transient static String DEFAULT_MUT = "50/($NB_GEN/2)";
	public Formula fitness;
	public transient float mutaChance;

	public BrainSimulationSet() {
		try {
			fitness = Formula.parse(DEFAULT_FIT, FormulaTypes.FITNESS);
			mutation = Formula.parse(DEFAULT_MUT, FormulaTypes.MUTATION);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		String resp = brainTemplate.toString() + "\n";
		resp += "popSize=" + populationSize + ";";
		resp += "nbRelMut=" + mutation + ";";
		resp += "loiChang=" + changeLaw + ";";
		resp += "sigma=" + sigma + ";";
		resp += "nbAbsMut=" + nbAbsMut + ";";
		resp += "childProp=" + keepedProportion + ";";
		resp += "chilsOrg=" + childOrigin + ";";
		resp += "fitness=" + fitness + ";";
		return resp;
	}

	private void readObject(ObjectInputStream ois) throws Exception {
		// temporary measure to account for old files
		ois.defaultReadObject();
	}

	public void computeMutation(SimuState s) {
		mutaChance = mutation.getValue(new Context(s));
	}

	public float getMutaChance() {
		return mutaChance;
	}
}
