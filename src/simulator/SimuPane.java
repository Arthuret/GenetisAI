package simulator;

import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
/**
 * A JPanel that call SimulationManager.draw on paintcomponent.
 * This component mesure the FPS and milliseconds between each draw call
 * @author Arthur France
 *
 */
public class SimuPane extends JPanel {
	
	private SimulationManager m;
	private long nextSecond = System.currentTimeMillis();//timestamp of the next counter reset
	private long timeLastFrame = nextSecond;
	private long nbFrames = 0, nbFrameLast = 0;//frame counters
	private long nbMillisInterframe;

	/**
	 * Create the panel.
	 */
	public SimuPane(SimulationManager m) {
		this.m = m;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		long time = System.currentTimeMillis();
		nbMillisInterframe = time-timeLastFrame;
		timeLastFrame = time;
		if(nextSecond < time) {
			nextSecond = time+1000;//to prevent system sleep cumulation
			nbFrameLast = nbFrames;
			nbFrames = 0;
		}
		nbFrames++;
		m.draw(g,this.getSize(),nbFrameLast,nbMillisInterframe);
	}
}
