package menu.training_editor;

import formula.FitVariables;
import formula.MutVariables;
import formula.Variable;

public enum FormulaTypes {
	FITNESS(FitVariables.class),
	MUTATION(MutVariables.class);
	
	private Class<? extends Variable> varEnum;
	
	private FormulaTypes(Class<? extends Variable> varEnum) {
		this.varEnum = varEnum;
	}
	
	public Class<? extends Variable> getEnum(){
		return varEnum;
	}
	
	public Variable[] getValues(){
		switch(this) {
		case FITNESS:
			return FitVariables.values();
		case MUTATION:
			return MutVariables.values();
		default:
			return null;
		}
	}
}
