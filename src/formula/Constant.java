package formula;

import java.text.ParseException;

/**
 * A part of a formula
 * Contains a constant value
 * @author Arthur France
 */
public class Constant implements Element {

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

}
