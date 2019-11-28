package menu.training_editor;

import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import environnement.TerrainVariation;
import environnement.TerrainVariationSet;

public class TerrainTreeModel implements TreeModel {
	
	private List<TerrainVariationSet> terrainSets;
	
	public TerrainTreeModel(List<TerrainVariationSet> terrainSets) {
		this.terrainSets = terrainSets;
	}

	@Override
	public Object getRoot() {
		return "Terrains";//(terrainSets.size() != 0)?"Terrains":null;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if(parent instanceof String) {//root
			if(index >= 0 && index < terrainSets.size())
				return terrainSets.get(index);
			else return null;
		}else if(parent instanceof TerrainVariationSet) {
			TerrainVariationSet set = (TerrainVariationSet) parent;
			if(index >= 0 && index < set.getVariationCount())
				return set.getVariations().get(index);
			else return null;
		}else return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if(parent instanceof String) {//root
			return terrainSets.size();
		}else if(parent instanceof TerrainVariationSet) {
			TerrainVariationSet set = (TerrainVariationSet) parent;
			return set.getVariationCount();
		}else return 0;
	}

	@Override
	public boolean isLeaf(Object node) {
		return (node instanceof TerrainVariation);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		//non editable
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent instanceof String)
			return terrainSets.indexOf(child);
		else if(parent instanceof TerrainVariationSet)
			return ((TerrainVariationSet)parent).getVariations().indexOf(child);
		else return -1;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		//non utilise
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		//non utilise
	}
}
