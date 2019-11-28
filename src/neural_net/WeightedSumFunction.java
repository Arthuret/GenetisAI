package neural_net;

import java.util.List;

/**
 * A summing function implementation using a weighted system. This is a
 * singleton, use getFunction() to get the instance of the object
 * 
 * @author Arthur France
 *
 */
public class WeightedSumFunction implements SummingFunction {

	private static WeightedSumFunction function = new WeightedSumFunction();

	@Override
	public double sum(List<Link> inputs, double counterWeight) {
		double resp = 0;

		for(Link l : inputs) {
			resp += l.getOutput();
		}

		resp += counterWeight;

		return resp;
	}

	private WeightedSumFunction() {
	}

	/**
	 * This class is a singleton, because it consist only in a single function, with
	 * no atributes.
	 * 
	 * @return The unique instance of the class
	 */
	public static WeightedSumFunction getFunction() {
		return function;
	}

}
