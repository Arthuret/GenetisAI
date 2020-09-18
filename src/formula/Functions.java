package formula;

public enum Functions {
	EXP("exp", 1, 1, "e<sup>x</sup>"), SQRT("sqrt", 1, 1, "&#8730;x ; negative x will be turned positive"),
	ABS("abs", 1, 1, "|x|"), POW("pow", 2, 2, "x<sup>y</sup>"), CEIL("ceil", 1, 1, "The nearest bigger integer"),
	FLOOR("floor", 1, 1, "The nearest smaller integer"), ROUND("round", 1, 1, "The nearest integer"),
	MIN("min", 1, -1, "The minimum value between all the parameters"),
	MAX("max", 1, -1, "The maximum value between all the parameters"), LOG("log", 1, 1, "log(x) ; log base 10"),
	LN("ln", 1, 1, "ln(x) ; log base e");

	private String name;
	private int paramMin, paramMax;
	private String description;

	private Functions(String name, int paramMin, int paramMax, String description) {
		this.name = name;
		this.paramMin = paramMin;
		this.paramMax = paramMax;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public boolean nbParamValid(int nb) {
		return ((paramMax == -1 || nb <= paramMax) && nb >= paramMin);
	}

	public static String[] getNames() {
		Functions[] fs = Functions.values();
		String[] resp = new String[fs.length];
		for (int i = 0; i < fs.length; i++) {
			resp[i] = fs[i].getName();
		}
		return resp;
	}

	public static Functions getByName(String name) {
		for (Functions f : Functions.values()) {
			if (f.getName().equals(name))
				return f;
		}
		return null;
	}

	public String getDescription() {
		return description;
	}
}
