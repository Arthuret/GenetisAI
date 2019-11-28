package menu.training_editor;

import java.io.Serializable;

import brain.BrainTemplate;

public class BrainSimulationSet implements Serializable {
	private static final long serialVersionUID = -8263389888642229445L;
	public BrainTemplate brainTemplate = null;
	public int populationSize = 200;
	public int relMutDivider = 50;
	public ChangementLaws changeLaw = ChangementLaws.NORMAL_LAW;
	public float sigma = 0.1f;
	public int absMutDivider = 1000;
	public int childProportion = 50;
	public ChildOrigin childOrigin = ChildOrigin.OLD_GENERATION;
	
	public String toString() {
		String resp = brainTemplate.toString()+"\n";
		resp+="popSize="+populationSize+";";
		resp+="relMutDiv="+relMutDivider+";";
		resp+="loiChang="+changeLaw+";";
		resp+="sigma="+sigma+";";
		resp+="absMutDiv="+absMutDivider+";";
		resp+="childProp="+childProportion+";";
		resp+="chilsOrg="+childOrigin+";";
		return resp;
	}
}
