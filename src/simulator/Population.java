package simulator;

import java.awt.Graphics;
import java.util.Random;

import menu.training_editor.BrainSimulationSet;
import tools.math.Vector;

/**
 * Contains a population of dots with brains
 * @author Arthur France
 */
public class Population {
	
	private Dot[] pop;

	/**
	 * Generate a random population based on parameters in set
	 * @param set The set containing all the parameters ans template for the population
	 */
	public Population(BrainSimulationSet set,Vector startPos) {
		pop = new Dot[set.populationSize];
		for(int i = 0;i < set.populationSize;i++) {
			pop[i] = new Dot(set.brainTemplate.generateRandomAgent(),startPos);
		}
	}
	
	private Population(Dot[] pop) {
		this.pop = pop;
	}
	
	/**
	 * Force all dots to a new position and reset them
	 * @param pos The new position for all the dots of the population
	 */
	public void setPosition(Vector pos) {
		for(Dot d:pop) {
			d.reset(pos);
		}
	}
	
	/**
	 * Generate a new Population based on the set parameters
	 * @param set The parameters for the generation evolution and selection
	 * @param oldtvar Old terrainVariation
	 * @param nextOrigin The origin of the next simulation
	 * @return
	 */
	public Population getNextGeneration(BrainSimulationSet set,TerrainAndVar oldtvar,Vector nextOrigin) {
		float[] fitness = new float[pop.length];
		float sum = 0;
		for(int i = 0;i < pop.length;i++) {
			fitness[i] = pop[i].computeFitness(oldtvar.tvar.getGoal().getPosition());
			sum+=fitness[i];
		}
		Dot[] newPop = new Dot[pop.length];
		float max = 0;
		for(int i = 0;i < pop.length;i++) {
			if(fitness[i] > max) {
				max = fitness[i];
			}
		}
		oldtvar.maxFitness = max;
		System.out.println("fit:sum="+sum+";\tmax="+max);
		Random r = new Random();
		switch(set.childOrigin) {
		case OLD_GENERATION:
			for(int i = 0;i < pop.length;i++) {
				float num = r.nextFloat()*sum;
				float tempSum = 0;
				for(int j = 0;j < pop.length;j++) {
					tempSum+=fitness[j];
					if(tempSum >= num) {
						newPop[i] = new Dot(pop[j].getBrain().copy(),nextOrigin);
						break;
					}
				}
			}
			for(Dot d:newPop) {
				d.getBrain().mutate(set);
			}
			return new Population(newPop);
		}
		return null;
	}
	
	/**
	 * Show the population on screen
	 * @param g the Graphics object
	 * @param factor The display scale
	 * @param offset the display offset
	 */
	public void show(Graphics g,float factor,Vector offset) {
		for(Dot d:pop) {
			d.show(g,factor,offset);
		}
	}
	
	/**
	 * Perform a physic step on oll dots
	 * @param du The updater to use
	 * @param frameNumber The actual frame number
	 */
	public void step(DotUpdater du,int frameNumber) {
		for(Dot d:pop) {
			du.updateDot(d,frameNumber);
		}
	}
	
	/**
	 * Add the position of the [0] dot to the history
	 * @param h the history to update
	 */
	public void updateHisto(History h) {
		h.appPos(pop[0].getPosition());
	}
	
	/**
	 * Test if all dots are dead or win
	 * @return true if all dots are dead or win
	 */
	public boolean isAllDead() {
		for(Dot d:pop) {
			if(!d.isDead() && !d.isWin()) return false;
		}
		return true;
	}
}
