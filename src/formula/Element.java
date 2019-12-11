package formula;

import java.io.Serializable;

public interface Element extends Serializable {
	public float getValue(Context c);
}
