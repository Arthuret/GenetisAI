package formula;

import java.text.ParseException;

import menu.training_editor.FormulaTypes;

/**
 * A part of a formula.
 * Contain a designator to a variable
 * @author Arthur France
 *
 */
public class VariableElement implements Element {
	private static final long serialVersionUID = 1L;
	private Variable v;

	public VariableElement(Variable v,FormulaTypes type) {
		this.v = v;
	}

	public static VariableElement parse(String s, int begin, int end,FormulaTypes type) throws ParseException {
		String el = s.substring(begin + 1, end);
		try {
			return new VariableElement(FitVariables.valueOf(el),type);
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
