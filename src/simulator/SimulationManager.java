package simulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.text.DecimalFormat;

import menu.MainMenu;
import menu.training_editor.SimulationDataSet;
import tools.math.Vector;

/**
 * Manage the simulation parameters and features, threads and evolution. The
 * heart of the simulator.
 * 
 * @author Arthur France
 */
public class SimulationManager implements Runnable {

	private SimulationDataSet set;
	private SimuFrame frame;
	private SimuPane pane;

	private TerrainAndVar current;
	private History newHist;
	private boolean running = true;// exit switch

	private int genNumber = 0;// the number of the current generation
	private int framerate = 60;// the number of frames to display every second
	private static final int FRAMERATE_UNLIMITED = -2, FRAMERATE_DRAW_LIMITED = -1;
	private long frameInterval = (int) ((1. / framerate) * 1000);// the interval between 2 frames. This value is
																	// truncated, so the framerate will not be precise
	private boolean pause = false;// if the simulation is paused
	private boolean stepCall = false;// ask for one step forward while in pause
	private boolean stepGenCall = false;// ask for one gen forward while in pause
	private int frameNumber = 0;// the number of the actual frame
	private long nbUpLast = 0;// number of physics step in the last second

	private boolean restart = false;// request for reinitializing the simulator
	private boolean statShow = true;// show the text infos
	private boolean history = true;// show the histories

	private boolean fill = false, showCount = false;// fill the obstacles, show the number of collisions on the
													// obstacles
	private boolean multithread = false;

	private boolean endOfDraw = false;// flag used to signal the end of the call to draw to the physics thread. used
										// to synchronize the calculations and the render

	private static final int SLEEP_PAUSE_MILLIS = 100;// pause cycle while the simulaton is in pause
	private static final int SLEEP_WAIT_DRAW_MILLIS = 1;// sleep duration while waiting for the render to end
	private static final int SLEEP_WAIT_FRAME_MILLIS = 1;// sleep duration while waiting for the next frame time

	private static final int MAX_FRAME_PER_GEN = 300;// temporary//TODO

	private static final int CHECK_DEAD_MOD = 10;// check for all dots dead every [] frames

	private Population pop;
	private DotUpdater dup;

	private static DecimalFormat df1 = new DecimalFormat();// used to display memory used
	private static DecimalFormat df2 = new DecimalFormat();// idem

	public SimulationManager(SimulationDataSet set) {
		this.set = set;
		df1.setMaximumFractionDigits(1);
		df2.setMaximumFractionDigits(2);
	}

	public Thread startSimulation() {
		Thread t = new Thread(this);
		t.start();
		return t;
	}

