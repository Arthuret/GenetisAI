package menu.training_editor;

import java.io.Serializable;
import java.text.ParseException;

import brain.BrainTemplate;
import formula.Formula;

public class BrainSimulationSet implements Serializable {
	private static final long serialVersionUID = -8263389888642229445L;
	public BrainTemplate brainTemplate = null;
	public int populationSize = 200;
	public int relMutDivider = 50;
	public ChangementLaws changeLaw = ChangementLaws.NORMAL_LAW;
	public float sigma = 0.1f;
	public int absMutDivider = 1000;
	public int keepedProportion = 50;
	public ChildOrigin childOrigin = ChildOrigin.OLD_GENERATION;
	public transient static String DEFAULT_FIT = "10/$DISTANCE";
	public Formula fitness;
	
	public BrainSimulationSet() {
		try {
			fitness = Formula.parse(DEFAULT_FIT);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		String resp = brainTemplate.toString()+"\n";
		resp+="popSize="+populationSize+";";
		resp+="relMutDiv="+relMutDivider+";";
		resp+="loiChang="+changeLaw+";";
		resp+="sigma="+sigma+";";
		resp+="absMutDiv="+absMutDivider+";";
		resp+="childProp="+keepedProportion+";";
		resp+="chilsOrg="+childOrigin+";";
		resp+="fitness="+fitness+";";
		return resp;
	}
}
