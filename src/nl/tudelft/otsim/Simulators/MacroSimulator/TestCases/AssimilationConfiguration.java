package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import Jama.Matrix;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;
import nl.tudelft.otsim.Simulators.MacroSimulator.Model;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeDetector;
import nl.tudelft.otsim.Simulators.MacroSimulator.TestCases.StateDefinition.StateFunction;

public class AssimilationConfiguration {
	AssimilationMethod assimilationMethod;

	int nrSpeedObservations;
	int nrFlowObservations;

	int nrCells;
	int nrStateVariablesPerCell = 0;

	int nrLinks;
	int nrStateVariablesPerLink= 0;

	int nrInflowNodes;
	int nrStateVariablesPerInflowNode= 0;

	int nrJunctionNodes;
	int nrStateVariablesPerJunctionNode= 0;

	public int nrStateVariables;
	public int nrStateVariablesOut;
	public int[] indicesCellsInState;
	public int[] indicesLinksInState;
	public int[] indicesInflowNodesInState;
	public int[] indicesJunctionNodesInState;

	boolean densityInState = false;
	boolean inflowInState = false;
	boolean speedLimitPerCellInState = false;
	boolean criticalDensityPerCellInState = false;
	boolean jamDensityPerCellInState = false;
	boolean criticalSpeedPerCellInState = false;

	boolean speedLimitPerLinkInState = false;
	boolean criticalDensityPerLinkInState = false;
	boolean jamDensityPerLinkInState = false;
	boolean criticalSpeedPerLinkInState = false;

	public ErrorConfiguration[] state;
	public EnumSet<StateDefinition> stateIds = EnumSet.noneOf(StateDefinition.class);
	public EnumMap<StateDefinition, Double> initialErrors = new EnumMap<StateDefinition, Double>(StateDefinition.class);
	public EnumMap<StateDefinition, Double> inflationFactors = new EnumMap<StateDefinition, Double>(StateDefinition.class);
	public EnumMap<StateType, Integer> nrOfElements = new EnumMap<StateType, Integer>(StateType.class);
	public EnumSet<StateDefinition> outputIds = EnumSet.noneOf(StateDefinition.class);
	public EnumMap<StateType, Integer> nrOfElementsOutput = new EnumMap<StateType, Integer>(StateType.class);


