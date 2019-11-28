package neural_net;

import java.util.ArrayList;
import java.util.List;

/**
 * A node represent a neuron. It can compute it's output based on it's input
 * links.
 * 
 * @author Arthur France
 *
 */
public class Node {
	private int id;
	private List<Link> inputs;
	private List<Link> outputs;
	private double counterWeight;

	private SummingFunction sumFct;
	protected ActivationFunction actFct;

	protected double output;

	/**
	 * The default constructor. Gives a node with the given base functions and no
	 * links The counterweight is set to a random value
	 * 
	 * @param id
	 *            The id of the node
	 * @param sumFct
	 *            The summing function to be used
	 * @param actFct
	 *            The activation function to be used
	 */
	public Node(int id, SummingFunction sumFct, ActivationFunction actFct) {
		this(id, (Math.random() * 2) - 1, sumFct, actFct);
	}

	/**
	 * The full constructor. Gives a node with the given base functions and no links
	 * Use the given counterweight
	 * 
	 * @param id
	 *            The id of the node
	 * @param counterWeight
	 *            The counterweight, which is the weight of a link to 1 source
	 * @param sumFct
	 *            The summing function to be used
	 * @param actFct
	 *            The activation function to be used
	 */
	public Node(int id, double counterWeight, SummingFunction sumFct, ActivationFunction actFct) {
		this.counterWeight = counterWeight;
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
		this.id = id;
		this.sumFct = sumFct;
		this.actFct = actFct;
	}

	/**
	 * Adds an input link to the node. This function adds the node to the link by
	 * itself.
	 * 
	 * @param l
	 *            The new Input link to add.
	 */
	public void addInputLink(Link l) {
		inputs.add(l);
		l.setOutputNode(this);
	}

	/**
	 * Adds an output link to the node. This function adds the node to the link by
	 * itself
	 * 
	 * @param l
	 *            The new Output link to add.
	 */
	public void addOutputLink(Link l) {
		outputs.add(l);
		l.setInputNode(this);
	}

	/**
	 * Returns the output value without recomputing it.
	 * 
	 * @return The output value computed by last call to computeOutput()
	 */
	public double getOutput() {
		return output;
	}

	/**
	 * Compute the output values based on the input links and the counterweight,
	 * using the base functions. The output value can be accessed by a call to
	 * getOutput()
	 */
	public void computeOutput() {
		double sum = sumFct.sum(inputs, counterWeight);
		output = actFct.computeOutput(sum);
	}

	/**
	 * The unique identifier of the node
	 * 
	 * @return The id of the node
	 */
	public int getId() {
		return id;
	}

	/**
	 * The internal list of input links. DO NOT modify this list externally.
	 * 
	 * @return The List of input links
	 */
	public List<Link> getInputs() {
		return inputs;
	}

	/**
	 * The weight of the link to 1 source
	 * 
	 * @return The counterWeight
	 */
	public double getCounterWeight() {
		return counterWeight;
	}
}