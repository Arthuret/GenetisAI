package menu.training_editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import formula.Formula;
import formula.Variable;

@SuppressWarnings("serial")
public class FormulaEditor extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textF;
	private JLabel labelError;
	private static String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ_$1234567890.()+-*/abcdefghijklmnopqrstuvwxyz";
	private Formula f;
	private boolean sendData = false;
	private FormulaTypes type;

	/**
	 * Create the dialog.
	 */
	public FormulaEditor(JFrame parent, Formula f, FormulaTypes type) {
		this(parent, f.toString().replaceAll(" ", ""), type);
	}

	public FormulaEditor(JFrame parent, String function, FormulaTypes type) {
		super(parent, true);
		this.type = type;
		this.setMinimumSize(new Dimension(400, 200));
		Font font = new Font("Courier", 0, 16);
		textF = new JTextField(function);
		textF.setFont(font);
		textF.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (Character.isAlphabetic(c))
					e.setKeyChar(Character.toUpperCase(c));
				boolean finded = false;
				for (int i = 0; i < ALLOWED_CHARS.length(); i++) {
					if (ALLOWED_CHARS.charAt(i) == c) {
						finded = true;
						break;
					}
				}
				if (!finded)
					e.consume();
			}

			public void keyReleased(KeyEvent e) {
				if (!e.isActionKey()) {
					JTextField textField = (JTextField) e.getSource();
					int pos = textField.getCaretPosition();
					String text = textField.getText();
					textField.setText(text.toUpperCase());
					textField.setCaretPosition(pos);
				}
			}
		});
		textF.addActionListener(a -> extractFormula());
		textF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.add(textF);
		contentPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		labelError = new JLabel();
		labelError.setFont(font);
		contentPanel.add(labelError);

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(a -> {
					if (extractFormula()) {
						sendData = true;
						setVisible(false);
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(a -> {
					sendData = false;
					setVisible(false);
				});
			}
		}

		JMenuBar jmb = new JMenuBar();
		JMenu h = new JMenu("?");
		jmb.add(h);

		JMenuItem helpForBtn = new JMenuItem("Help Formula");
		h.add(helpForBtn);
		helpForBtn.addActionListener(e -> showHelpFormula());

		JMenuItem helpVarBtn = new JMenuItem("Help Variables");
		h.add(helpVarBtn);
		helpVarBtn.addActionListener(e -> showHelpVar());

		this.setJMenuBar(jmb);

		this.pack();
		this.setLocationRelativeTo(parent);
	}

	private void showHelpVar() {
		String message = "<html>";
		for (Variable v : type.getValues()) {
			message += "<b>" + v + ":</b> " + v.getDescription() + "<br/>";
		}
		message += "</html>";
		JOptionPane.showMessageDialog(this, message, "Help Variables", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showHelpFormula() {
		String message = "<html>" + "A formula is a simple mathematical equation "
				+ "describing a calculation executed automatically during the simulation<br/>"
				+ "The fitness formula is executed for each dot at the end of each generation to estimate it's score.<br/>"
				+ "The higher score, the better performance.<br/>"
				+ "The higher the score relative to the total score of the population , the higher the chance to give an offspring for the next generation.<br/>"
				+ "<br/>The 4 basic maths operands are usable (+,-,*,/) as well as positive numbers (0-x works).<br/>"
				+ "All operations are performed using floating point values.<br/>"
				+ "The math priority apply for those operands, so parenthesis can be used to force operation orders.<br/>"
				+ "The variables are marked by a '$' and are written using captial letters : '$DISTANCE'.";
		message += "</html>";
		JOptionPane.showMessageDialog(this, message, "Help Formula", JOptionPane.INFORMATION_MESSAGE);
	}

	private boolean extractFormula() {
		try {
			f = Formula.parse(textF.getText(), type);
			labelError.setForeground(Color.BLACK);
			labelError.setText(f.toString());
			return true;
		} catch (ParseException e) {
			String p = "";
			for (int i = 0; i < e.getErrorOffset(); i++)
				p += " ";
			p += "^";
			labelError.setForeground(Color.RED);
			labelError.setText("<html><pre>" + e.getMessage() + "<br>" + textF.getText() + "<br>" + p + "</pre>");
		}
		return false;
	}

	public Formula showDialog() {
		this.sendData = false;
		System.out.println("showdialog");
		this.setVisible(true);
		return (sendData) ? f : null;
	}

}
