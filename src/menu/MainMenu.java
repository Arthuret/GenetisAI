package menu;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import menu.brain_editor.AIMenu;
import menu.environnement_editor.EnvMenu;
import menu.training_editor.BrainSimulationSet;
import menu.training_editor.SimulationDataSet;
import menu.training_editor.TerrainSimulationSet;
import menu.training_editor.TrainingMenu;
import simulator.SimuState;
import simulator.SimulationManager;
import tools.menu.VerticalButtonsMenu;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import brain.BrainTemplate;
import environnement.Terrain;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Main menu of the application Uses the VerticalButtonMenu class for easy
 * readability
 * 
 * @author Arthur France
 */
@SuppressWarnings("serial")
public class MainMenu extends VerticalButtonsMenu {

	private static final String cred = "Conception : Arthur France\n" + "Developpement : Arthur France\n"
			+ "Art : Arthur France";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu frame = new MainMenu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Generate the menuBar
	 */
	private void setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(new Color(160, 160, 160));
		menuBar.setBorderPainted(false);

		JMenu mnFile = new JMenu("File");
		mnFile.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		menuBar.add(mnFile);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		mntmOpen.addActionListener(e -> open(false));

		JMenuItem mntmCrdits = new JMenuItem("Credits");
		mnFile.add(mntmCrdits);
		mntmCrdits.addActionListener(e -> {
			JOptionPane.showMessageDialog(this, cred, "Credits", JOptionPane.INFORMATION_MESSAGE);
		});

		setJMenuBar(menuBar);
	}

	private File emplacement = null;

	private void open(boolean simu) {
		JFileChooser fc = new JFileChooser();
		if(!simu)
			fc.setFileFilter(new FileNameExtensionFilter("GenetisAI files", "terrain", "brntpl", "terrainset", "simconf",
				"brainset","simustate"));
		else
			fc.setFileFilter(new FileNameExtensionFilter("Simulation State file (.simustate)","simustate"));
		fc.setDialogTitle("Open");
		if (emplacement != null)
			fc.setCurrentDirectory(emplacement);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			emplacement = fc.getSelectedFile().getParentFile();
			load(fc.getSelectedFile());
		}
	}

	private void load(File f) {
		if (f != null) {
			try (FileInputStream fis = new FileInputStream(f)) {
				ObjectInputStream ois = new ObjectInputStream(fis);
				Object o = (ois.readObject());
				ois.close();
				fis.close();
				if (o instanceof SimulationDataSet || o instanceof BrainSimulationSet
						|| o instanceof TerrainSimulationSet) {
					passTo(new TrainingMenu(f));
				} else if (o instanceof Terrain) {
					passTo(new EnvMenu(f));
				} else if (o instanceof BrainTemplate) {
					passTo(new AIMenu(f));
				} else if (o instanceof SimuState) {
					SimulationManager m = new SimulationManager(null);
					if(m.load(f, this)) {
						dispose();
						m.startSimulation();
					}
				} else {
					JOptionPane.showMessageDialog(this, "Unknown file", "Read error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "Unable to open : File do not exists\n" + e.getMessage(),
						"Read error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Unable to open : Error\n" + e.getMessage(), "Read error",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				JOptionPane.showMessageDialog(this, "Unable to open : Unrecognized content\n" + e.getMessage(),
						"Read error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	private static Font fontBtn = new Font("Elephant", Font.PLAIN, 30);

	/**
	 * Create the frame.
	 */
	public MainMenu() {
		// VerticalButtonMenu constructor
		super(4, Color.DARK_GRAY, fontBtn);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 400);
		setLocationRelativeTo(null);

		// creating menu bar
		setupMenuBar();

		// buttons creation
		createJButton(new Color(154, 205, 50), "Environnement").addActionListener(e -> passTo(new EnvMenu()));

		createJButton(new Color(62, 183, 239), "IA").addActionListener(e -> passTo(new AIMenu()));

		createJButton(new Color(209, 100, 100), "Entrainement").addActionListener(e -> passTo(new TrainingMenu()));

		createJButton(new Color(255,180,43),"Simulation").addActionListener(e->open(true));;//will launch a
		// simulation with the given brains
	}

	private void passTo(JFrame f) {
		dispose();
		f.setVisible(true);
	}
}
