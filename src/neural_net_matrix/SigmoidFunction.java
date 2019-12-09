package neural_net_matrix;

import java.security.InvalidParameterException;

public class SigmoidFunction implements TfFunction {
	private static final long serialVersionUID = 1L;
	private transient static final SigmoidFunction f = new SigmoidFunction();//singleton
	//Created at the loading of the class
	
	private SigmoidFunction() {
		
	}
	
	/**
	 * Accessing the singleton
	 * @return The only instance of the class
	 */
	public static SigmoidFunction get() {
		return f;
	}
	

	@Override
	public float compute(float val, Object... args) throws InvalidParameterException {
		return (float)(1./(1.+Math.exp(-val)));
	}

}
