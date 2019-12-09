package neural_net_matrix;

import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * A generic interface used to describe a mathematical operation from a float to a float
 * @author Arthur France
 *
 */
public interface TfFunction extends Serializable{

	/**
	 * Compute the given value with a specific math function
	 * @param val the input value
	 * @return f(val)
	 */
	public float compute(float val,Object...args) throws InvalidParameterException;
}
