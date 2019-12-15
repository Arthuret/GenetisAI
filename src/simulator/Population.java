package simulator;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import tools.math.Vector;

/**
 * Contains a population of dots with brains
 * 
 * @author Arthur France
 */
public class Population implements Serializable {
	private static final long serialVersionUID = 1L;
	private Dot[] pop;
	private SimuState s;

	// debug and performance
	public transient long minThreadTime = Long.MAX_VALUE, maxThreadTime = -1;
	public transient long selectionTime = -1, fitComputeTime = -1;

	/**
	 * Generate a random population based on parameters in set
	 * 
	 * @param set The set containing all the parameters ans template for the
	 *            population
	 */
	public Population(SimuState s, Vector startPos) {
		this.s = s;
		pop = new Dot[s.set.brainSimuSet.populationSize];
		for (var i = 0; i < s.set.brainSimuSet.populationSize; i++) {
			pop[i] = new Dot(s.set.brainSimuSet.brainTemplate.generateRandomAgent(), startPos);
		}
	}

	private Population(Dot[] pop, long fitTime, long selTime, SimuState s) {
		this.s = s;
		this.pop = pop;
		this.selectionTime = selTime;
		this.fitComputeTime = fitTime;
	}

	/**
	 * To be called after deserialization (for at least one version) Because the
	 * SimuState store will be null when loading a file from an older version
	 * 
	 * @param s The SimuState
	 */
	public void setSimuState(SimuState s) {
		this.s = s;
	}

	/**
	 * Force all dots to a new position and reset them
	 * 
	 * @param pos The new position for all the dots of the population
	 */
	public void setPosition(Vector pos) {
		for (Dot d : pop) {
			d.reset(pos);
		}
	}

	private transient float[] fitnesses;
	private transient float max, sum;

	/**
	 * Will compute the fitness of all dots in the population and store it
	 * internally
	 * 
	 * @param old the TerrainAndVar of the just passed simulation
	 */
	public void computeFitness(TerrainAndVar old) {
		long t = System.currentTimeMillis();
		fitnesses = new float[pop.length];
		max = 0;
		sum = 0;
		for (int i = 0; i < pop.length; i++) {
			fitnesses[i] = pop[i].computeFitness(s.set.brainSimuSet.fitness, old);
			max = Math.max(max, fitnesses[i]);
			sum += fitnesses[i];
		}
		old.maxFitness = max;
		// System.out.println("fit:sum=" + sum + ";\tmax=" + max);
		fitComputeTime = System.currentTimeMillis() - t;
	}

	/**
	 * Generate a new Population. Account for the parameters
	 * 
	 * @param nextOrigin The origin of the next simulation
	 * @return The new generated population
	 */
	public Population getNextGeneration(Vector nextOrigin) {
		long t = System.currentTimeMillis();
		Dot[] newPop = new Dot[pop.length];
		Random r = new Random();
		switch (s.set.brainSimuSet.childOrigin) {
		case OLD_GENERATION:
			for (int i = 0; i < pop.length; i++) {
				float num = r.nextFloat() * sum;
				float tempSum = 0;
				for (int j = 0; j < pop.length; j++) {
					tempSum += fitnesses[j];
					if (tempSum >= num) {
						newPop[i] = new Dot(pop[j].getBrain().copy(), nextOrigin);
						break;
					}
				}
			}
			for (Dot d : newPop) {
				d.getBrain().mutate(s.set.brainSimuSet);
			}
			;
			break;
		case REMAINING_POPULATION:
			// sorting the old population
			List<Dot> ranked = getDotsRanked();
			int size_keep = (int) ((s.set.brainSimuSet.keepedProportion / 100f) * s.set.brainSimuSet.populationSize);
			// selecting dots
			for (int i = 0; i < size_keep; i++) {
				newPop[i] = ranked.get(i);
				newPop[i].reset(nextOrigin);
			}
			for (int i = size_keep; i < newPop.length; i++) {
				newPop[i] = new Dot(newPop[r.nextInt(size_keep)].getBrain().copy(), nextOrigin);
				newPop[i].getBrain().mutate(s.set.brainSimuSet);
			}
		}
		return new Population(newPop, fitComputeTime, System.currentTimeMillis() - t, s);
	}

	private class DotFit {
		private Dot d;
		private float fit;

		private DotFit(Dot d, float fit) {
			this.d = d;
			this.fit = fit;
		}
	}

