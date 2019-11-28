package menu;
import java.awt.EventQueue;

import javax.swing.JFrame;

import menu.brain_editor.AIMenu;
import menu.environnement_editor.EnvMenu;
import menu.training_editor.TrainingMenu;
import tools.menu.VerticalButtonsMenu;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Color;
import java.awt.Font;

/**
 * Main menu of the application
 * Uses the VerticalButtonMenu class for easy readability
 * @author Arthur France
 */
@SuppressWarnings("serial")
public class MainMenu extends VerticalButtonsMenu {

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
	 * Generate the menuBar//TODO
	 */
	private void setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(new Color(160,160,160));
		menuBar.setBorderPainted(false);
		
		JMenu mnFichier = new JMenu("File");
		mnFichier.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		menuBar.add(mnFichier);
		
		JMenuItem mntmCharger = new JMenuItem("Load");
		mnFichier.add(mntmCharger);
		
		JMenuItem mntmCrdits = new JMenuItem("Credits");
		mnFichier.add(mntmCrdits);

		setJMenuBar(menuBar);
	}
	
	private static Font fontBtn = new Font("Elephant", Font.PLAIN, 30);

	/**
	 * Create the frame.
	 */
	public MainMenu() {
		//VerticalButtonMenu constructor
		super(3,Color.DARK_GRAY,fontBtn);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 400);
		setLocationRelativeTo(null);
		
		//creating menu bar
		//setupMenuBar();
		
		//buttons creation
		createJButton(new Color(154, 205, 50),"Environnement").addActionListener(e->passTo(new EnvMenu()));
		
		createJButton(new Color(62, 183, 239),"IA").addActionListener(e->passTo(new AIMenu()));
		
		createJButton(new Color(209, 100, 100),"Entrainement").addActionListener(e->passTo(new TrainingMenu()));
		
		//createJButton(new Color(255,180,43),"Simulation (TODO)");//will launch a simulation with the given brains
	}
	
	private void passTo(JFrame f) {
		dispose();
		f.setVisible(true);
	}

}
