package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

public class ErrorConfiguration {
	StateDefinition id;
	double initialError;
	double inflationFactor;
	
	public ErrorConfiguration(StateDefinition id, double initialError, double inflationFactor) {
		this.id = id;
		this.initialError = initialError; 
		this.inflationFactor = inflationFactor;
	}
	public ErrorConfiguration(StateDefinition id, double initialError) {
		this(id, initialError, 1);
	}

	public StateDefinition getId() {
		return id;
	}

	public void setId(StateDefinition id) {
		this.id = id;
	}

	public double getInitialError() {
		return initialError;
	}

	public void setInitialError(double initialError) {
		this.initialError = initialError;
	}
	public double getInflationFactor() {
		return inflationFactor;
	}
	public void setInflationFactor(double inflationFactor) {
		this.inflationFactor = inflationFactor;
	}
	public String toString() {
		return "{"+id +","+initialError+","+inflationFactor + "}";
	}
	public String toMatlab() {
		return "'"+id +"',"+initialError+","+inflationFactor;
	}
	
	
}
