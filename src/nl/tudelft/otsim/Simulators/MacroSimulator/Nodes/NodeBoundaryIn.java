package nl.tudelft.otsim.Simulators.MacroSimulator.Nodes;

import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;
import nl.tudelft.otsim.Utilities.TimeScaleFunction;

public class NodeBoundaryIn extends Node {
	//Currently, hardcoded inflow is taken
	//private double inflowPerLane;
	private TimeScaleFunction tsf = new TimeScaleFunction("[0.0/0.0]");
	private double estimationfactor = 1;
	private boolean initialized = false;
	double restVehicles = 0;
	public NodeBoundaryIn(Vertex loc, double inflow) {
		super(loc);
		//setInflowPerLane(inflow);
	}
	public void addTimeScaleFunction(TimeScaleFunction other) {
		this.tsf = this.tsf.add(other);
	}
	public void initTSF() {
		//this.inflowPerLane = tsf.getFactor(0.0)/;
		initialized = true;
	}
	public double getInflowPerLane() {
		return estimationfactor*tsf.getFactor(cellsOut.get(0).model.t())/cellsOut.get(0).lanes;
	}
	
	public void setInflowPerLane(double in) {
		if (in == 0) {
			//System.out.println("in=0");
		}
		if (cellsOut.size() >0 && initialized)
			this.estimationfactor = in/(tsf.getFactor(cellsOut.get(0).model.t())/cellsOut.get(0).lanes);
		//this.inflowPerLane = in;
	}
	public void setInflow(double in) {
		setInflowPerLane(in/cellsOut.get(0).lanes);
	}
	public double  getInflow() {
		return getInflowPerLane()*cellsOut.get(0).lanes;
	}
	public void calcFlux() {
		
		if (nrIn == 0 && nrOut == 1) {
			//double res = Math.min(cellsOut.get(0).Supply, inflowPerLane*cellsOut.get(0).lanes);
			//System.out.println(cellsOut.get(0).model.t()+" @ " + inflowPerLane +" vs " + getInflowPerLane());
			double res = Math.min(cellsOut.get(0).Supply, getInflow()+restVehicles/cellsOut.get(0).model.dt);
			double diff =  ((getInflow()-res)*cellsOut.get(0).model.dt);
			if (Math.abs(diff)>0.000001) {
				restVehicles += diff;
			}
			if (Math.abs(restVehicles)<0.00000001) 
				restVehicles = 0;
			if (restVehicles != 0) {
				//System.out.println("restVehicles positief");
			}

			fluxesIn[0] = res;
			fluxesOut[0] = res;
		}
				
	}
	public double calcFluxValue(MacroCell cell, double[] addedParam, double addedFlowIn) {
			double supply = cellsOut.get(0).calcSupplyValue(new double[]{cell.KCell+addedParam[0], cell.vLim + addedParam[1], cell.kCri + addedParam[2], cell.kJam+addedParam[3], cell.vCri+addedParam[4]});
			return Math.min(supply, getInflow() + addedFlowIn);

	}
}
