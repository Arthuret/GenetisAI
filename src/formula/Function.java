package formula;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Function implements Element {
	private static final long serialVersionUID = 1L;

	private Element[] params;
	private Functions f;

	public Function(String function, int index, Element... param) throws ParseException {
		Functions f = Functions.getByName(function);
		if (f == null)
			throw new ParseException("Unknown function name", index);
		if (!f.nbParamValid(param.length))
			throw new ParseException(f.getName() + ": invalid number of parameters (" + param.length + ")", index);
		this.params = param;
		this.f = f;
	}

	@Override
	public float getValue(Context c) {
		switch (f) {
		case EXP:
			return (float) (Math.exp(params[0].getValue(c)));
		case ABS:
			return Math.abs(params[0].getValue(c));
		case POW:
			var v = (float) Math.pow(params[0].getValue(c), params[0].getValue(c));
			if (v != Float.NaN)
				return v;
			break;// return 0;
		case SQRT:
			return (float) Math.sqrt(Math.abs(params[0].getValue(c)));
		case CEIL:
			return (float) Math.ceil(params[0].getValue(c));
		case FLOOR:
			return (float) Math.floor(params[0].getValue(c));
		case ROUND:
			return (float) Math.round(params[0].getValue(c));
		case MIN:
			return maxmin(false, c);
		case MAX:
			return maxmin(true, c);
		case LN:
			return (float) Math.log(params[0].getValue(c));
		case LOG:
			return (float) Math.log10(params[0].getValue(c));
		}
		return 0;
	}

	private float maxmin(boolean max, Context c) {
		float resp = params[0].getValue(c);
		for (Element e : params) {
			float v = e.getValue(c);
			if ((max && v > resp) || (!max && v < resp))
				resp = v;
		}
		return resp;
	}

	public String toString() {
		String resp = f.getName() + "(";
		for (int i = 0; i < params.length; i++) {
			resp += params[i].toString();
			if (params.length > i + 1)
				resp += ",";
		}
		resp += ")";
		return resp;
	}

	public static Element parse(String s, int begin, int end, FormulaTypes type) throws ParseException {
		String name;
		for (int i = begin; i < end; i++) {
			if (s.charAt(i) == '(') {
				Element[] params;
				name = s.substring(begin, i);
				if (s.charAt(i + 1) != ')') {
					params = parseParameters(s, i, end, type);
				} else {
					params = new Element[0];
				}
				return new Function(name, begin, params);
			}
		}
		throw new ParseException("Function error : expecting '('", begin);
	}

	private static Element[] parseParameters(String s, int begin, int end, FormulaTypes type) throws ParseException {
		List<Element> params = new ArrayList<>();
		int e, b = begin + 1;
		while (b < end) {
			e = Formula.searchNextComma(s, b, end - 1);
			if (e != -1) {// comma found
				params.add(Formula.parse(s, b, e, type));
				b = e + 1;
			} else {// no comma found, last parameter
				params.add(Formula.parse(s, b, end - 1, type));
				break;
			}
		}
		return params.toArray(new Element[0]);

	}

}
