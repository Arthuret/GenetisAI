package formula;

public enum Variables {
	DISTANCE("Distance to the objective (pixels)"),
	SPEED("The speed of the dot at it's last move (just before it wins or die) (pixels/step)"),
	NB_STEPS("The number of steps the dot played (before it wins or die)"),
	WIN("1 if the dot reached the objective, 0 otherwise"),
	DEAD("1 if the dot touched a wall, 0 otherwise"),
	MAX_DISTANCE("The maximum distance possible on the current terrain (a diagonal)"),
	MAX_SPEED("The speed limit of the dot (set in brain parameters)");
	
	private String description;
	
	Variables(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
