package formula;

import java.text.ParseException;

/**
 * A part of a formula
 * Contains a constant value
 * @author Arthur France
 */
public class Constant implements Element {
	private static final long serialVersionUID = 1L;

	private float constant;
	private String repr;
	
	public Constant(float value) {
		this(value,Float.toString(value));
	}

	public Constant(float value,String repr) {
		this.constant = value;
		this.repr = repr;
	}
	
	public static Constant parse(String s,int begin,int end) throws ParseException {
		String val = s.substring(begin, end);
		String st = val.replaceAll("_","");
		try {
			return new Constant(Float.parseFloat(st),val);
		}catch(NumberFormatException e) {
			throw new ParseException(e.getMessage(),begin);
		}
	}

	@Override
	public float getValue(Context c) {
		return constant;
	}
	
	public String toString() {
		return repr;
	}
	
	public Constant getNegative() {
		Constant resp = new Constant(-constant);
		if(repr.charAt(0) == '-')
			resp.repr = repr.substring(1);
		else
			resp.repr = "-"+repr;
		return resp;
	}
	
	public boolean isNegative() {
		return constant < 0;
	}
	
	public String toStringWN() {
		if(repr.charAt(0) == '-')
			return repr.substring(1);
		return repr;
	}
	
	public boolean hasOnlyConstOrVar() {
		return true;
	}
}
