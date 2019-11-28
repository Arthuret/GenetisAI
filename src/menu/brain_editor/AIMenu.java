package menu.brain_editor;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import brain.BrainTemplate;
import brain.BrainType;
import menu.MainMenu;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A Frame used to create and read Brain Templates using modular Config Panels
 * 
 * @author Arthur France
 *
 */
@SuppressWarnings("serial")
public class AIMenu extends JFrame {

	private BrainConfigPanel activePanel = null;

	private JComboBox<String> brainType;

	private Map<String, BrainConfigPanel> panelMap = new HashMap<>();

	private JPanel cardPanel;
	private CardLayout cl;

	/**
	 * Create a Brain configurator Frame. This configurator manage the config panels
	 * added to it. This automatically add all brains referenced in BrainType.
	 */
	public AIMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);

		setupMenuBar();

		setupContentPane();

		for (BrainType bt : BrainType.values()) {
			try {
				addBrainConfigPanel(bt.toString(), bt.getEditor().getDeclaredConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	private void setupContentPane() {
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		// The combobox used to choose the brain type
		brainType = new JComboBox<>();
		contentPane.add(brainType, BorderLayout.NORTH);
		brainType.addActionListener(e -> {
			cl.show(cardPanel, (String) brainType.getSelectedItem());
			activePanel = panelMap.get((String) brainType.getSelectedItem());
		});

		cardPanel = new JPanel();
		contentPane.add(cardPanel, BorderLayout.CENTER);
		cl = new CardLayout();
		cardPanel.setLayout(cl);
	}

	private void setupMenuBar() {
		JMenuBar mb = new JMenuBar();
		this.setJMenuBar(mb);

		JMenu mFile = new JMenu("File");
		mb.add(mFile);

		JMenuItem miNew = new JMenuItem("New");
		mFile.add(miNew);
		miNew.addActionListener(e -> newBrain());
		miNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));

		JMenuItem miOpen = new JMenuItem("Open");
		mFile.add(miOpen);
		miOpen.addActionListener(e -> open());
		miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));

		JMenuItem miSave = new JMenuItem("Save");
		mFile.add(miSave);
		miSave.addActionListener(e -> save());
		miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));

		JMenuItem miSaveAs = new JMenuItem("Save as");
		mFile.add(miSaveAs);
		miSaveAs.addActionListener(e -> saveAs());
		miSaveAs.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));

		JMenuItem miExit = new JMenuItem("Exit");
		mFile.add(miExit);
		miExit.addActionListener(e -> goBack());
		miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
	}

	/**
	 * Add a BrainConfigPanel to the list of all usable configurators
	 * 
	 * @param name The name of the configurator (the name of the brain type)
	 * @param p    The config panel to show when the user select it
	 */
	private void addBrainConfigPanel(String name, BrainConfigPanel p) {
		brainType.addItem(name);
		cardPanel.add(p, name);
		if (activePanel == null) {
			activePanel = p;
		}
		panelMap.put(name, p);
	}

	private File currentFile = null;
	private File currentDir = null;

	/**
	 * Call saver or saveAs appropriately based on currentFile
	 */
	private void save() {
		if (currentFile == null)
			saveAs();
		else
			saver(currentFile);
	}

	/**
	 * Let the user choose the location of the file, and check for overwrite, then
	 * call saver
	 */
	private void saveAs() {
		File selectedFile;
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("IA model file (.brntpl)", "brntpl"));
		fc.setDialogTitle("Save as");
		if (currentDir != null)
			fc.setCurrentDirectory(currentDir);
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (!fc.getSelectedFile().getAbsolutePath().endsWith(".brntpl"))
				selectedFile = new File(fc.getSelectedFile().getAbsolutePath() + ".brntpl");
			else
				selectedFile = fc.getSelectedFile();
			if (selectedFile.exists() && JOptionPane.showConfirmDialog(this,
					"This file already exist. Do you want to overwrite it ?", "Overwrite", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
				return;
			currentDir = selectedFile.getParentFile();
			saver(selectedFile);
		}
	}

	/**
	 * Save the BrainTemplate from the activePanel to the given file
	 * 
	 * @param file The file location to save to.
	 */
	private void saver(File file) {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(activePanel.getBrainTemplate());
			oos.flush();
			currentFile = file;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error during file save\n" + e.getMessage(),
					"Save error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Ask the user for the file to open, load it, then search for a configPanel
	 * capable of reading it
	 */
	private void open() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("IA model file (.brntpl)", "brntpl"));
		fc.setDialogTitle("Open");
		if (currentDir != null)
			fc.setCurrentDirectory(currentDir);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			currentDir = fc.getSelectedFile().getParentFile();
			currentFile = fc.getSelectedFile();
			try (FileInputStream fis = new FileInputStream(currentFile)) {
				ObjectInputStream ois = new ObjectInputStream(fis);
				Object o = ois.readObject();
				ois.close();
				if (!(o instanceof BrainTemplate))
					JOptionPane.showMessageDialog(this, "The file is corrupted or do not contain a recognized type",
							"Read error", JOptionPane.ERROR_MESSAGE);
				else {
					BrainTemplate bt = (BrainTemplate) o;
					boolean found = false;
					System.out.println(bt);
					for (Entry<String, BrainConfigPanel> panels : panelMap.entrySet()) {
						if (panels.getValue().load(bt)) {
							found = true;
							cl.show(cardPanel, panels.getKey());
							break;
						}
					}
					if (!found) {
						JOptionPane.showMessageDialog(this, "No editor can handle this AI type",
								"Unrecognized AI type", JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				JOptionPane.showMessageDialog(this, "Error when opening the file\n" + e.getMessage(),
						"Read error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	private void goBack() {
		dispose();
		new MainMenu().setVisible(true);
	}

	/**
	 * Ask the activePanel to reset its content
	 */
	private void newBrain() {
		activePanel.reset();
	}
}