	/**
	 * Compute the ranking of the current gen. Will only work at the end of a
	 * generation, after a call to compute fitness
	 * 
	 * @return The list of the dots ranked by fitness from the best to least.
	 */
	public List<Dot> getDotsRanked() {
		List<DotFit> l = new ArrayList<>();
		for (int i = 0; i < pop.length; i++) {
			l.add(new DotFit(pop[i], fitnesses[i]));
		}
		l.sort((a, b) -> (b.fit > a.fit) ? 1 : (b.fit == a.fit) ? 0 : -1);
		List<Dot> resp = new ArrayList<>();
		l.forEach(e -> resp.add(e.d));
		return resp;
	}

	/**
	 * Show the population on screen
	 * 
	 * @param g      the Graphics object
	 * @param factor The display scale
	 * @param offset the display offset
	 */
	public void show(Graphics g, float factor, Vector offset) {
		for (Dot d : pop) {
			d.show(g, factor, offset);
		}
	}

	private transient Thread[] threads;
	private transient Worker[] workers;
	private transient CyclicBarrier cbBegin, cbEnd;

	/**
	 * Initialise the threading system To be called before using the multithread
	 * option of step()
	 */
	public void initMultiThread() {
		// get number of maximum concurrently running threads
		// if more threads are created, they will execute on the same logical processor
		// and thus reduce the performance of all the threads on the same logical
		// processor.
		var nbProc = Math.min(Runtime.getRuntime().availableProcessors(), pop.length);
		// The barriers await for all the computing thread + the managing thread (+1)
		cbBegin = new CyclicBarrier(nbProc + 1);
		cbEnd = new CyclicBarrier(nbProc + 1);
		threads = new Thread[nbProc];
		workers = new Worker[nbProc];
		// near equal distribution of the processing need to the threads
		var numberPerThread = pop.length / nbProc;
		var remain = pop.length % nbProc;
		var tmp = 0;
		for (var i = 0; i < nbProc; i++) {
			var begin = tmp;
			tmp += (i < remain) ? numberPerThread + 1 : numberPerThread;
			workers[i] = new Worker(begin, tmp);
			threads[i] = new Thread(workers[i]);
			// all threads wait at the first barrier
			threads[i].start();
		}
	}

	public void destroyMultiThread() {
		// a infinite non limited while is possible because the wait time will be less
		// than a millisecond
		// we wait for all the threads to reach the gate
		// if we don't wait, one could exit befor the gate, and deadlock the app
		while (cbBegin.getNumberWaiting() != cbBegin.getParties() - 1)
			;
		// tell all the threads to exit after the gate
		for (var c : workers)
			c.running = false;
		try {
			// and unlocking them
			cbBegin.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Call step() on all dots.
	 */
	public void stepMonoThread() {
		var t = System.currentTimeMillis();
		for (var d : pop)
			s.dup.updateDot(d, s.frameNumber);
		minThreadTime = System.currentTimeMillis() - t;
		maxThreadTime = minThreadTime;
	}

	/**
	 * Call step() on all dots, using a set of threads to parallelize the processing
	 * Cannot be called before initMultiThread()
	 * Be sure to call destroyMultiThread() after using the multiThreading
	 */
	public void stepMultiThread() {
		try {
			cbEnd.reset();
			cbBegin.await();// starting signal for all computing threads
			cbBegin.reset();
			cbEnd.await();// wait for the end of processing
			minThreadTime = workers[0].time;
			maxThreadTime = workers[0].time;
			for(var i = 1;i < workers.length;i++) {
				minThreadTime = Math.min(workers[i].time, minThreadTime);
				maxThreadTime = Math.max(workers[i].time, maxThreadTime);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	private class Worker implements Runnable {
		private int begin, end;
		private boolean running = true;
		private long time = -1;

		private Worker(int begin, int end) {
			this.begin = begin;
			this.end = end;
		}

		@Override
		public void run() {
			try {
				while (running) {
					cbBegin.await();
					if (running) {
						var t = System.currentTimeMillis();
						for (var i = begin; i < end; i++)
							s.dup.updateDot(pop[i], s.frameNumber);
						time = System.currentTimeMillis()-t;
						cbEnd.await();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add the position of the [0] dot to the history
	 * 
	 * @param h the history to update
	 */
	public void updateHisto(History h) {
		h.appPos(pop[0].getPosition());
	}

	/**
	 * Test if all dots are dead or win
	 * 
	 * @return true if all dots are dead or win
	 */
	public boolean isAllDead() {
		for (Dot d : pop) {
			if (!d.isDead() && !d.isWin())
				return false;
		}
		return true;
	}
}
