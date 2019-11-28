package environnement;

public enum PointType {
	USELESS("pointIcon.png"),
	ORIGIN("originIcon.png"),
	GOAL("objectifIcon.png");
	
	private final String fileName;
	
	PointType(String fileName){
		this.fileName = fileName;
	}
	public String getIconFileName() {
		return fileName;
	}
}
