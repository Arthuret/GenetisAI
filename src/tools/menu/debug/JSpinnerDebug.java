package tools.menu.debug;

import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;

@SuppressWarnings("serial")
/**
 * A handy Frame containing a Spinner to test different values.
 * Not to be used outside testing
 * @author Arthur France
 *
 */
public class JSpinnerDebug extends JFrame {
	private JSpinner sp;
	public JSpinnerDebug(SpinnerNumberModel snm) {
		sp = new JSpinner(snm);
		getContentPane().add(sp, BorderLayout.CENTER);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public double getValue() {
		return (double) sp.getValue();
	}
	
	public void addListener(ChangeListener a) {
		sp.addChangeListener(a);
	}
}
