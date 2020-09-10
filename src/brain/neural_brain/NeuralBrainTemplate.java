package brain.neural_brain;

import brain.BrainData;
import brain.BrainTemplate;
import brain.BrainType;
import neural_net_matrix.Matrix;
import neural_net_matrix.SigmoidFunction;

/**
 * The configuration of a neural brain. Can generate a new random
 * NeuralBrainData. Does not contain the datas of a functionnal brain
 * 
 * @author Arthur France
 */
public class NeuralBrainTemplate extends BrainTemplate {
	private static final long serialVersionUID = 8479513160852697960L;

	private int[] internalLayers;
	private float maxSpeed, maxAccel, sensorRange;
	private boolean distance, direction, speed, walls;
	private int nbMemory;
	private transient Matrix inputMatrix = null;
	private transient Matrix outputFactor = null;
	private transient Matrix outputOffset = null;

	public NeuralBrainTemplate(int[] internalLayers, float maxSpeed, float maxAccel, float sensorRange,
			boolean distance, boolean direction, boolean speed, boolean walls, int nbMemory) {
		super();
		this.internalLayers = internalLayers;
		this.maxSpeed = maxSpeed;
		this.maxAccel = maxAccel;
		this.sensorRange = sensorRange;
		this.distance = distance;
		this.direction = direction;
		this.speed = speed;
		this.walls = walls;
	}

	public int[] getInternalLayers() {
		return internalLayers;
	}

	public void setInternalLayers(int[] internalLayers) {
		this.internalLayers = internalLayers;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public float getMaxAccel() {
		return maxAccel;
	}

	public void setMaxAccel(float maxAccel) {
		this.maxAccel = maxAccel;
	}

	public float getSensorRange() {
		return sensorRange;
	}

	public void setSensorRange(float sensorRange) {
		this.sensorRange = sensorRange;
	}

	public boolean isDistance() {
		return distance;
	}

	public void setDistance(boolean distance) {
		this.distance = distance;
	}

	public boolean isDirection() {
		return direction;
	}

	public void setDirection(boolean direction) {
		this.direction = direction;
	}

	public boolean isSpeed() {
		return speed;
	}

	public void setSpeed(boolean speed) {
		this.speed = speed;
	}

	public boolean isWalls() {
		return walls;
	}

	public void setWalls(boolean walls) {
		this.walls = walls;
	}

	public int getNbMemory() {
		return nbMemory;
	}

	public void setNbMemory(int nb) {
		nbMemory = nb;
	}

	public String toString() {
		return "NeuralBrainTemplate:S" + maxSpeed + "a" + maxAccel + "r" + sensorRange + "m" + nbMemory + "D" + distance
				+ "d" + direction + "s" + speed + "w" + walls;
	}

	@Override
	public int getNumberParameters() {
		if (internalLayers.length == 0)
			return 0;
		int resp = 0;
		int nbIn = getNumberInputs();
		resp += internalLayers[0] * (nbIn + 1);
		for (int i = 1; i < internalLayers.length; i++) {
			resp += (internalLayers[i - 1] + 1) * internalLayers[i];
		}
		resp += (internalLayers[internalLayers.length - 1] + 1) * getNumberOutputs();// 2 outputs
		return resp;
	}

	@Override
	public BrainType getType() {
		return BrainType.NEURAL_BRAIN;
	}

	/**
	 * @return The number of inputs of the brain
	 */
	private int getNumberInputs() {
		int nbIn = 0;
		if (distance)
			nbIn += 1;
		if (direction)
			nbIn += 2;
		if (speed)
			nbIn += 2;
		if (walls)
			nbIn += 8;
		nbIn += nbMemory;
		return nbIn;
	}
	
	/**
	 * @return The number of inputs of the brain
	 */
	private int getNumberOutputs() {
		return nbMemory + 2;
	}

	@Override
	public BrainData generateRandomAgent() {
		int[] sizes = new int[internalLayers.length + 2];// we add the input/output layers
		sizes[0] = getNumberInputs();
		for (int i = 0; i < internalLayers.length; i++) {
			sizes[i + 1] = internalLayers[i];
		}
		sizes[sizes.length - 1] = getNumberOutputs();
		if (inputMatrix == null)
			generateInputOutputMatrix();
		return new NeuralBrainData(this, SigmoidFunction.get(), inputMatrix, outputFactor, outputOffset, sizes);
	}

	/**
	 * Generate matrices used to standardize the inputs and outputs
	 */
	private void generateInputOutputMatrix() {
		float[] resp = new float[getNumberInputs()];
		int nb = 0;
		float temp = 1f / sensorRange;
		if (walls) {
			for (int i = 0; i < 8; i++) {
				resp[i] = temp;
			}
			nb = 8;
		}
		if (direction) {
			resp[nb++] = temp;
			resp[nb++] = temp;
		}
		if (distance)
			resp[nb++] = temp;
		if (speed) {
			resp[nb++] = temp;
			resp[nb++] = temp;
		}
		inputMatrix = Matrix.getColumnMatrix(resp);

		resp = new float[2];
		temp = 2 * maxAccel;
		resp[0] = temp;
		resp[1] = temp;
		outputFactor = Matrix.getColumnMatrix(resp);
		resp[0] = -maxAccel;
		resp[1] = -maxAccel;
		outputOffset = Matrix.getColumnMatrix(resp);
	}
}
