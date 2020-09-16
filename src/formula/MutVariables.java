package formula;

public enum MutVariables implements Variable{
	MAX_FIT("The fitness of the best dot of the generation"),
	MAX_FIT_LAST("The fitness of the best dot of the last generation"),
	IS_WIN("1 if at least one dot reached the goal, 0 otherwise"),
	NB_WIN("The number of dots that reached the objective"),
	POP_SIZE("The number of dots in each generation"),
	MEAN_FIT("The mean fitness between all the dots"),
	NB_GEN("The number of generations passed (from 1)");
	
	private String description;
	
	MutVariables(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
