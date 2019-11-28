package simulator;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
/**
 * Conainer for the SimuPane, and JMenuBar holder
 * @author Arthur France
 *
 */
public class SimuFrame extends JFrame {

	private SimulationManager manager;
	
	/**
	 * setup the Frame
	 */
	private void setup() {
		setupMenuBar();
	}
	
	/**
	 * Setup the JMenu
	 */
	private void setupMenuBar() {
		JMenuBar mb = new JMenuBar();
		setJMenuBar(mb);
		
		JMenu simu = new JMenu("Simu");
		mb.add(simu);
		
		JMenuItem pp = new JMenuItem("Play/Pause");
		pp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
		simu.add(pp);
		pp.addActionListener(e->manager.playPause());
		
		JMenuItem st = new JMenuItem("Simulate one step");
		st.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
		simu.add(st);
		st.addActionListener(e->manager.advanceOne());
		
		JMenuItem limit = new JMenuItem("Cycle UPS Limit");
		limit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,0));
		simu.add(limit);
		limit.addActionListener(e->manager.cycleLimit());
		
		JMenuItem reset = new JMenuItem("Reset");
		simu.add(reset);
		reset.addActionListener(e->manager.restart());
		
		JMenuItem stop = new JMenuItem("Exit");
		stop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0));
		simu.add(stop);
		stop.addActionListener(e->manager.stop());
		
		
		JMenu db = new JMenu("Stat");
		mb.add(db);
		
		JMenuItem tgFill = new JMenuItem("Tg Fill");
		tgFill.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,0));
		db.add(tgFill);
		tgFill.addActionListener(e->manager.toggleFill());
		
		JMenuItem tgCollCnt = new JMenuItem("Tg Coll Cnt");
		db.add(tgCollCnt);
		tgCollCnt.addActionListener(e->manager.toggleCollCnt());
		
		JMenuItem tgStat = new JMenuItem("Toggle Stat");
		tgStat.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
		db.add(tgStat);
		tgStat.addActionListener(e->manager.toggleStatShow());
		
		JMenuItem tgHist = new JMenuItem("Toggle histo");
		tgHist.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0));
		db.add(tgHist);
		tgHist.addActionListener(e->manager.toggleHistory());
	}

	/**
	 * Create the frame.
	 */
	public SimuFrame(SimuPane pane,SimulationManager manager) {
		this.manager = manager;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle("GenetisAI Simulation");
		setContentPane(pane);
		setup();
	}

}
