package formula;

import java.text.ParseException;

/**
 * A part of a formula.
 * Contain a designator to a variable
 * @author Arthur France
 *
 */
public class VariableElement implements Element {

	private Variables v;

	public VariableElement(Variables v) {
		this.v = v;
	}

	public static VariableElement parse(String s, int begin, int end) throws ParseException {
		String el = s.substring(begin + 1, end);
		try {
			return new VariableElement(Variables.valueOf(el));
		} catch (IllegalArgumentException e) {
			throw new ParseException("Unrecognized variable", begin);
		}
	}

	@Override
	public float getValue(Context c) {
		return c.getVariable(v);
	}

	public String toString() {
		return "$" + v;
	}

}
