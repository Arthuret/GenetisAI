package menu.training_editor;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import brain.BrainTemplate;
import brain.BrainType;
import menu.brain_editor.BrainConfigPanel;

@SuppressWarnings("serial")
public class DialogNewAI extends JDialog {
	JComboBox<BrainType> comboBox;
	
	BrainConfigPanel editor = null;
	
	boolean sendData = false;
	
	private static final int HEIGHT = 480,WIDTH = (int) (HEIGHT/(9./16));

	public DialogNewAI(JFrame parent, boolean modal, BrainTemplate template) {
		this(parent,modal);
		comboBox.setSelectedItem(template.getType());
		editor.load(template);
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public DialogNewAI(JFrame parent, boolean modal) {
		super(parent, "On the fly Template creation",modal);
		
		this.setSize(WIDTH, HEIGHT);
		this.setMinimumSize(this.getSize());
		this.setLocationRelativeTo(null);
	
		comboBox = new JComboBox<>();
		comboBox.setModel(new DefaultComboBoxModel<BrainType>(BrainType.values()));
		getContentPane().add(comboBox, BorderLayout.NORTH);
		comboBox.addActionListener(e -> changeEditor());
		
		changeEditor();

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton okButton = new JButton("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
			okButton.addActionListener(e->{
				if(editor != null) {
					if(editor.valid()) {
						sendData = true;
						setVisible(false);
					}else
						JOptionPane.showMessageDialog(this, editor.getUnvalidReason(), "Input error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		{
			JButton cancelButton = new JButton("Cancel");
			buttonPane.add(cancelButton);
			cancelButton.addActionListener(e->setVisible(false));
		}
		
	}
	
	private void changeEditor() {
		try {
			BrainConfigPanel neditor = ((BrainType)comboBox.getSelectedItem()).getEditor().getDeclaredConstructor().newInstance();
			if(editor != null) getContentPane().remove(editor);
			getContentPane().add(neditor, BorderLayout.CENTER);
			editor = neditor;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		pack();
		repaint();
	}
	
	public BrainTemplate showDialog() {
		this.sendData = false;
		this.setVisible(true);
		return (sendData) ? editor.getBrainTemplate() : null;
	}
}
