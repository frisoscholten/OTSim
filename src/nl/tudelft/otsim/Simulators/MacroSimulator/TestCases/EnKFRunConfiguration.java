package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.util.ArrayList;
import java.util.Arrays;

public class EnKFRunConfiguration {
	AssimilationMethod assimilationMethod;
	ArrayList<ErrorConfiguration> errorConfigurations;
	int nrSurroundingCells;
	int ensembleSize;
	int inflowTFWidth;
	
	public EnKFRunConfiguration(AssimilationMethod assimilationMethod, int nrSurroundingCells, ArrayList<ErrorConfiguration> errorConfigurations, int ensembleSize, int inflowTFWidth) {
		//this.nrEnsembles = nrEnsembles;
		this.assimilationMethod = assimilationMethod;
		this.errorConfigurations = errorConfigurations;
		this.nrSurroundingCells = nrSurroundingCells;
		this.ensembleSize = ensembleSize;
		this.inflowTFWidth = inflowTFWidth;
		
	}
	public EnKFRunConfiguration(AssimilationMethod assimilationMethod, int nrSurroundingCells, ArrayList<ErrorConfiguration> errorConfigurations, int ensembleSize) {
		this(assimilationMethod, nrSurroundingCells, errorConfigurations, ensembleSize, nrSurroundingCells);
		
	}
	public int getEnsembleSize() {
		return ensembleSize;
	}
	public int getInflowTFWidth() {
		return inflowTFWidth;
	}
	public AssimilationMethod getAssimilationMethod() {
		return assimilationMethod;
	}
	public ArrayList<ErrorConfiguration> getErrorConfigurations() {
		return errorConfigurations;
	}
	public int getNrSurroundingCells() {
		return nrSurroundingCells;
	}
	
	
	public String toString() {
		String s = "{'"+assimilationMethod.toString()+"',"+nrSurroundingCells+","+ensembleSize+","+inflowTFWidth;
		for (ErrorConfiguration er : errorConfigurations) {
			s = s +","+er.toMatlab();
		}
		return s+"};";
	}
	
	
	
}
