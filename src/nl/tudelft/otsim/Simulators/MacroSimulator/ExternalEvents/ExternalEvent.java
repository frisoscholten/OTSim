package nl.tudelft.otsim.Simulators.MacroSimulator.ExternalEvents;

import nl.tudelft.otsim.Simulators.MacroSimulator.Model;

public interface ExternalEvent {
	/*double beginTime;
	double endTime;
	double oldPar;
	double newPar;
	
	public ExternalEvent(double beginTime, double endTime, int oldPar, int newPar) {
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.oldPar = oldPar;
		this.newPar = newPar;
	}

	public double getBeginTime() {
		return beginTime;
	}

	public double getEndTime() {
		return endTime;
	}

	public double getOldPar() {
		return oldPar;
	}

	public double getNewPar() {
		return newPar;
	}*/
	abstract public double getBeginTime();
	abstract public double getEndTime();
	abstract public boolean eventStarted();
	abstract public boolean eventEnded();
	abstract public void startEvent();
	abstract public void endEvent();
	abstract public void init(Model macromodel);
	

}
