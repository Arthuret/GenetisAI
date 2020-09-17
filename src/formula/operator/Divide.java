package formula.operator;

import formula.Context;
import formula.Element;

public class Divide implements Element {
	private static final long serialVersionUID = 1L;

	private Element a, b;

	public Divide(Element a, Element b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public float getValue(Context c) {
		float bv = b.getValue(c);
		if(bv == 0) return 0;
		return a.getValue(c) / b.getValue(c);
	}
	
	public String toString() {
		return "("+a.toString()+" / "+b.toString()+")";
	}

}