	public AssimilationConfiguration(ErrorConfiguration[] state, Model macromodel, AssimilationMethod assimilationMethod) {
		//this(convertArrayToNames(state),macromodel);
		this.assimilationMethod = assimilationMethod;
		this.state = state;


		for (ErrorConfiguration e: state) {
			if (e.getId().is(StateFunction.IN)) {
				switch (e.getId().getType()) {
				case CELL: nrStateVariablesPerCell++; break;
				case LINK: nrStateVariablesPerLink++; break;
				case NODEIN: nrStateVariablesPerInflowNode++; break;
				case NODEJUNCTION: nrStateVariablesPerJunctionNode++; break;
				case DETECTOR: break;
				}

				stateIds.add(e.getId());

				initialErrors.put(e.getId(), e.getInitialError());
				inflationFactors.put(e.getId(), e.getInflationFactor());
			}
		}
		this.nrCells = macromodel.getCells().size();
		nrOfElements.put(StateType.CELL, nrCells);
		/*for (StateDefinition s: stateIds) {
			System.out.println(s);
		}
		;*/

		this.nrLinks = macromodel.getLinks().size();
		nrOfElements.put(StateType.LINK, nrLinks);
		this.nrInflowNodes = macromodel.getInflowNodes().size();
		nrOfElements.put(StateType.NODEIN, nrInflowNodes);
		this.nrJunctionNodes = macromodel.getJunctionNodes().size();
		nrOfElements.put(StateType.NODEJUNCTION, nrJunctionNodes);
		this.nrStateVariables = nrCells*nrStateVariablesPerCell + nrLinks*nrStateVariablesPerLink + nrInflowNodes*nrStateVariablesPerInflowNode + nrJunctionNodes*nrStateVariablesPerJunctionNode;
		this.indicesCellsInState = new int[]{0,nrCells*nrStateVariablesPerCell};
		this.indicesLinksInState= new int[]{indicesCellsInState[1],indicesCellsInState[1] + nrLinks*nrStateVariablesPerLink};
		this.indicesInflowNodesInState= new int[]{indicesLinksInState[1],indicesLinksInState[1] + nrInflowNodes*nrStateVariablesPerInflowNode};
		this.indicesJunctionNodesInState= new int[]{indicesInflowNodesInState[1],indicesInflowNodesInState[1] + nrJunctionNodes*nrStateVariablesPerJunctionNode};


	}
	private static ArrayList<String> convertArrayToNames(ErrorConfiguration[] state) {
		ArrayList<String> names =  new ArrayList<String>();
		for (ErrorConfiguration s: state) {
			names.add(s.getId().getName());
		}
		return names;
	}
	public AssimilationConfiguration(ArrayList<String> stateVariables, Model macromodel) {



		throw new Error("out of use");	
		/*
		if (stateVariables.contains("density")) { 
				densityInState = true;
				nrStateVariablesPerCell++;
			}
			if (stateVariables.contains("inflow")) {
				inflowInState = true;
				nrStateVariablesPerInflowNode++;
			}
			if (stateVariables.contains("speedLimit")){
				speedLimitPerCellInState = true;
				nrStateVariablesPerCell++;
			}
			if (stateVariables.contains("criticalDensity")){
				criticalDensityPerCellInState = true;
				nrStateVariablesPerCell++;
			}
			if (stateVariables.contains("jamDensity")){
				jamDensityPerCellInState = true;
				nrStateVariablesPerCell++;
			}
			if (stateVariables.contains("criticalSpeed")){
				criticalSpeedPerCellInState = true;
				nrStateVariablesPerCell++;
			}

			if (stateVariables.contains("speedLimitLink")){
				speedLimitPerLinkInState = true;
				nrStateVariablesPerLink++;
			}
			if (stateVariables.contains("criticalDensityLink")){
				criticalDensityPerLinkInState = true;
				nrStateVariablesPerLink++;
			}
			if (stateVariables.contains("jamDensityLink")){
				jamDensityPerLinkInState = true;
				nrStateVariablesPerLink++;
			}
			if (stateVariables.contains("criticalSpeedLink")){
				criticalSpeedPerLinkInState = true;
				nrStateVariablesPerLink++;
			}

			this.nrCells = macromodel.getCells().size();
			this.nrLinks = macromodel.getLinks().size();
			this.nrInflowNodes = macromodel.getInflowNodes().size();
			this.nrStateVariables = nrCells*nrStateVariablesPerCell + nrLinks*nrStateVariablesPerLink + nrInflowNodes*nrStateVariablesPerInflowNode;
			this.indicesCellsInState = new int[]{0,nrCells*nrStateVariablesPerCell};
			this.indicesLinksInState= new int[]{indicesCellsInState[1],indicesCellsInState[1] + nrLinks*nrStateVariablesPerLink};
			this.indicesInflowNodesInState= new int[]{indicesLinksInState[1],indicesLinksInState[1] + nrInflowNodes*nrStateVariablesPerInflowNode};
			//this.indKs = ;
		 */	
	}
	public double[] getInitialErrorArray() {

		double[] result = new double[nrStateVariables];
		int l = 0;
		/*for (StateDefinition s: StateDefinition.values()) {
			if (stateIds.contains(s)) {
				int nrEl = nrOfElements.get(s.getType());
				double[] tmp = new double[nrEl];
				Arrays.fill(tmp, initialErrors.get(s));
				System.arraycopy(tmp, 0, result, l, tmp.length);
				l+= nrEl;
			}
		}*/
		for (StateDefinition s: stateIds) {
			if (s.is(StateFunction.IN)) {
				int nrEl = nrOfElements.get(s.getType());
				double[] tmp = new double[nrEl];
				Arrays.fill(tmp, initialErrors.get(s));
				System.arraycopy(tmp, 0, result, l, tmp.length);
				l+= nrEl;
			}
		}

		return result;

		/*if (densityInState == true) {
			double[] tmp = new double[nrCells];
			Arrays.fill(tmp, initialErrors.get(StateDefinition.K_CELL));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrCells;
		}

		if (speedLimitPerCellInState == true) {
			double[] tmp = new double[nrCells];
			Arrays.fill(tmp, initialErrors.get(StateDefinition.VLIM_CELL));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrCells;
		}
		if (criticalDensityPerCellInState == true) {
			double[] tmp = new double[nrCells];
			Arrays.fill(tmp, initialErrors.get(StateDefinition.KCRI_CELL));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrCells;
		}
		if (jamDensityPerCellInState == true) {
			double[] tmp = new double[nrCells];
			Arrays.fill(tmp, initialErrors.get(StateDefinition.KJAM_CELL));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrCells;
		}

		if (criticalSpeedPerCellInState == true) {
			double[] tmp = new double[nrCells];
			Arrays.fill(tmp, initialErrors.get(StateDefinition.VCRI_CELL));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrCells;
		}

		if (speedLimitPerLinkInState == true) {
			double[] tmp = new double[nrLinks];
			Arrays.fill(tmp, initialErrors.get(StateDefinition.VLIM_LINK));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrLinks;

		}
		if (criticalDensityPerLinkInState == true) {
			double[] tmp = new double[nrLinks];
			Arrays.fill(tmp, initialErrors.get(StateDefinition.KCRI_LINK));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrLinks;
		}
		if (jamDensityPerLinkInState == true) {
			double[] tmp = new double[nrLinks];
			Arrays.fill(tmp, initialErrors.get(StateDefinition.KJAM_LINK));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrLinks;
		}
		if (criticalSpeedPerLinkInState == true) {
			double[] tmp = new double[nrLinks];
			Arrays.fill(tmp, initialErrors.get(StateDefinition.VCRI_LINK));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrLinks;
		}
		if (inflowInState == true) {
			double[] tmp = new double[nrInflowNodes];
			Arrays.fill(tmp, initialErrors.get(StateDefinition.INFLOW_NODE));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrInflowNodes;
		}


		return result;*/

	}
	public double[] getInflationFactorArray() {
		double[] result = new double[nrStateVariables];
		int l = 0;
		//StateDefinition s;
		//stateIds.values();
		for (StateDefinition s: stateIds) {
			if (s.is(StateFunction.IN)) {
				int nrEl = nrOfElements.get(s.getType());
				double[] tmp = new double[nrEl];
				Arrays.fill(tmp, inflationFactors.get(s));
				System.arraycopy(tmp, 0, result, l, tmp.length);
				l+= nrEl;
			}

		}
		/*for (StateDefinition s: StateDefinition.values()) {
			if (stateIds.contains(s)) {
				int nrEl = nrOfElements.get(s.getType());
				double[] tmp = new double[nrEl];
				Arrays.fill(tmp, inflationFactors.get(s));
				System.arraycopy(tmp, 0, result, l, tmp.length);
				l+= nrEl;
			}
		}*/
		return result;
		/*


		double[] result = new double[nrStateVariables];
		int l = 0;
		if (densityInState == true) {
			double[] tmp = new double[nrCells];
			Arrays.fill(tmp, inflationFactors.get(StateDefinition.K_CELL));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrCells;
		}

		if (speedLimitPerCellInState == true) {
			double[] tmp = new double[nrCells];
			Arrays.fill(tmp, inflationFactors.get(StateDefinition.VLIM_CELL));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrCells;
		}
		if (criticalDensityPerCellInState == true) {
			double[] tmp = new double[nrCells];
			Arrays.fill(tmp, inflationFactors.get(StateDefinition.KCRI_CELL));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrCells;
		}
		if (jamDensityPerCellInState == true) {
			double[] tmp = new double[nrCells];
			Arrays.fill(tmp, inflationFactors.get(StateDefinition.KJAM_CELL));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrCells;
		}
		if (criticalSpeedPerCellInState == true) {
			double[] tmp = new double[nrCells];
			Arrays.fill(tmp, inflationFactors.get(StateDefinition.VCRI_CELL));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrCells;
		}


		if (speedLimitPerLinkInState == true) {
			double[] tmp = new double[nrLinks];
			Arrays.fill(tmp, inflationFactors.get(StateDefinition.VLIM_LINK));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrLinks;

		}
		if (criticalDensityPerLinkInState == true) {
			double[] tmp = new double[nrLinks];
			Arrays.fill(tmp, inflationFactors.get(StateDefinition.KCRI_LINK));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrLinks;
		}
		if (jamDensityPerLinkInState == true) {
			double[] tmp = new double[nrLinks];
			Arrays.fill(tmp, inflationFactors.get(StateDefinition.KJAM_LINK));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrLinks;
		}
		if (criticalSpeedPerLinkInState == true) {
			double[] tmp = new double[nrLinks];
			Arrays.fill(tmp, inflationFactors.get(StateDefinition.VCRI_LINK));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrLinks;
		}
		if (inflowInState == true) {
			double[] tmp = new double[nrInflowNodes];
			Arrays.fill(tmp, inflationFactors.get(StateDefinition.INFLOW_NODE));
			System.arraycopy(tmp, 0, result, l, tmp.length);
			l+= nrInflowNodes;
		}


		return result;*/

	}

