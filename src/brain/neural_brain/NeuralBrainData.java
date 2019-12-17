package brain.neural_brain;

import java.security.InvalidParameterException;
import java.util.Random;

import brain.BrainData;
import menu.training_editor.BrainSimulationSet;
import neural_net_matrix.Limiter;
import neural_net_matrix.Matrix;
import neural_net_matrix.TfFunction;

/**
 * The datas of a functionnal neural brain
 * 
 * @author Arthur France
 */
public class NeuralBrainData implements BrainData {
	private static final long serialVersionUID = 4033880239984919552L;

	// A neural brain store all it's link wheights and bias, or parameters.
	/*
	 * The weights matrices (p) ar multiplyed with the columns of value (v) = (p*v)
	 * so p is of width x = height of v_(n) and of height y = height of v_(n+1) =>
	 * width of the input, height of the output The biases (b) are added after that
	 * =>(p*v)+b then the function if applied to all values.
	 * 
	 * Les matrices de poids (p) sont multipliées aux colonnes de valeurs (v) =>
	 * (p*v) p est donc de largeur x = hauteur de v_(n) et de hauteur y = hauteur de
	 * v_(n+1) => largeur de l'entree, hauteur de la sortie Les biais (b) sont
	 * ajoutes ensuite =>(p*v)+b Puis la fonction est appliquée sur toutes les
	 * valeurs.
	 */
	private Matrix[] weights, biases;
	private TfFunction function;
	private NeuralBrainTemplate template;
	private Matrix inputFactorMatrix;
	private Matrix outputOffset, outputFactor;

	/**
	 * Generate a neural brain with the given number of layers and layer sizes Fill
	 * them with random values between -1 and 1
	 * 
	 * @param sizes The list of the layer sizes from the number of inputs to the
	 *              number of outputs
	 */
	public NeuralBrainData(NeuralBrainTemplate tmpl, TfFunction fct, Matrix inputFactors, Matrix outputFactor,
			Matrix outputOffset, int... sizes) throws UnsupportedOperationException {
		if (sizes.length < 2)
			throw new UnsupportedOperationException("A Neural brain can't have less tha one input and one output");
		for (int i = 0; i < sizes.length; i++) {
			if (sizes[i] <= 0)
				throw new UnsupportedOperationException("A Neural brain can't have a void layer");
		}
		this.function = fct;
		this.template = tmpl;
		this.inputFactorMatrix = inputFactors;
		this.outputFactor = outputFactor;
		this.outputOffset = outputOffset;
		weights = new Matrix[sizes.length - 1];
		biases = new Matrix[sizes.length - 1];
		for (int i = 1; i < sizes.length; i++) {
			weights[i - 1] = new Matrix(sizes[i - 1], sizes[i], 1, -1);
			biases[i - 1] = new Matrix(1, sizes[i], 1, -1);
		}
	}

	/**
	 * Copy constructor
	 * 
	 * @param nbd The data to copy
	 */
	public NeuralBrainData(NeuralBrainData nbd) {
		this.function = nbd.function;
		this.template = nbd.template;
		this.inputFactorMatrix = nbd.inputFactorMatrix;
		this.outputFactor = nbd.outputFactor;
		this.outputOffset = nbd.outputOffset;
		weights = new Matrix[nbd.weights.length];
		biases = new Matrix[nbd.biases.length];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = nbd.weights[i].copy();
			biases[i] = nbd.biases[i].copy();
		}
	}

	/**
	 * Takes the inputs, process them through the brain, and retrives the output
	 * 
	 * @param inputs The input values. The number of values have to be the same as
	 *               inputs in the brain
	 * @return The output values of the brain after processing the inputs
	 */
	public float[] compute(float[] inputs) {
		Matrix temp = Matrix.getColumnMatrix(inputs).multipliyTerm(inputFactorMatrix);
		for (int i = 0; i < weights.length; i++) {
			temp = weights[i].multiply(temp).add(biases[i]);
			temp.applyFunction(function);
		}
		return temp.multipliyTerm(outputFactor).add(outputOffset).getColumn();
	}

	/**
	 * Create an independant data copy of the brain
	 * 
	 * @return A new independant brain
	 */
	public NeuralBrainData copy() {
		return new NeuralBrainData(this);
	}

	/**
	 * Mutate the brain using the parameters in the set
	 * 
	 * @param set the set containing the parameters for the mutation
	 */
	public void mutate(BrainSimulationSet set) {
		TfFunction abs = (e,a) -> absoluteMutation(e,a);
		TfFunction rel = new Limiter((e,a) -> relativeMutation(e,a), -1, 1);
		
		applyFunctionRandom(set,set.nbAbsMut,abs,set);
		applyFunctionRandom(set,set.nbRelMut,rel,set);
	}
	
	private void applyFunctionRandom(BrainSimulationSet set,int number,TfFunction f,Object...args) {
		var nbPara = template.getNumberParameters();
		var rand = new Random();
		for(var i = 0;i < number;i++) {
			var index = rand.nextInt(nbPara);
			var found = false;
			var cpt = 0;
			for(Matrix m:weights) {
				if(m.getLength()+cpt > index) {
					m.applyFunctionToIndex(f, index-cpt, args);
					found = true;
					break;
				}else {
					cpt+=m.getLength();
				}
			}
			if(!found) {
				for(Matrix m:biases) {
					if(m.getLength()+cpt > index) {
						m.applyFunctionToIndex(f, index-cpt, args);
						break;
					} else {
						cpt+=m.getLength();
					}
				}
			}
		}
	}

	/**
	 * Function applying the absolute mutation, implementing TfFunction
	 * @param val The value to mutate (not used here)
	 * @param args (not used here)
	 * @return A new random value between -1.0 and 1.0
	 */
	private float absoluteMutation(float val, Object... args) {
		Random r = new Random();
		return (r.nextFloat() * 2) - 1;
	}

	/**
	 * Function applying the relative mutation, implementing TfFunction
	 * @param val The value to mutate
	 * @param args The set containing the mutation parameters
	 * @return the value mutated
	 * @throws InvalidParameterException when the first argument is not a valid BrainSimulationSet
	 */
	private float relativeMutation(float val, Object... args) throws InvalidParameterException {
		if (args.length < 1 || !(args[0] instanceof BrainSimulationSet))
			throw new InvalidParameterException();
		var set = (BrainSimulationSet) args[0];
		var rand = new Random();
		switch(set.changeLaw) {
		case NORMAL_LAW:
			return (float) (val + (rand.nextGaussian() * set.sigma));
		case UNIFORM_LAW:
			return val + (((rand.nextFloat() * 2)-1) * set.sigma);
		}
		return val;

	}

	@Override
	public boolean cSpeed() {
		return template.isSpeed();
	}

	@Override
	public boolean cDistance() {
		return template.isDistance();
	}

	@Override
	public boolean cDirection() {
		return template.isDirection();
	}

	@Override
	public boolean cWalls() {
		return template.isWalls();
	}

	@Override
	public float sensorLimit() {
		return template.getSensorRange();
	}

	@Override
	public float getSpeedLimit() {
		return template.getMaxSpeed();
	}
}
