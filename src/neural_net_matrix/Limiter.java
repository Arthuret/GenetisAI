package neural_net_matrix;

import java.security.InvalidParameterException;

/**
 * A limiter function. Used to limit the output of another function
 * @author Arthur France
 *
 */
public class Limiter implements TfFunction {
	
	private TfFunction f;
	private float min,max;
	
	/**
	 * A limiter function
	 * @param f the function to limit
	 * @param min The minimum allowed value
	 * @param max the maximum allowed value
	 */
	public Limiter(TfFunction f, float min, float max) {
		super();
		this.f = f;
		this.min = min;
		this.max = max;
	}


	@Override
	public float compute(float val, Object... args) throws InvalidParameterException {
		return Math.min(max, Math.max(min,f.compute(val, args)));
	}

}
