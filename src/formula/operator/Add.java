package formula.operator;

import java.util.ArrayList;
import java.util.List;

import formula.Context;
import formula.Element;

public class Add implements Element {
	private static final long serialVersionUID = 1L;

	private List<Element> elements;

	public Add(Element... elements) {
		if (elements.length < 2)
			throw new UnsupportedOperationException("Cannot add less than two elements");
		this.elements = new ArrayList<>();
		for (Element e : elements) {
			if (e instanceof Add)
				this.elements.addAll(((Add) e).elements);
			else
				this.elements.add(e);
		}
	}

	@Override
	public float getValue(Context c) {
		float resp = 0;
		for (Element e : elements)
			resp += e.getValue(c);
		return resp;
	}

	public String toString() {
		String resp = elements.get(0).toString();
		for (int i = 1; i < elements.size(); i++) {
			if (elements.get(i).isNegative())
				resp += " - " + elements.get(i).toStringWN();
			else
				resp += " + " + elements.get(i);
		}
		return "(" + resp + ")";
	}
	
	public boolean hasOnlyConstOrVar() {
		for(Element e:elements) {
			if(!e.hasOnlyConstOrVar())
				return false;
		}
		return true;
	}
	
	public Add getNegative() {
		Element[] resp = new Element[elements.size()];
		for(int i = 0;i < elements.size();i++)
			resp[i] = Negative.negative(elements.get(i));
		return new Add(resp);
	}
}
