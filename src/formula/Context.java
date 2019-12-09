package formula;

import simulator.Dot;
import simulator.TerrainAndVar;
import tools.math.Vector;

public class Context {
	
	private Dot d;
	private TerrainAndVar tvar;
	
	public Context(Dot d,TerrainAndVar tvar) {
		this.d = d;
		this.tvar = tvar;
	}
	
	public void setDot(Dot d) {
		this.d = d;
	}
	
	public void setTVar(TerrainAndVar tvar) {
		this.tvar = tvar;
	}
	
	public float getVariable(Variables v) {
		switch(v) {
		case DEAD:
			return (d.isDead())?1f:0f;
		case DISTANCE:
			return (float) Vector.distance(tvar.tvar.getGoal().getPosition(),d.getPosition());
		case MAX_DISTANCE:
			return (float) tvar.t.getWalls().getAmplitude();
		case MAX_SPEED:
			return d.getBrain().getSpeedLimit();
		case NB_STEPS:
			return d.getLastFrame();
		case SPEED:
			return (float) d.getSpeed().getAmplitude();
		case WIN:
			return (d.isWin())?1f:0f;
		}
		return 0f;
	}
	
	/*public float getVariable(Variables v) {//debug
		switch(v) {
		case DEAD:
			return 1f;
		case DISTANCE:
			return 100f;
		case MAX_DISTANCE:
			return 1000f;
		case MAX_SPEED:
			return 20f;
		case NB_STEPS:
			return 25f;
		case SPEED:
			return 5f;
		case WIN:
			return 1f;
		}
		return 0f;
	}*/
}
