package menu.environnement_editor;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import environnement.Obstacle;
import environnement.Point;
import environnement.TerrainElement;

@SuppressWarnings("serial")
public class ListModel extends AbstractTableModel {

	private List<Obstacle> obs = null;
	private List<Point> pts = null;
	private final String[] enteteListe = { "Elements", "Type", "Name" };

	private EnvMenu parentMenu;

	public ListModel(EnvMenu parent) {
		super();
		this.parentMenu = parent;
	}

	@Override
	public int getRowCount() {
		int resp = 0;
		if (obs != null)
			resp += obs.size();
		if (pts != null)
			resp += pts.size();
		return resp;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		TerrainElement elem = getElementAt(rowIndex);
		if (elem != null) {
			switch (columnIndex) {
			case 0:
				return elem.getElementCategory();
			case 1:
				return elem.getElementType();
			case 2:
				return elem.getName();
			default:
				return null;
			}
		} else
			return null;
	}

	private TerrainElement getElementAt(int index) {
		int lenPts = (pts == null) ? 0 : pts.size();
		int lenObs = (obs == null) ? 0 : obs.size();
		return (index < lenObs) ? obs.get(index) : (index < (lenPts + lenObs)) ? pts.get(index - lenObs) : null;
	}

	@Override
	public String getColumnName(int numCol) {
		return enteteListe[numCol];
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return (col == 2);
	}

	@Override
	public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
		if (columnIndex == 2) {
			getElementAt(rowIndex).setName((String) newValue);
			parentMenu.repaint();
			parentMenu.historyPush();
		}
	}

	public void setList(List<Obstacle> obs, List<Point> pts) {
		this.obs = obs;
		this.pts = pts;
	}
}
