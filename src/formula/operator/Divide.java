package formula.operator;

import formula.Context;
import formula.Element;

public class Divide implements Element {

	private Element a, b;

	public Divide(Element a, Element b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public float getValue(Context c) {
		float bv = b.getValue(c);
		return a.getValue(c) / ((Math.abs(bv) >= 1) ? bv : Math.signum(bv));
	}
	
	public String toString() {
		return "("+a.toString()+" / "+b.toString()+")";
	}

}
