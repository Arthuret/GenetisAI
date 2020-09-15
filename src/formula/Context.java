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
	
	public float getVariable(Variable v) {
		if(v instanceof FitVariables)
			return getVariableFit((FitVariables) v);
		if(v instanceof MutVariables)
			return getVariableMut((MutVariables) v);
		return 0;
	}
	
	public float getVariableFit(FitVariables v) {
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
	
	public float getVariableMut(MutVariables v) {
		return 0;
	}
}
