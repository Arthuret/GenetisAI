package formula.operator;

import java.util.ArrayList;
import java.util.List;

import formula.Context;
import formula.Element;

public class Multiply implements Element {
	private static final long serialVersionUID = 1L;

	private List<Element> elements;

	public Multiply(Element... elements) {
		if (elements.length < 2)
			throw new UnsupportedOperationException("Cannot multiply less than two elements");
		this.elements = new ArrayList<>();
		for (Element e : elements) {
			if(e instanceof Multiply)
				this.elements.addAll(((Multiply)e).elements);
			else
				this.elements.add(e);
		}
	}

	@Override
	public float getValue(Context c) {
		float resp = 1;
		for (Element e : elements)
			resp *= e.getValue(c);
		return resp;
	}
	
	public String toString() {
		String resp = elements.get(0).toString();
		for(int i = 1;i < elements.size();i++) {
			resp+=" * "+elements.get(i);
		}
		return "("+resp+")";
	}
}
