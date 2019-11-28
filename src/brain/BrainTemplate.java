package brain;

import java.io.Serializable;

public abstract class BrainTemplate implements Serializable{
	private static final long serialVersionUID = 3282030578280118662L;
	
	public abstract int getNumberParameters();
	
	public abstract BrainType getType();
	
	public abstract BrainData generateRandomAgent();

}
