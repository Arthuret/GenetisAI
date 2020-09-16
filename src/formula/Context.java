package formula;

import simulator.Dot;
import simulator.SimuState;
import tools.math.Vector;

public class Context {
	
	private Dot d;
	private SimuState sState;
	
	public Context(Dot d, SimuState sState) {
		this.d = d;
		this.sState = sState;
	}
	
	public Context(SimuState s) {
		this.sState = s;
		this.d = null;
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
			return (float) Vector.distance(sState.current.tvar.getGoal().getPosition(),d.getPosition());
		case MAX_DISTANCE:
			return (float) sState.current.t.getWalls().getAmplitude();
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
		switch(v) {
		case IS_WIN:
			return (sState.pop.getNumberWin() > 0)?1:0;
		case NB_WIN:
			return sState.pop.getNumberWin();
		case MAX_FIT_LAST:
			return sState.current.getOldFitness();
		case MAX_FIT:
			return sState.current.getFitness();
		case MEAN_FIT:
			return sState.pop.getMeanFitness();
		case NB_GEN:
			return sState.genNumber+1;
		case POP_SIZE:
			return sState.set.brainSimuSet.populationSize;
		default:
			break;
		}
		return 0;
	}
}
