package environnement.objects;
import java.awt.Cursor;
import java.awt.Graphics;

import environnement.Constants;
import environnement.Obstacle;
import menu.environnement_editor.PropertiesModel;
import tools.math.Segment;
import tools.math.Vector;

/**
 * A straight rectangle obstacle
 * 
 * @author Arthur France
 *
 */
public class Rectangle implements Obstacle{
	private static final long serialVersionUID = -8685922076489299837L;
	
	private Vector begin;
	private Vector end;
	private String name;
	
	private transient int stucked = 0;
	private transient int lastStucked = 0;
	
	public static final transient int DEFAULT_WIDTH = 30;
	public static final transient int DEFAULT_HEIGHT = 30;
	public static final transient int NAME_OFFSET_X = 10,NAME_OFFSET_Y = -10;
	
	private static int numRectangle = 0;

	/**
	 * Create a new Rectangle obstacle
	 * 
	 * @param begin
	 *            The position of the Up-Left corner of the rectangle
	 * @param size
	 *            The size of the rectangle
	 */
	public Rectangle(Vector begin, Vector size) {
		this.begin = begin;
		this.end = size.add(begin);
		this.name = "Rectangle #"+numRectangle;
		numRectangle++;
	}
	
	public Rectangle(Vector middle) {
		Vector diff = new Vector(DEFAULT_WIDTH/2.,DEFAULT_HEIGHT/2.);
		this.begin = middle.sub(diff);
		this.end = middle.add(diff);
		this.name = "Rectangle #"+numRectangle;
		numRectangle++;
	}
	
	/**
	 * Constructeur copie
	 */
	private Rectangle(Rectangle r) {
		this.begin = r.begin.copy();
		this.end = r.end.copy();
		this.name = r.name;
	}

	@Override
	public boolean isInside(Vector vect) {
		boolean resp = (vect.x() > begin.x() && vect.x() < end.x() && vect.y() > begin.y() && vect.y() < end.y());
		if(resp)
			stucked++;
		return resp;
	}

	@Override
	public void show(Graphics g,boolean affCpt,boolean fill,float ratio,Vector offset,boolean showName) {
		Vector rdebut = begin.times(ratio).add(offset),rfin = end.times(ratio).add(offset);
		if(fill)
			g.fillRect((int) rdebut.x(), (int) rdebut.y(), (int) (rfin.x() - rdebut.x()), (int) (rfin.y() - rdebut.y()));
		else
			g.drawRect((int) rdebut.x(), (int) rdebut.y(), (int) (rfin.x() - rdebut.x()), (int) (rfin.y() - rdebut.y()));
		if(affCpt) {
			g.drawString(Integer.toString(stucked), (int) ((rdebut.x() + rfin.x()) / 2), (int) ((rdebut.y() + rfin.y()) / 2));
			g.drawString(Integer.toString(lastStucked), (int) ((rdebut.x() + rfin.x()) / 2), (int) ((rdebut.y() + rfin.y()) / 2) + 15);
		}
		if(showName) g.drawString(name,(int) (rdebut.x()+NAME_OFFSET_X),(int) (rdebut.y()+NAME_OFFSET_Y));
	}

	@Override
	public void reset() {
		lastStucked = stucked;
		stucked = 0;
	}

