package tools.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

/**
 * Abstract class used to facilitate the creation of column layout buttons menus
 * 
 * @author Arthur France
 *
 */
public abstract class VerticalButtonsMenu extends JFrame {
	private static final long serialVersionUID = 485468962538861597L;

	private void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
	}

	// Create and set a JPanel with GridBagLayout as content pane
	private void setupContentPane(int buttonCount, Color backgroundColor) {
		JPanel contentPane = new JPanel();
		contentPane.setBackground(backgroundColor);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWidths = new int[] { 0 };
		gbl.columnWeights = new double[] { 1.0 };
		gbl.rowHeights = new int[buttonCount];
		gbl.rowWeights = new double[buttonCount];
		for (int i = 0; i < buttonCount; i++) {
			gbl.rowHeights[i] = 50;
			gbl.rowWeights[i] = 1.0;
		}
		contentPane.setLayout(gbl);

		setContentPane(contentPane);
	}

	/**
	 * The Font used for the buttons text
	 */
	private Font buttonFont;

	/**
	 * Track the index of the button being created
	 */
	private int currentButton = 0;
	/**
	 * Store the total number of buttons
	 */
	private int maxButton;

	/**
	 * Create a new JButton, place it in the layout and set it's insets. The order
	 * of creation is important as it rule the display order of the buttons on the
	 * panel.
	 * 
	 * @param c    The color of the button. With the windows Look and Feel, this
	 *             change only the border of the button
	 * @param text The text displayed in the button
	 * @return A new button already placed on the panel
	 */
	protected JButton createJButton(Color c, String text) {
		JButton jbtn = new JButton(text);
		jbtn.setFont(buttonFont);
		jbtn.setForeground(Color.BLACK);
		if (c != null)
			jbtn.setBackground(c);
		jbtn.setFocusPainted(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		if (currentButton < maxButton)
			gbc.insets = new Insets(0, 0, 5, 0);
		gbc.gridx = 0;
		gbc.gridy = currentButton;
		currentButton++;
		this.add(jbtn, gbc);
		return jbtn;
	}

	/**
	 * Setup the look and feel and the contentPane.
	 * 
	 * @param buttonCount     The number of buttons that will be placed on the menu
	 *                        using <b><i>createJButton</i></b>
	 * @param backgroundColor The background color of the frame
	 * @param textFont        The <b>Font</b> used in the buttons
	 */
	public VerticalButtonsMenu(int buttonCount, Color backgroundColor, Font textFont) {
		setupLookAndFeel();
		setupContentPane(buttonCount, backgroundColor);
		buttonFont = textFont;
		maxButton = buttonCount - 1;
	}
}
