package nl.tudelft.otsim.Simulators.MacroSimulator;


import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;




import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.Events.Step;
import nl.tudelft.otsim.GUI.GraphicsPanel;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.GUI.ObjectInspector;
import nl.tudelft.otsim.GUI.WED;
import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.Simulators.ShutDownAble;
import nl.tudelft.otsim.Simulators.SimulatedObject;
import nl.tudelft.otsim.Simulators.Simulator;
import nl.tudelft.otsim.Simulators.LaneSimulator.Vehicle;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroSimulator;
import nl.tudelft.otsim.Simulators.MacroSimulator.ExternalEvents.ExternalEvent;
import nl.tudelft.otsim.Simulators.MacroSimulator.ExternalEvents.ExternalEvents;
import nl.tudelft.otsim.Simulators.MacroSimulator.FundamentalDiagrams.FDs;
import nl.tudelft.otsim.Simulators.MacroSimulator.FundamentalDiagrams.IFD;
import nl.tudelft.otsim.Simulators.MacroSimulator.FundamentalDiagrams.FDSmulders;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.Node;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeBoundaryIn;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeBoundaryOut;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeDetector;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeInterior;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeInteriorTampere;
import nl.tudelft.otsim.SpatialTools.Planar;
//import nl.tudelft.otsim.Simulators.MacroSimulator.MacroModel;
import nl.tudelft.otsim.Utilities.TimeScaleFunction;

/**
 * Macro Simulator for OpenTraffic
 * 
 * @author Friso Scholten
 */
public class MacroSimulator extends Simulator implements ShutDownAble{
	/** Type of this Simulator */
	public static final String simulatorType = "Macro simulator";
	public static boolean output = false;
	private final Model model = new Model();
	private final Scheduler scheduler;
	//private double endTime = 1000;	// should be overridden in the configuration
	private double randomSeed = 0;	// idem




