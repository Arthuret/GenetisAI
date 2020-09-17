package formula.operator;

import formula.Constant;
import formula.Context;
import formula.Element;

public class Negative implements Element {
	private static final long serialVersionUID = 1L;
	private Element e;
	
	private Negative(Element e) {
		this.e = e;
	}
	
	public static Element negative(Element e) {//the negative of an element not always require the negative element
		if(e instanceof Negative)// -- > +
			return ((Negative) e).e;
		else if(e instanceof Constant)
			return ((Constant) e).getNegative();
		else if(e instanceof Add)
			return ((Add) e).getNegative();
		else
			return new Negative(e);
	}

	@Override
	public float getValue(Context c) {
		return -e.getValue(c);
	}
	
	public String toString() {
		return "-"+e;
	}
	
	public String toStringWN() {
		return e.toString();
	}
	
	public boolean isNegative() {
		return true;
	}
	
	public boolean hasOnlyConstOrVar() {
		return e.hasOnlyConstOrVar();
	}

}
