package neural_net;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The 3 dimentional representation of a feed forward neural net. This class can
 * generate fully functionnal neural nets, deep duplicate them, and store them
 * 
 * @author Arthur France
 *
 */
public class NetContainer implements Serializable{
	private static final long serialVersionUID = -3513764660892643780L;
	
	private double[][][] weights;// [layerNumber][nodeNumber][wightNumber]
	// c'est tout ce qu'il faut pour representer un reseau feed forward

	/**
	 * Create a representation of the given net
	 * 
	 * @param net
	 *            The net to represent
	 */
	public NetContainer(NeuralNet net) {
		List<List<Node>> hiddenLayers = net.getHiddenLayers();
		List<OutputNode> outputLayer = net.getOutputLayer();

		weights = new double[hiddenLayers.size() + 1][][];// on cree une matrice par layer

		for(int i = 0; i < hiddenLayers.size(); i++) {// pour chaque layer (sauf l'output)
			weights[i] = new double[hiddenLayers.get(i).size()][];// on cree une colonne par node
			for(int j = 0; j < hiddenLayers.get(i).size(); j++) {// pour chaque node
				List<Link> links = hiddenLayers.get(i).get(j).getInputs();// on recupere les poids des liens
				weights[i][j] = new double[links.size() + 1];// +1 pour le contrepoids
				weights[i][j][0] = hiddenLayers.get(i).get(j).getCounterWeight();// on memorise le contrepoid
				for(int k = 0; k < links.size(); k++) {// pour chaque link
					weights[i][j][k + 1] = links.get(k).getWeight();// on en memorise le poids
				}
			}
		}

		// output layer
		weights[weights.length - 1] = new double[outputLayer.size()][];
		for(int j = 0; j < outputLayer.size(); j++) {// pour chaque node
			List<Link> links = outputLayer.get(j).getInputs();// on recupere les poids des liens
			weights[weights.length - 1][j] = new double[links.size() + 1];// +1 pour le contrepoids
			weights[weights.length - 1][j][0] = outputLayer.get(j).getCounterWeight();// on memorise le contrepoid
			for(int k = 0; k < links.size(); k++) {// pour chaque link
				weights[weights.length - 1][j][k + 1] = links.get(k).getWeight();// on en memorise le poids
			}
		}
	}

	/**
	 * Generate a new functionnal neural net based on the stored representation
	 * 
	 * @param id
	 *            The id of the new net
	 * @param sumFct
	 *            The summing function to use in the net
	 * @param actFct
	 *            The activation funtion to use in the net
	 * @return The new functional neural net
	 */
	public NeuralNet getNet(int id, SummingFunction sumFct, ActivationFunction actFct) {
		NeuralNet net = new NeuralNet(id);
		int cId = 0;

		List<InputNode> inputs = net.getInputLayer();
		List<List<Node>> hiddenLayers = net.getHiddenLayers();
		List<OutputNode> outputs = net.getOutputLayer();

		for(int i = 0; i < weights[0][0].length - 1; i++) {// premiere layer, premiere node, le nombre d'inputs = le
															// nombre d'inputs du reseau
			inputs.add(new InputNode(cId++, sumFct, actFct));
		}

		List<Node> previousLayer = new ArrayList<>();
		previousLayer.addAll(inputs);

		for(int i = 0; i < weights.length - 1; i++) {// pour tout les hidden layers
			List<Node> thisLayer = new ArrayList<>();
			for(int j = 0; j < weights[i].length; j++) {// pour chaque node de la layer
				Node n = new Node(cId++, weights[i][j][0], sumFct, actFct);
				thisLayer.add(n);// on cree la node avec son counterweight
				for(int k = 1; k < weights[i][j].length; k++) {// pour chaque link de la node (les poids commencent a 1
																// a cause du counterweight)
					Link temp = new Link(cId++, weights[i][j][k]);// on cree le link avec sont poids
					n.addInputLink(temp);
					previousLayer.get(k - 1).addOutputLink(temp);
				}
			}
			hiddenLayers.add(thisLayer);
			previousLayer = thisLayer;
		}

		// outputLayer
		int i = weights.length - 1;
		for(int j = 0; j < weights[i].length; j++) {// pour chaque node de la layer
			OutputNode n = new OutputNode(cId++, weights[i][j][0], sumFct, actFct);
			outputs.add(n);// on cree la node avec son counterweight
			for(int k = 1; k < weights[i][j].length; k++) {// pour chaque link de la node (les poids commencent a 1 a
															// cause du counterweight)
				Link temp = new Link(cId++, weights[i][j][k]);// on cree le link avec sont poids
				n.addInputLink(temp);
				previousLayer.get(k - 1).addOutputLink(temp);
			}
		}

		return net;
	}

	/**
	 * Create a NetContainer based on the passed template (number of layers, size of
	 * each layer) with random weights.
	 * 
	 * @param template
	 *            The template of the network : a vector containing the number of
	 *            node in each layer. the size of the vector is used for the number
	 *            of hidden layers.
	 */
	public NetContainer(int[] template) {
		weights = new double[template.length - 1][][];//on cree un tableau par layer a memoriser (donc toutes sauf l'input)
		for(int i = 0; i < template.length - 1; i++) {// pour chaque layer sauf l'input
			weights[i] = new double[template[i + 1]][];//on cree les tableau de poids des nodes
			for(int j = 0; j < template[i + 1]; j++) {// pour chaque node
				weights[i][j] = new double[template[i]+1];//on cree une case pour chaque weight (en comptant le counterweight)
				for(int k = 0; k <= template[i]; k++) {// pour chaque link
					weights[i][j][k] = (Math.random() * 2) - 1;
				}
			}
		}
	}
	
	public double[][][] getWeights(){
		return weights;
	}
}