	private ArrayList<MacroCell> macroCells = new ArrayList<MacroCell>();
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<NodeDetector> detectors = new ArrayList<NodeDetector>();
	private static Set<Integer> cellsInts = new TreeSet<Integer>();
	private static int lastInt = 1;
	/**
	 * Create a MacroSimulator.
	 * @param configuration String; textual description of the network,
	 * traffic demand and measurement plans
	 * @param graphicsPanel {@link GraphicsPanel} to draw on
	 * @param scheduler {@link Scheduler} for this simulation
	 * @throws Exception 
	 */
		public MacroSimulator(String configuration, GraphicsPanel graphicsPanel, Scheduler scheduler) throws Exception {
	
	
		if (output)
			System.out.println("Creating a new MacroSimulator based on description:\n" + configuration);
		this.scheduler = scheduler;
		scheduler.enqueueEvent(0, new Stepper(this));	// Set up my first evaluation
		model.period = 1800;
		model.dt = 2.0;
		double offset = 0;

		// Set minimum length of cells to be generated (in [m])
		double minLengthCells = 100;

		// Set used fundamental diagram
		IFD fd = new FDSmulders();

		// Set inflow at boundaries in (in vehicles per sec per lane)
		double inflowBoundary = (2000.0/3600.0);

		//ArrayList<MacroCell> cells = new ArrayList<MacroCell>();
		ArrayList<MacroCell> copySimPaths = new ArrayList<MacroCell>();

		Routes routes = new Routes();
		TimeScaleFunction nrTripsPattern = new TimeScaleFunction();
		ArrayList<Vertex> detectorLocations = new ArrayList<Vertex>();
		ArrayList<Integer> idmap = new ArrayList<Integer>();
		HashMap<Integer, Double> tfs = new HashMap<Integer, Double>();
		HashMap<Integer, TimeScaleFunction> flows = new HashMap<Integer, TimeScaleFunction>();
		boolean routeBased = true;
		int nrC = 1;
		List<ExternalEvent> externalEvents = new ArrayList<ExternalEvent>();

		/*
		 * It does make sense to first join successive roadway sections that
		 * have the same capacity and speed limit. Then split up longer roadways 
		 * into multiple cells and create a class that macro-simulates one cell.
		 */
		//String[] con = configuration.split("[\n$]");
		//String s1 = con[300];
		//String[] s12 = s1.split("[\t|\\|]");
		for (String line : configuration.split("[\n$]")) {
			line = line.trim();
			String[] fields = line.split("[\t|\\|]");
			if (fields.length == 0)
				continue;	// Ignore empty lines in configuration
			else if (fields[0].equals("Offset:"))
				offset = Double.parseDouble(fields[1]);
			else if (fields[0].equals("EndTime:"))
				model.period = Double.parseDouble(fields[1]);
			else if (fields[0].equals("RouteBased:")) {
				if (fields[1].equals("false")) 
					routeBased=false;
			}
			else if (fields[0].equals("Seed:"))
				this.randomSeed = Double.parseDouble(fields[1]);
			else if (fields[0].equals("Roadway:")) {
				MacroCell sp = new MacroCell(model);
				
				// set ID of MacroCell
				sp.setId(Integer.parseInt(fields[1]));

				for (int i = 2; i < fields.length; i++) {
					if (fields[i].equals("from"))
						sp.setConfigNodeIn(Integer.parseInt(fields[++i]));
					if (fields[i].equals("to"))
						sp.setConfigNodeOut(Integer.parseInt(fields[++i]));
					if (fields[i].equals("speedlimit"))
						sp.setVLim(Double.parseDouble(fields[++i])/3.6);
					if (fields[i].equals("lanes"))
						sp.setWidth(3.5 * Double.parseDouble(fields[++i]));
					else if (fields[i].equals("vertices")) {
						// add vertices
						while (fields[++i].startsWith("(")) {
							Vertex tmp = new Vertex(fields[i]);
							sp.addVertex(tmp);

						}

						// decrease i to start check the right field in the following loop
						--i;

					} else if (fields[i].equals("ins")) {
						// add all incoming links to MacroCell
						while (!fields[++i].startsWith("outs")) {
							if (fields[i].equals("")) {
								System.out.println(i + " hallo ");
							}
							sp.addIn(Integer.valueOf(fields[i]));
						}
						// decrease i to start check the right field in the following loop
						--i;
					} else if (fields[i].equals("outs")) {
						// add all outgoing links to MacroCell
						++i;
						if (output)
						System.out.println(Arrays.toString(fields));
						while (i < fields.length && !fields[i].startsWith("fd") && !fields[i].startsWith("tf") && !fields[i].isEmpty()) {
							if (output)
							System.out.println(fields[i]);
							sp.addOut(Integer.valueOf(fields[i]));
							++i;
							
						}
						
						--i;
						
					} 
					else if (fields[i].equals("fd")) {
						// add all outgoing links to MacroCell
						sp.kCriPerLane = Double.valueOf(fields[i+1]);
						sp.kJamPerLane = Double.valueOf(fields[i+2]);
						sp.vCriBeforeInit = Double.valueOf(fields[i+3]);
						fd = FDs.fromString(fields[i+4]).create();
						i = i+4;
					}

				}
				Integer ID = sp.getId();
				idmap.add(ID);
				copySimPaths.add(idmap.indexOf(ID),sp); 
				

			} else if (!routeBased && fields[0].equals("Inflow:")) {
				
				ArrayList<Integer> route = new ArrayList<Integer>(); 
				route.add(Integer.parseInt(fields[1]));
				
				TimeScaleFunction t = new TimeScaleFunction(fields[2]);
				nrTripsPattern = new TimeScaleFunction(t,1/3600.0);
				nrTripsPattern.shiftTime(nrTripsPattern, offset);
				flows.put(Integer.parseInt(fields[1]), nrTripsPattern);
				//routes.addRoute(route, nrTripsPattern.getFactor(0), nrTripsPattern);
			}
			else if (!routeBased && fields[0].equals("Turn:")) {
				MacroCell sp = copySimPaths.get(idmap.indexOf(Integer.parseInt(fields[1])));
				if (sp.outs.size() == 2) {
					tfs.put(sp.getConfigNodeOut(), Double.parseDouble(fields[2]));
				} else {
					throw new Error("wrong number of incoming and outgoing cells for reading turnfractions");
				}
			}
			
			else if (routeBased && fields[0].equals("TripPatternPath")) { 
				
				TimeScaleFunction t = new TimeScaleFunction(fields[2]);
				nrTripsPattern = new TimeScaleFunction(t,1/3600.0);
				nrTripsPattern.shiftTime(nrTripsPattern, offset);
			}
			else if (routeBased && fields[0].equals("Path:")) {
				//if (null != exportTripPattern)
				//	tripList.add(exportTripPattern);
				//exportTripPattern = new ExportTripPattern(flowGraph, classProbabilities);

				ArrayList<Integer> route = new ArrayList<Integer>(); 
				for (int i = 3; i < fields.length; i++) {
					String field = fields[i];
					if (field.endsWith("a"))
						route.add(Integer.parseInt(field.substring(0, field.length() - 1)));
					if (! field.endsWith("a"))
						route.add(Integer.parseInt(field));
				}
				double routeProbability = Double.parseDouble(fields[1]);
				routes.addRoute(route, nrTripsPattern.getFactor(0)*routeProbability, nrTripsPattern);
				//exportTripPattern.addRoute(route, routeProbability);
			} 
			else if (fields[0].equals("Detector:")) {
				/*MacroCell mc = copySimPaths.get(Integer.parseInt(fields[1]));
    			for (int i = 2; i < fields.length; i++) {
    				Vertex v = new Vertex(fields[i]);
        			mc.addVertex(i-1,v);
        			detectorLocations.add(v);

        		}
				 */	

				Vertex v = new Vertex(fields[2]);
				NodeDetector det = new NodeDetector(v);
				det.setName(fields[1]);
				detectors.add(det);

				detectorLocations.add(v);
			} 
			else if (fields[0].equals("FD:")) {
				MacroCell sp = copySimPaths.get(idmap.indexOf(Integer.valueOf(fields[1])));
				sp.kCriPerLane = Double.valueOf(fields[2]);
				sp.kJamPerLane = Double.valueOf(fields[3]);
				sp.vCriBeforeInit = Double.valueOf(fields[4]);
				fd = FDs.fromString(fields[5]).create();
				//i = i+4;
				
				
				
			} else if (fields[0].equals("ExternalEvent:")) {
				ExternalEvent ext = ExternalEvents.fromString(fields[1]).create(Double.valueOf(fields[2]),Double.valueOf(fields[3]),Double.valueOf(fields[4]),Double.valueOf(fields[5]),fields[6]);
				externalEvents.add(ext);
				/*MacroCell sp = copySimPaths.get(idmap.indexOf(Integer.valueOf(fields[1])));
				sp.kCriPerLane = Double.valueOf(fields[2]);
				sp.kJamPerLane = Double.valueOf(fields[3]);
				sp.vCriBeforeInit = Double.valueOf(fields[4]);
				fd = FDs.fromString(fields[5]).create();*/
				//i = i+4;
				
				
				
			}
			else {
				//throw new Exception("Don't know how to parse " + line);
			}

			// TODO: write code to handle the not-yet-handled lines in the configuration
		}
		boolean mergeAndSplit = true;
		// Now all macrocells are generated, link upstream and downstream macrocells together. 
		
		for (MacroCell mc: copySimPaths) {
			//System.out.println("test");

			for (Integer i: mc.ins) {
				int k = idmap.indexOf((int) i);
				if (k==-1) {
					System.out.println("id niet gevonden: " + i);
				}
				mc.addIn(copySimPaths.get(idmap.indexOf((int) i)));
			}
			for (Integer j: mc.outs) {
				int k = idmap.indexOf((int) j);
				if (k==-1) {
					System.out.println("id niet gevonden: " + j);
				}
				mc.addOut(copySimPaths.get(k));
			}

		}

		// Next step: join cells as much as possible
		// Cells are joined when no difference in speed limit, no difference in lane, and no merges and splits are present
		if (mergeAndSplit) {
		// make list of links that still need to be joined
		ArrayList<Integer> todo = new ArrayList<Integer>();
		for (int i=0; i<copySimPaths.size(); i++) {
			todo.add((Integer) copySimPaths.get(i).id);
		}

		boolean join = true;

		// while there are links to be joined
		while ((!(todo.size() == 0))) {
			// make new cell with the same properties as the first cell in the to do list
			MacroCell snew = new MacroCell(model);
			MacroCell sbegin = copySimPaths.get(idmap.indexOf((int) todo.get(0)));

			todo.remove(0);
			snew.id = sbegin.id;
			snew.vertices.addAll(0, sbegin.vertices);
			snew.ups = (ArrayList<MacroCell>) sbegin.ups.clone();
			if (snew.id == 3474)
				System.out.println(1);
			for (MacroCell c: snew.ups) {
				int ind = c.downs.indexOf(sbegin);
				c.downs.remove(sbegin);
				c.downs.add(ind,snew);
			}
			snew.downs = (ArrayList<MacroCell>) sbegin.downs.clone();
			for (MacroCell c: snew.downs) {
				int ind = c.ups.indexOf(sbegin);
				c.ups.remove(sbegin);
				c.ups.add(ind,snew);
			}
			snew.setWidth(sbegin.getWidth());
			snew.setVLim(sbegin.getVLim());
			snew.setConfigNodeIn(sbegin.getConfigNodeIn());
			snew.setConfigNodeOut(sbegin.getConfigNodeOut());
			snew.kCriPerLane = sbegin.kCriPerLane;
			snew.kJamPerLane = sbegin.kJamPerLane;
			snew.vCriBeforeInit = sbegin.vCriBeforeInit;
			sbegin = null;
			// if there is only one cell upstream of considered cell
			while((snew.ups.size() == 1)) {

				MacroCell sp = snew.ups.get(0);
				if (sp.getConfigNodeOut() == 0 && snew.getConfigNodeIn() == 0) {
					snew.setConfigNodeIn(sp.getConfigNodeIn());
					sp.setConfigNodeOut(snew.getConfigNodeOut());
				}
				// test if cell upstream has the right nr of lanes and speedlimit
				if (!(sp.downs.size() == 1) || (!(sp.getWidth() == snew.getWidth())) || (!(sp.getVLim() == snew.getVLim())) || (join == false)) {

					break;
				} else {
					// cell upstream has the right nr of lanes and speed limit

					// add vertices of cell in front of vertices of current cell 
					snew.vertices.addAll(0,sp.vertices);

					// new cell to be considered is the upstream cell
					snew.ups = (ArrayList<MacroCell>) sp.ups.clone();
					// update links to upstream cells
					for (MacroCell c: snew.ups) {
						int ind = c.downs.indexOf(sp);
						c.downs.remove(sp);
						c.downs.add(ind,snew);
					}
					int configNodeIn = sp.getConfigNodeIn();
					if (configNodeIn != 0) {
						snew.setConfigNodeIn(configNodeIn);
					}

					// remove the upstream cell from to do list
					todo.remove(new Integer(sp.getId()));
					sp = null;
				}
			}
			// test if cell downstream has the right nr of lanes and speedlimit
			while((snew.downs.size() == 1)) {
				MacroCell sp = snew.downs.get(0);
				if (sp.getConfigNodeIn() == 0 && snew.getConfigNodeOut() == 0) {
					snew.setConfigNodeOut(sp.getConfigNodeOut());
					sp.setConfigNodeIn(snew.getConfigNodeIn());
				}
				if (!(sp.ups.size() == 1) || (!(sp.getWidth() == snew.getWidth())) || (!(sp.getVLim() == snew.getVLim()))|| (join == false)) {

					break;
				} else {
					// cell downstream has the right nr of lanes and speed limit

					// add vertices of cell at the end of vertices of current cell
					snew.vertices.remove(snew.vertices.size() -1);
					snew.vertices.addAll(sp.vertices);

					// new cell to be considered is the downstream cell
					snew.downs = (ArrayList<MacroCell>) sp.downs.clone();
					// update links to downstream cells
					for (MacroCell c: snew.downs) {
						int ind = c.ups.indexOf(sp);
						c.ups.remove(sp);
						c.ups.add(ind,snew);
					}
					if (sp.getConfigNodeOut() != 0) {
						snew.setConfigNodeOut(sp.getConfigNodeOut());
					}
					// remove the downstream cell from todo list
					todo.remove(new Integer(sp.getId()));
					sp = null;
				}
			}

			// add new (joined) cell to the list of cells
			//if (snew.id == 69385)
				//System.out.println("foute node Out");

			macroCells.add(snew);


		}
		}
		if (output)
			System.out.println(routes.routes);
		HashSet<Integer> nodesUsed = new HashSet<Integer>();
		for (MacroCell m: macroCells) {

			if (m.downs.size()==1 & m.getConfigNodeOut()==0) {
				m.setConfigNodeOut(m.downs.get(0).getConfigNodeOut());
			}
			if (m.ups.size()==1 & m.getConfigNodeIn()==0) {
				m.setConfigNodeIn(m.ups.get(0).getConfigNodeIn());
			}
		}
		for (MacroCell m: macroCells) {
			if (output) {
			System.out.println("Vertices pre-split: "+m.vertices.toString());
			System.out.println("Node at In: "+m.getConfigNodeIn());
			System.out.println("Node at Out: "+m.getConfigNodeOut());
			}

			nodesUsed.add(m.getConfigNodeIn());
			nodesUsed.add(m.getConfigNodeOut());
		}
		if (output)
			System.out.println(nodesUsed);
		routes.cleanRoutes(nodesUsed);
		if (output){
		System.out.println(routes.routes);
		System.out.println(routes.flows);
		}
		// Next step: split the joined cells into smaller cells of similar size
		ArrayList<MacroCell> copyCells = new ArrayList<MacroCell>();
		ArrayList<Link> setLinks = new ArrayList<Link>();
		for (MacroCell m: macroCells) {
			cellsInts.add(m.id);
		}
		for (MacroCell m: macroCells) {

			Link l = new Link(m);
			minLengthCells = l.vLim*model.dt+1;
			//minLengthCells = 100;
			// determine number of parts in which the cell must be split
			int nrParts = (int) Math.floor(m.calcLength()/minLengthCells);
			//int nrParts = 1;
			if (nrParts == 0)
				nrParts = 1;
			// add the cells that are split to the list
			ArrayList<MacroCell> list = splitInParts(m,nrParts);
			l.setCells(list);
			copyCells.addAll(list);
			setLinks.add(l);
			//if (m.calcLength()/m.getVLim() < 3.5)
			//System.out.println(m.id +":"+m.calcLength()/m.getVLim());
			//copyCells.addAll(m.splitInParts(1));
		}
		
		macroCells = copyCells;

		// give all the cells in the list new IDs
		/*int tel = 0;
		for (MacroCell m: macroCells) {
			m.setId(tel);
			tel++;
		}*/

		for (MacroCell m: macroCells) {
			//if (m.id == 3474)
				//System.out.println("foute node Out");
			//if (m.id == 69385)
				//System.out.println("foute node Out");
			if (m.nodeIn == null) {

				if (m.ups.size() > 0) {
					//NodeInteriorTampere n = new NodeInteriorTampere(m.vertices.get(0));
					NodeInterior n = new NodeInterior(m.vertices.get(0));
					for (MacroCell c: m.ups.get(0).downs) {
						n.cellsOut.add(c);
						c.nodeIn = n;

					}
					for (MacroCell c: m.ups) {
						//if (c.id == 216)
							//System.out.println("foute node Out");
						n.cellsIn.add(c);

						c.nodeOut = n;
						c.vertices.add(m.vertices.get(0));
					}
					nodes.add(n);
				} else {

					NodeBoundaryIn n = new NodeBoundaryIn(m.vertices.get(0),0);
					n.cellsOut.add(m);
					m.nodeIn = n;
					nodes.add(n);
				}


			}
			if (m.nodeOut == null) {

				if (m.downs.size() > 0) {
					//NodeInteriorTampere n = new NodeInteriorTampere(m.vertices.get(m.vertices.size()-1));
					NodeInterior n = new NodeInterior(m.vertices.get(m.vertices.size()-1));

					for (MacroCell c: m.downs.get(0).ups) {
						n.cellsIn.add(c);
						c.nodeOut = n;
					}

					for (MacroCell c: m.downs) {
						n.cellsOut.add(c);
						c.nodeIn = n;
						c.vertices.add(0,m.vertices.get(m.vertices.size()-1));
					}
					nodes.add(n);
				} else {
					NodeBoundaryOut n = new NodeBoundaryOut(m.vertices.get(0));
					n.cellsIn.add(m);
					m.nodeOut = n;
					nodes.add(n);
				}

			}
		}



		ArrayList<NodeInterior> junctionNodes = new ArrayList<NodeInterior>();
		ArrayList<NodeBoundaryIn> inflowNodes = new ArrayList<NodeBoundaryIn>();
		ArrayList<NodeBoundaryOut> outflowNodes = new ArrayList<NodeBoundaryOut>();
		for (Node n: nodes) {
			if ((n.cellsIn.size() != 1 || n.cellsOut.size() != 1) && (n.cellsOut.size()+n.cellsIn.size() != 0)) {
				HashSet<Integer> nodeIds = new HashSet<Integer>();

				for(MacroCell up: n.cellsIn) {
					nodeIds.add(up.getConfigNodeOut());
				}
				for(MacroCell down: n.cellsOut) {
					nodeIds.add(down.getConfigNodeIn());
				}
				if (output)
				System.out.println(nodeIds);
				if (nodeIds.size() == 1) {
					if (n.cellsIn.size() != 0) {
						n.setId(n.cellsIn.get(0).getConfigNodeOut());
					} else { 
						n.setId(n.cellsOut.get(0).getConfigNodeIn());
					}

					if ((n.cellsIn.size() != 0 && n.cellsOut.size() != 0)) {
						junctionNodes.add((NodeInterior) n);
					} else if (n.cellsIn.size() == 0) {
						inflowNodes.add((NodeBoundaryIn) n);
					} else if (n.cellsIn.size() == 0) {
						outflowNodes.add((NodeBoundaryOut) n);
					}
				} else {
					throw new Error("Wrong references to nodes in adjacent cells");
				}
			}
		}
		if (output)
		System.out.println(junctionNodes);
		// initialize all cells (e.g. determine parameters needed for simulation) and add to the model

		for (Node n: nodes) {

			n.init();
			n.setDefaultTurningRatio();
			model.addNode(n);


		}

		for (MacroCell m: macroCells) {
			//System.out.println("Vertices1: "+m.vertices.toString());
			//m.smoothVertices(0.8);
			//System.out.println("Vertices2: "+m.vertices.toString());

		}


		for (MacroCell m: macroCells) {
			
			m.fd = fd;
			m.init();
			model.addMacroCell(m);

		}
		for (Link l: setLinks) {
			l.updateVars();
			
		}
		model.setLinks(setLinks);
		model.setJunctionNodes(junctionNodes);
		if (routeBased) {
			routes.setTurnFractions(junctionNodes);
			routes.setInflowBoundaries(inflowNodes);
		} else {
			for (NodeInterior n: model.getJunctionNodes()) {
					n.setTurningRatioCompact(tfs.get(new Integer(n.getId())));
				}
			for (NodeBoundaryIn n: inflowNodes) {
				n.setInflow(flows.get(new Integer(n.getId())).getFactor(0)*n.cellsOut.get(0).lanes);
				n.addTimeScaleFunction(flows.get(new Integer(n.getId())));
			}
		
		}
		

		for (MacroCell m: macroCells) {
			//System.out.println("length:"+m.l);
			//System.out.println("NodeIn: "+m.indexNodeIn);
			//System.out.println("NodeOut: "+m.indexNodeOut);
		}
		model.setExternalEvents(externalEvents);
		model.init();
		if (output) {
		for (MacroCell m: macroCells) {
			System.out.println("index: "+macroCells.indexOf(m)+" from:"+m.vertices.get(0)+" to:"+m.vertices.get(m.vertices.size()-1));
			//System.out.println("NodeIn: "+m.indexNodeIn);
			//System.out.println("NodeOut: "+m.indexNodeOut);
		} 
		}
		HashMap<NodeDetector, MacroCell> detLoc = new HashMap<NodeDetector, MacroCell>();
		HashMap<NodeDetector, Double> detDist = new HashMap<NodeDetector, Double>();
		long bt = System.currentTimeMillis();
		for (NodeDetector n: detectors) {

			double bestDistance = Double.MAX_VALUE;
			double relDistance = Double.MAX_VALUE;
			double distanceFromBegin = Double.MAX_VALUE;
			MacroCell selectedCell = null;
			for (MacroCell m: macroCells) {
				ArrayList<Vertex> cellVertices = m.vertices;
				double[] distance = new double[]{0,0};
				/*for (Vertex v: cellVertices) {
		        		//Point2D.Double translated = graphicsPanel.translate(v.getPoint());

		        		distance += p.distance(v);
		        	}
		        	distance = distance/cell.l;*/
				if (n.location.distance(m.vertices.get(0)) > 100000)
					continue;
				distance = m.getSquaredDistanceToVertices(n.location);
				//double distance = p.distance(new Vertex(translated,0));
				if ((distance[0] < bestDistance)) {
					selectedCell = m;
					bestDistance = distance[0];
					relDistance = distance[1];
					distanceFromBegin = distance[2];

				}


			}
			if (selectedCell == null)
				System.out.println("selectCell = null");
			n.setClosestCell(selectedCell);
			n.setDistanceToCell(Math.sqrt(bestDistance));

			n.setDistanceFromNode(distanceFromBegin);
			n.setFromNode(selectedCell.getConfigNodeIn());
			n.setToNode(selectedCell.getConfigNodeOut());
			detLoc.put(n, selectedCell);
			detDist.put(n, relDistance);
			selectedCell.detector = true;
			//n.addMeasurements(0);


			if (output)
			System.out.println(n.location + " in cell "+selectedCell+": d:" + bestDistance);


		}
		//System.out.println(System.currentTimeMillis() - bt);
		model.setDetectors(detectors);
		if (routeBased)
			routes.setExtendedRoutes(inflowNodes);
		model.setRoutes(routes);
		
		boolean outputNeeded = false;
		if (outputNeeded) {
		PrintWriter out;
		out = new PrintWriter("C:\\Users\\Friso\\Documents\\PilotDatafusie\\detectorOutput.txt");
		String sep = ",";
		out.println("DetectorID"+ sep +"LaneNr"+ sep +"LinkID"+ sep +"DistanceFromOriginNode");
		for (int i = 0; i < detectors.size(); i++) {
			NodeDetector n = detectors.get(i);
			//if (!links.contains(new Integer(cellId.get(i)))) {
			/*String ins = "ins\t";
			String outs = "outs\t";
			String v = "";
			for (Integer j: detectors.get(i)) {
				ins += j+"\t";
			}
			for (Integer j: cellsOut.get(i)) {
				outs += j+"\t";
			}
			for (String s: vertices.get(i)) {
				v += s+"\t";
			}*/
			//ins = ins.trim();
			//outs = outs.trim();
			//v = v.trim();

			String output = n.getName() + sep + n.getName().charAt(n.getName().length()-1) + sep + n.getClosestCell().id + sep + n.getDistanceFromNode();
			//String output = "Roadway:	"+cellId.get(i)+"	from	"+nodeIn.get(i)+"	to	"+nodeOut.get(i)+"	speedlimit	"+speedlimit.get(i)+"	lanes	"+lanes.get(i)+"	vertices	"+v+ins+outs;
			//String[] o2 = output.split("\t");
			out.println(output);
			//}
		}
		out.close();

		}



	}
	
	
	@SuppressWarnings("unchecked")
	static public ArrayList<MacroCell> splitInParts(MacroCell mc, int nrParts) {
		ArrayList<MacroCell> result = new ArrayList<MacroCell>();
		//System.out.println("Joined link " + id + " is splitted into " + nrParts + " parts");
		//System.out.println(nrParts);
		if (nrParts == 1 || nrParts == 0) {
			result.add(mc);
			
		} else {
			cellsInts.remove(mc.id);
			int vert = 0;
			mc.l = mc.calcLength();
		for (int i = 0; i< nrParts - 1; i++) {
			
			MacroCell m = new MacroCell(mc.model);
			
			m.setWidth(mc.getWidth());
			m.setVLim(mc.vLim);
			
			while(cellsInts.contains(lastInt)) {
				lastInt++;
			}
			m.setId(lastInt);
			lastInt++;
			m.setConfigNodeIn(mc.getConfigNodeIn());
			m.setConfigNodeOut(mc.getConfigNodeOut());
			m.kCriPerLane = mc.kCriPerLane;
			m.kJamPerLane =mc.kJamPerLane;
			m.vCriBeforeInit = mc.vCriBeforeInit;
			
			double res[] = mc.calcPointAtDistance((i+1)*(mc.l)/(nrParts));
			//System.out.println(Arrays.toString(res));
			//System.out.println("vert: " + Integer.toString(vert) + " res: " + Double.toString(res[2]));
			m.vertices = new ArrayList<Vertex>(mc.vertices.subList(vert, (int) res[2]));
			vert = (int) res[2];
			m.vertices.add(new Vertex(res[0],res[1],0));
			mc.vertices.add(vert, new Vertex(res[0],res[1],0));
		
			
			result.add(m);
		}
		mc.vertices = new ArrayList<Vertex>(mc.vertices.subList(vert, mc.vertices.size()));
	
		
		result.get(0).ups = (ArrayList<MacroCell>) mc.ups.clone();
		for (MacroCell c: mc.ups) {
			int ind = c.downs.indexOf(mc);
			c.downs.remove(mc);
			c.downs.add(ind,result.get(0));
		}
		
			
		
		for(int j=1; j < nrParts -1;j++) {
			//result.get(j-1).downs.clear();
			result.get(j-1).downs.add(result.get(j));
			result.get(j).ups.add(result.get(j-1));
		}
		
		
		result.get(nrParts-2).downs.add(mc);
		mc.ups.clear();
		mc.ups.add(result.get(nrParts-2));
		
		
		result.add(mc);
		}
		//System.out.println(result.toString());
		return result;
	}


