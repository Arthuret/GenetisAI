package formula;

public enum MutVariables implements Variable{
	MAXFITNESS("The fitness of the best dot of the generation"),
	MAXFITLAST("The fitness of the best dot of the last generation"),
	ISWIN("1 if at least one dot reached the goal, 0 otherwise"),
	POPSIZE("The number of dots in each generation"),
	MEANFIT("The mean fitness between all the dots");
	
	private String description;
	
	MutVariables(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