	@Override
	public float getDistanceImpact(Vector position, double angle) {
		boolean checkUp = false;//note that on the display, up and down are inverted
		boolean checkDown = false;
		boolean checkLeft = false;
		boolean checkRight = false;
		
		//limiting to -PI;PI range
		while (angle > Math.PI)// reduction if the angle if over
			angle -= 2 * Math.PI;
		while (angle < -Math.PI)// augmentation if the angle is below
			angle += 2 * Math.PI;
		
		//separating zones
		if(position.x() < begin.x()) {// quarters 1,2 or 3
			if(Math.abs(angle) < Math.PI/2) {
				if(position.y() > end.y()) {
					if(angle < 0) {//between -PI/2 and 0
						checkLeft = true;
						checkUp = true;
					}
				} else if(position.y() < begin.y()) {
					if(angle > 0) {//between 0 and PI/2
						checkLeft = true;
						checkDown = true;
					}
				} else
					checkLeft = true;
			}
		} else if(position.x() > end.x()) {// quarters 6,7 or 8
			if(Math.abs(angle) > Math.PI/2) {
				if(position.y() > end.y()) {
					if(angle < 0) {//between -PI and -PI/2
						checkRight = true;
						checkUp = true;
					}
				} else if(position.y() < begin.y()) {
					if(angle > 0) {//between PI/2 and PI
						checkRight = true;
						checkDown = true;
					}
				} else
					checkRight = true;
			}
		} else {
			if(position.y() > end.y()) {
				if(angle < 0)
					checkUp = true;
			} else if(position.y() < begin.y()) {
				if(angle > 0)
					checkDown = true;
			} else
				return 0f;
		}
		
		double minDist = Double.MAX_VALUE;
		if(checkUp) {
			double opp = position.y()-end.y();
			double hyp = opp/Math.sin(-angle);
			double posX = Math.signum(angle+(Math.PI/2))*Math.sqrt((hyp*hyp)-(opp*opp))+position.x();
			if(posX <= end.x() && posX >= begin.x())
				minDist = Math.min(hyp, minDist);
		}
		if(checkDown) {
			double opp = begin.y()-position.y();
			double hyp = opp/Math.sin(angle);
			double posX = (-Math.signum(angle-Math.PI/2)*Math.sqrt((hyp*hyp)-(opp*opp)))+position.x();
			if(posX <= end.x() && posX >= begin.x())
				minDist = Math.min(hyp, minDist);
		}
		if(checkLeft) {
			double adj = begin.x()-position.x();
			double hyp = adj/Math.cos(angle);
			double posY = Math.signum(angle)*Math.sqrt((hyp*hyp)-(adj*adj))+position.y();
			if(posY <= end.y() && posY >= begin.y())
				minDist = Math.min(hyp, minDist);
		}
		if(checkRight) {
			double adj = position.x()-end.x();
			double hyp = adj/-Math.cos(angle);
			double posY = Math.signum(angle)*Math.sqrt((hyp*hyp)-(adj*adj))+position.y();
			if(posY <= end.y() && posY >= begin.y())
				minDist = Math.min(hyp, minDist);
		}
		
		return (float) minDist;
	}
	
	private transient boolean left;
	private transient boolean right;
	private transient boolean top;
	private transient boolean bottom;
	private transient boolean center;
	private transient Vector diffMouse;

	private void detector(Vector mousePos) {
		left = false;
		right = false;
		top = false;
		bottom = false;
		center = false;
		Segment seg = new Segment(begin,new Vector(begin.x(),end.y()));
		if(seg.distanceFromPoint(mousePos) < Constants.DISTANCE_SELECT_ELEMENT)
			left = true;
		seg.setB(new Vector(end.x(),begin.y()));
		if(seg.distanceFromPoint(mousePos) < Constants.DISTANCE_SELECT_ELEMENT)
			top = true;
		seg.setA(end);
		if(seg.distanceFromPoint(mousePos) < Constants.DISTANCE_SELECT_ELEMENT)
			right = true;
		seg.setB(new Vector(begin.x(),end.y()));
		if(seg.distanceFromPoint(mousePos) < Constants.DISTANCE_SELECT_ELEMENT)
			bottom = true;
		if(Vector.distance(Vector.middle(begin, end), mousePos) < Constants.DISTANCE_SELECT_ELEMENT)
			center = true;
	}
	
	@Override
	public void grab(Vector mousePos) {
		detector(mousePos);
		double x = 0;
		double y = 0;
		if(top)
			y = begin.y()-mousePos.y();
		if(bottom)
			y = end.y()-mousePos.y();
		if(left)
			x = begin.x()-mousePos.x();
		if(right)
			x = end.x()-mousePos.x();
		if(center) {
			Vector center = end.sub(begin).times(0.5).add(begin);
			x = center.x()-mousePos.x();
			y = center.y()-mousePos.y();
		}
		diffMouse = new Vector(x,y);
	}

