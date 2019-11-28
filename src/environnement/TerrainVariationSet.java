package environnement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object that contains a Terrain and all its configurations/variations
 * @author Arthur France
 *
 */
public class TerrainVariationSet implements Serializable{
	private static final long serialVersionUID = -1717675130939861085L;
	private Terrain t;
	private List<TerrainVariation> variations;
	public String name = "";
	
	public TerrainVariationSet(Terrain t) {
		this.t = t;
		variations = new ArrayList<>();
	}

	public Terrain getT() {
		return t;
	}

	public void setT(Terrain t) {
		this.t = t;
	}
	
	public void addVariation(TerrainVariation v) {
		variations.add(v);
	}
	
	public List<TerrainVariation> getVariations(){
		return variations;
	}
	
	public int getVariationCount() {
		return variations.size();
	}
	
	public String toString() {
		if(name == null) name = "";
		return name;
	}
	
	public void removeVariation(TerrainVariation var) {
		variations.remove(var);
	}
}