	@Override
	public void run() {
		// Open the window
		pane = new SimuPane(this);
		pane.setPreferredSize(set.terrainSets.getMaxSize());
		frame = new SimuFrame(pane, this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		System.out.println("Starting simulation");

		// grab an environnement
		nextTerrain();

		// seting up the population and timings
		pop = new Population(set.brainSimuSet, current.tvar.getOrigin().getPosition());
		long nextSecond = System.currentTimeMillis();
		long nbUp = 0;
		boolean endGen = false;
		while (running) {
			// main loop
			long nextFrameTime = System.currentTimeMillis();
			newHist = new History();
			do {
				// physic engine
				simuStep();
				nbUp++;
				// framerate management
				if (frameInterval != 0) {
					while (System.currentTimeMillis() < nextFrameTime)
						safeSleep(SLEEP_WAIT_FRAME_MILLIS);
					nextFrameTime = System.currentTimeMillis() + frameInterval;
				}
				// performance reading
				if (nextSecond < System.currentTimeMillis()) {
					nextSecond = System.currentTimeMillis() + 1000;
					nbUpLast = nbUp;
					nbUp = 0;
				}
				// call to paint
				pane.repaint();
				frameNumber++;
				// synchronizing with render thread (awt)
				if (framerate != FRAMERATE_UNLIMITED) {// bypass
					while (!endOfDraw)
						safeSleep(SLEEP_WAIT_DRAW_MILLIS);
					endOfDraw = false;
				}
				// pause management
				while (pause && !stepCall && !stepGenCall)
					safeSleep(SLEEP_PAUSE_MILLIS);
				stepCall = false;
				// test for generation shortcut
				if (frameNumber % CHECK_DEAD_MOD == 0)
					endGen = pop.isAllDead();
			} while (frameNumber < MAX_FRAME_PER_GEN && !endGen && running);
			endGen = false;
			if(stepGenCall) {
				stepGenCall = false;
				while(pause && !stepGenCall && !stepCall && running) safeSleep(SLEEP_PAUSE_MILLIS);
				stepCall = false;
			}

			// generation management
			if (running) {
				genStep();// perform history management only
				// generate next generation
				TerrainAndVar old = current;
				nextTerrain();
				if (restart) {
					restart = false;
					pop = new Population(set.brainSimuSet, current.tvar.getOrigin().getPosition());
					genNumber = 0;
				} else {
					pop = pop.getNextGeneration(set.brainSimuSet, old, current.tvar.getOrigin().getPosition());
					genNumber++;
				}
				frameNumber = 0;
			}
		}
		System.out.println("Simulation terminated");

		new MainMenu().setVisible(true);
		frame.dispose();
	}

	/**
	 * Thread.sleep with try catch
	 * 
	 * @param milli the number of milliseconds to sleep. See thread.sleep() for more
	 *              details
	 */
	public void safeSleep(int milli) {
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show the terrain and dots and datas. Called by simuPane
	 * 
	 * @param g                 Graphics object
	 * @param size              The size of the panel
	 * @param nbFrames          The number of frames in the last second
	 * @param timeFromLastFrame The time passed in millisecond from the previous
	 *                          frame
	 */
	public void draw(Graphics g, Dimension size, long nbFrames, long timeFromLastFrame) {
		float factor = getFactor(current.t.getWalls(), size);
		Vector offset = getOffset(factor, current.t.getWalls(), size);
		current.t.showSimulation(g, current.tvar, factor, offset, fill, showCount);
		if (pop != null)
			pop.show(g, factor, offset);
		if (history) {
			if (current.hist != null)
				current.hist.show(g, factor, offset);
			if (newHist != null)
				newHist.show(g, factor, offset);
		}
		hideOutOfBound(g, size, current.t.getWalls(), factor, offset);
		if (statShow)
			showDebug(g, nbFrames, timeFromLastFrame);
		endOfDraw = true;
	}

	/**
	 * Compute the maximum display factor
	 * @param sizeT the size of the Terrain
	 * @param window the size of the panel
	 * @return The maximum applicable display factor
	 */
	private float getFactor(Vector sizeT, Dimension window) {
		float h = (float) (window.height / sizeT.y());
		float w = (float) (window.width / sizeT.x());
		return Math.min(h, w);
	}

	/**
	 * Compute the display offset to maintain the terrain display in the center of the pane
	 * @param factor the display factor used
	 * @param sizeT the size of the Terrain
	 * @param window the size of the panel
	 * @return the offset to center the display
	 */
	private Vector getOffset(float factor, Vector sizeT, Dimension window) {
		Vector fSizeT = sizeT.times(factor);
		float x = (float) ((window.width - fSizeT.x()) / 2f);
		float y = (float) ((window.height - fSizeT.y()) / 2f);
		return new Vector(x, y);
	}

	/**
	 * Show all interesting numbers on screen
	 * 
	 * @param g                 Graphics object
	 * @param nbFrames          The number of frames in the last second
	 * @param timeFromLastFrame The time passed in millisecond from the previous
	 *                          frame
	 */
	private void showDebug(Graphics g, long nbFrames, long timeFromLastFrame) {
		int y = 10, yp = 12;
		g.setColor(Color.RED);
		g.drawString("FPS:" + nbFrames, 10, y);
		y += yp;
		g.drawString("FPS Limit:"
				+ ((framerate > 0) ? framerate : (framerate == FRAMERATE_DRAW_LIMITED) ? "Draw limit" : "No limit"), 10,
				y);
		y += yp;
		g.drawString("FMS:" + timeFromLastFrame, 10, y);
		y += yp;
		g.drawString("UPS:" + nbUpLast, 10, y);
		y += yp;
		g.drawString("Frame:" + frameNumber + "/" + MAX_FRAME_PER_GEN, 10, y);
		y += yp;
		g.drawString("Generation:" + genNumber, 10, y);
		y += yp;
		g.drawString("Brain type:" + set.brainSimuSet.brainTemplate.getType(), 10, y);
		y += yp;
		g.drawString("Pop size:" + set.brainSimuSet.populationSize, 10, y);
		y += yp;
		g.drawString("Threading:"+((multithread)?"Multi":"Mono"), 10, y);
		y += yp;
		Runtime r = Runtime.getRuntime();
		long memAll = r.totalMemory();
		long memUsed = memAll - r.freeMemory();
		g.drawString("Memory-All:" + df2.format(memAll / 1_000_000f) + "MB; Used:"
				+ df1.format((memUsed / (float) memAll) * 100) + "%", 10, y);
	}

	private static final Color BACKGROUND = Color.DARK_GRAY;

	/**
	 * Draw rectangles to cover all borders of the display area, to hide any bad obstacle display
	 * @param g the Graphics object
	 * @param size the size of the panel
	 * @param sizeT the size of the terrain
	 * @param factor the display factor
	 * @param offsetT the display offset
	 */
	private void hideOutOfBound(Graphics g, Dimension size, Vector sizeT, float factor, Vector offsetT) {
		g.setColor(BACKGROUND);
		// left/right sides
		Vector endT = offsetT.add(sizeT.times(factor));
		if (size.width >= sizeT.x()) {
			g.fillRect(0, 0, (int) offsetT.x(), size.height);
			g.fillRect((int) endT.x(), 0, (int) (size.width - endT.x()), size.height);
		}
		// up/down sides
		if (size.height >= sizeT.y()) {
			g.fillRect(0, 0, size.width, (int) offsetT.y());
			g.fillRect(0, (int) endT.y(), size.width, (int) (size.height - endT.y()));
		}
	}

	/**
	 * compute one simulation step (one frame)
	 */
	private void simuStep() {
		pop.step(dup, frameNumber, multithread);
		pop.updateHisto(newHist);
	}

	/**
	 * pass to the next generation
	 */
	private void genStep() {
		newHist.setOld();
		current.hist = newHist;
	}

	/**
	 * Load the next terrain and variation. Will pass through all variation of a
	 * terrain before getting to the next terrain
	 */
	private void nextTerrain() {
		// current = set.terrainSets.nextVar();
		current = set.terrainSets.nextLowVar();
		dup = new DotUpdater(current);
	}

	/**
	 * Play / pause the simulation
	 */
	public void playPause() {
		pause = !pause;
	}

	/**
	 * While in pause, simulate the next state
	 */
	public void advanceOne() {
		stepCall = true;
	}
	
	/**
	 * While in pause, simulate the current gen to the end, or the next gen to the end
	 */
	public void advanceOneGen() {
		stepGenCall = true;
	}

	/**
	 * Cycle between 60, screen native fps and unlimited. If the screen native fps
	 * is 60 or below, only cycle between 60 and unlimited
	 * Two unlimited modes available : drawLimited and draw bypass
	 * In drawLimited, will wait for the end of draw to continue.
	 * in draw bypass, will no end for the renderer, and will compute as fast as possible
	 */
	public void cycleLimit() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int frm = gd.getDisplayMode().getRefreshRate();
		if (frm <= 60) {
			if (framerate == 60)
				framerate = FRAMERATE_DRAW_LIMITED;
			else if (framerate == FRAMERATE_DRAW_LIMITED)
				framerate = FRAMERATE_UNLIMITED;
			else
				framerate = 60;
		} else {
			if (framerate == 60)
				framerate = frm;// TODO SCREEN_native
			else if (framerate == frm)
				framerate = FRAMERATE_DRAW_LIMITED;
			else if (framerate == FRAMERATE_DRAW_LIMITED)
				framerate = FRAMERATE_UNLIMITED;
			else
				framerate = 60;
		}
		frameInterval = (framerate == -1) ? 0 : (int) ((1. / framerate) * 1000);
	}

	/**
	 * Stop the simulation and end the thread. Discard all datas.
	 */
	public void stop() {
		running = false;
	}

	/**
	 * Delete the population and start the generations from the beginning
	 */
	public void restart() {
		restart = true;
	}

	/**
	 * Toggle whether to fill or not the obstacles
	 */
	public void toggleFill() {
		fill = !fill;
	}

	/**
	 * Toggle whether to show or not the collision count on obstacles
	 */
	public void toggleCollCnt() {
		showCount = !showCount;
	}

	/**
	 * Toggle whether to show or not the basic informations on the upper left corner
	 */
	public void toggleStatShow() {
		statShow = !statShow;
	}

	/**
	 * Toggle whether to show or not the history of the dot[0]
	 */
	public void toggleHistory() {
		history = !history;
	}
	
	public void toggleMultiThread() {
		multithread = !multithread;
	}
}
