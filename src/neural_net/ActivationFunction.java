package neural_net;

/**
 * A function that take an input in a specific range and transform it in the
 * output range of -1:1 or 0:1, not necessarily linearly
 * 
 * @author Arthur France
 *
 */
public interface ActivationFunction {

	/**
	 * Compute an output with the sum previously computed by the Summing function
	 * 
	 * @param sum
	 *            The sum computed by the summing function
	 * @return The output computed
	 */
	public double computeOutput(double sum);
}