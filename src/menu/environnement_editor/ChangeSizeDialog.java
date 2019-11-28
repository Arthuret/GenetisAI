package menu.environnement_editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JFrame;

import java.awt.GridLayout;
import java.text.ParseException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import tools.math.Vector;

@SuppressWarnings("serial")
public class ChangeSizeDialog extends JDialog {
	private boolean sendData;
	private JSpinner width;
	private JSpinner height;
	private Vector values;
	
	public ChangeSizeDialog(JFrame parent,boolean modal,Vector actualSize) {
		super(parent,"Change the size of the Terrain",modal);
		
		getContentPane().setLayout(new BorderLayout());
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(e->{
					try {
						width.commitEdit();
						height.commitEdit();
						values = new Vector((long)width.getValue(),(long)height.getValue());
						sendData = true;
						setVisible(false);
					} catch (ParseException e1) {
						JOptionPane.showMessageDialog(null, "Invalid values", "Input error", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(e->setVisible(false));
			}
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.CENTER);
			panel.setLayout(new GridLayout(2, 2, 0, 0));
			{
				JLabel lblLargeur = new JLabel("Width :");
				lblLargeur.setHorizontalAlignment(SwingConstants.CENTER);
				panel.add(lblLargeur);
			}
			{
				width = new JSpinner(new SpinnerNumberModel((int)actualSize.x(), 10, null, 10));
				panel.add(width);
			}
			{
				JLabel lblHauteur = new JLabel("Height :");
				lblHauteur.setHorizontalAlignment(SwingConstants.CENTER);
				panel.add(lblHauteur);
			}
			{
				height = new JSpinner(new SpinnerNumberModel((int)actualSize.y(), 10, null, 10));
				panel.add(height);
			}
		}
		
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		
	}
	
	public Vector showChangeSizeDialog() {
		this.sendData = false;
		this.setVisible(true);
		return (sendData)?values:null;
	}

}
