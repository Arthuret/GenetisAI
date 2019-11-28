package neural_net;

import java.util.ArrayList;
import java.util.List;

/**
 * A net that represent a neural brain
 * @author Arthur France
 *
 */
public class NeuralNet {
	private List<InputNode> inputs;
	private List<List<Node>> hiddenLayers;
	private List<OutputNode> outputs;
	
	private int id;
	
	/**
	 * The base constructor that gives a void brain without any node
	 * @param id The unique identifier of the net
	 */
	public NeuralNet(int id) {//empty net
		this.id = id;
		inputs = new ArrayList<>();
		hiddenLayers = new ArrayList<>();
		outputs = new ArrayList<>();
	}
	
	/**
	 * Give the hidden layers (the node layers without the inputs and outputs)
	 * @return The List containing the hidden layers
	 */
	public List<List<Node>> getHiddenLayers(){
		return hiddenLayers;
	}
	
	/**
	 * Give the list of the output nodes
	 * @return The list containing the output nodes
	 */
	public List<OutputNode> getOutputLayer(){
		return outputs;
	}
	
	/**
	 * Give the list of the input nodes
	 * @return The list containing the input nodes
	 */
	public List<InputNode> getInputLayer(){
		return inputs;
	}
	
	/**
	 * Allows update of the input values of the brain
	 * @param inputs The inputs values. If there is not enough values for all nodes, the remaining nodes will not be updated. If there is too much values, all the nodes will be updated and the rest of the values will be droped.
	 */
	public void setInputSignals(double[] inputs) {
		for(int i = 0;i < inputs.length && i < this.inputs.size();i++) {
			this.inputs.get(i).setOutput(inputs[i]);
		}
	}
	
	/**
	 * Compute successively each layer of the net to get the updated values of the output nodes
	 */
	public void update() {
		for(int i = 0;i < hiddenLayers.size();i++) {
			for(Node n:hiddenLayers.get(i)) {
				n.computeOutput();
			}
		}
		for(Node n:outputs) {
			n.computeOutput();
		}
	}
	
	/**
	 * Gives the output values of the output nodes
	 * @return The values array containing the output values of the output nodes
	 */
	public double[] getOutput() {
		double[] resp = new double[outputs.size()];
		for(int i = 0;i < outputs.size();i++) {
			resp[i] = outputs.get(i).getOutput();
		}
		return resp;
	}
	
	/**
	 * Give the unique identifier of the net
	 * @return The unique id of the net
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Generate a deep copy of the net, using the netContainer class.
	 * @param copyId The id of the new net
	 * @return An independant copy of the net
	 */
	public NeuralNet getDeepCopy(int copyId) {
		return new NetContainer(this).getNet(copyId,WeightedSumFunction.getFunction(),BiSigmoidActivationFunction.getFunction());
	}
}