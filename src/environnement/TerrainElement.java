package environnement;

import java.awt.Cursor;
import java.awt.Graphics;
import java.io.Serializable;

import menu.environnement_editor.PropertiesModel;
import tools.math.Vector;

public interface TerrainElement extends Serializable {
	
	public static final float NAME_COVER_ALPHA = 0.6f;
	
	/**
	 * Test if the position described by the given vector is inside or touch the
	 * obstacle
	 * 
	 * @param vect
	 *            The vector representation of the position to test
	 * @return true if the given position is inside or touch the obstacle
	 */
	public boolean isInside(Vector vect);
	
	/**
	 * Test if the position described by the given vector is inside or touch visually the
	 * obstacle
	 * 
	 * @param vect
	 *            The vector representation of the position to test
	 * @return true if the given position is inside or touch the obstacle
	 */
	public boolean mouseIsInside(Vector vect);

	/**
	 * Show the visual representation of the obstacle on screen
	 * 
	 * @param g
	 *            The Graphics object to use
	 * @param affCpt
	 *            True to display additional infos
	 * @param fill
	 * 			  Fill the forms that allow it
	 * @param ratio
	 * 			  Allow for zoom out. Display the element with all coordinates multiplied by it
	 */
	public void show(Graphics g, boolean affCpt, boolean fill, float ratio, Vector offset, boolean showName);
	//public default void show(Graphics g, boolean affCpt,boolean fill) {show(g,affCpt,fill,1);}
	//public default void show(Graphics g, boolean affCpt) {show(g,affCpt,false,1);}
	public default void show(Graphics g) {show(g,false,false,1,new Vector(0),false);}
	
	/**
	 * Indicate to the obstacle where the user has grabed the obstacle, to select
	 * the action to take on drag
	 * 
	 * @param mousePos
	 *            The position of the mouse when the user grabed the obstacle
	 */
	public void grab(Vector mousePos);

	/**
	 * Indicate to the obstacle where the mouse has moved since the last grab. This
	 * function is always called after a call to grab. Calls to drag are made when
	 * the user move the mouse as mouseEvent calls
	 * 
	 * @param mousePos
	 *            The position of the mouse as it move
	 * @param
	 * 			  The limit of the terrain
	 */
	public void drag(Vector mousePos,Vector limit);

	/**
	 * Return the mouse icon to display at the current mouse location
	 * 
	 * @param mousePos
	 *            The current mouse location
	 * @return The cursor to display
	 */
	public Cursor getCursorAtLocation(Vector mousePos);
	
	public void setName(String name);
	
	public String getName();
	
	public void genProperties(PropertiesModel propModel);
	
	public boolean editableProperty(int propertyNum);
	
	public void setNewProperty(Object newValue,int propertyNum);
	
	public String getElementType();
	
	public String getElementCategory();
	
	public String toString();
	
	public TerrainElement copy();
}