	public final Model getModel() {
		return model;
	}

	@Override
	public void setModified() {
		// TODO Auto-generated method stub

	}

	@Override
	public void repaintGraph(GraphicsPanel graphicsPanel) {
		for (MacroCell sp : macroCells) {
			sp.draw(graphicsPanel);
		}
		for (Node n: nodes) {
			n.draw(graphicsPanel);
		}
		for (NodeDetector n: detectors) {
			n.draw(graphicsPanel);
		}
		for (NodeDetector n: detectors) {
			//double[] point = n.getClosestCell().calcPointAtDistance(n.getDistanceFromNode());
			//double[] point = ;
			//Vertex v = new Vertex(point[0], point[1], 0);
			Vertex v = n.location;
			int nonSelectedNodeDiameter = (int) (5.2 *  graphicsPanel.getZoom());
	    	          
	        final Color color = Color.BLACK;
	        graphicsPanel.setColor(color);
	        graphicsPanel.setStroke(1f);
	        graphicsPanel.drawCircle(v.getPoint(), color, nonSelectedNodeDiameter);
	        graphicsPanel.setStroke(3f);
		}
		
	}
	private ObjectInspector objectInspector = null;
	private Vertex mouseDown = null;
	MacroCell selectedCell = null;
	NodeDetector selectedDetector = null;

