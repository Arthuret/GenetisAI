package menu.training_editor;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import brain.BrainTemplate;
import environnement.Terrain;
import environnement.TerrainVariation;
import environnement.TerrainVariationSet;
import formula.Formula;
import menu.MainMenu;
import simulator.SimulationManager;
import tools.menu.SpinnerPercentModel;

import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public class TrainingMenu extends JFrame {
	private JTextField brainTemplateNameTF;
	private JSpinner spinnerPopSize;
	private JLabel lblParameterNumber;
	private JTextField mutaFunc;
	private JComboBox<ChangementLaws> cbChangLaw;
	private JSpinner spinnerSigma;
	private JSpinner spinnerProbaMutAbs;
	private JSpinner spinnerPropChild;
	private JComboBox<ChildOrigin> cbOrgChild;
	private JTextField fitnessFunc;

	private JPanel leftPanel, rightPanel;

	private JTree terrainTree;

	private static final String NB_PARAM_TEXT = "Number of parameters : ";

	private static final int DEFAULT_WIDTH = 800, DEFAULT_HEIGHT = DEFAULT_WIDTH / 16 * 9;

	private static final Dimension SPACING = new Dimension(5, 5);

	private SimulationDataSet dataSet = new SimulationDataSet();
	private BrainSimulationSet brainSimuSet = dataSet.brainSimuSet;
	private TerrainSimulationSet terrains = dataSet.terrainSets;

	private JButton btnNewBrain;

	private File terrainFile = null;
	private File brainFile = null;
	private File dataSetFile = null;
	private File emplacement = null;
	private SimpleTerrainShower simpleTerrainShower;

	private void initializeDatas() {
		TerrainTreeModel model = new TerrainTreeModel(terrains.terrains);
		terrainTree.setModel(model);
		if (brainSimuSet.brainTemplate != null) {
			brainTemplateNameTF.setText(brainSimuSet.brainTemplate.getType().toString());
			lblParameterNumber.setText(NB_PARAM_TEXT + brainSimuSet.brainTemplate.getNumberParameters());
			btnNewBrain.setText("Edit");
		} else {
			lblParameterNumber.setText(NB_PARAM_TEXT + "XXX");
			btnNewBrain.setText("New");
		}
		spinnerPopSize.setValue(brainSimuSet.populationSize);
		mutaFunc.setText((brainSimuSet.mutation == null) ? "" : brainSimuSet.mutation.toString());
		cbChangLaw.setSelectedItem(brainSimuSet.changeLaw);
		spinnerSigma.setValue(brainSimuSet.sigma);
		spinnerProbaMutAbs.setValue(brainSimuSet.nbAbsMut);
		spinnerPropChild.setValue(brainSimuSet.keepedProportion);
		cbOrgChild.setSelectedItem(brainSimuSet.childOrigin);
		fitnessFunc.setText((brainSimuSet.fitness == null) ? "" : brainSimuSet.fitness.toString());
	}

	private void setupGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setTitle("GenetisAI Training configurator");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 440, 440, 0 };
		gridBagLayout.rowHeights = new int[] { 45, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		setupMenuBar();
		setupLeft();
		setupRight();
		setLocationRelativeTo(null);
	}

	private void setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		JMenu file = new JMenu("File");
		menuBar.add(file);

		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(e -> open());
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		file.add(open);
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(e -> save());
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		file.add(save);
		JMenuItem saveAs = new JMenuItem("Save as");
		saveAs.addActionListener(e -> saveAs());
		saveAs.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		file.add(saveAs);

		file.addSeparator();

		JMenu jmBrainDataSet = new JMenu("Brain and population configuration");
		JMenuItem saveAsBrain = new JMenuItem("Save as");
		saveAsBrain.addActionListener(e -> saveAsBrain());
		jmBrainDataSet.add(saveAsBrain);

		JMenuItem saveBrain = new JMenuItem("Save");
		saveBrain.addActionListener(e -> saveBrain());
		jmBrainDataSet.add(saveBrain);

		file.add(jmBrainDataSet);

		JMenu jmTerrainDataSet = new JMenu("Terrains configuration");
		JMenuItem saveAsTerrain = new JMenuItem("Save as");
		saveAsTerrain.addActionListener(e -> saveAsTerrain());
		jmTerrainDataSet.add(saveAsTerrain);

		JMenuItem saveTerrain = new JMenuItem("Save");
		saveTerrain.addActionListener(e -> saveTerrain());
		jmTerrainDataSet.add(saveTerrain);

		file.add(jmTerrainDataSet);

		file.addSeparator();

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(e -> exit());
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		file.add(exit);
	}

	private void setupLeft() {
		leftPanel = new JPanel();
		GridBagConstraints gbc_leftPanel = new GridBagConstraints();
		gbc_leftPanel.anchor = GridBagConstraints.NORTH;
		gbc_leftPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_leftPanel.insets = new Insets(5, 5, 5, 5);
		gbc_leftPanel.gridx = 0;
		gbc_leftPanel.gridy = 0;
		getContentPane().add(leftPanel, gbc_leftPanel);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		brainTemplateGui();

		leftPanel.add(new JSeparator());
		leftPanel.add(Box.createRigidArea(SPACING));

		brainMutGui();

		leftPanel.add(Box.createRigidArea(SPACING));
		leftPanel.add(new JSeparator());

		leftPanel.add(Box.createRigidArea(SPACING));

		enfantsGui();

		leftPanel.add(Box.createRigidArea(SPACING));
		leftPanel.add(new JSeparator());
		leftPanel.add(Box.createRigidArea(SPACING));

		fitnessGui();
	}

	private void brainTemplateGui() {
		JPanel templatePanel = new JPanel();
		leftPanel.add(templatePanel);
		templatePanel.setLayout(new BoxLayout(templatePanel, BoxLayout.Y_AXIS));

		JPanel templateFilePanel = new JPanel();
		templatePanel.add(templateFilePanel);
		templateFilePanel.setLayout(new BoxLayout(templateFilePanel, BoxLayout.X_AXIS));

		templateFilePanel.add(new JLabel("Brain Template :"));

		templateFilePanel.add(Box.createRigidArea(SPACING));

		brainTemplateNameTF = new JTextField();
		brainTemplateNameTF.setEditable(false);
		templateFilePanel.add(brainTemplateNameTF);

		templateFilePanel.add(Box.createRigidArea(SPACING));
		JButton btnParcourir = new JButton("...");
		templateFilePanel.add(btnParcourir);
		btnParcourir.addActionListener(e -> parcourirBrainsTemplate());

		templateFilePanel.add(Box.createRigidArea(SPACING));
		btnNewBrain = new JButton("New");
		templateFilePanel.add(btnNewBrain);
		btnNewBrain.addActionListener(e -> newBrainTemplate());

		templatePanel.add(Box.createRigidArea(SPACING));

		JPanel populationSizePanel = new JPanel();
		templatePanel.add(populationSizePanel);
		populationSizePanel.setLayout(new BoxLayout(populationSizePanel, BoxLayout.X_AXIS));

		populationSizePanel.add(new JLabel("Population size :"));

		populationSizePanel.add(Box.createRigidArea(SPACING));

		spinnerPopSize = new JSpinner();
		SpinnerNumberModel m = new SpinnerNumberModel(100, 50, null, 50);
		spinnerPopSize.setModel(m);
		populationSizePanel.add(spinnerPopSize);
		m.addChangeListener(e -> brainSimuSet.populationSize = (int) m.getNumber());

		templatePanel.add(Box.createRigidArea(SPACING));

		lblParameterNumber = new JLabel(NB_PARAM_TEXT + "XXX");
		lblParameterNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblParameterNumber.setHorizontalAlignment(SwingConstants.CENTER);
		templatePanel.add(lblParameterNumber);

		templatePanel.add(Box.createRigidArea(SPACING));
	}

	private void brainMutGui() {
		JPanel evolutionPanel = new JPanel();
		leftPanel.add(evolutionPanel);
		evolutionPanel.setLayout(new BoxLayout(evolutionPanel, BoxLayout.Y_AXIS));

		JLabel lblMutationsRelatives = new JLabel("Relative mutations");
		lblMutationsRelatives.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblMutationsRelatives.setHorizontalAlignment(SwingConstants.CENTER);
		evolutionPanel.add(lblMutationsRelatives);

		evolutionPanel.add(Box.createRigidArea(SPACING));

		JPanel mutParaRelatPanel = new JPanel();
		evolutionPanel.add(mutParaRelatPanel);
		mutParaRelatPanel.setLayout(new BoxLayout(mutParaRelatPanel, BoxLayout.X_AXIS));

		mutParaRelatPanel.add(new JLabel("Mutation probability :"));

		mutParaRelatPanel.add(Box.createRigidArea(SPACING));

		mutaFunc = new JTextField();
		mutaFunc.setEditable(false);
		mutaFunc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					editMutation();
			}
		});
		mutParaRelatPanel.add(mutaFunc);

		evolutionPanel.add(Box.createRigidArea(SPACING));

		JPanel loiChangementPanel = new JPanel();
		evolutionPanel.add(loiChangementPanel);
		loiChangementPanel.setLayout(new BoxLayout(loiChangementPanel, BoxLayout.X_AXIS));

		loiChangementPanel.add(new JLabel("Random law :"));

		loiChangementPanel.add(Box.createRigidArea(SPACING));

		cbChangLaw = new JComboBox<ChangementLaws>();
		cbChangLaw.setModel(new DefaultComboBoxModel<>(ChangementLaws.values()));
		loiChangementPanel.add(cbChangLaw);
		cbChangLaw.addActionListener(e -> brainSimuSet.changeLaw = (ChangementLaws) cbChangLaw.getSelectedItem());

		evolutionPanel.add(Box.createRigidArea(SPACING));

		JPanel sigmaPanel = new JPanel();
		evolutionPanel.add(sigmaPanel);
		sigmaPanel.setLayout(new BoxLayout(sigmaPanel, BoxLayout.X_AXIS));

		sigmaPanel.add(new JLabel("Sigma :"));

		sigmaPanel.add(Box.createRigidArea(SPACING));

		spinnerSigma = new JSpinner();
		SpinnerNumberModel m = new SpinnerNumberModel(0.1, 0.01, 1, 0.01);
		spinnerSigma.setModel(m);
		sigmaPanel.add(spinnerSigma);
		m.addChangeListener(e -> brainSimuSet.sigma = m.getNumber().floatValue());

		evolutionPanel.add(Box.createRigidArea(SPACING));

		evolutionPanel.add(new JSeparator());

		evolutionPanel.add(Box.createRigidArea(SPACING));

		JLabel lblMutAbs = new JLabel("Absolute mutations");
		lblMutAbs.setAlignmentX(Component.CENTER_ALIGNMENT);
		evolutionPanel.add(lblMutAbs);

		evolutionPanel.add(Box.createRigidArea(SPACING));

		JPanel mutParaAbsoPanel = new JPanel();
		evolutionPanel.add(mutParaAbsoPanel);
		mutParaAbsoPanel.setLayout(new BoxLayout(mutParaAbsoPanel, BoxLayout.X_AXIS));

		mutParaAbsoPanel.add(new JLabel("Mutation probability :"));

		mutParaAbsoPanel.add(Box.createRigidArea(SPACING));

		spinnerProbaMutAbs = new JSpinner();
		var spfAB = new SpinnerNumberModel(0, 0, null, 1);
		spinnerProbaMutAbs.setModel(spfAB);
		mutParaAbsoPanel.add(spinnerProbaMutAbs);
		spfAB.addChangeListener(e -> brainSimuSet.nbAbsMut = (int) spfAB.getValue());
	}

	private void editMutation() {
		FormulaEditor fedit;
		if (brainSimuSet.mutation == null)
			fedit = new FormulaEditor(this, "", FormulaTypes.MUTATION);
		else
			fedit = new FormulaEditor(this, brainSimuSet.mutation, FormulaTypes.MUTATION);
		Formula fo = fedit.showDialog();
		if (fo != null) {
			brainSimuSet.mutation = fo;
			initializeDatas();
		}
	}

	private void enfantsGui() {
		JPanel selectionPanel = new JPanel();
		leftPanel.add(selectionPanel);
		selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));

		JPanel propEnfantsPanel = new JPanel();
		selectionPanel.add(propEnfantsPanel);
		propEnfantsPanel.setLayout(new BoxLayout(propEnfantsPanel, BoxLayout.X_AXIS));

		propEnfantsPanel.add(new JLabel("Keeped proportion :"));

		propEnfantsPanel.add(Box.createRigidArea(SPACING));

		spinnerPropChild = new JSpinner();
		spinnerPropChild.setToolTipText("The proportion of dots keeped unmuted from the old generation");
		SpinnerPercentModel p = new SpinnerPercentModel(50);
		spinnerPropChild.setModel(p);
		propEnfantsPanel.add(spinnerPropChild);
		p.addChangeListener(e -> brainSimuSet.keepedProportion = p.getPercent());

		selectionPanel.add(Box.createRigidArea(SPACING));

		JPanel origineEnfantPanel = new JPanel();
		selectionPanel.add(origineEnfantPanel);
		origineEnfantPanel.setLayout(new BoxLayout(origineEnfantPanel, BoxLayout.X_AXIS));

		origineEnfantPanel.add(new JLabel("Child origin :"));

		origineEnfantPanel.add(Box.createRigidArea(SPACING));

		cbOrgChild = new JComboBox<ChildOrigin>();
		cbOrgChild.setModel(new DefaultComboBoxModel<>(ChildOrigin.values()));
		origineEnfantPanel.add(cbOrgChild);
		cbOrgChild.addActionListener(e -> brainSimuSet.childOrigin = (ChildOrigin) cbOrgChild.getSelectedItem());
	}

	private void fitnessGui() {
		JPanel fitpanel = new JPanel();
		leftPanel.add(fitpanel);
		fitpanel.setLayout(new BoxLayout(fitpanel, BoxLayout.X_AXIS));
		fitnessFunc = new JTextField();
		fitnessFunc.setEditable(false);
		fitnessFunc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					editFitness();
			}
		});
		fitpanel.add(new JLabel("Fitness function : "));
		fitpanel.add(Box.createRigidArea(SPACING));
		fitpanel.add(fitnessFunc);
	}

	private void editFitness() {
		FormulaEditor fedit;
		if (brainSimuSet.fitness == null)
			fedit = new FormulaEditor(this, "", FormulaTypes.FITNESS);
		else
			fedit = new FormulaEditor(this, brainSimuSet.fitness, FormulaTypes.FITNESS);
		Formula fo = fedit.showDialog();
		if (fo != null) {
			brainSimuSet.fitness = fo;
			initializeDatas();
		}
	}

	private void setupRight() {
		rightPanel = new JPanel();
		GridBagConstraints gbc_rightPanel = new GridBagConstraints();
		gbc_rightPanel.insets = new Insets(5, 0, 5, 0);
		gbc_rightPanel.anchor = GridBagConstraints.NORTH;
		gbc_rightPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_rightPanel.gridx = 1;
		gbc_rightPanel.gridy = 0;
		getContentPane().add(rightPanel, gbc_rightPanel);
		GridBagLayout gbl_rightPanel = new GridBagLayout();
		gbl_rightPanel.columnWidths = new int[] { 0, 0 };
		gbl_rightPanel.rowHeights = new int[] { 0, 94, 0, 0, 0, 0 };
		gbl_rightPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_rightPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		rightPanel.setLayout(gbl_rightPanel);

		JPanel buttonPanel = new JPanel();
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.gridwidth = 2;
		gbc_buttonPanel.insets = new Insets(0, 0, 0, 5);
		gbc_buttonPanel.fill = GridBagConstraints.BOTH;
		gbc_buttonPanel.gridx = 0;
		gbc_buttonPanel.gridy = 1;
		getContentPane().add(buttonPanel, gbc_buttonPanel);

		JButton simuBtn = new JButton("Start Simulation");
		simuBtn.addActionListener(e -> startSimulation());
		buttonPanel.add(simuBtn);

		btnPanel();
		setupTree();
		terrainBtnsGui();
	}

	private void btnPanel() {
		JPanel btnPanel = new JPanel();
		GridBagConstraints gbc_btnPanel = new GridBagConstraints();
		gbc_btnPanel.insets = new Insets(0, 0, 5, 0);
		gbc_btnPanel.fill = GridBagConstraints.VERTICAL;
		gbc_btnPanel.gridx = 0;
		gbc_btnPanel.gridy = 0;
		rightPanel.add(btnPanel, gbc_btnPanel);
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

		JButton btnAdd = new JButton("Add terrain");
		btnPanel.add(btnAdd);
		btnAdd.addActionListener(e -> addTerrain());

		JButton btnDelete = new JButton("Remove");
		btnPanel.add(btnDelete);
		btnDelete.addActionListener(e -> removeTerrain());
	}

	private void setupTree() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		rightPanel.add(scrollPane, gbc_scrollPane);

		terrainTree = new JTree();
		terrainTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		scrollPane.setViewportView(terrainTree);

		terrainTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selRow = terrainTree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = terrainTree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					Object o = selPath.getLastPathComponent();
					if (e.getClickCount() == 1) {
						// selection
						if (o instanceof TerrainVariation) {
							TerrainVariationSet t = (TerrainVariationSet) selPath.getParentPath()
									.getLastPathComponent();
							simpleTerrainShower.setTerrain(t.getT(), (TerrainVariation) o);
						} else
							simpleTerrainShower.setTerrain(null, null);
						repaint();
					} else if (e.getClickCount() == 2) {
						// edition
						if (o instanceof TerrainVariation) {
							TerrainVariationSet t = (TerrainVariationSet) selPath.getParentPath()
									.getLastPathComponent();
							editVariation(t.getT(), (TerrainVariation) o);
						} else if (o instanceof TerrainVariationSet) {
							addVariation();
						}
					}
				}
			}
		});
	}

	private void editVariation(Terrain t, TerrainVariation tvar) {
		new TerrainVariationEditor(this, true, t, tvar).showEditor();
	}

	private void terrainBtnsGui() {
		JPanel terrainBtnPanel = new JPanel();
		GridBagConstraints gbc_terrainBtnPanel = new GridBagConstraints();
		gbc_terrainBtnPanel.insets = new Insets(0, 0, 5, 0);
		gbc_terrainBtnPanel.fill = GridBagConstraints.BOTH;
		gbc_terrainBtnPanel.gridx = 0;
		gbc_terrainBtnPanel.gridy = 2;
		rightPanel.add(terrainBtnPanel, gbc_terrainBtnPanel);
		GridBagLayout gbl_terrainBtnPanel = new GridBagLayout();
		gbl_terrainBtnPanel.columnWidths = new int[] { 0, 0 };
		gbl_terrainBtnPanel.rowHeights = new int[] { 0 };
		gbl_terrainBtnPanel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_terrainBtnPanel.rowWeights = new double[] { 0.0 };
		terrainBtnPanel.setLayout(gbl_terrainBtnPanel);

		JButton btnNewVariation = new JButton("New variation");
		GridBagConstraints gbc_btnNewVariation = new GridBagConstraints();
		gbc_btnNewVariation.fill = GridBagConstraints.BOTH;
		gbc_btnNewVariation.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewVariation.gridx = 0;
		gbc_btnNewVariation.gridy = 0;
		terrainBtnPanel.add(btnNewVariation, gbc_btnNewVariation);
		btnNewVariation.addActionListener(e -> addVariation());

		JButton btnAllVar = new JButton("Generate all variaitons");
		GridBagConstraints gbc_btnAllVar = new GridBagConstraints();
		gbc_btnAllVar.fill = GridBagConstraints.BOTH;
		gbc_btnAllVar.insets = new Insets(0, 0, 5, 0);
		gbc_btnAllVar.gridx = 1;
		gbc_btnAllVar.gridy = 0;
		terrainBtnPanel.add(btnAllVar, gbc_btnAllVar);
		btnAllVar.addActionListener(e -> generateAllVars());

		simpleTerrainShower = new SimpleTerrainShower(new Dimension(200, 200), null, null, false, false);
		GridBagConstraints gbc_simpleTerrainShower = new GridBagConstraints();
		gbc_simpleTerrainShower.insets = new Insets(0, 0, 5, 0);
		gbc_simpleTerrainShower.fill = GridBagConstraints.BOTH;
		gbc_simpleTerrainShower.gridx = 0;
		gbc_simpleTerrainShower.gridy = 3;
		rightPanel.add(simpleTerrainShower, gbc_simpleTerrainShower);
	}

	public TrainingMenu() {
		setupGui();
		initializeDatas();
	}

	public TrainingMenu(File simuSet) throws FileNotFoundException, IOException, ClassNotFoundException {
		setupGui();
		load(simuSet);
		initializeDatas();
	}

	private void parcourirBrainsTemplate() {
		File f = selectFile(true, "Browser", "IA model file (.brntpl)", "brntpl");
		if (f != null)
			load(f, BrainTemplate.class);
	}

	private void newBrainTemplate() {// new/edit
		DialogNewAI d;
		if (brainSimuSet.brainTemplate != null)
			d = new DialogNewAI(this, true, brainSimuSet.brainTemplate);
		else
			d = new DialogNewAI(this, true);
		BrainTemplate bt = d.showDialog();
		if (bt != null)
			brainSimuSet.brainTemplate = bt;
		initializeDatas();
	}

	/**
	 * Add a Terrain (not a variation)
	 */
	private void addTerrain() {
		open("terrain");
	}

	/**
	 * Add a variation in the selected terrainset
	 */
	private void addVariation() {
		Object path = terrainTree.getLastSelectedPathComponent();

		if (path instanceof TerrainVariationSet) {
			TerrainVariationSet tvs = (TerrainVariationSet) path;
			TerrainVariation var = new TerrainVariationEditor(this, true, tvs.getT(), null).showEditor();
			if (var != null) {
				tvs.addVariation(var);
				initializeDatas();
			}
		}
	}

	private void generateAllVars() {
		Object path = terrainTree.getLastSelectedPathComponent();

		if (path instanceof TerrainVariationSet) {
			TerrainVariationSet tvs = (TerrainVariationSet) path;
			tvs.generateAllVars();
			initializeDatas();
		}
	}

	/**
	 * Remove the selected item from the tree (Terrain set or variation)
	 */
	private void removeTerrain() {
		Object path = terrainTree.getLastSelectedPathComponent();

		if (path instanceof String) {// root, tout supprimer
			if (JOptionPane.showConfirmDialog(this, "Remove all variations ?\nAny unsaved data will be lost.",
					"Remove confirm", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
				terrains.terrains.clear();
				initializeDatas();
			}
		} else if (path instanceof TerrainVariationSet) {
			TerrainVariationSet set = (TerrainVariationSet) path;
			if (set.getVariationCount() == 0 || JOptionPane.showConfirmDialog(this,
					"Deleting this set will delete all linked variations.\nAny unsaved data will be lost.",
					"Remove confirm", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
				terrains.terrains.remove(set);
				initializeDatas();
			}
		} else if (path instanceof TerrainVariation) {
			terrains.removeVariation((TerrainVariation) path);
		}
	}

	/**
	 * Can open files, scan its type and load it Manage all errors and user
	 * messaging
	 * 
	 * @param f            the file to open
	 * @param allowedTypes the object types to be expected, an nothing if any type
	 *                     is allowed
	 * @return true if the object has been successfully loaded
	 */
	private boolean load(File f, Class<?>... allowedTypes) {
		try (FileInputStream fis = new FileInputStream(f)) {
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object o = (ois.readObject());
			ois.close();
			if (allowedTypes.length == 0 || contains(o, allowedTypes)) {
				if (o instanceof SimulationDataSet) {
					dataSet = (SimulationDataSet) o;
					brainSimuSet = dataSet.brainSimuSet;
					terrains = dataSet.terrainSets;
				} else if (o instanceof BrainSimulationSet) {
					brainSimuSet = (BrainSimulationSet) o;
					dataSet.brainSimuSet = brainSimuSet;
				} else if (o instanceof TerrainSimulationSet) {
					terrains = (TerrainSimulationSet) o;
					dataSet.terrainSets = terrains;
				} else if (o instanceof Terrain) {
					TerrainVariationSet set = new TerrainVariationSet((Terrain) o);
					set.name = f.getName();
					terrains.terrains.add(set);
				} else if (o instanceof BrainTemplate) {
					brainSimuSet.brainTemplate = (BrainTemplate) o;
				} else {
					JOptionPane.showMessageDialog(this,
							"File \"" + f.getName() + "\"corrupted or does not contains a known type", "Read error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				initializeDatas();
				return true;
			} else {
				JOptionPane.showMessageDialog(this,
						"The file \"" + f.getName() + "\" does not correspond to the intended content", "Wrong file",
						JOptionPane.ERROR_MESSAGE);
				System.out.println(o.getClass().getSuperclass() + "  " + o.getClass() + ":" + o);
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "File \"" + f.getName() + "\" not found\n" + e.getMessage(),
					"Read error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"An error occured when reading \"" + f.getName() + "\" : Acces denied\n" + e.getMessage(),
					"Read error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this,
					"An error occured while reading \"" + f.getName() + "\" : Unknown type\n" + e.getMessage(),
					"Read error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * test if the type of o is contained in the set equivalent of "instanceof" but
	 * for multiple classes at once
	 * 
	 * @param o   the object to be tested
	 * @param set the classes expected
	 * @return true if o is an instance of one of the classes in set
	 */
	private boolean contains(Object o, Class<?>... set) {
		for (Class<?> c : set) {
			if (c.isAssignableFrom(o.getClass()))
				return true;
		}
		return false;
	}

	/**
	 * Generic file selector
	 * 
	 * @param open        true to open, false to save
	 * @param title       window title
	 * @param description extension dexcriptor
	 * @param extensions  extensions list
	 * @return the choosen file, or null if the operation has been canceled
	 */
	private File selectFile(boolean open, String title, String description, String... extensions) {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter(description, extensions));
		fc.setDialogTitle(title);
		if (emplacement != null)
			fc.setCurrentDirectory(emplacement);
		if (open) {
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				emplacement = fc.getSelectedFile().getParentFile();
				return fc.getSelectedFile();
			}
		} else {
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				emplacement = fc.getSelectedFile().getParentFile();
				if (!endsWithSet(fc.getSelectedFile().getName(), extensions))
					return new File(fc.getSelectedFile() + "." + extensions[0]);
				else
					return fc.getSelectedFile();
			}
		}
		return null;
	}

	private boolean endsWithSet(String name, String... extensions) {
		for (String e : extensions) {
			if (name.endsWith(e))
				return true;
		}
		return false;
	}

	/**
	 * Load a file. Sense the type of file automatically
	 * 
	 * @param extensions The allowed extensions. Nothing = all default extensions
	 */
	private void open(String... extensions) {
		File f;
		if (extensions.length == 0)
			f = selectFile(true, "Open", "GenetisAI files", "terrain", "brntpl", "terrainset", "simconf", "brainset");
		else
			f = selectFile(true, "Open", "GenetisAI files", extensions);
		if (f != null) {
			load(f);
		}
	}

	private void saveAs() {
		File f = selectFile(false, "Save a simulation configuration", "Simulation configuration file (.simconf)",
				"simconf");
		saver(f);
	}

	private void save() {
		if (dataSetFile != null)
			saver(dataSetFile);
		else
			saveAs();
	}

	private void saver(File f) {
		if (writeFile(f, dataSet))
			dataSetFile = f;
	}

	private void saveTerrain() {
		if (terrainFile != null)
			saverTerrain(terrainFile);
		else
			saveAsTerrain();
	}

	private void saveAsTerrain() {
		File f = selectFile(false, "Save a Terrain set", "Terrain set file (.terrainset)", "terrainset");
		saverTerrain(f);
	}

	private void saverTerrain(File f) {
		if (writeFile(f, terrains))
			terrainFile = f;
	}

	private void saveBrain() {
		if (brainFile != null)
			saverBrain(brainFile);
		else
			saveAsBrain();
	}

	private void saveAsBrain() {
		File f = selectFile(false, "Save a population configuration", "Population configuration file (.brainset)",
				"brainset");
		saverBrain(f);
	}

	private void saverBrain(File f) {
		if (writeFile(f, brainSimuSet))
			brainFile = f;
	}

	private void exit() {
		dispose();
		new MainMenu().setVisible(true);
	}

	/**
	 * Write an Object to a file, and manage all user interactions
	 * 
	 * @param f the filename to create. will overwrite it if already present
	 * @param o the object to serializer
	 * @return true if the o has been successfully written in f
	 */
	private boolean writeFile(File f, Object o) {
		if (f != null) {
			try (FileOutputStream fos = new FileOutputStream(f)) {
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(o);
				oos.close();
				return true;
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "File \"" + f.getName() + "\"not found\n" + e.getMessage(),
						"Write error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this,
						"Error when accessing \"" + f.getName() + "\": Access denied\n" + e.getMessage(), "Write error",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		} else
			JOptionPane.showMessageDialog(this, "File error", "null file", JOptionPane.ERROR_MESSAGE);
		return false;
	}

	private void startSimulation() {
		if (!terrains.hasVariation()) {
			JOptionPane.showMessageDialog(this, "No valid terrain", "Error in simulation launching",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (brainSimuSet.fitness == null) {
			JOptionPane.showMessageDialog(this, "Empty fitness is not allowed", "Error in simulation launching",
					JOptionPane.ERROR_MESSAGE);
		}
		if (brainSimuSet.mutation == null) {
			JOptionPane.showMessageDialog(this, "Empty mutation is not allowed", "Error in simulation launching",
					JOptionPane.ERROR_MESSAGE);
		}
		SimulationManager m = new SimulationManager(dataSet);
		System.out.println(dataSet.toString());
		dispose();
		m.startSimulation();
	}
}