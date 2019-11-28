package neural_net;

import java.util.List;

/**
 * Compute the sum of the inputs
 * 
 * @author Arthur France
 *
 */
public interface SummingFunction {

	/**
	 * Compute the sum of the inputs
	 * 
	 * @param inputs
	 *            The list of the input links of the node
	 * @param counterWeight
	 *            The weight of the link to 1 source
	 * @return The computed sum of the inputs
	 */
	public double sum(List<Link> inputs, double counterWeight);
}
