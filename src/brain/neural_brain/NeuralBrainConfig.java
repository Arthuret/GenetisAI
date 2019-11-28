package brain.neural_brain;

import brain.BrainTemplate;
import menu.brain_editor.BrainConfigPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.InvalidParameterException;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
/**
 * An editor that can generate neuralBrain templates
 * 
 * @author Arthur France
 */
public class NeuralBrainConfig extends BrainConfigPanel {
	private JTextField textField;
	private JSpinner spinnerSpeed, spinnerAccel, spinnerRange;
	private JCheckBox chckbxDistance, chckbxDirection, chckbxSpeed, chckbxWalls;

	private static final float DEFAULT_MAX_SPEED = 10;
	private static final float DEFAULT_MAX_ACCEL = 2;
	private static final float DEFAULT_RANGE = 40;
	private static final String ALLOWED_CHARS = "1234567890;";

	public NeuralBrainConfig() {
		setBackground(Color.LIGHT_GRAY);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblFullyConnectedFeed = new JLabel("Fully connected feed forward neural network");
		lblFullyConnectedFeed.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_lblFullyConnectedFeed = new GridBagConstraints();
		gbc_lblFullyConnectedFeed.insets = new Insets(0, 0, 5, 0);
		gbc_lblFullyConnectedFeed.gridx = 0;
		gbc_lblFullyConnectedFeed.gridy = 0;
		add(lblFullyConnectedFeed, gbc_lblFullyConnectedFeed);

		JPanel netConf = new JPanel();
		GridBagConstraints gbc_netConf = new GridBagConstraints();
		gbc_netConf.insets = new Insets(0, 5, 5, 5);
		gbc_netConf.fill = GridBagConstraints.BOTH;
		gbc_netConf.gridx = 0;
		gbc_netConf.gridy = 1;
		add(netConf, gbc_netConf);
		GridBagLayout gbl_netConf = new GridBagLayout();
		gbl_netConf.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_netConf.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_netConf.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_netConf.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		netConf.setLayout(gbl_netConf);

		JLabel lblInternalLayerSetup = new JLabel("Internal layers setup");
		GridBagConstraints gbc_lblInternalLayerSetup = new GridBagConstraints();
		gbc_lblInternalLayerSetup.gridwidth = 4;
		gbc_lblInternalLayerSetup.insets = new Insets(0, 0, 5, 0);
		gbc_lblInternalLayerSetup.gridx = 0;
		gbc_lblInternalLayerSetup.gridy = 0;
		netConf.add(lblInternalLayerSetup, gbc_lblInternalLayerSetup);

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (ALLOWED_CHARS.indexOf(c) == -1) {// si le char n'existe pas dans la chaine
					e.consume(); // ignore event
				}
			}
		});
		textField.setToolTipText(
				"<html>Setup the number of hidden (internal) layers and the number of nodes in each layer<br>\r\nFormat : [Nb1];[Nb2];[Nb3]<br>\r\nExemple : 12;10<br>\r\n\tThis will produce a layer of 12 nodes and a layer of 10 nodes</html>");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 4;
		gbc_textField.insets = new Insets(0, 5, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 1;
		netConf.add(textField, gbc_textField);
		textField.setColumns(10);

		JLabel lblMaxSpeed = new JLabel("Max Speed");
		GridBagConstraints gbc_lblMaxSpeed = new GridBagConstraints();
		gbc_lblMaxSpeed.anchor = GridBagConstraints.EAST;
		gbc_lblMaxSpeed.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxSpeed.gridx = 0;
		gbc_lblMaxSpeed.gridy = 2;
		netConf.add(lblMaxSpeed, gbc_lblMaxSpeed);

		spinnerSpeed = new JSpinner();
		spinnerSpeed.setModel(new SpinnerNumberModel(DEFAULT_MAX_SPEED, 1f, null, 1f));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.insets = new Insets(0, 0, 5, 5);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 2;
		netConf.add(spinnerSpeed, gbc_spinner);

		JLabel lblSensorRange = new JLabel("Sensors range");
		GridBagConstraints gbc_lblSensorRange = new GridBagConstraints();
		gbc_lblSensorRange.anchor = GridBagConstraints.EAST;
		gbc_lblSensorRange.insets = new Insets(0, 0, 5, 5);
		gbc_lblSensorRange.gridx = 2;
		gbc_lblSensorRange.gridy = 2;
		netConf.add(lblSensorRange, gbc_lblSensorRange);

		spinnerRange = new JSpinner();
		spinnerRange.setModel(new SpinnerNumberModel(DEFAULT_RANGE, 1f, null, 1f));
		GridBagConstraints gbc_spinner_1 = new GridBagConstraints();
		gbc_spinner_1.insets = new Insets(0, 0, 5, 5);
		gbc_spinner_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_1.gridx = 3;
		gbc_spinner_1.gridy = 2;
		netConf.add(spinnerRange, gbc_spinner_1);

		JLabel lblMaxAcceleration = new JLabel("Max Acceleration");
		GridBagConstraints gbc_lblMaxAcceleration = new GridBagConstraints();
		gbc_lblMaxAcceleration.anchor = GridBagConstraints.EAST;
		gbc_lblMaxAcceleration.insets = new Insets(0, 5, 0, 5);
		gbc_lblMaxAcceleration.gridx = 0;
		gbc_lblMaxAcceleration.gridy = 3;
		netConf.add(lblMaxAcceleration, gbc_lblMaxAcceleration);

		spinnerAccel = new JSpinner();
		spinnerAccel.setModel(new SpinnerNumberModel(DEFAULT_MAX_ACCEL, 0.1f, null, 0.1f));
		GridBagConstraints gbc_spinner_2 = new GridBagConstraints();
		gbc_spinner_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_2.insets = new Insets(0, 0, 0, 5);
		gbc_spinner_2.gridx = 1;
		gbc_spinner_2.gridy = 3;
		netConf.add(spinnerAccel, gbc_spinner_2);

		JPanel inputConf = new JPanel();
		GridBagConstraints gbc_inputConf = new GridBagConstraints();
		gbc_inputConf.insets = new Insets(0, 5, 5, 5);
		gbc_inputConf.fill = GridBagConstraints.BOTH;
		gbc_inputConf.gridx = 0;
		gbc_inputConf.gridy = 2;
		add(inputConf, gbc_inputConf);
		GridBagLayout gbl_inputConf = new GridBagLayout();
		gbl_inputConf.columnWidths = new int[] { 0, 0, 0 };
		gbl_inputConf.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_inputConf.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_inputConf.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		inputConf.setLayout(gbl_inputConf);

		JLabel lblInputs = new JLabel("Inputs");
		GridBagConstraints gbc_lblInputs = new GridBagConstraints();
		gbc_lblInputs.gridwidth = 2;
		gbc_lblInputs.insets = new Insets(0, 0, 5, 0);
		gbc_lblInputs.gridx = 0;
		gbc_lblInputs.gridy = 0;
		inputConf.add(lblInputs, gbc_lblInputs);

		JLabel lblObjective = new JLabel("Objective :");
		GridBagConstraints gbc_lblObjective = new GridBagConstraints();
		gbc_lblObjective.insets = new Insets(0, 0, 5, 5);
		gbc_lblObjective.gridx = 0;
		gbc_lblObjective.gridy = 1;
		inputConf.add(lblObjective, gbc_lblObjective);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		inputConf.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		chckbxDistance = new JCheckBox("Distance");
		GridBagConstraints gbc_chckbxDistance = new GridBagConstraints();
		gbc_chckbxDistance.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxDistance.gridx = 0;
		gbc_chckbxDistance.gridy = 0;
		panel.add(chckbxDistance, gbc_chckbxDistance);

		chckbxDirection = new JCheckBox("Direction");
		GridBagConstraints gbc_chckbxDirection = new GridBagConstraints();
		gbc_chckbxDirection.gridx = 1;
		gbc_chckbxDirection.gridy = 0;
		panel.add(chckbxDirection, gbc_chckbxDirection);

		JLabel lblSensors = new JLabel("Sensors :");
		GridBagConstraints gbc_lblSensors = new GridBagConstraints();
		gbc_lblSensors.insets = new Insets(0, 5, 0, 5);
		gbc_lblSensors.gridx = 0;
		gbc_lblSensors.gridy = 2;
		inputConf.add(lblSensors, gbc_lblSensors);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 2;
		inputConf.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		chckbxSpeed = new JCheckBox("Speed");
		GridBagConstraints gbc_chckbxSpeed = new GridBagConstraints();
		gbc_chckbxSpeed.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxSpeed.gridx = 0;
		gbc_chckbxSpeed.gridy = 0;
		panel_1.add(chckbxSpeed, gbc_chckbxSpeed);

		chckbxWalls = new JCheckBox("Murs");
		chckbxWalls.addActionListener(e -> spinnerRange.setEnabled(chckbxWalls.isSelected()));
		GridBagConstraints gbc_chckbxMurs = new GridBagConstraints();
		gbc_chckbxMurs.gridx = 1;
		gbc_chckbxMurs.gridy = 0;
		panel_1.add(chckbxWalls, gbc_chckbxMurs);

		reset();
	}

	@Override
	public void reset() {
		textField.setText("");
		spinnerSpeed.setValue(DEFAULT_MAX_SPEED);
		spinnerAccel.setValue(DEFAULT_MAX_ACCEL);
		chckbxDistance.setSelected(true);
		chckbxDirection.setSelected(true);
		chckbxSpeed.setSelected(true);
		chckbxWalls.setSelected(true);
		spinnerRange.setEnabled(chckbxWalls.isSelected());
	}

	@Override
	/**
	 * Load the given BrainTemplate onto the editing fields
	 */
	public boolean load(BrainTemplate bt) {
		if (bt instanceof NeuralBrainTemplate) {
			NeuralBrainTemplate nbt = (NeuralBrainTemplate) bt;
			String text = "";
			int[] layers = nbt.getInternalLayers();
			for (int i = 0; i < layers.length; i++) {
				text += layers[i];
				if (i + 1 < layers.length)
					text += ";";
			}
			textField.setText(text);
			spinnerSpeed.setValue(nbt.getMaxSpeed());
			spinnerAccel.setValue(nbt.getMaxAccel());
			spinnerRange.setValue(nbt.getSensorRange());
			chckbxDistance.setSelected(nbt.isDistance());
			chckbxDirection.setSelected(nbt.isDirection());
			chckbxSpeed.setSelected(nbt.isSpeed());
			chckbxWalls.setSelected(nbt.isWalls());
			spinnerRange.setEnabled(chckbxWalls.isSelected());
			return true;
		}
		return false;
	}

	/**
	 * Parse the content of the text field
	 * 
	 * @return The layers configuration parsed
	 * @throws InvalidParameterException when the content of the textField is
	 *                                   invalid
	 */
	private int[] getLayers() throws InvalidParameterException {
		String[] layers = textField.getText().split(";");
		int[] resp = new int[layers.length];
		for (int i = 0; i < layers.length; i++) {
			if (layers[i].length() == 0)
				throw new InvalidParameterException("Empty layers not allowed");
			try {
				resp[i] = Integer.parseInt(layers[i]);
			} catch (NumberFormatException e) {
				throw new InvalidParameterException("Invalid layer format");
			}
		}
		return resp;
	}

	@Override
	public BrainTemplate getBrainTemplate() throws InvalidParameterException {
		int[] layers = getLayers();
		return new NeuralBrainTemplate(layers, (float) spinnerSpeed.getValue(), (float) spinnerAccel.getValue(),
				(float) spinnerRange.getValue(), chckbxDistance.isSelected(), chckbxDirection.isSelected(),
				chckbxSpeed.isSelected(), chckbxWalls.isSelected());
	}

	private String reasonInvalid;// used to optimise getUnvalidReason

	@Override
	public boolean valid() {
		boolean resp = true;
		try {
			getBrainTemplate();
		} catch (Exception e) {
			resp = false;
			reasonInvalid = e.getMessage();
		}
		return resp;
	}

	@Override
	public String getUnvalidReason() {
		return reasonInvalid;
	}

}
