package menu.training_editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;

import environnement.Point;
import environnement.Terrain;
import environnement.TerrainVariation;

import java.awt.GridBagConstraints;
import javax.swing.JLabel;

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class TerrainVariationEditor extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JComboBox<Point> comboBoxOrigin, comboBoxGoal;
	private Terrain t;
	private JTextField textFieldName;

	private boolean sendData;

	private SimpleTerrainShower sTShw;
	private boolean creation;//creation/edition mode
	
	private TerrainVariation current,edit;

	JButton okButton;

	/**
	 * Create the dialog. Can edit and modify Terrain variations
	 * @param parent The parent JFrame
	 * @param modal true if the parent JFrame should not be accessible
	 * @param t the base Terrain
	 * @param tvar the TerrainVariation to edit. null to create a new TerrainVariation
	 */
	public TerrainVariationEditor(JFrame parent, boolean modal, Terrain t, TerrainVariation tvar) {
		super(parent,"Terrain variation editor", modal);
		if(t.getPoints().size() == 0) throw new IllegalArgumentException("The terrain doesn't contain any point.");
		setMinimumSize(new Dimension(300, 190));
		this.t = t;
		creation = tvar == null;
		edit = tvar;
		this.current = (tvar != null)?tvar.refcopy():new TerrainVariation(t.getPoints().get(0));
		setupGui();
		setupCbBox();
		updateCbBox();
	}

	ActionListener cbBoxL = e -> updateCbBox();

	private void setupCbBox() {
		t.getPoints().forEach(p -> {
			comboBoxOrigin.addItem(p);
			comboBoxGoal.addItem(p);
		});
		comboBoxOrigin.setSelectedItem(current.getOrigin());
		comboBoxGoal.setSelectedItem(current.getGoal());
		comboBoxOrigin.addActionListener(cbBoxL);
		comboBoxGoal.addActionListener(cbBoxL);
		sTShw.repaint();
	}

	private void updateCbBox() {
		okButton.setEnabled(comboBoxOrigin.getSelectedIndex() != comboBoxGoal.getSelectedIndex());
		Point pg = (Point) comboBoxGoal.getSelectedItem();
		Point po = (Point) comboBoxOrigin.getSelectedItem();
		current.setGoal(pg);
		current.setOrigin(po);
		sTShw.repaint();
	}

	public TerrainVariation showEditor() {
		this.sendData = false;
		this.setVisible(true);// modal
		// dialog ferme
		return (this.sendData) ? agregateData() : null;
	}

	private TerrainVariation agregateData() {
		String nom = textFieldName.getText();
		if(creation) {
			if (nom.length() == 0)
				return new TerrainVariation(current.getOrigin(),current.getGoal());
			else
				return new TerrainVariation(current.getOrigin(),current.getGoal(), nom);
		}else {
			current.setName(nom);
			edit.absorb(current);
			return edit;
		}
	}

	private void setupGui() {
		setBounds(100, 100, 349, 315);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 24, 50, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);

		ListCellRenderer<Point> cbrender = (list, value, index, isSelected,
				cellHasFocus) -> new JLabel(value.getName());

		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportBorder(null);
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridheight = 6;
			gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 0;
			contentPanel.add(scrollPane, gbc_scrollPane);

			{
				sTShw = new SimpleTerrainShower(null,t,current, true, true);
				scrollPane.setViewportView(sTShw);
			}
		}
		{
			JLabel lblOriginePoint = new JLabel("Origin : Starting point of the dots");
			GridBagConstraints gbc_lblOriginePoint = new GridBagConstraints();
			gbc_lblOriginePoint.gridwidth = 2;
			gbc_lblOriginePoint.insets = new Insets(0, 0, 5, 0);
			gbc_lblOriginePoint.gridx = 1;
			gbc_lblOriginePoint.gridy = 0;
			contentPanel.add(lblOriginePoint, gbc_lblOriginePoint);
		}
		{
			comboBoxOrigin = new JComboBox<>();
			comboBoxOrigin.setRenderer(cbrender);
			GridBagConstraints gbc_comboBoxOrigin = new GridBagConstraints();
			gbc_comboBoxOrigin.gridwidth = 2;
			gbc_comboBoxOrigin.insets = new Insets(0, 0, 5, 0);
			gbc_comboBoxOrigin.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxOrigin.gridx = 1;
			gbc_comboBoxOrigin.gridy = 1;
			contentPanel.add(comboBoxOrigin, gbc_comboBoxOrigin);
		}
		{
			JLabel lblObjectifObjectif = new JLabel("Goal : Point to be reached by the Dots");
			GridBagConstraints gbc_lblObjectifObjectif = new GridBagConstraints();
			gbc_lblObjectifObjectif.gridwidth = 2;
			gbc_lblObjectifObjectif.insets = new Insets(0, 0, 5, 0);
			gbc_lblObjectifObjectif.gridx = 1;
			gbc_lblObjectifObjectif.gridy = 2;
			contentPanel.add(lblObjectifObjectif, gbc_lblObjectifObjectif);
		}
		{
			comboBoxGoal = new JComboBox<>();
			comboBoxGoal.setRenderer(cbrender);
			GridBagConstraints gbc_comboBoxGoal = new GridBagConstraints();
			gbc_comboBoxGoal.gridwidth = 2;
			gbc_comboBoxGoal.insets = new Insets(0, 0, 5, 0);
			gbc_comboBoxGoal.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxGoal.gridx = 1;
			gbc_comboBoxGoal.gridy = 3;
			contentPanel.add(comboBoxGoal, gbc_comboBoxGoal);
		}
		{
			JLabel lblName = new JLabel("Name :");
			GridBagConstraints gbc_lblNom = new GridBagConstraints();
			gbc_lblNom.anchor = GridBagConstraints.EAST;
			gbc_lblNom.insets = new Insets(0, 0, 5, 5);
			gbc_lblNom.gridx = 1;
			gbc_lblNom.gridy = 4;
			contentPanel.add(lblName, gbc_lblNom);
		}
		{
			textFieldName = new JTextField(current.getName());
			GridBagConstraints gbc_textFieldNom = new GridBagConstraints();
			gbc_textFieldNom.insets = new Insets(0, 0, 5, 0);
			gbc_textFieldNom.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldNom.gridx = 2;
			gbc_textFieldNom.gridy = 4;
			contentPanel.add(textFieldName, gbc_textFieldNom);
			textFieldName.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				buttonPane.add(okButton);
				okButton.addActionListener(e -> {
					sendData = true;
					setVisible(false);
				});
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(e -> setVisible(false));
				buttonPane.add(cancelButton);
			}
		}
		this.setMaximumSize(new Dimension(1200, 800));
		this.pack();
		this.setLocationRelativeTo(null);
	}

}
