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
		
		JMenuItem sgt = new JMenuItem("Simulate one gen");
		sgt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_DOWN_MASK));
		simu.add(sgt);
		sgt.addActionListener(e->manager.advanceOneGen());
		
		JMenuItem limit = new JMenuItem("Cycle UPS Limit");
		limit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,0));
		simu.add(limit);
		limit.addActionListener(e->manager.cycleLimit());

		JMenuItem multithread = new JMenuItem("Tg Multi T");
		multithread.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0));
		simu.add(multithread);
		multithread.addActionListener(e->manager.toggleMultiThread());
		
		JMenuItem saveS = new JMenuItem("Save state");
		saveS.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		simu.add(saveS);
		saveS.addActionListener(e->manager.saveState());
		
		JMenuItem saveBrains = new JMenuItem("Save brains");
		saveBrains.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		simu.add(saveBrains);
		saveBrains.addActionListener(e->manager.saveBrains());
		
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
		
		JMenuItem tgDebug = new JMenuItem("Toggle Debug");
		tgDebug.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0));
		db.add(tgDebug);
		tgDebug.addActionListener(e->manager.toggleDebug());
		
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
