package nl.tudelft.otsim.Simulators.MacroSimulator.Nodes;

import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;


// For now: turnfractions are hardcoded to 50% in each direction
public class NodeInterior extends Node {
	public NodeInterior(Vertex loc) {
		super(loc);
	}
	
	public void calcFlux() {
		if (nrIn == 1 && nrOut == 1) {
			double res = Math.min(cellsOut.get(0).Supply, cellsIn.get(0).Demand);
			fluxesIn[0] = res;
			fluxesOut[0] = res;
		/*	if (Double.isNaN(res)) {
				throw new Error("NaN");
			}*/
		}
		if (nrIn == 1 && nrOut > 1) {
		/*	double totIn = 0;
						
			for (int i=0; i<nrOut; i++) {
				fluxesOut[i]=Math.min((cellsIn.get(0).Demand)*turningRatio[0][i],cellsOut.get(i).Supply);
				totIn += fluxesOut[i];
    		}
			fluxesIn[0] = totIn;*/
			double totIn = 0;
			double totalflux = cellsIn.get(0).Demand;
			for (int i=0; i<nrOut; i++) {
				if (turningRatio[0][i] > 0)
					totalflux = Math.min(totalflux, cellsOut.get(i).Supply/turningRatio[0][i]);
			}
			
			for (int i=0; i<nrOut; i++) {
				fluxesOut[i]=totalflux*turningRatio[0][i];
				//totIn += fluxesOut[i];
    		}
			fluxesIn[0] = totalflux;
			
				/*if (Double.isNaN(totalflux)) {
						throw new Error("NaN");
					}*/
				
			
		}
		if (nrIn >1 && nrOut == 1) {
			double totOut = 0;
			double totalCapacity = 0;
			double[] Sstar = new double[nrIn];
			if (nrIn >2) {
				throw new Error("only applicable for 2->1 node");
			}
			double totalSstar = 0;
			for (int i=0; i<nrIn; i++) {
				totalCapacity += cellsIn.get(i).qCap;
			}
			for (int i=0; i<nrIn; i++) {
				Sstar[i] = cellsIn.get(i).qCap/totalCapacity*cellsOut.get(0).Supply;
				//totalSstar += Sstar[i];
			}
			double[] S = new double[2];
			S[0] = Sstar[0] + Math.max(0, Sstar[1] - cellsIn.get(1).Demand);
			S[1] = Sstar[1] + Math.max(0, Sstar[0] - cellsIn.get(0).Demand);
			//double a = cellsOut.get(0).Supply/totalDemand;
    		for (int i=0; i<nrIn; i++) {
    			fluxesIn[i] = Math.min(cellsIn.get(i).Demand,S[i]);
    			totOut += fluxesIn[i];
    			/*//System.out.println(FluxIn2.length);
    			//System.out.println(Supply);
    			double Sstar = ((cellsOut.get(0).Supply)/nrIn);
    			//System.out.println(Sstar);
    			double S = Sstar;
    			fluxesIn[i] = Math.min(cellsIn.get(i).Demand,S);
    			totOut += fluxesIn[i];*/
    		}
    		fluxesOut[0] = totOut;
    		/*if (Double.isNaN(totOut)) {
				throw new Error("NaN");
			}*/
		}
		
	}
}
