package formula.operator;

import formula.Context;
import formula.Element;

public class Substract implements Element {

	private Element a, b;

	public Substract(Element a, Element b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public float getValue(Context c) {
		return a.getValue(c) - b.getValue(c);
	}
	
	public String toString() {
		return "("+a.toString()+" - "+b.toString()+")";
	}

}
