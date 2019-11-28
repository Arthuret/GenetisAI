package neural_net;

/**
 * A node specialized for the input of the neural brain.
 * 
 * @author Arthur France
 *
 */
public class InputNode extends Node {
	public InputNode(int id, SummingFunction sumFct, ActivationFunction actFct) {
		super(id, 0, sumFct, actFct);
		if(sumFct == null || actFct == null)
			System.out.println("debug");
	}

	/**
	 * Change directly the output value of the node
	 * 
	 * @param v
	 *            The new output value
	 */
	public void setOutput(double v) {
		this.output = v;
	}

	/**
	 * Compute the output value of the node from the given inputValue based on the
	 * activation function
	 * 
	 * @param inputValue
	 *            The input value to be considered by the cativation function
	 */
	public void computeOutput(double inputValue) {
		this.output = actFct.computeOutput(inputValue);
	}

	/**
	 * Compute the output value of the node linearly from a value and it's range
	 * 
	 * @param inputValue
	 *            The value to consider
	 * @param max
	 *            The maximum value the inputValue could take (should not change
	 *            trough the evolution)
	 * @param min
	 *            The minimum value the inputValue could take (should not change
	 *            trough the evolution)
	 */
	public void computeOutputLinear(double inputValue, double max, double min) {
		// output have to be within -1 and 1
		this.output = (((inputValue - min) / (max - min)) * 2) - 1;
	}
}
