package neural_net;

/**
 * An node specialized for the ouput of the neural brain.
 * 
 * @author Arthur France
 *
 */
public class OutputNode extends Node {

	public OutputNode(int id, SummingFunction sumFct, ActivationFunction actFct) {
		super(id, sumFct, actFct);
	}

	public OutputNode(int id, double counterWeight, SummingFunction sumFct, ActivationFunction actFct) {
		super(id, counterWeight, sumFct, actFct);
	}
}
