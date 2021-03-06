package formula;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import formula.operator.*;

/**
 * A simple mathemaic parsing class used to contain the fitness expression.
 * 
 * @author Arthur France
 */
public class Formula implements Serializable {
	private static final long serialVersionUID = 1L;
	private Element rootElement;

	/**
	 * Parse s into a Formula, usable as a fitness function, by providing a Context.
	 * 
	 * @param s the String to parse
	 * @return The parsed Formula
	 * @throws ParseException If the provided String doesn't respect the synthax
	 */
	public static Formula parse(String s, FormulaTypes type) throws ParseException {
		return new Formula(parse(s, 0, s.length(), type));
	}

	/**
	 * Parse a sub-block into an Element
	 * 
	 * @param s     The full String to parse
	 * @param begin The begining (included) of the sub-block
	 * @param end   The ending (excluded) of the sub-block
	 * @return The parsed Element
	 * @throws ParseException if the String doesn't respect the synthax
	 */
	public static Element parse(String s, int begin, int end, FormulaTypes type) throws ParseException {// parse one
																										// level
		// an element can be another sub-block, a number, a variable, or an operation
		int cpt = begin;
		List<Element> elements = new ArrayList<>();
		List<Character> signs = new ArrayList<>();
		if (s.length() == 0)
			throw new ParseException("Empty input", 0);
		if (begin == end)
			throw new ParseException("Empty block", begin);
		do {
			// extracting element
			int d = searchEndBlock(s, cpt, end);
			elements.add(parseBlock(s, cpt, d, type));
			cpt = d;
			if (cpt < end) {
				// extracting sign
				char c = s.charAt(cpt);
				if (c == ')')
					throw new ParseException("Unmatched parenthesis", cpt);
				if (c != '+' && c != '-' && c != '*' && c != '/')
					throw new ParseException("Unrecognized operator", cpt);
				signs.add(c);
				cpt++;
			}
		} while (cpt < end);
		if (elements.size() != (signs.size() + 1))// signs are between elements. so nbElements = nbSigns+1
			throw new ParseException("Element expected", end);
		manageMultiply(elements, signs);// combine elements arround * and / signs into corresponding Element
		try {
			manageAdd(elements, signs);// same with remaining + and - signs
			// after combining all signs, there should be only one element remaining
			if (elements.size() != 1 || signs.size() != 0)
				throw new ParseException("Unknown parsing error", 0);
			return elements.get(0);
		} catch (ParseException e) {
			throw new ParseException(e.getMessage(), begin);
		}
	}

	static int searchNextComma(String s, int begin, int end) throws ParseException {
		if (s.charAt(begin) == ',')
			return begin;
		for (int i = begin; i < end; i++) {
			switch (s.charAt(i)) {
			case ',':
				return i;
			case '+':
			case '-':
			case '*':
			case '/':
				continue;
			default:
				i = searchEndBlock(s, i, end) - 1;
				break;
			}
		}
		return -1;
	}

	/**
	 * Will remove all * and / signs by replacing the operands by an Element
	 * 
	 * @param elements The list of elements on the current level
	 * @param signs    The list of signs separating the elements
	 */
	private static void manageMultiply(List<Element> elements, List<Character> signs) {
		int i = 0;
		while (i < signs.size()) {
			char c = signs.get(i);
			if (c == '*') {
				Element t = new Multiply(elements.get(i), elements.get(i + 1));
				elements.remove(i);
				elements.remove(i);// the list has been shifted due to the precedent remove
				signs.remove(i);
				elements.add(i, t);
			} else if (c == '/') {
				Element t = new Divide(elements.get(i), elements.get(i + 1));
				elements.remove(i);
				elements.remove(i);
				signs.remove(i);
				elements.add(i, t);
			} else
				i++;
		}
	}

	/**
	 * Will remove all + and - signs by replacing the operands by an Element ! Will
	 * not respect the * / operators priority. Have to be called after
	 * manageMultiply so that there isn't any * or / signs anymore
	 * 
	 * @param elements The list of elements on the current level
	 * @param signs    The list of signs separating the elements
	 * @throws ParseException
	 */
	private static void manageAdd(List<Element> elements, List<Character> signs) throws ParseException {
		while (!signs.isEmpty()) {
			char c = signs.get(0);
			if (c == '+') {
				Element t = new Add(elements.get(0), elements.get(1));
				elements.remove(0);
				elements.remove(0);
				signs.remove(0);
				elements.add(0, t);
			} else if (c == '-') {// negting the element, and throwing it back to '+'
				Element t = Negative.negative(elements.get(1));
				elements.remove(1);
				elements.add(1, t);
				signs.remove(0);
				signs.add(0, '+');
			} else
				throw new ParseException("Signs extraction error", -1);
		}
	}