	double selectCell(GraphicsPanel graphicsPanel, Vertex p) {
		//System.out.println(String.format("Searching vehicle near %f,%f (rev %f,%f", p.x, p.y, graphicsPanel.reverseTranslate(p).getX(), graphicsPanel.reverseTranslate(p).getY()));
		final int maxDistance = 100;	// pixels
		double bestDistance = Double.MAX_VALUE;
		MacroCell prevSelectedCell = selectedCell;
		if (null != prevSelectedCell)
			prevSelectedCell.selected = false;
		p = new Vertex(graphicsPanel.reverseTranslate(p.getPoint()),0);
		//System.out.println(p);
		for (MacroCell cell : model.getCells()) {
			ArrayList<Vertex> cellVertices = cell.vertices;
			double distance[] = new double[]{0,0};
			/*for (Vertex v: cellVertices) {
        		//Point2D.Double translated = graphicsPanel.translate(v.getPoint());

        		distance += p.distance(v);
        	}
        	distance = distance/cell.l;*/
			distance = cell.getSquaredDistanceToVertices(p);
			//if (distance[0] < maxDistance) {
			//System.out.println(cell.id + ": "+ distance);
			//}
			//double distance = p.distance(new Vertex(translated,0));
			if ((distance[0] < maxDistance) && (distance[0] < bestDistance)) {
				selectedCell = cell;
				bestDistance = distance[0];

			}
		}
		//if (null == selectedCell)
		//System.out.println(String.format("No cell found near %f,%f", p.getX(), p.getY()));
		// Repaint is only required if selected nodes are painted different from non-selected nodes
		// Actually, GraphicsPanel ALWAYS generates a repaint so these two lines are redundant
		if (prevSelectedCell != selectedCell)
			graphicsPanel.repaint();
		return bestDistance;
	}
	double selectDetector(GraphicsPanel graphicsPanel, Vertex p) {
		final int maxDistance = 100;	// pixels
		double bestDistance = Double.MAX_VALUE;
		NodeDetector prevSelectedDetector = selectedDetector;
		//if (null != prevSelectedDetector)
		//prevSelectedDetector.selected = false;
		p = new Vertex(graphicsPanel.reverseTranslate(p.getPoint()),0);
		//System.out.println(p);
		for (NodeDetector c : detectors) {
			double distance = c.location.distance(p);
			/*for (Vertex v: cellVertices) {
        		//Point2D.Double translated = graphicsPanel.translate(v.getPoint());

        		distance += p.distance(v);
        	}
        	distance = distance/cell.l;*/
			//distance = cell.getSquaredDistanceToVertices(p);
			//if (distance[0] < maxDistance) {
			//System.out.println(cell.id + ": "+ distance);
			//}
			//double distance = p.distance(new Vertex(translated,0));
			if ((distance < maxDistance) && (distance < bestDistance)) {
				selectedDetector = c;
				bestDistance = distance;

			}
		}
		//if (null == selectedCell)
		//System.out.println(String.format("No cell found near %f,%f", p.getX(), p.getY()));
		// Repaint is only required if selected nodes are painted different from non-selected nodes
		// Actually, GraphicsPanel ALWAYS generates a repaint so these two lines are redundant
		//if (prevSelectedCell != selectedCell)
		//graphicsPanel.repaint();
		return bestDistance;
	}
	@Override
	public void mousePressed(GraphicsPanel graphicsPanel, MouseEvent evt) {
		if (null != mouseDown){ // Ignore mouse presses that occur
			return;            	//  when user is already drawing a curve.
		}//    (This can happen if the user presses two mouse buttons at the same time.)

		if (null != objectInspector) {
			objectInspector.dispose();
			objectInspector = null;
		}
		mouseDown = new Vertex(evt.getX(), evt.getY(),0);

		if (evt.getButton() == evt.BUTTON1 || evt.getButton() == evt.BUTTON3) {
			if (selectCell(graphicsPanel, mouseDown) != Double.MAX_VALUE) {
				objectInspector = new ObjectInspector(selectedCell, this);
				selectedCell.selected = true;
				if (evt.getButton() == evt.BUTTON3)
					System.out.println("onandofframps.add("+selectedCell.id+");");
			} 
		} else if (evt.getButton() == evt.BUTTON2) {
			double val = selectDetector(graphicsPanel, mouseDown);
			if (val != Double.MAX_VALUE) {
				objectInspector = new ObjectInspector(selectedDetector, this);
				//selectedCell.selected = true;


			}
		}
	}
	@Override
	public void mouseDragged(GraphicsPanel graphicsPanel, MouseEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(GraphicsPanel graphicsPanel, MouseEvent evt) {

		mouseDown = null;
	}

	@Override
	public void mouseMoved(GraphicsPanel graphicsPanel, MouseEvent evt) {
		Point2D.Double reversePosition = graphicsPanel.reverseTranslate(new Point2D.Double(evt.getX(), evt.getY()));
		Main.mainFrame.setStatus(-1, "Mouse pointer at %.2f,%.2f", reversePosition.x, reversePosition.y);

	}

	@Override
	public void ShutDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preStep() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Scheduler getScheduler() {
		return scheduler;
	}

	@Override
	public ArrayList<SimulatedObject> SampleMovables() {
		// TODO Auto-generated method stub
		return null;
	}
	class Stepper implements Step {
		final private MacroSimulator macroSimulator;

		public Stepper (MacroSimulator macroSimulator) {
			this.macroSimulator = macroSimulator;
		}


		@Override
		public Scheduler.SchedulerState step(double now) {
			//System.out.println("step entered");
			Model model = macroSimulator.getModel();
			//System.out.println(Double.toString(model.period));
			//System.out.println(Double.toString(now));
			//System.out.println(Double.toString(model.t()));
			if (now >= model.period)
				return Scheduler.SchedulerState.EndTimeReached;
			while (model.t() < now) {
				//System.out.println("step calling run(1)");
				try {
					//System.out.format(Main.locale, "Time is %.3f\r\n", now);
					model.run(1);
				} catch (RuntimeException e) {
					WED.showProblem(WED.ENVIRONMENTERROR, "Error in MacroSimulator:\r\n%s", WED.exeptionStackTraceToString(e));
					return Scheduler.SchedulerState.SimulatorError;
				}
			}
			// re-schedule myself
			macroSimulator.getScheduler().enqueueEvent(model.t() + model.dt, this);
			//System.out.println("step returning true");
			return null;
		}


	}
}
