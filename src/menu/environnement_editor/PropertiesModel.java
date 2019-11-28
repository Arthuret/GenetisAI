package menu.environnement_editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import environnement.TerrainElement;

@SuppressWarnings("serial")
public class PropertiesModel extends AbstractTableModel {
	private TerrainElement element = null;
	
	private final String[] headersObj = {"Properties","Values"};
	
	private List<Couple> propMap = new ArrayList<>();
	
	private EnvMenu parentMenu;
	
	public PropertiesModel(EnvMenu parent) {
		element = null;
		this.parentMenu = parent;
		updateMap();
	}
	
	@Override
	public int getRowCount() {
		return propMap.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(element != null) {
			Couple a = propMap.get(rowIndex);
			return (columnIndex == 0)?a.getKey():a.getValue();
		}else {
			return null;
		}
	}
	
	@Override
	public String getColumnName(int numCol) {
		return headersObj[numCol];
	}
	
	@Override
	public boolean isCellEditable(int row,int col) {
		if(col == 1) {
			if(row == 2)//Nom
				return true;
			if(row >= 3)
				return this.element.editableProperty(row-3);//Attention 3 = nombre de prop ajoutees dans updateMap
		}
		return false;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(columnIndex == 1) {
			if(rowIndex == 2 && aValue instanceof String)
				element.setName((String)aValue);
			else if(rowIndex >= 3)
				element.setNewProperty(aValue, rowIndex-3);//Attention 3 = nombre de prop ajoutees dans updateMap
		}
		parentMenu.repaint();
	}
	
	public void changeElement(TerrainElement element) {
		this.element = element;
		updateMap();
	}
	
	private void updateMap() {
		propMap.clear();
		if(element != null) {
			addProp("Element",element.getElementCategory());
			addProp("Type",element.getElementType());
			addProp("Nom",element.getName());
			
			element.genProperties(this);
		}
	}
	
	public void addProp(String key,Object value) {
		propMap.add(new Couple(key,value));
	}
	
	private class Couple {
		private String key;
		private Object value;
		
		public Couple(String key,Object value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}
	}
}