	public void addErrorConfiguration(ErrorConfiguration errorConfiguration) {

	}
	public double[] saveStateToArray(Model macromodel, StateDefinition state)  {
		//int nrEl = nrOfElements.get(state.getType());
		double[] result = macromodel.saveStateToArray(state.getName());

		return result;
	}
	public double[] saveStateToArray(Model macromodel) {

		double[] result = new double[nrStateVariables];
		int l = 0;
		for (StateDefinition s: stateIds) {
			if (s.is(StateFunction.IN)) {
				int nrEl = nrOfElements.get(s.getType());
				double[] tmp = macromodel.saveStateToArray(s.getName());
				System.arraycopy(tmp, 0, result, l, nrEl);
				l+= nrEl;
			}
		}
		return result;

		/*
		double[] result = new double[nrStateVariables];
		int l = 0;
		if (densityInState == true) {
			double[] tmp = macromodel.saveStateToArray("density");
			System.arraycopy(tmp, 0, result, l, nrCells);
			l+= nrCells;
		}

		if (speedLimitPerCellInState == true) {
			double[] tmp = macromodel.saveStateToArray("speedLimit");
			System.arraycopy(tmp, 0, result, l, nrCells);
			l+= nrCells;
		}
		if (criticalDensityPerCellInState == true) {
			double[] tmp = macromodel.saveStateToArray("criticalDensity");
			System.arraycopy(tmp, 0, result, l, nrCells);
			l+= nrCells;
		}
		if (jamDensityPerCellInState == true) {
			double[] tmp = macromodel.saveStateToArray("jamDensity");
			System.arraycopy(tmp, 0, result, l, nrCells);
			l+= nrCells;
		}
		if (criticalSpeedPerCellInState == true) {
			double[] tmp = macromodel.saveStateToArray("criticalSpeed");
			System.arraycopy(tmp, 0, result, l, nrCells);
			l+= nrCells;
		}


		if (speedLimitPerLinkInState == true) {
			double[] tmp = macromodel.saveStateToArray("speedLimitLink");
			System.arraycopy(tmp, 0, result, l, nrLinks);
			l+= nrLinks;

		}
		if (criticalDensityPerLinkInState == true) {
			double[] tmp = macromodel.saveStateToArray("criticalDensityLink");
			System.arraycopy(tmp, 0, result, l, nrLinks);
			l+= nrLinks;
		}
		if (jamDensityPerLinkInState == true) {
			double[] tmp = macromodel.saveStateToArray("jamDensityLink");
			System.arraycopy(tmp, 0, result, l, nrLinks);
			l+= nrLinks;
		}
		if (criticalSpeedPerLinkInState == true) {
			double[] tmp = macromodel.saveStateToArray("criticalSpeedLink");
			System.arraycopy(tmp, 0, result, l, nrLinks);
			l+= nrLinks;
		}
		if (inflowInState == true) {
			double[] tmp = macromodel.saveStateToArray("inflow");
			System.arraycopy(tmp, 0, result, l, nrInflowNodes);
			l+= nrInflowNodes;
		}


		return result;*/
	}
	public static Matrix[] getOutput(Model macromodel, StateDefinition ... states) {
		Matrix[] output = new Matrix[states.length];
		int i = 0;
		for (StateDefinition state: states) {
			if (state.is(StateFunction.OUT)) {
				if (state.is(StateFunction.IN)) {
					double[] o = macromodel.saveStateToArray(state.getName());
					output[i] = new Matrix(o,o.length);
				} else {
					double[] o =macromodel.getOutput(state.getName());
					output[i] = new Matrix(o,o.length);
				}
				
				
				
			} else { throw new Error("StateFunction "+state.toString()+" is not defined as OUT"); }
			i++;
		}
		return output;
	}
	
	
	public void restoreState(double[] stateVector, Model macromodel) {

		int l = 0;

		//double[] result = new double[nrStateVariables];
		//int l = 0;
		for (StateDefinition s: stateIds) {
			if (s.is(StateFunction.IN)) {
				int nrEl = nrOfElements.get(s.getType());
				if (s.getType() == StateType.LINK) {
					macromodel.restoreStateLinks(Arrays.copyOfRange(stateVector,l,l+nrEl),s.getName().substring(0,s.getName().length()-4));
				} else {
					macromodel.restoreState(Arrays.copyOfRange(stateVector,l,l+nrEl),s.getName());
				}
				l+= nrEl;
			}

		}
		//return result;
		/*
		if (densityInState == true) {
			macromodel.restoreState(Arrays.copyOfRange(stateVector,l,l+nrCells),"density");
			l+= nrCells;
		}

		if (speedLimitPerCellInState == true) {
			macromodel.restoreState(Arrays.copyOfRange(stateVector,l,l+nrCells),"speedLimit");
			l+= nrCells;
		}
		if (criticalDensityPerCellInState == true) {
			macromodel.restoreState(Arrays.copyOfRange(stateVector,l,l+nrCells),"criticalDensity");
			l+= nrCells;
		}
		if (jamDensityPerCellInState == true) {
			macromodel.restoreState(Arrays.copyOfRange(stateVector,l,l+nrCells),"jamDensity");
			l+= nrCells;
		}
		if (criticalSpeedPerCellInState == true) {
			macromodel.restoreState(Arrays.copyOfRange(stateVector,l,l+nrCells),"criticalSpeed");
			l+= nrCells;
		}


		if (speedLimitPerLinkInState == true) {
			macromodel.restoreStateLinks(Arrays.copyOfRange(stateVector,l,l+nrLinks),"speedLimit");
			l+= nrLinks;
		}
		if (criticalDensityPerLinkInState == true) {
			macromodel.restoreStateLinks(Arrays.copyOfRange(stateVector,l,l+nrLinks),"criticalDensity");
			l+= nrLinks;
		}
		if (jamDensityPerLinkInState == true) {
			macromodel.restoreStateLinks(Arrays.copyOfRange(stateVector,l,l+nrLinks),"jamDensity");
			l+= nrLinks;
		}
		if (criticalSpeedPerLinkInState == true) {
			macromodel.restoreStateLinks(Arrays.copyOfRange(stateVector,l,l+nrLinks),"criticalSpeed");
			l+= nrLinks;
		}

		if (inflowInState == true) {
			macromodel.restoreState(Arrays.copyOfRange(stateVector,l,l+nrInflowNodes),"inflow");
			l+= nrInflowNodes;
		}*/


	}
	static public ArrayList<Object> getStateVariables(Model macromodel, StateType ... stateTypes) {
		ArrayList<Object> result = new ArrayList<Object>();
		
		for (StateType st: stateTypes) {
			switch (st) {
			case CELL: result.addAll(macromodel.getCells()); break;
			case LINK: result.addAll(macromodel.getLinks()); break;
			case NODEIN: result.addAll(macromodel.getInflowNodes()); break;
			case NODEJUNCTION: result.addAll(macromodel.getJunctionNodes()); break;
			}

		}
		return result;
	}
	public ArrayList<Object> getStateVariables(Model macromodel) {
		ArrayList<Object> result = new ArrayList<Object>();
		//double[] result = new double[nrStateVariables];
		//int l = 0;
		for (StateDefinition s: stateIds) {
			switch (s.getType()) {
			case CELL: result.addAll(macromodel.getCells()); break;
			case LINK: result.addAll(macromodel.getLinks()); break;
			case NODEIN: result.addAll(macromodel.getInflowNodes()); break;
			case NODEJUNCTION: result.addAll(macromodel.getJunctionNodes()); break;
			}

		}
		
		/*
		if (densityInState == true) {
			result.addAll(macromodel.getCells());
		}

		if (speedLimitPerCellInState == true) {
			result.addAll(macromodel.getCells());
		}
		if (criticalDensityPerCellInState == true) {
			result.addAll(macromodel.getCells());
		}
		if (jamDensityPerCellInState == true) {
			result.addAll(macromodel.getCells());
		}
		if (criticalSpeedPerCellInState == true) {
			result.addAll(macromodel.getCells());
		}


		if (speedLimitPerLinkInState == true) {
			result.addAll(macromodel.getLinks());

		}
		if (criticalDensityPerLinkInState == true) {
			result.addAll(macromodel.getLinks());
		}
		if (jamDensityPerLinkInState == true) {
			result.addAll(macromodel.getLinks());
		}
		if (criticalSpeedPerLinkInState == true) {
			result.addAll(macromodel.getLinks());
		}
		if (inflowInState == true) {
			result.addAll(macromodel.getInflowNodes());
		}
		 */

		return result;
	}

	public int[] getIndices(Model model, LinkedHashSet<MacroCell> cells) {
		return model.getIndices(cells);
	}
	public int[] getIndicesDetector(Model model, LinkedHashSet<NodeDetector> detectors) {
		return model.getIndicesDetector(detectors);
	}
	public int[] getIndicesObject(Model model, LinkedHashSet<Object> objects) {
		ArrayList<Object> listOfObjects = getStateVariables(model);

		ArrayList<Integer> res = new ArrayList<Integer>();
		//ArrayList<Object> obj2 = new ArrayList<Object>(objects);
		for (int i = 0; i<listOfObjects.size(); i++) {
			if (objects.contains(listOfObjects.get(i)))
				res.add(i);
		}
		
		/*
		for (Object o: objects) {
			int index = listOfObjects.indexOf(o);
			if (index != -1){
				res.add(index);
			}
		}*/
		
		int[] result = new int[res.size()];
		int i = 0;
		for (Integer in: res) {
			result[i++]=in;

		}
		return result;
	}
}
