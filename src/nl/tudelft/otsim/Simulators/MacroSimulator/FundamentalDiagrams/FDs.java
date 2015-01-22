package nl.tudelft.otsim.Simulators.MacroSimulator.FundamentalDiagrams;

public enum FDs {
	DRAKE("Drake","nl.tudelft.otsim.Simulators.MacroSimulator.FundamentalDiagrams.FDDrake"),
	SMULDERS("Smulders","nl.tudelft.otsim.Simulators.MacroSimulator.FundamentalDiagrams.FDSmulders"),
	TRIANGULAR("Triangular","nl.tudelft.otsim.Simulators.MacroSimulator.FundamentalDiagrams.FDTrian");
	
	//private class c;
	private final String name;
	private final String classname;
	private Class<?> c;
	private FDs(String s, String cn) {
		this.name = s;
		this.classname = cn;
		try {
			this.c = Class.forName(cn);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static FDs fromString(String s) {
		FDs ret = null;
		for (FDs f: values()) {
			if (s.equals(f.toString())) {	
				ret = f;
			}
			
		}
		return ret;
	}
	public IFD create() {
		try {
			return (IFD) c.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
