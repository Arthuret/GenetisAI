package neural_net;

/**
 * A link is a connection between two nodes. It is used to compute the inputs of
 * the nodes base on the previous layer of nodes
 * 
 * @author Arthur France
 *
 */
public class Link {
	private int id;
	private Node inputNode = null;
	private Node outputNode = null;

	private double weight;

	/**
	 * The random constructor of the link, with no input or output node
	 * 
	 * @param id
	 *            The unique identifier of the new link
	 */
	public Link(int id) {
		this(id, (Math.random() * 2) - 1);
	}

	/**
	 * The full constructor of the link, with no input or output node
	 * 
	 * @param id
	 *            The unique identifier of the new link
	 * @param weight
	 *            The weight of the new link
	 */
	public Link(int id, double weight) {
		this.id = id;
		this.weight = weight;
	}

	/**
	 * Compute the value of the link. I doesn't compute the ouput of the input node,
	 * it just take the previously computed value
	 * 
	 * @return The output of the input node * the weight of the link
	 */
	public double getOutput() {
		return inputNode.getOutput() * weight;
	}

	/**
	 * Give the unique identifier of the link
	 * 
	 * @return The id of the link
	 */
	public int getId() {
		return id;
	}

	/**
	 * Give the input node of the link
	 * 
	 * @return The input node of the link
	 */
	public Node getInputNode() {
		return inputNode;
	}

	/**
	 * Set the input node of the link. The getOutput() function will get it's value
	 * from it. WARNING : this function don't add the link to the node. It should be
	 * called only from the node class
	 * 
	 * @param inputNode
	 *            The new input node
	 */
	public void setInputNode(Node inputNode) {
		this.inputNode = inputNode;
	}

	/**
	 * Give the output node of th link.
	 * 
	 * @return The output node of the link
	 */
	public Node getOutputNode() {
		return outputNode;
	}

	/**
	 * Set the output node of the link. WARNING : this function don't add the link
	 * to the node. It should be called only from the node class
	 * 
	 * @param outputNode
	 *            The new output node
	 */
	public void setOutputNode(Node outputNode) {
		this.outputNode = outputNode;
	}

	/**
	 * The weight is used to compute the output value
	 * 
	 * @return The weight of the link
	 */
	public double getWeight() {
		return weight;
	}
}
