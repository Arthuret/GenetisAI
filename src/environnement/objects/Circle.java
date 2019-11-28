package environnement.objects;

import java.awt.Cursor;
import java.awt.Graphics;

import environnement.Constants;
import environnement.Obstacle;
import menu.environnement_editor.PropertiesModel;
import tools.math.Vector;

/**
 * A circle shaped obstacle
 * 
 * @author Arthur France
 *
 */
public class Circle implements Obstacle {
	private static final long serialVersionUID = 3306434260577027018L;
	
	private Vector center;
	private double radius;
	private String name;
	
	private transient int stucked = 0;
	private transient int lastStucked = 0;
	
	public static final transient double DEFAULT_RADIUS = 30;
	public static final transient int NAME_OFFSET_X = 10,NAME_OFFSET_Y = -15;
	
	private static int numCircle = 0;

	/**
	 * Create a new Circle obstacle
	 * 
	 * @param center
	 *            The position of the center of the circle
	 * @param radius
	 *            The radius of the circle
	 */
	public Circle(Vector center, double radius) {
		this.center = center;
		this.radius = radius;
		this.name = "Circle #"+numCircle;
		numCircle++;
	}
	
	public Circle(Vector center) {
		this(center, DEFAULT_RADIUS);
	}
	
	/**
	 * Copy constructor
	 */
	private Circle(Circle c) {
		this.center = c.center.copy();
		this.radius = c.radius;
		this.name = c.name;
	}

	@Override
	public boolean isInside(Vector vect) {
		boolean resp = Vector.distance(center, vect) <= radius;
		if(resp)
			stucked++;
		return resp;
	}

	@Override
	public void show(Graphics g, boolean affCpt, boolean fill,float ratio,Vector offset,boolean showName) {
		Vector rcenter = center.times(ratio).add(offset);
		double rradius = radius*ratio;
		if(fill)
			g.fillOval((int) (rcenter.x() - rradius), (int) (rcenter.y() - rradius), (int)(2 * rradius), (int)(2 * rradius));
		else
			g.drawOval((int) (rcenter.x()- rradius), (int) (rcenter.y() - rradius), (int)(2 * rradius), (int)(2 * rradius));
		if(affCpt) {
			g.drawString("" + stucked, (int) (rcenter.x()), (int) (rcenter.y()));
			g.drawString("" + lastStucked, (int) (rcenter.x()), (int) (rcenter.y()) + 15);
		}
		if(showName) g.drawString(name,(int) (rcenter.x()+NAME_OFFSET_X), (int) (rcenter.y()+NAME_OFFSET_Y));
	}

	@Override
	public void reset() {
		lastStucked = stucked;
		stucked = 0;
	}

	@Override
	public float getDistanceImpact(Vector position, double angle) {
		if(isInside(position))
			return 0;
		Vector rcenter = this.center.sub(position);
		double b = -2 * (rcenter.y() * Math.sin(angle) + rcenter.x() * Math.cos(angle));
		double c = rcenter.y() * rcenter.y() + rcenter.x() * rcenter.x() - radius * radius;
		double delta = b * b - 4 * c;
		if(delta >= 0) {
			if(delta != 0)
				delta = Math.sqrt(delta);
			double resp = (-b - delta) / 2;
			if(resp < 0)
				return Float.MAX_VALUE;
			return (float) resp;
		} else {
			return Float.MAX_VALUE;
		}
	}

	private transient boolean selcenter;
	private transient boolean selradius;
	private transient Vector diffMouse;
	
	@Override
	public void grab(Vector mousePos) {
		selcenter = false;
		selradius = false;
		double distCenter = Vector.distance(mousePos, center);
		if(distCenter < radius+Constants.DISTANCE_SELECT_ELEMENT && distCenter > radius-Constants.DISTANCE_SELECT_ELEMENT) {
			//selection du cercle
			selradius = true;
			diffMouse = center.sub(mousePos).limit(Vector.distance(center, mousePos)-radius);
		}else if(distCenter < Constants.DISTANCE_SELECT_ELEMENT) {
			//selection du centre
			selcenter = true;
			diffMouse = center.sub(mousePos);
		}
	}

	@Override
	public void drag(Vector mousePos,Vector limit) {
		if(selradius)
			radius = (int) Vector.distance(mousePos.add(diffMouse), center);
		else if(selcenter) {
			center = mousePos.add(diffMouse);
		}
	}

	@Override
	public Cursor getCursorAtLocation(Vector mousePos) {
		double distFCenter = Vector.distance(mousePos, center);
		if(distFCenter <= radius + Constants.DISTANCE_SELECT_ELEMENT
				&& distFCenter >= radius - Constants.DISTANCE_SELECT_ELEMENT) {
			double angle = mousePos.sub(center).getAngle();
			double absAngle = Math.abs(angle);
			if(absAngle < Math.PI / 8 || absAngle > (7 * Math.PI) / 8)
				return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			else if(absAngle > (3 * Math.PI) / 8 && absAngle < (5 * Math.PI) / 8)
				return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			else if(angle >= Math.PI / 8 && angle <= (3 * Math.PI) / 8
					|| angle >= (-7 * Math.PI) / 8 && angle <= (-5 * Math.PI) / 8)
				return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
			else if(angle <= -Math.PI / 8 && angle >= (-3 * Math.PI) / 8
					|| angle <= (7 * Math.PI) / 8 && angle >= (5 * Math.PI) / 8)
				return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		} else if(distFCenter <= Constants.DISTANCE_SELECT_ELEMENT) {
			return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		}
		return Cursor.getDefaultCursor();
	}
	
	@Override
	public void showCenter(Graphics g,float ratio) {
		Vector rcenter = center.times(ratio);
		g.drawLine((int)(rcenter.x()-Constants.CROSS_RADIUS), (int)(rcenter.y()), (int)(rcenter.x()+Constants.CROSS_RADIUS), (int)(rcenter.y()));
		g.drawLine((int)(rcenter.x()), (int)(rcenter.y()-Constants.CROSS_RADIUS), (int)(rcenter.x()), (int)(rcenter.y()+Constants.CROSS_RADIUS));
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public Vector getCenter() {
		return center;
	}
	
	public double getRadius() {
		return radius;
	}

	@Override
	public void genProperties(PropertiesModel propModel) {
		propModel.addProp("Center.X", center.x());
		propModel.addProp("Center.Y", center.y());
		propModel.addProp("Radius", radius);
	}

	@Override
	public boolean editableProperty(int propertyNum) {
		return true;
	}

	@Override
	public void setNewProperty(Object newValue, int propertyNum) {
		double val = Double.parseDouble((String)newValue);
		switch(propertyNum) {
		case 0:
			center.setX(val);
			break;
		case 1:
			center.setY(val);
			break;
		case 2:
			radius = (int) val;
			break;
		}
	}

	@Override
	public String getElementType() {
		return "Circle";
	}
	
	public String toString() {
		return "Circle(center"+center+";radius:"+radius+";name:"+name+")";
	}
	
	public Circle copy() {
		return new Circle(this);
	}
}
