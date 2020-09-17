package formula;

import java.io.Serializable;

public interface Element extends Serializable {
	public float getValue(Context c);

	public default boolean isNegative() {
		return false;
	}

	/**
	 * If the return value of isNegative is true, then this method have to return
	 * the value without a '-' prefix
	 * 
	 * @return the value withour a '-' prefix
	 */
	public default String toStringWN() {
		return this.toString();
	}
	
	public default boolean hasOnlyConstOrVar() {
		return false;
	}
}
