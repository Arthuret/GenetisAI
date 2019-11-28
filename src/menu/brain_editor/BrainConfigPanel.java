package menu.brain_editor;

import javax.swing.JPanel;

import brain.BrainTemplate;
/**
 * An abstract class used to implement brain configuration panels
 * @author Arthur France
 */
@SuppressWarnings("serial")
public abstract class BrainConfigPanel extends JPanel{
	
	/**
	 * Called when reinitializing the configuration panel
	 * (i.e. To create a new template)
	 * Should return the panel to a state similar of the boot state
	 */
	public abstract void reset();
	
	/**
	 * Called when opening a template file
	 * @param bt The template opened
	 * @return <b>true</b> when the given brain template has been successfully extracted and showed in the panel
	 */
	public abstract boolean load(BrainTemplate bt);
	
	/**
	 * Called when saving of using the template
	 * @return The <b>BrainTemplate</b> object entered by the user 
	 */
	public abstract BrainTemplate getBrainTemplate();
	
	/**
	 * Allow for verification of user inputs
	 * @return True if the values entered by the user are valids, false if not.
	 */
	public abstract boolean valid();
	
	/**
	 * Used to show the user a message about why valid() == false
	 * Do not call this method if valid returned true, as the return value is not guaranteed.
	 * @return A user readable String describing the reason valid() returned false
	 */
	public abstract String getUnvalidReason();
}