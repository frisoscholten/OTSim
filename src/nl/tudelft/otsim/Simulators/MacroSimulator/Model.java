package nl.tudelft.otsim.Simulators.MacroSimulator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import nl.tudelft.otsim.Simulators.SimulatedModel;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.Node;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeBoundaryIn;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeDetector;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeInterior;
import nl.tudelft.otsim.Simulators.MacroSimulator.TestCases.AssimilationConfiguration;



public class Model implements SimulatedModel {

	/** Time step number. Always starts as 0. */
	protected int k;

	/** Current time of the model [s]. Always starts at 0. */
	protected double t;

	/** Time step size of the model [s]. */
	public double dt;

	/** Maximum simulation period [s]. */
	public double period;

	protected int nrCells;
	protected int nrInflowNodes;

	protected Routes routes;


	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
	}

	private java.util.ArrayList<MacroCell> cells = new java.util.ArrayList<MacroCell>();

	private java.util.ArrayList<Node> nodes = new java.util.ArrayList<Node>();
	private java.util.ArrayList<NodeBoundaryIn> inflowNodes = new java.util.ArrayList<NodeBoundaryIn>();
	private ArrayList<NodeDetector> detectors;
	private ArrayList<NodeInterior> junctionNodes = new ArrayList<NodeInterior>();
	private ArrayList<Link> links;
	protected double[] state;

	public void init() {
		// Set attributes
		/* k = 0;
        t = 0;
        cells = new java.util.ArrayList<MacroCell>();
        nodes = new java.util.ArrayList<Node>();*/

		nrCells = getCells().size();
		state = new double[nrCells];
		if (inflowNodes.isEmpty()) {
			for (Node n: nodes) {
				if (n instanceof NodeBoundaryIn) {
					inflowNodes.add((NodeBoundaryIn) n);
				}
			}
		}
		nrInflowNodes = inflowNodes.size();
		for (NodeBoundaryIn n: inflowNodes) {
			n.initTSF();
		}


	}


	public void run(int n) {
		// Simulate n steps
		//int i = 0;
		//System.out.println("testrun");
		if (n>1) {
			throw new Error("hallo");
		}
		for (int nn = 0; (nn < n) && (t < period); nn++) {
			//System.out.println("test");
			//java.util.ArrayList<MacroCell> tmp2 = new java.util.ArrayList<MacroCell>(cells);
			//System.out.println("size Arraylist: " + Integer.toString(tmp2.size()));
			for (NodeDetector nd: detectors ) {
				nd.addMeasurements(t);
			}
			/*for (RouteTravelTime rtt: routes.rtt) {
				rtt.modelUpdate(t);
			}*/
			for (MacroCell c : getCells()) {
				//System.out.println("ID:\t" + Integer.toString(c.id()));
				//System.out.println(Double.toString(c.getK_r()));
				//System.out.println(Double.toString(c.getV_r()));
				//System.out.println("Ins: "+c.getIns() + " outs: " + c.getOuts());
				//System.out.println(Integer.toString(k));
				c.calcDemand();
				c.calcSupply();

			}
			for (Node node: getNodes() ) {
				node.calcFlux();
				//
				/*if (node.nrIn + node.nrOut != 2) {
    				for (double v: node.fluxesIn) {
    					System.out.println("FluxIn node:" + v);
    				}
    				for (double v: node.fluxesOut) {
    					System.out.println("FluxOut node:" + v);
    				}

    			}*/
			}
			/*for (NodeInterior node: getJunctionNodes()) {
				System.out.println(Arrays.deepToString(node.turningRatio));
			}*/

			for (MacroCell c2: getCells()) {
				c2.calcFluxOut();
				c2.calcFluxIn();
				c2.updateDensity();
				//System.out.println(Double.toString(c2.qCap));
				//System.out.println(Double.toString(c2.getV_r()));
			}

			/*for (MacroCell c2: getCells()) {
				if (Double.isNaN(c2.KCell)) {
						throw new Error("NaN");
					}

			}*/
			// Update time
			k++; // Increment time step number
			t = k * dt; // time [s]
			//if (t)


		}
	}
	public double t() {
		return t;
	}
	public void addMacroCell(MacroCell m) {
		getCells().add(m);
	}
	public void addLink(Link link) {
		this.links.add(link);
	}
	public void addNode(Node m) {
		getNodes().add(m);
	}
	public String saveStateToString() {
		String res = "[";
		for (MacroCell c: getCells()) {
			res = res + Double.toString(c.KCell) + ",";
		}
		res = res.substring(0, res.length()-1) + "]";
		return res;
	}
	public double[] saveStateToArray(String outputType) {
		double[] tmpstate;
		if (outputType == "density") {
			tmpstate = new double[nrCells];
			for (int i=0; i<getCells().size(); i++) {
				tmpstate[i] = getCells().get(i).KCell;
			}
		} else if (outputType == "speed") {
			tmpstate = new double[nrCells];
			for (int i=0; i<getCells().size(); i++) {
				tmpstate[i] = getCells().get(i).VCell;
				if (Double.isNaN(tmpstate[i])) {
					System.out.println("is nan");
				}
			}
		} else if (outputType == "inflow") {
			tmpstate = new double[nrInflowNodes];
			for (int i=0; i<nrInflowNodes; i++) {
				tmpstate[i] = inflowNodes.get(i).getInflow();
			}

		} else if (outputType == "criticalDensity") {
			tmpstate = new double[nrCells];
			for (int i=0; i<getCells().size(); i++) {
				tmpstate[i] = getCells().get(i).kCri;
			}

		} else if (outputType == "speedLimit") {
			tmpstate = new double[nrCells];
			for (int i=0; i<getCells().size(); i++) {
				tmpstate[i] = getCells().get(i).vLim;
			}

		} else if (outputType == "jamDensity") {
			tmpstate = new double[nrCells];
			for (int i=0; i<getCells().size(); i++) {
				tmpstate[i] = getCells().get(i).kJam;
			}

		} else if (outputType == "criticalSpeed") {
			tmpstate = new double[nrCells];
			for (int i=0; i<getCells().size(); i++) {
				tmpstate[i] = getCells().get(i).vCri;
			}

		}else if (outputType == "speedLimitLink") {
			tmpstate = new double[getLinks().size()];
			for (int i=0; i<getLinks().size(); i++) {
				tmpstate[i] = getLinks().get(i).vLim;
			}

		} else if (outputType == "criticalDensityLink") {
			tmpstate = new double[getLinks().size()];
			for (int i=0; i<getLinks().size(); i++) {
				tmpstate[i] = getLinks().get(i).kCri;
			}

		} else if (outputType == "jamDensityLink") {
			tmpstate = new double[getLinks().size()];
			for (int i=0; i<getLinks().size(); i++) {
				tmpstate[i] = getLinks().get(i).kJam;
			}

		} else if (outputType == "criticalSpeedLink") {
			tmpstate = new double[getLinks().size()];
			for (int i=0; i<getLinks().size(); i++) {
				tmpstate[i] = getLinks().get(i).vCri;
			}

		} else if (outputType.equals("turnfraction")) {
			tmpstate = new double[getJunctionNodes().size()];
			for (int i=0; i<junctionNodes.size(); i++) {
				tmpstate[i] = (getJunctionNodes().get(i)).getTurningRatioCompact();
			}


		} 
		else {

			throw new IllegalStateException(getClass().getSimpleName() + ": wrong outputType in saveStateToArray class");
		}

		return tmpstate;
	}

	/*public void saveStateToArray(AssimilationConfiguration config) {
		tmpstate = new double[config.nrStateVariables];

	}*/
	public void restoreState(double[] array, String outputType) {
		if (outputType.equals("density")) {
			if (array.length != nrCells) {
				throw new Error("Wrong number of state variables");
			} else {
				for (int i=0; i<nrCells; i++) {
					if (Double.isNaN(array[i])) 
						throw new Error("NaN");
					getCells().get(i).KCell = Math.min(Math.max(array[i],0),getCells().get(i).kJam);

				}
			}
		} else if (outputType.equals("inflow")) {
			if (array.length != nrInflowNodes) {
				throw new Error("Wrong number of inflow nodes");
			} else {
				for (int i=0; i<nrInflowNodes; i++) {
					if (Double.isNaN(array[i])) 
						throw new Error("NaN");
					inflowNodes.get(i).setInflow(Math.min(Math.max(array[i],0),99));
				}
			}

		} else if (outputType.equals("criticalDensity")) {
			if (array.length != nrCells) {
				throw new Error("Wrong number of state variables");
			} else {
				for (int i=0; i<nrCells; i++) {
					getCells().get(i).kCri = Math.max(array[i],0.0001);
				}
			}

		} else if (outputType.equals("speedLimit")) {
			if (array.length != nrCells) {
				throw new Error("Wrong number of state variables");
			} else {
				for (int i=0; i<nrCells; i++) {
					getCells().get(i).setVLim(Math.max(array[i],0));
				}
			}

		} else if (outputType.equals("jamDensity")) {
			if (array.length != nrCells) {
				throw new Error("Wrong number of state variables");
			} else {
				for (int i=0; i<nrCells; i++) {
					getCells().get(i).kJam = Math.max(array[i],0.0001);
				}
			}

		} else if (outputType.equals("turnfraction")) {
			if (array.length != junctionNodes.size()) {
				throw new Error("Wrong number of state variables");
			} else {
				for (int i=0; i<junctionNodes.size(); i++) {
					if (Double.isNaN(array[i])) 
						throw new Error("NaN");
					(getJunctionNodes().get(i)).setTurningRatioCompact(Math.min(Math.max(array[i],0.0000),1));
				}
			}

		} else {
			throw new Error("Wrong parameter to be restored");
		}
		for (MacroCell c: getCells()) {
			c.updateVariables();
		}
	}
	public void restoreStateLinks(double[] array, String outputType) {
		if (outputType.equals("criticalDensity"))  {
			if (array.length != links.size()) {
				throw new Error("Wrong number of state variables");
			} else {
				for (int i=0; i<links.size(); i++) {
					getLinks().get(i).setKCri(Math.max(array[i],0.0001));
				}
			}

		} else if (outputType.equals("speedLimit")) {
			if (array.length != links.size()) {
				throw new Error("Wrong number of state variables");
			} else {
				for (int i=0; i<links.size(); i++) {
					getLinks().get(i).setVLim(Math.max(array[i],0));
				}
			}

		} else if (outputType.equals("jamDensity")) {
			if (array.length != links.size()) {
				throw new Error("Wrong number of state variables");
			} else {
				for (int i=0; i<links.size(); i++) {
					getLinks().get(i).setKJam(Math.max(array[i],0.0001));
				}
			}

		} else if (outputType.equals("criticalSpeed")) {
			if (array.length != links.size()) {
				throw new Error("Wrong number of state variables");
			} else {
				for (int i=0; i<links.size(); i++) {
					getLinks().get(i).setVCri(Math.max(array[i],0.0001));
				}
			}

		} else {
			throw new Error("Wrong parameter to be restored");
		}
		for (MacroCell c: getCells()) {
			c.updateVariables();
		}
	}
	public double[] getOutput(String outputType) {
		double[] tmpstate = null;

		if (outputType.equals("speed")) {

			tmpstate = new double[nrCells];
			for (int i=0; i<nrCells; i++) {
				tmpstate[i] = getCells().get(i).VCell;
				if (Double.isNaN(tmpstate[i])) {
					System.out.println("is nan");
				}

			}
		} else if (outputType.equals("flow")) {

			tmpstate = new double[nrCells];
			for (int i=0; i<nrCells; i++) {
				tmpstate[i] = getCells().get(i).QCell;
				if (Double.isNaN(tmpstate[i])) {
					System.out.println("is nan");
				}

			}
		} else if (outputType.equals("1min")) {

			tmpstate = new double[detectors.size()*2];
			for (int i=0; i<detectors.size(); i++) {
				double[] obs = detectors.get(i).getMeasurements(this.t()-60, this.t(), this.dt);
				tmpstate[i] = obs[1];
				tmpstate[detectors.size()+i] = obs[0];

			}
		} else if (outputType.equals("trafficregime")) {

			tmpstate = new double[nrCells];
			for (int i=0; i<nrCells; i++) {

				tmpstate[i] = getCells().get(i).isCongested();

			}
		}
		return tmpstate;
	}

	/**
	 * @return the cells
	 */
	public java.util.ArrayList<MacroCell> getCells() {
		return cells;
	}
	public ArrayList<Link> getLinks() {
		return links;
	}
	/**
	 * @return the nodes
	 */
	public java.util.ArrayList<Node> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(java.util.ArrayList<Node> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the inflowNodes
	 */
	public java.util.ArrayList<NodeBoundaryIn> getInflowNodes() {
		return inflowNodes;
	}

	/**
	 * @param inflowNodes the inflowNodes to set
	 */
	public void setJunctionNodes(java.util.ArrayList<NodeInterior> junctionNodes) {
		for (NodeInterior n: junctionNodes) {
			if (n.cellsOut.size() >1)
				this.junctionNodes.add(n);
		}
		//this.junctionNodes = junctionNodes;
	}
	public java.util.ArrayList<NodeInterior> getJunctionNodes() {
		return junctionNodes;
	}

	/**
	 * @param inflowNodes the inflowNodes to set
	 */
	public void setInflowNodes(java.util.ArrayList<NodeBoundaryIn> inflowNodes) {
		this.inflowNodes = inflowNodes;
	}
	public void setDetectors(ArrayList<NodeDetector> detectors) {
		this.detectors = detectors;
	}
	public ArrayList<NodeDetector> getDetectors() {
		return this.detectors;
	}
	public void setRoutes(Routes routes) {
		this.routes = routes;
	}
	public Routes getRoutes() {
		return this.routes;
	}
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	public int[] getIndices(LinkedHashSet<MacroCell> cells) {
		int[] result = new int[cells.size()];
		int i=0;
		for (MacroCell c: cells) {
			result[i]=this.cells.indexOf(c);
			i++;
		}

		return result;
	}
	public int[] getIndicesDetector(LinkedHashSet<NodeDetector> detectors) {
		int[] result = new int[detectors.size()*2];
		int i=0;
		for (NodeDetector n: detectors) {
			int index = this.detectors.indexOf(n);
			result[i]=index;

			result[detectors.size()+i]=index+this.detectors.size();
			i++;
		}

		return result;
	}


}
