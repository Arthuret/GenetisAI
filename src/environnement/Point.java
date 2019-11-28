package environnement;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import menu.environnement_editor.PropertiesModel;
import tools.math.Vector;

public class Point implements TerrainElement {

	private static final long serialVersionUID = -2125731988184355483L;
	
	private Vector position;// vecteur position of the point
	private double collisionRadius;// threshold distance for collisions
	private String name;// name of the point
	
	private static int num = 0;// used for automatic naming
	private static final transient int TAILLE_MIN_AFF = 10;// minimal display radius (display depends on collRadius)
	private static final transient String PATH_RESOURCES = "resources/";//chemin vers les ressources image
	private transient boolean grabed = false;//indique si l'utilisateur manipule le point
	private transient Vector oldMousePos;//permet le calcul du vecteur deplacement de la souris
	private static final transient int NAME_OFFSET_X = 10,NAME_OFFSET_Y = -10;

	private static final transient Map<PointType,Image> imgs = new HashMap<>();
	
	static {
		PointType[] v = PointType.values();
		for(int i = 0;i < v.length;i++) {
			imgs.put(v[i],readImage(v[i]));
		}
	}

	/**
	 * Primary constructor.
	 * @param position The position of the point on the Terrain
	 * @param collisionRadius Detection radius for 'collisions'
	 */
	public Point(Vector position, double collisionRadius) {
		if (position == null)
			throw new NullPointerException("null position not allowed");
		this.position = position;
		this.collisionRadius = collisionRadius;
		this.name = "Point#" + num;
		num++;
	}

	/**
	 * Create a point with default collision radius (5.0)
	 * @param position The position of the point ont the Terrain
	 */
	public Point(Vector position) {
		this(position, 5.0);
	}
	
	/**
	 * Copy constructor.
	 * Create an independant data copy.
	 */
	private Point(Point p) {
		this.name = p.name;
		this.position = p.position.copy();
		this.collisionRadius = p.collisionRadius;
	}

	/**
	 * Load an image
	 * @param pt The type of point which the loaded image should represent
	 */
	private static Image readImage(PointType pt) {
		try {
			return ImageIO.read(new File(PATH_RESOURCES + pt.getIconFileName()));
		} catch (IOException e) {
			System.err.println("Unable to load Point icon for " + pt);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isInside(Vector vect) {
		return (Vector.distance(position, vect) < collisionRadius);
	}

	@Override
	public boolean mouseIsInside(Vector vect) {
		return (Vector.distance(position, vect) < Constants.DISTANCE_SELECT_ELEMENT);
	}

	@Override
	public void grab(Vector mousePos) {
		grabed = mouseIsInside(mousePos);
		oldMousePos = mousePos;
	}

	@Override
	public void drag(Vector mousePos, Vector limit) {
		if (grabed) {
			position = position.add(mousePos.sub(oldMousePos));
			oldMousePos = mousePos;
		}
	}

	@Override
	public Cursor getCursorAtLocation(Vector mousePos) {
		return (mouseIsInside(mousePos))?Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR):
			Cursor.getDefaultCursor();
	}
	
	public void show(Graphics g, boolean affCpt,boolean fill,float ratio,Vector offset,boolean showName) {
		show(g,affCpt,fill,ratio,offset,showName,PointType.USELESS);
	}
	
	public void show(Graphics g, boolean affCpt,boolean fill,float ratio,Vector offset,boolean showName,PointType type) {
		int radius = (int) ((collisionRadius*ratio < TAILLE_MIN_AFF) ? TAILLE_MIN_AFF : collisionRadius*ratio);
		int x = (int) ((position.x()*ratio - (radius / 2))+offset.x());
		int y = (int) ((position.y()*ratio - (radius / 2))+offset.y());
		Image img = imgs.get(type);
		if (img != null)
			g.drawImage(img, x, y, x + radius, y + radius, 0, 0, img.getWidth(null), img.getHeight(null), null);
		else
			g.fillOval(x, y, radius, radius);
		if(showName) {
			drawNameBox(g,x,y);
			g.drawString(name,x+NAME_OFFSET_X,y+NAME_OFFSET_Y);
		}
		
	}
	
	private void drawNameBox(Graphics g,int x,int y) {
		// get metrics from the graphics
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		int adv = metrics.stringWidth(name);
		// calculate the size of a box to hold the
		// text with some padding.
		Color c = g.getColor();
		g.setColor(new Color(1f, 1f, 1f, NAME_COVER_ALPHA));
		g.fillRect(x+NAME_OFFSET_X-1, y+NAME_OFFSET_Y-hgt-1, adv+2, hgt+2);
		g.setColor(c);
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector nPos) {
		this.position = nPos;
	}

	public double getCollisionRadius() {
		return collisionRadius;
	}

	public void setCollisionRadius(double collisionRadius) {
		this.collisionRadius = collisionRadius;
	}

	public String getName() {
		return name;
	}

	public void setName(String nom) {
		this.name = nom;
	}

	//used by the property table
	public void genProperties(PropertiesModel propModel) {
		propModel.addProp("X", position.x());
		propModel.addProp("Y", position.y());
		propModel.addProp("Collision Radius", collisionRadius);
	}

	//idem
	@Override
	public boolean editableProperty(int propertyNum) {
		return true;
	}

	//idem
	@Override
	public void setNewProperty(Object newValue, int propertyNum) {
		double val = Double.parseDouble((String) newValue);
		switch (propertyNum) {
		case 0:
			position.setX(val);
			break;
		case 1:
			position.setY(val);
			break;
		case 2:
			collisionRadius = val;
			break;
		}
	}

	//idem
	@Override
	public String getElementType() {
		return PointType.USELESS.toString();
	}

	public String toString() {
		return "Point(Pos(" + position + ");collisionRadius:" + collisionRadius + ";name:" + name + ")";
	}

	@Override
	public String getElementCategory() {
		return "Point";
	}
	
	public Point copy() {
		return new Point(this);
	}
}
