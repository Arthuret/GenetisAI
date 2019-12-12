package simulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

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

	private SimuFrame frame;
	private SimuPane pane;

	private boolean running = true;// exit switch

	private SimuState s = null;

	private int framerate = 60;// the number of frames to display every second
	private static final int FRAMERATE_UNLIMITED = -2, FRAMERATE_DRAW_LIMITED = -1;
	private long frameInterval = (int) ((1. / framerate) * 1000);// the interval between 2 frames. This value is
																	// truncated, so the framerate will not be precise
	private boolean pause = false;// if the simulation is paused
	private boolean stepCall = false;// ask for one step forward while in pause
	private boolean stepGenCall = false;// ask for one gen forward while in pause
	private long nbUpLast = 0;// number of physics step in the last second

	private boolean restart = false;// request for reinitializing the simulator
	private boolean statShow = true;// show the text infos
	private boolean debug = false;//show the debug informations
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

	private static DecimalFormat df1 = new DecimalFormat();// used to display memory used
	private static DecimalFormat df2 = new DecimalFormat();// idem

	private SimulationDataSet tempSet;
	
	//debug and performance
	private long stepTime=0;
	private long drawWaitTime=0;
	

	public SimulationManager(SimulationDataSet set) {
		this.tempSet = set;
		df1.setMaximumFractionDigits(1);
		df2.setMaximumFractionDigits(2);
	}

	public boolean load(File f, JFrame parent) {
		try (FileInputStream fis = new FileInputStream(f)) {
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object o = ois.readObject();
			ois.close();
			if (o instanceof SimuState) {
				this.s = (SimuState) o;
				pause = true;
				return true;
			}
		} catch (ClassNotFoundException | IOException e) {
			JOptionPane.showMessageDialog(parent, "An error occured while reading the file :\n" + e.getMessage(),
					"Reading error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return false;
	}

	public Thread startSimulation() {
		Thread t = new Thread(this);
		t.start();
		return t;
	}

	@Override
	public void run() {
		System.out.println("Available cores : "+Runtime.getRuntime().availableProcessors());
		// Open the window
		pane = new SimuPane(this);
		if (s == null) {
			this.s = new SimuState();
			this.s.set = tempSet;
			// grab an environnement
			nextTerrain();
			s.pop = new Population(s.set.brainSimuSet, s.current.tvar.getOrigin().getPosition());
		}
		pane.setPreferredSize(s.set.terrainSets.getMaxSize());
		frame = new SimuFrame(pane, this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		System.out.println("Starting simulation");

		// seting up the population and timings
		long nextSecond = System.currentTimeMillis();
		long nbUp = 0;
		boolean endGen = false;
		while (running) {
			// main loop
			long nextFrameTime = System.currentTimeMillis();
			s.newHist = new History();// TODO
			do {
				// physic engine
				if (!pause || stepCall || stepGenCall) {
					long t = System.currentTimeMillis();
					simuStep();
					stepTime = System.currentTimeMillis()-t;
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
					s.frameNumber++;
					// synchronizing with render thread (awt)
					if (framerate != FRAMERATE_UNLIMITED) {// bypass
						t = System.currentTimeMillis();
						while (!endOfDraw)
							safeSleep(SLEEP_WAIT_DRAW_MILLIS);
						endOfDraw = false;
						drawWaitTime = System.currentTimeMillis()-t;
					}
				}
				// pause management
				stepCall = false;
				while (pause && !stepCall && !stepGenCall && running)
					safeSleep(SLEEP_PAUSE_MILLIS);
				// test for generation shortcut
				if (s.frameNumber % CHECK_DEAD_MOD == 0)
					endGen = s.pop.isAllDead();
			} while (s.frameNumber < MAX_FRAME_PER_GEN && !endGen && running);
			endGen = false;
			if (stepGenCall) {
				stepGenCall = false;
				while (pause && !stepGenCall && !stepCall && running)
					safeSleep(SLEEP_PAUSE_MILLIS);
				stepCall = false;
			}

			// generation management
			if (running) {
				genStep();// perform history management only
				// generate next generation
				s.pop.computeFitness(s.set.brainSimuSet.fitness, s.current);
				nextTerrain();
				if (restart) {
					restart = false;
					s.pop = new Population(s.set.brainSimuSet, s.current.tvar.getOrigin().getPosition());
					s.genNumber = 0;
				} else {
					s.pop = s.pop.getNextGeneration(s.set.brainSimuSet, s.current.tvar.getOrigin().getPosition());
					s.genNumber++;
				}
				s.frameNumber = 0;
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
		float factor = getFactor(s.current.t.getWalls(), size);
		Vector offset = getOffset(factor, s.current.t.getWalls(), size);
		s.current.t.showSimulation(g, s.current.tvar, factor, offset, fill, showCount);
		if (s.pop != null)
			s.pop.show(g, factor, offset);
		if (history) {
			if (s.current.hist != null)
				s.current.hist.show(g, factor, offset);
			if (s.newHist != null)
				s.newHist.show(g, factor, offset);
		}
		hideOutOfBound(g, size, s.current.t.getWalls(), factor, offset);
		if (statShow)
			showStat(g, nbFrames, timeFromLastFrame);
		if(debug)
			showDebug(g);
		endOfDraw = true;
	}

	/**
	 * Compute the maximum display factor
	 * 
	 * @param sizeT  the size of the Terrain
	 * @param window the size of the panel
	 * @return The maximum applicable display factor
	 */
	private float getFactor(Vector sizeT, Dimension window) {
		float h = (float) (window.height / sizeT.y());
		float w = (float) (window.width / sizeT.x());
		return Math.min(h, w);
	}

	/**
	 * Compute the display offset to maintain the terrain display in the center of
	 * the pane
	 * 
	 * @param factor the display factor used
	 * @param sizeT  the size of the Terrain
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
	private void showStat(Graphics g, long nbFrames, long timeFromLastFrame) {
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
		g.drawString("Frame:" + s.frameNumber + "/" + MAX_FRAME_PER_GEN, 10, y);
		y += yp;
		g.drawString("Generation:" + s.genNumber, 10, y);
		y += yp;
		g.drawString("Brain type:" + s.set.brainSimuSet.brainTemplate.getType(), 10, y);
		y += yp;
		g.drawString("Pop size:" + s.set.brainSimuSet.populationSize, 10, y);
		y += yp;
		g.drawString("Threading:" + ((multithread) ? "Multi" : "Mono"), 10, y);
		y += yp;
		g.drawString("Selection:" + s.set.brainSimuSet.childOrigin, 10, y);
		y += yp;
		Runtime r = Runtime.getRuntime();
		long memAll = r.totalMemory();
		long memUsed = memAll - r.freeMemory();
		g.drawString("Memory-All:" + df2.format(memAll / 1_000_000f) + "MB; Used:"
				+ df1.format((memUsed / (float) memAll) * 100) + "%", 10, y);
	}
	
	private void showDebug(Graphics g) {
		int y = pane.getHeight()-10,yp = -12;
		g.setColor(Color.GREEN);
		g.drawString("Draw wait:"+drawWaitTime, 10, y);
		y+=yp;
		g.drawString("Step time:"+stepTime, 10, y);
		y+=yp;
		g.drawString("MinThread time:"+s.pop.minThreadTime, 10, y);
		y+=yp;
		g.drawString("MaxThread time:"+s.pop.maxThreadTime, 10, y);
		y+=yp;
		g.drawString("Selection time:"+s.pop.selectionTime, 10, y);
		y+=yp;
		g.drawString("FitCompute time:"+s.pop.fitComputeTime, 10, y);
	}

	private static final Color BACKGROUND = Color.DARK_GRAY;

	/**
	 * Draw rectangles to cover all borders of the display area, to hide any bad
	 * obstacle display
	 * 
	 * @param g       the Graphics object
	 * @param size    the size of the panel
	 * @param sizeT   the size of the terrain
	 * @param factor  the display factor
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
		s.pop.step(s.dup, s.frameNumber, multithread);
		s.pop.updateHisto(s.newHist);
	}

	/**
	 * pass to the next generation
	 */
	private void genStep() {
		s.newHist.setOld();
		s.current.hist = s.newHist;
	}

	/**
	 * Load the next terrain and variation. Will pass through all variation of a
	 * terrain before getting to the next terrain
	 */
	private void nextTerrain() {
		// s.current = set.terrainSets.nextVar();
		s.current = s.set.terrainSets.nextLowVar();
		s.dup = new DotUpdater(s.current);
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
	 * While in pause, simulate the current gen to the end, or the next gen to the
	 * end
	 */
	public void advanceOneGen() {
		stepGenCall = true;
	}

	/**
	 * Cycle between 60, screen native fps and unlimited. If the screen native fps
	 * is 60 or below, only cycle between 60 and unlimited Two unlimited modes
	 * available : drawLimited and draw bypass In drawLimited, will wait for the end
	 * of draw to continue. in draw bypass, will no end for the renderer, and will
	 * compute as fast as possible
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
		boolean currentPause = pause;
		pause = true;
		switch (JOptionPane.showConfirmDialog(frame,
				"All datas will be lost. Do you wish to save the simulation state before exit ?", "Exiting",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)) {
		case JOptionPane.YES_OPTION:
			if (saveState())
				break;
		case JOptionPane.CANCEL_OPTION:
			pause = currentPause;
			return;
		}
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
	 * Toggle whether to show or not the debug and performance informations on the down left corner
	 */
	public void toggleDebug() {
		debug = !debug;
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

	private File currentDir = null;

	private File selectFile(boolean state) {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter(
				(state) ? "Simulation State File (.simustate)" : "Brain Data File (.braindata)",
				(state) ? "simustate" : "braindata"));
		fc.setDialogTitle(
				(state) ? "Save the actual state of the simulation" : "Save the best brains of the simulation");
		if (currentDir != null)
			fc.setCurrentDirectory(currentDir);
		if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			currentDir = fc.getSelectedFile().getParentFile();
			if (!(fc.getSelectedFile().getName().endsWith((state) ? ".simustate" : ".braindata")))
				return new File(fc.getSelectedFile() + ((state) ? ".simustate" : ".braindata"));
			else
				return fc.getSelectedFile();
		}
		return null;
	}

	public boolean saveState() {
		boolean currentPause = pause;
		pause = true;
		boolean resp = false;
		if (!stepGenCall) {
			File f;
			int r = JOptionPane.YES_OPTION;
			do {
				f = selectFile(true);
				if (f != null && f.exists())
					r = JOptionPane.showConfirmDialog(frame,
							"A file with this name already exixst.\nDo you wish to overwrite it ?", "File overwrite",
							JOptionPane.YES_NO_CANCEL_OPTION);
			} while (f != null && r != JOptionPane.YES_OPTION && r != JOptionPane.CANCEL_OPTION);
			if (f != null && r == JOptionPane.YES_OPTION)
				resp = saverState(f);
		}
		pause = currentPause;
		return resp;
	}

	private boolean saverState(File f) {
		try (FileOutputStream fos = new FileOutputStream(f)) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(s);
			oos.close();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Error : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return false;
	}

	public void saveBrains() {
		boolean currentpause = pause;
		pause = true;
		if (!stepGenCall && (s.frameNumber == MAX_FRAME_PER_GEN || s.pop.isAllDead())) {// only possible if at the end
																						// of a generation
			JSpinner sp = new JSpinner(new SpinnerNumberModel(10, 1, s.set.brainSimuSet.populationSize, 1));
			if (JOptionPane.showOptionDialog(frame, sp, "Enter the number of brains to save",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null,
					null) == JOptionPane.OK_OPTION) {
				File f;
				int r = JOptionPane.YES_OPTION;
				do {
					f = selectFile(false);
					if (f != null && f.exists())
						r = JOptionPane.showConfirmDialog(frame,
								"A file with this name already exixst.\nDo you wish to overwrite it ?",
								"File overwrite", JOptionPane.YES_NO_CANCEL_OPTION);
				} while (f != null && r != JOptionPane.YES_OPTION && r != JOptionPane.CANCEL_OPTION);
				if (f != null && r == JOptionPane.YES_OPTION)
					saverBrain(f,(int) sp.getValue());
			}
		}
		pause = currentpause;
	}

	/**
	 * Save the best nb brains in f file.
	 * @param f the file to save the brain in
	 * @param nb the number of brains to save
	 */
	private void saverBrain(File f,int nb) {
		s.pop.computeFitness(s.set.brainSimuSet.fitness, s.current);
		List<Dot> dots = s.pop.getDotsRanked();
		List<Dot> l = new ArrayList<>();
		for(int i = 0;i < nb;i++) l.add(dots.get(i));
		try (FileOutputStream fos = new FileOutputStream(f)) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(l);
			oos.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Error : "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