	@Override
	public void drag(Vector mousePos,Vector limit) {
		//movement of the borders
		
		Vector mpos = mousePos.add(diffMouse);
		if(top)
			begin.setY(mpos.y());
		if(right)
			end.setX(mpos.x());
		if(left)
			begin.setX(mpos.x());
		if(bottom)
			end.setY(mpos.y());
		//movement of the center
		if(!top && !bottom && !left && !right && center) {
			Vector diag = end.sub(begin).times(0.5);
			begin = mousePos.add(diffMouse).sub(diag);
			end = mousePos.add(diffMouse).add(diag);
		}
		//maj oldPos
		//detection of invertion
		Vector diff = end.sub(begin);
		
		if(top && diff.y() < 0) {
			top = false;
			bottom = true;
			double temp = end.y();
			end.setY(begin.y());
			begin.setY(temp);
		} else if(bottom && diff.y() < 0) {
			top = true;
			bottom = false;
			double temp = end.y();
			end.setY(begin.y());
			begin.setY(temp);
		}
		
		if(left && diff.x() < 0) {
			left = false;
			right = true;
			double temp = end.x();
			end.setX(begin.x());
			begin.setX(temp);
		} else if(right && diff.x() < 0) {
			left = true;
			right = false;
			double temp = end.x();
			end.setX(begin.x());
			begin.setX(temp);
		}
	}

	@Override
	public Cursor getCursorAtLocation(Vector mousePos) {
		detector(mousePos);
		
		if(top && !bottom && !left && !right)
			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		else if(!top && bottom && !left && !right)
			return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		else if(!top && !bottom && left && !right)
			return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		else if(!top && !bottom && !left && right)
			return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		else if(top && !bottom && left && !right)
			return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		else if(top && !bottom &&!left && right)
			return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		else if(!top && bottom && left && !right)
			return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
		else if(!top && bottom && !left && right)
			return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		else if(center)
			return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		else
			return Cursor.getDefaultCursor();
	}

	@Override
	public void showCenter(Graphics g,float ratio) {
		Vector middle = Vector.middle(begin, end).times(ratio);
		g.drawLine((int)(middle.x()-Constants.CROSS_RADIUS), (int)(middle.y()), (int)(middle.x()+Constants.CROSS_RADIUS), (int)(middle.y()));
		g.drawLine((int)(middle.x()), (int)(middle.y()-Constants.CROSS_RADIUS), (int)(middle.x()), (int)(middle.y()+Constants.CROSS_RADIUS));
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public Vector getBegin() {
		return begin;
	}

	public Vector getEnd() {
		return end;
	}

	@Override
	public void genProperties(PropertiesModel propModel) {
		propModel.addProp("Begin.X",begin.x());
		propModel.addProp("Begin.Y",begin.y());
		propModel.addProp("End.X",end.x());
		propModel.addProp("End.Y",end.y());
		Vector diff = end.sub(begin);
		propModel.addProp("Diff.X",diff.x());
		propModel.addProp("Diff.Y",diff.y());
	}

	@Override
	public boolean editableProperty(int propertyNum) {
		return true;
	}

	@Override
	public void setNewProperty(Object newValue, int propertyNum) {
		double val = Double.parseDouble((String)newValue);
		switch(propertyNum) {
		case 0://begin.x
			begin.setX(val);
			break;
		case 1://begin.y
			begin.setY(val);
			break;
		case 2://end.x
			end.setX(val);
			break;
		case 3://end.y
			end.setY(val);
			break;
		case 4://diff.x
			end.setX(begin.x()+val);
			break;
		case 5://diff.y
			end.setY(begin.y()+val);
			break;
		}
	}

	@Override
	public String getElementType() {
		return "Rectangle";
	}
	
	public String toString() {
		return "Rectangle(begin"+begin+";end:"+end+";name:"+name+")";
	}

	@Override
	public Rectangle copy() {
		return new Rectangle(this);
	}
}