	/**
	 * Delegate the parsing to the appropriate parser
	 * 
	 * @param s     the String to parse
	 * @param begin the begining (included) of the sub-block
	 * @param end   the ending (excluded) of the sub-block
	 * @return The returned Element by the selected parsers
	 * @throws ParseException When the string doesn't respect the synthax
	 */
	private static Element parseBlock(String s, int begin, int end, FormulaTypes type) throws ParseException {
		if (begin == end)
			throw new ParseException("Empty block", begin);
		if (begin > end)
			throw new ParseException("Negative size block", begin);
		char c = s.charAt(begin);
		if (c == '(')
			return parse(s, begin + 1, end - 1, type);
		else if (c == '$')
			return VariableElement.parse(s, begin, end, type);
		else if (Character.isDigit(c))
			return Constant.parse(s, begin, end);
		else if (c == '-')
			return Negative.negative(parseBlock(s, begin + 1, end, type));
		else if (Character.getType(c) == Character.LOWERCASE_LETTER)
			return Function.parse(s, begin, end, type);
		throw new ParseException("Unexpected element:'" + c + "'", begin);
	}

	private static int searchEndBlock(String s, int begin, int end) throws ParseException {
		char c = s.charAt(begin);
		if (c == '-')
			return searchEndBlock(s, begin + 1, end);
		if (c == '(')
			return searchEndParenthesis(s, begin, end);
		else if (c == '$')
			return searchEndVariable(s, begin, end);
		else if (Character.isDigit(c))
			return searchEndNumber(s, begin, end);
		else if (Character.getType(c) == Character.LOWERCASE_LETTER)
			return searchEndFunction(s, begin, end);
		throw new ParseException("Unexpected element:'" + c + "'", begin);
	}

	private static int searchEndFunction(String s, int begin, int end) throws ParseException {
		int i = begin;
		while (s.charAt(i) != '(') {
			i++;
			if (i >= end)
				throw new ParseException("'(' expected", end);
		}
		return searchEndParenthesis(s, i, end);
	}

	private static int searchEndNumber(String s, int begin, int end) {
		int i = begin;
		while (i < end && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.' || s.charAt(i) == '_'))
			i++;
		return i;
	}

	private static int searchEndVariable(String s, int begin, int end) {
		int i = begin + 1;
		while (i < end && (Character.isAlphabetic(s.charAt(i)) || s.charAt(i) == '_'))
			i++;
		return i;
	}

	private static int searchEndParenthesis(String s, int begin, int end) throws ParseException {
		int i = begin;
		int cpt = 1;
		while (cpt > 0 && i < end - 1) {
			i++;
			char c = s.charAt(i);
			if (c == '(')
				cpt++;
			if (c == ')')
				cpt--;
		}
		if (cpt != 0)
			throw new ParseException("\")\" expected", end);
		return i + 1;
	}

	private Formula(Element root) {
		this.rootElement = root;
	}

	/**
	 * Compute the formula result using the given context
	 * 
	 * @param c the context to get the variables values
	 * @return The result of the equation
	 */
	public float getValue(Context c) {
		return rootElement.getValue(c);
	}

	public String toString() {
		return rootElement.toString();
	}

	public static void main(String[] args) {// debug purposes
		String f = "10.5/-22+((-(5+$DISTANCE)))*$NB_STEPS-(1+2-3+pow(4,2+2))";
		// String f = "(1*2*3*4/5*6/7*8*9)-(-5)";
		// String f = "pow(4,4)";
		try {
			Formula form = Formula.parse(f, FormulaTypes.FITNESS);
			System.out.println(form + " OK");
			// System.out.println(form.getValue(null));
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			System.err.println(f);
			for (int i = 0; i < e.getErrorOffset(); i++)
				System.err.print(" ");
			System.err.println("^");
		}
	}
}
