package neural_net;

/**
 * An activation function implementation using a bisigmoid function. This is a
 * singleton, use getFunction() to get the instance of the object.
 * 
 * @author Arthur France
 *
 */
public class BiSigmoidActivationFunction implements ActivationFunction {

	private static BiSigmoidActivationFunction function = new BiSigmoidActivationFunction();

	private BiSigmoidActivationFunction() {
	}

	@Override
	public double computeOutput(double sum) {
		if(sum >= 500)
			return 1;
		if(sum <= -500)
			return -1;
		double exp = Math.exp(-sum);
		return ((1 - exp) / (1 + exp));
	}

	/**
	 * This class is a singleton, because it consist only in a function, with no
	 * atributes.
	 * 
	 * @return The unique instance of the class.
	 */
	public static BiSigmoidActivationFunction getFunction() {
		return function;
	}
}
